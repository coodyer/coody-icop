更新记录：

2018-02-24：初步研发，实现IOC+AOP

2018-02-25：拓展MVC，基于MVC结合ICO+AOP实现轻量化无依赖类spring开发模式

2018-02-26：整改为maven模式，并整合自写ORM框架配合aop实现事物管理。


=======================================================

1. 项目背景：

由于笔者近期参与的一些项目体系未使用到任何框架，而笔者对spring体系特别向往，故此研发本项目。

2. 功能说明：

本项目实现注解形式的bean加载、依赖注入、切面等功能。简单实现mvc。

3. 项目特点：

本项目使用cglib。秉承轻量、易用、简单、高效等原则。依赖jar：cglib-nodep-3.1.jar fastjson-1.2.31.jar log4j-1.2.17.jar  依赖jar包其余版本自行测试。

4. 环境说明：

JDK1.8+

5.目录结构： 

![Image text](https://static.oschina.net/uploads/space/2018/0225/215135_sePy_3094707.png)

6. 程序架构：

由于在撰写本文背景下无作图环境，故此略去架构图。以下提供一些结构说明

1、包说明

org.coody.framework.entity常用实体包。

org.coody.framework.util  常用工具包

org.coody.framework.box  核心实现包

org.coody.framework.box.adapt 适配器包

org.coody.framework.box.annotation 注解包

org.coody.framework.box.container 容器包

org.coody.framework.box.constant 常量包

org.coody.framework.box.iface 接口包

org.coody.framework.box.init 初始化入口包

org.coody.framework.box.mvc MVC实现包

org.coody.framework.box.proyx 动态代理包

org.coody.framework.box.wrapper 包装类

2、类说明-注解

org.coody.framework.box.annotation.Around环绕通知注解标识，用于切面实现

org.coody.framework.box.annotation.InitBean初始化Bean。类似于spring的Service等注解，标记一个bean类

org.coody.framework.box.annotation.JsonSerialize序列化JSON输出，用于controller方法标识。类似于spring的ResponseBody注解

org.coody.framework.box.annotation.OutBean 输出Bean。类似于Resource/AutoWired注解

org.coody.framework.box.annotation.PathBinding 输出Bean。类似于Resource/AutoWired注

3、类说明-适配器

org.coody.framework.box.adapt.ParamsAdapt  参数适配器，用于MVC下参数的装载(目前只实现request、response、session三个参数的自动装载)

4、类说明-容器

org.coody.framework.box.container.BeanContainer 容器，用于存储bean，类似于spring的application

org.coody.framework.box.container.MappingContainer  Mvc映射地址容器

5、类说明-接口

org.coody.framework.box.iface.InitFace 初始化接口，凡是实现该接口的bean需实现init方法。在容器启动完成后执行。

6、类说明-启动器

org.coody.framework.box.init.BoxRute 容器入口。通过该类的init(packet)方法指定扫描包路径

org.coody.framework.box.init.BoxServletListen 监听器，配置在webxml用于引导容器初始化

7、类说明-mvc分发器

org.coody.framework.box.mvc.DispatServlet 类似于spring的DispatServlet

8、类说明-代理工具

org.coody.framework.box.proyx.CglibProxy 基于cglib字节码创建子类的实现

9、类说明-包装类

org.coody.framework.box.wrapper.AspectWrapper 本处命名可能不尽规范。本类用于多切面的调度和适配  

7. 实现效果：
![Image text](https://static.oschina.net/uploads/space/2018/0225/215613_79vh_3094707.png)

![Image text](https://static.oschina.net/uploads/space/2018/0225/215847_nlKD_3094707.png)




8. 版权说明：

在本项目源代码中，已有测试demo，包括mvc、切面等示例

作者：Coody

反馈邮箱：644556636@qq.com
