package com.leesin.spring.formework.webmvc.servlet;

import com.leesin.spring.formework.annotation.Controller;
import com.leesin.spring.formework.annotation.RequestMapping;
import com.leesin.spring.formework.context.ApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description:
 * @author: Leesin Dong
 * @date: Created in 2020/3/1 16:03
 * @version:
 * @modified By:
 */
@Slf4j
public class DispatcherServlet extends HttpServlet {

    private final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    private ApplicationContext applicationContext;

    //url和method的一个对应关系
    private List<HandlerMapping> handlerMappings = new ArrayList<HandlerMapping>();
    //HandlerAdapter，一个handler对应一个handlerAdapter，去分析request参数的值，将request和handler的参数进行一一对应
    private Map<HandlerMapping, HandlerAdapter> handlerAdapters = new HashMap<HandlerMapping, HandlerAdapter>();
    //把文件变成一个可以输出的html字符串
    private List<ViewResolver> ViewResolvers = new ArrayList<ViewResolver>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.doDispatcher(req, resp);
        } catch (Exception e) {
            //如果匹配过程出现异常，将异常信息打印出来
            try {
                processDispatchResult(req, resp, new ModelAndView("500"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private void doDispatcher(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //1 通过从request中拿到url，去匹配一个HandlerMapping
        //虽然这里写的是handler，但是这里的handler其实就是HandlerMapping，handler在spring中是和method对应的，
        //这里为了简便，直接用的HandlerMapping，没有用handler
        HandlerMapping handler = getHandler(req);
        if (handler == null) {
            processDispatchResult(req, resp, new ModelAndView("404"));
            return;
        }
        //2准备调用前的参数
        HandlerAdapter ha = getHandlerAdapter(handler);
        //3真正的调用方法，返回ModelAndView，存储了要传回页面上的值，和页面模板的名称
        ModelAndView mv = ha.handle(req, resp, handler);
        //这一步才是真正的输出
        processDispatchResult(req, resp, mv);

    }

    private HandlerAdapter getHandlerAdapter(HandlerMapping handler) {
        if (this.handlerAdapters.isEmpty()) {
            return null;
        }
        HandlerAdapter handlerAdapter = this.handlerAdapters.get(handler);
        if (handlerAdapter.supports(handler)) {
            return handlerAdapter;
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //1 初始化ApplicationContext
        try {
            applicationContext = new ApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //2 初始化Spring MVC
        initStrategies(applicationContext);
    }

    private HandlerMapping getHandler(HttpServletRequest req) throws Exception {
        if (this.handlerMappings.isEmpty()) {
            return null;
        }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");

        for (HandlerMapping handler : this.handlerMappings) {
            try {
                Matcher matcher = handler.getPattern().matcher(url);
                //如果没有匹配上继续下一个匹配
                if (!matcher.matches()) {
                    continue;
                }

                return handler;
            } catch (Exception e) {
                throw e;
            }
        }
        return null;
    }

    //初始化策略
    protected void initStrategies(ApplicationContext context) {
        //也不是全部要实现，因为是简化版的，但是核心的还是要是极限的。

        //多文件上传的组件
        initMultipartResolver(context);
        //初始化本地语言环境
        initLocaleResolver(context);
        //初始化模板处理器
        initThemeResolver(context);
        //handlerMapping，必须实现
        initHandlerMappings(context);
        //初始化参数适配器，必须实现
        initHandlerAdapters(context);
        //初始化异常拦截器
        initHandlerExceptionResolvers(context);
        //初始化视图预处理器
        initRequestToViewNameTranslator(context);
        //初始化视图转换器，必须实现
        initViewResolvers(context);
        //参数缓存器
        initFlashMapManager(context);
    }

    private void initFlashMapManager(ApplicationContext context) {

    }

    private void initViewResolvers(ApplicationContext context) {
        //拿到模板的存放目录
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);
        for (File file : templateRootDir.listFiles()) {
            this.ViewResolvers.add(new ViewResolver(templateRoot));
        }
    }

    private void initRequestToViewNameTranslator(ApplicationContext context) {
    }

    private void initHandlerExceptionResolvers(ApplicationContext context) {
    }

    //把一个request请求变成一个handler，参数都是字符串的，自动匹配到handler中的形参
    //可想而知，要拿到HanderMapping才能干活，意味着，有几个HandlerMapping就有几个HandlerAdapter
    //就意味着，有几个HandlerMapping就有几个HandlerAdapter
    private void initHandlerAdapters(ApplicationContext context) {
        for (HandlerMapping handlerMapping : this.handlerMappings) {
            this.handlerAdapters.put(handlerMapping, new HandlerAdapter());
        }
    }

    private void initHandlerMappings(ApplicationContext context) {
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            try {
                Object controller = context.getBean(beanDefinitionName);
                Class<?> clazz = controller.getClass();
                if (!clazz.isAnnotationPresent(Controller.class)) {
                    continue;
                }
                String baseUrl = "";
                //获取Controller的url配置
                if (clazz.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                    baseUrl = requestMapping.value();
                }
                //获取Method的url配置
                for (Method method : clazz.getMethods()) {
                    if (method.isAnnotationPresent(RequestMapping.class)) {
                        String methodUrl = method.getAnnotation(RequestMapping.class).value();
                        String regex = ("/" + baseUrl + "/" + methodUrl.replaceAll("\\*", ".*")).replaceAll("/+", "/");
                        Pattern pattern = Pattern.compile(regex);
                        this.handlerMappings.add(new HandlerMapping(pattern, controller, method));
                        log.info("Mapped" + regex + "," + method);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initThemeResolver(ApplicationContext context) {
    }

    private void initLocaleResolver(ApplicationContext context) {
    }

    private void initMultipartResolver(ApplicationContext context) {
    }

    private void processDispatchResult(HttpServletRequest request, HttpServletResponse response, ModelAndView modelAndView) throws Exception {
        //把ModelAndView变成一个HTML、OutputStram、json、freemark、veolcity
        //ContextType
        if (null == modelAndView) {
            return;
        }
        //如果ModelAndView不为null，怎么办？
        if (this.ViewResolvers.isEmpty()) {
            return;
        }
        for (ViewResolver ViewResolver : this.ViewResolvers) {
            View view = ViewResolver.resolveVieName(modelAndView.getViewName(), null);
            view.render(modelAndView.getModel(), request, response);
        }
    }
}
