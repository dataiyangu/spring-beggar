package com.leesin.spring.formework.beans.support;

import com.leesin.spring.formework.beans.config.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @description:
 * @author: Administrator
 * @date: Created in 2020/2/27 22:34
 * @version:
 * @modified By: Leesin Dong
 */
public class BeanDefinitionReader {
    private List<String> registyBeanClasses = new ArrayList<String>();
    private Properties config = new Properties();
    //固定配置文件中的key，相对于xml的规范
    private final String SCAN_PACKAGE = "scanPackage";

    public BeanDefinitionReader(String... locations) {
        //通过URL定位到其所对应的文件，然后转换为文件流
        //以“/”开头是从classPath根下获取，不以“/”开头是从此类所在的包下取资源
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""));
        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void doscanner(String scanPackage) {
        //转换为文件路径，实际上就是把.替换为/就OK了
        URL url = this.getClass().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doscanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String className = (scanPackage + "." + file.getName().replace(".class", ""));
                registyBeanClasses.add(className);
            }
        }
    }

    public Properties getConfig() {
        return this.config;
    }

    //把配置文件中扫描到的所有的配置信息转换为BeanDefinition对象，以便于之后IOC操作方便
    public List<BeanDefinition> loadBeanDefinitions() throws ClassNotFoundException {
        List<BeanDefinition> result = new ArrayList<BeanDefinition>();
        for (String className : registyBeanClasses) {
            Class<?> beanClass = Class.forName(className);
            //如果是一个接口是不能实例化的
            //用它实现类来实例化
            if (beanClass.isInterface()) {
                continue;
            }
            //beanName有三种情况：
            //1 默认是类名首字母小写
            //2 自定义名字
            //3 接口注入
            result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));
            Class<?>[] interfaces = beanClass.getInterfaces();
            for (Class<?> i : interfaces) {
                //如果是多个实现类，只能覆盖
                //为什么？因为Spring没那么智能，就是这么傻
                //这个时候，可以自定义名字
                result.add(doCreateBeanDefinition(i.getName(), beanClass.getName()));
            }
        }
        return result;
    }

    /**
     * @param factoryBeanName
     * @param beanClassName
     * @description: 把每一个配置信息解析成一个BeanDefinition
     * @name: doCreateBeanDefinition
     * @return:
     * @date: 2020/2/28 9:36
     * @auther: Administrator
     **/
    private BeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName) {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }

    /**
     * @description: 如果类名本身是小写字母，确实会出问题，但是说明的是：这个方法是自己用的，private的
     * 传值也是自己传，类也是遵循了驼峰命名法
     * 默认传入的值，存在首字母小写的情况，也不可能出现非字母的情况
     * @name: toLowerFirstCase
     * @param: simpleName
     * @return: java.lang.String
     * @date: 2020/2/28 10:27
     * @auther: Administrator
     **/
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        //之所以加，是因为大小写字母的ASCII码相差32，
        // 而且大写字母的ASCII码要小于小写字母的ASCII码
        //在Java中，对char做算学运算，实际上就是对ASCII码做算学运算
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
