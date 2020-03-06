package com.leesin.spring.formework.context;

import com.leesin.spring.formework.annotation.Autowired;
import com.leesin.spring.formework.annotation.Controller;
import com.leesin.spring.formework.annotation.Service;
import com.leesin.spring.formework.aop.AopProxy;
import com.leesin.spring.formework.aop.CglibAopProxy;
import com.leesin.spring.formework.aop.JdkDynamicAopProxy;
import com.leesin.spring.formework.aop.config.AopConfig;
import com.leesin.spring.formework.aop.support.AdvisedSupport;
import com.leesin.spring.formework.beans.BeanFactory;
import com.leesin.spring.formework.beans.BeanWrapper;
import com.leesin.spring.formework.beans.config.BeanDefinition;
import com.leesin.spring.formework.beans.config.BeanPostProcessor;
import com.leesin.spring.formework.beans.support.BeanDefinitionReader;
import com.leesin.spring.formework.beans.support.DefaultListableBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: Leesin Dong
 * @date: Created in 2020/2/29 21:41
 * @version:
 * @modified By:
 */
//DefaultListableBeanFactory默认的容器实现，就像策略模式中的默认的（付款，既不是微信，也不是支付宝，但是至少默认要能付款）
//BeanFactory，是容器的规范，顶层的设计
public class ApplicationContext extends DefaultListableBeanFactory implements BeanFactory {
    private String[] configLocations;
    private BeanDefinitionReader reader;
    //单例的IOC容器，这里是用来保存instance的
    //这里为什么两个IOC容器，一个是为了incentice这个实例的存储，一个是为了PopulateBean之前BeanWrapper的存储
    private Map<String, Object> singletonObjects = new ConcurrentHashMap<String, Object>();
    //通用的IOC容器
    private Map<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, BeanWrapper>();

    public ApplicationContext() {

    }

    public ApplicationContext(String... configLocations) throws Exception {
        this.configLocations = configLocations;
        refresh();
    }

    @Override
    public void refresh() throws Exception {
        //1 定位配置文件
        reader = new BeanDefinitionReader(this.configLocations);
        //2 加载配置化文件，扫描相关的类，把他们封装成BeanDefinition
        List<BeanDefinition> beanDefinitions = reader.loadBeanDefinitions(configLocations);
        //3 注册，把配置信息放到容器里面（伪IOC容器）
        doRegisterBeanDefinition(beanDefinitions);
        //4 把不是延时加载的类，要提前初始化
        doAutoWrited();
    }

    //只处理非延时加载的情况
    private void doAutoWrited() throws Exception {
        for (Map.Entry<String, BeanDefinition> stringBeanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = stringBeanDefinitionEntry.getKey();
            if (!stringBeanDefinitionEntry.getValue().isLazyInit()) {
                getBean(beanName);
            }
        }

    }

    private void doRegisterBeanDefinition(List<BeanDefinition> beanDefinitions) {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
    }

    @Override
    public Object getBean(String beanName) throws Exception {

        //初始化
        Object instance = null;
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        instance = instantiateBean(beanName, beanDefinition);

        //工厂模式+策略模式
        BeanPostProcessor beanPostProcessor = new BeanPostProcessor();
        beanPostProcessor.postProcessBeforeInitiaLization(instance, beanName);

        //1 为什么出事话和注入是两个方法，而不是一个方法？，下面我到底先初始化谁？
        //class A{ B b;}
        //class B{ A a;}
        //先有鸡还是先有蛋的问题，一个方法是搞不定的，要分两次
        //2 拿到BeanWrapper后，把BeanWrapper保存到IOC容器中去
        BeanWrapper beanWrapper = new BeanWrapper(instance);
        this.factoryBeanInstanceCache.put(beanName, beanWrapper);

        beanPostProcessor.postProcessAfterInitiaLization(instance, beanName);

        //3 注入
        populateBean(beanName, new BeanDefinition(), beanWrapper);
        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }

    private void populateBean(String beanName, BeanDefinition beanDefinition, BeanWrapper beanWrapper) throws IllegalAccessException {
        Object instance = beanWrapper.getWrappedInstance();
        Class<?> clazz = beanWrapper.getWrappedClass();
        //判断只有加了注解的类，才执行依赖注入。
        if (!(clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(Service.class))) {
            return;
        }
        //获得所有的fields；
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Autowired.class)) {
                continue;
            }
            Autowired autowired = field.getAnnotation(Autowired.class);
            String autowiredBeanName = autowired.value().trim();
            if ("".equals(autowiredBeanName)) {
                autowiredBeanName = field.getType().getName();
            }
            field.setAccessible(true);
            //为什么会为NULL，先留个坑
            if (this.factoryBeanInstanceCache.get(autowiredBeanName) == null) {
                continue;
            }
            field.set(instance, this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
        }
    }

    private Object instantiateBean(String beanName, BeanDefinition beanDefinition) {
        //拿到要实例化的对象的类名
        String className = beanDefinition.getBeanClassName();
        //反射实例化，得到一个对象
        Object instance = null;
        try {
            //假设默认是单例
            if (this.singletonObjects.containsKey(className)) {
                instance = this.singletonObjects.get(className);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                //加载代理的配置
                AdvisedSupport config = instantionAopConfig(beanDefinition);
                config.setTargetClass(clazz);
                config.setTarget(instance);
                //符合PointCut的规则的话，将代理对象
                //其实之前的getBean中的BeanWrapper也是一个代理对象
                if (config.pointCutMatch()) {
                    instance = createProxy(config).getProxy();
                }

                this.singletonObjects.put(className, instance);
                //这里写不写都行，旨在也能根据这种方式注入
                this.singletonObjects.put(beanDefinition.getFactoryBeanName(), instance);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        //把这个对象封装到BeanWrapper
        //把BeanWrapper存到IOC容器里面
        return instance;
    }

    private AopProxy createProxy(AdvisedSupport config) {
        Class<?> targetClass = config.getTargetClass();
        if (targetClass.getInterfaces().length > 0) {
            return new JdkDynamicAopProxy(config);
        }
        return new CglibAopProxy(config);
    }

    //为什么这里返回的不能是map？最少知道原则
    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig() {
        return this.reader.getConfig();
    }

    private AdvisedSupport instantionAopConfig(BeanDefinition gpBeanDefinition) {
        AopConfig config = new AopConfig();
        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new AdvisedSupport(config);
    }
}
