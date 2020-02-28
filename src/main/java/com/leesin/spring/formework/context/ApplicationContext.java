package com.leesin.spring.formework.context;

import com.leesin.spring.formework.annotation.Autowired;
import com.leesin.spring.formework.annotation.Controller;
import com.leesin.spring.formework.annotation.Service;
import com.leesin.spring.formework.aop.AopProxy;
import com.leesin.spring.formework.aop.CglibAopProxy;
import com.leesin.spring.formework.aop.JdkDynamicAopProxy;
import com.leesin.spring.formework.aop.config.AopConfig;
import com.leesin.spring.formework.aop.support.AdvisedSupport;
import com.leesin.spring.formework.beans.BeanWrapper;
import com.leesin.spring.formework.beans.config.BeanDefinition;
import com.leesin.spring.formework.beans.config.BeanPostProcessor;
import com.leesin.spring.formework.beans.support.BeanDefinitionReader;
import com.leesin.spring.formework.beans.support.DefaultListableBeanFactory;
import com.leesin.spring.formework.core.BeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:按之前源码分析的套路，IOC DI MVC AOP
 * @author: Administrator
 * @date: Created in 2020/2/27 22:18
 * @version:
 * @modified By: Leesin Dong
 */
public class ApplicationContext extends DefaultListableBeanFactory implements BeanFactory {

    private String[] configLocations;
    private BeanDefinitionReader reader;
    //单例的IOC容器缓存
    private Map<String, Object> singletonObjects = new ConcurrentHashMap<String, Object>();
    //通用的IOC容器
    private Map<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, BeanWrapper>();

    public ApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        //定位配置文件
        reader = new BeanDefinitionReader(this.configLocations);
        //加载配置文件，扫描相关的类，把他们封装成BeanDefinition
        List<BeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        //注册，把配置信息放到容器里面（伪IOC容器）
        doRegisterBeanDefinition(beanDefinitions);
        //把不是延时加载的类，有提前初始化
        doAutowrited();
    }

    private void doRegisterBeanDefinition(List<BeanDefinition> beanDefinitions) throws Exception {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("The “" + beanDefinition.getFactoryBeanName() + "” is exists!!");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
        // 到这里，容器初始化完毕
    }

    //只处理非延时加载的情况
    private void doAutowrited() {
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (beanDefinitionEntry.getValue().isLazyInit()) {
                getBean(beanName);
            }
        }
    }

    /**
     * @description: 依赖注入从这里开始，通过读取BeanDefinition中的信息
     * 然后通过反射机制创建一个实例返回
     * spring的做法是，不会把原始的对象放出去，会用一个BeanWrapper来进行一次包装
     * 装饰器模式：
     * 1、保持原来的oop关系
     * 2、我需要对它进行扩展，增强（为了以后AOP打基础）
     * @name: getBean
     * @param: beanName
     * @return: java.lang.Object
     * @date: 2020/2/28 11:11
     * @auther: Administrator
     **/
    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        Object instance = null;
        //工厂模式+策略模式
        BeanPostProcessor postProcessor = new BeanPostProcessor();
        postProcessor.postProcessBeforeInitialization(instance, beanName);
        instance = instantiateBean(beanName, beanDefinition);
        //3、把这个对象封装到BeanWrapper中
        BeanWrapper beanWrapper = new BeanWrapper(instance);
        //2、拿到BeanWraoper之后，把BeanWrapper保存到IOC容器中去
        this.factoryBeanInstanceCache.put(beanName,beanWrapper);
        postProcessor.postProcessAfterInitialization(instance,beanName);
        //3、注入
        populateBean(beanName,new BeanDefinition(),beanWrapper);
        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }

    private Object instantiateBean(String beanName, BeanDefinition beanDefinition) {
        //拿到实例化的对象的类名
        String className = beanDefinition.getBeanClassName();
        //反射实例化，得到一个对象
        Object instance = null;
        try {
            if (this.factoryBeanInstanceCache.containsKey(className)) {
                instance = this.factoryBeanInstanceCache.get(className);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                instantionAopConfig(beanDefinition);

            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
        }
        return instance;
    }

    private AopProxy createProxy(AdvisedSupport config) {

        Class targetClass = config.getTargetClass();
        if(targetClass.getInterfaces().length > 0){
            return new JdkDynamicAopProxy(config);
        }
        return new CglibAopProxy(config);
    }

    private AdvisedSupport instantionAopConfig(BeanDefinition beanDefinition) {
        AopConfig config = new AopConfig();
        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new AdvisedSupport(config);
    }
    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new  String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount(){
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig(){
        return this.reader.getConfig();
    }

    public void refresh() {

    }

    @Override

    public Object GetBean(String Name) throws Exception {
        return null;
    }

    private void populateBean(String beanName, BeanDefinition gpBeanDefinition, BeanWrapper gpBeanWrapper) {
        Object instance = gpBeanWrapper.getWrappedInstance();

//        gpBeanDefinition.getBeanClassName();

        Class<?> clazz = gpBeanWrapper.getWrappedClass();
        //判断只有加了注解的类，才执行依赖注入
        if(!(clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(Service.class))){
            return;
        }

        //获得所有的fields
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if(!field.isAnnotationPresent(Autowired.class)){ continue;}

            Autowired autowired = field.getAnnotation(Autowired.class);

            String autowiredBeanName =  autowired.value().trim();
            if("".equals(autowiredBeanName)){
                autowiredBeanName = field.getType().getName();
            }

            //强制访问
            field.setAccessible(true);

            try {
                //为什么会为NULL，先留个坑
                if(this.factoryBeanInstanceCache.get(autowiredBeanName) == null){ continue; }
//                if(instance == null){
//                    continue;
//                }
                field.set(instance,this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

    }
}
