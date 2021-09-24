# rpc
一个简单的基于Springboot + Netty + ProtoStuff + Zookeeper实现的自定义RPC框架
#使用教程
(1)准备一个Zookeeper作为注册中心
(2)将项目生成maven依赖(在pom.xml文件所在目录用mvn install命令生成)
(3)新建一个项目来测试，将(2)生成的依赖导入，创建一个生产者一个消费者模块。在生产者模块中用@Service注入远程服务
消费者模块中用@InjectService注入远程服务，被注入的对象将会用动态代理的方式生成代理对象去调用远程服务
