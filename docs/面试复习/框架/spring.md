# spring 三级缓存：

1. singletonObjects:第一级缓存, 存放可用的完全初始化, 成品的Bean。
2. earlySingletonObjects:第二级缓存, 存放已创建对象, 但是未注入属性和初始化。用以解决循环依赖。
3. singletonFactories:第三级缓存, 存的是Bean工厂对象, 用来生成半成品的Bean并放入到二级缓存中。用以解决循环依赖。如果Bean存在AOP的话, 返回的是AOP的代理对象。

# 线程安全：

ThreadLocal 进行处理, ThreadLocal 是线程本地变量, 每个线程拥有变量的一个独立副本, 所以各个线程之间互不影响, 保证了线程安全

# Bean的生命周期：

1. 实例化 BeanFactoryPostProcessor 实现类执行 postProcessBeanFactory 方法创建 Bean 工厂
2. 实例化 BeanPostProcessor 实现类, 实例化 InstantiationAwareBeanPostProcessorAdapter 实现类
3. 执行 InstantiationAwareBeanPostProcessor 的 postProcessBeforeInstantiation 方法
4. 执行 bean 的构造器
5. 执行 InstantiationAwareBeanPostProcessor 的 postProcessPropertyValues 方法
6. 为 Bean 注入属性, 调用 setBeanName 方法和 setBeanFactory 方法
7. 执行 BeanPostProcessor 的 postProcessBeforeInitialization 方法
8. 调用 InitializingBean 的 afterPropertiesSet 方法
9. 调用 bean 的 init-method 属性指订的初始化方法
10. 执行 BeanPostProcessor 的 postProcessAfterInitialization 方法
11. 容器初始化成功, 执行正常调用后, destory

实例化 -> 属性赋值 -> 初始化 -> 销毁
# spring starter 原理
@springbootApplixation 注解读取每个 starter 中的 spring.factories, 里面配置了所有需要被创建 spring 容器的 bean, 自动配置把 bean 注入到 springcontext 中

# 常用注解
@autowried
@component
@requestmapping

# spring 
控制反转 ioc (依赖注入)
依赖注入 di (装配对象, 配置对象, 管理生命周期)
aop 横切关注点分离

# 作用域
singleton: bean 在每个 ioc 仅仅一个实例
prototype: bean 定义有多个实例