package com.lmh.rpc.server.register;

import com.lmh.rpc.annotation.InjectService;
import com.lmh.rpc.annotation.Service;
import com.lmh.rpc.client.ClientProxyFactory;
import com.lmh.rpc.server.RpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

/**
 * 实现了ApplicationListener,监听ContextRefreshedEvent事件
 * 该事件会在springboot启动时，在创建完容器之后，会有一个刷新容器的事件
 * 当监听到这个事件就会执行onApplicationEvent事件，扫描注解开启服务
 */
public class DefaultRpcProcessor implements ApplicationListener<ContextRefreshedEvent> {

    private Logger logger = LoggerFactory.getLogger(DefaultRpcProcessor.class);

    @Resource
    private ServiceRegister serviceRegister;

    @Resource
    private ClientProxyFactory clientProxyFactory;

    @Resource
    private RpcServer rpcServer;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(Objects.isNull(event.getApplicationContext().getParent())) {
            ApplicationContext context = event.getApplicationContext();
            // 开启服务
            startService(context);

            // 注入Service
            injectService(context);
        }
    }

    private void startService(ApplicationContext applicationContext) {
        //获取使用了注解@Service的类到Map中
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Service.class);
        if (beans.size() != 0) {
            boolean startServiceFlag = true;
            ServiceObject so;
            //遍历beans
            for(Object object : beans.values()) {
                try {
                    //将使用@Service注解的类赋值给clazz
                    Class<?> clazz = object.getClass();
                    //将该类的接口赋值给数组interfaces
                    Class<?>[] interfaces = clazz.getInterfaces();
                    if(interfaces.length != 1) {
                        Service service = clazz.getAnnotation(Service.class);
                        //注解的value值
                        String value = service.value();
                        if(value.equals("")) {
                            startServiceFlag = false;
                            throw new UnsupportedOperationException("The exposed interface is not specific with '" + object.getClass().getName() + "'");
                        }
                        so = new ServiceObject(value, Class.forName(value) ,object);
                    }
                    else {
                        Class<?> superClass = interfaces[0];
                        so = new ServiceObject(superClass.getName(), superClass, object);
                    }
                    serviceRegister.register(so);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(startServiceFlag == true) {
                rpcServer.start();
            }
        }
    }

    private void injectService(ApplicationContext context) {
        //获取所有bean的名称
        String[] names = context.getBeanDefinitionNames();
        for(String name : names) {
            Class<?> clazz = context.getType(name);
            if(Objects.isNull(clazz)) continue;
            //获取bean的所有属性
            Field[] fields = clazz.getDeclaredFields();
            for(Field field : fields) {
                InjectService injectService = field.getAnnotation(InjectService.class);
                if(Objects.isNull(injectService)) continue;
                Class<?> fieldClass = field.getType();
                //获取含有injectService注解的field的对象
                Object object = context.getBean(name);
                field.setAccessible(true);
                try {
                    //为objec对象的field生成代理对象,放入容器中
                    field.set(object, clientProxyFactory.getProxy(fieldClass));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
