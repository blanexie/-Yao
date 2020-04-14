package xyz.xiezc.ioc;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.Data;
import xyz.xiezc.ioc.common.BeanScanUtil;
import xyz.xiezc.ioc.common.ContextUtil;
import xyz.xiezc.ioc.common.event.Event;
import xyz.xiezc.ioc.common.event.EventListenerUtil;
import xyz.xiezc.ioc.common.event.Listener;
import xyz.xiezc.ioc.definition.BeanDefinition;
import xyz.xiezc.ioc.definition.BeanSignature;
import xyz.xiezc.ioc.enums.BeanTypeEnum;

import java.lang.annotation.Annotation;

/**
 * 超级简单的依赖注入小框架
 * <p>
 * 1. 不支持注解的注解
 * 2. 目前只支持单例的bean
 *
 * @author wb-xzc291800
 * @date 2019/03/29 14:17
 */
@Data
public final class Xioc {

    /**
     * 装载依赖的容器. 当依赖全部注入完成的时候,这个集合会清空
     */
    private final ContextUtil contextUtil = new ContextUtil();

    /**
     * 事件分发处理器
     */
    EventListenerUtil eventListenerUtil = new EventListenerUtil();
    /**
     * 扫描工具
     */
    private final BeanScanUtil beanScanUtil = new BeanScanUtil(contextUtil);

    /**
     * 加载其他starter需要扫描的package路径
     */
    public final String starterPackage = "xyz.xiezc.ioc.starter";

    /**
     * 单例模式
     */
    private static Xioc xioc = new Xioc();

    /**
     *
     */
    private Xioc() {
    }

    /**
     * 单例模式的获取
     *
     * @return
     */
    public static Xioc getSingleton() {
        return xioc;
    }

    /**
     * 启动方法,
     *
     * @param clazz 传入的启动类, 以这个启动类所在目录为根目录开始扫描bean类
     */
    public static Xioc run(Class<?> clazz) {
        //开始启动框架
        xioc.eventListenerUtil.syncCall(new Event("xioc-start"));
        BeanScanUtil beanScanUtil = xioc.getBeanScanUtil();
        //加载配置
        beanScanUtil.loadPropertie();
        xioc.eventListenerUtil.syncCall(new Event("xioc-loadPropertie"));
        //加载注解信息
        beanScanUtil.loadAnnotation();
        xioc.eventListenerUtil.syncCall(new Event("xioc-loadAnnotation"));
        //加载starter中的bean
        beanScanUtil.loadBeanDefinition(xioc.starterPackage);
        xioc.eventListenerUtil.syncCall(new Event("xioc-starter-loadBeanDefinition"));
        //加载bean信息
        String packagePath = ClassUtil.getPackage(clazz);
        beanScanUtil.loadBeanDefinition(packagePath);
        xioc.eventListenerUtil.syncCall(new Event("xioc-loadBeanDefinition"));

        //扫描容器中的bean， 处理所有在bean类上的注解
        beanScanUtil.scanBeanClass();
        xioc.eventListenerUtil.syncCall(new Event("xioc-scanBeanClass"));

        //扫描容器中的bean，处理bean上的字段的自定义注解
        beanScanUtil.scanBeanField();
        xioc.eventListenerUtil.syncCall(new Event("xioc-scanBeanField"));

        //扫描容器中的bean, 处理方法
        beanScanUtil.scanMethod();
        xioc.eventListenerUtil.syncCall(new Event("xioc-scanMethod"));

        //注入依赖和初始化
        beanScanUtil.initAndInjectBeans();
        xioc.eventListenerUtil.syncCall(new Event("xioc-initAndInjectBeans"));

        return xioc;
    }


    public Xioc web(Class<?> clazz) {
        BeanSignature beanSignature = new BeanSignature();
        beanSignature.setBeanClass(clazz);
        beanSignature.setBeanTypeEnum(BeanTypeEnum.bean);
        beanSignature.setBeanName("testc");
        BeanDefinition beanDefinition = xioc.getContextUtil().getComplatedBeanDefinitionBySignature(beanSignature);
        Object bean = beanDefinition.getBean();
        Object server = ReflectUtil.invoke(bean, "createServer");
        ReflectUtil.invoke(server, "start");
        ReflectUtil.invoke(server, "join");
        return xioc;
    }


    /**
     * 自动添加注解处理器， 可以使用这个特性自定义注解
     *
     * @param event
     * @param listener
     */
    public Xioc addEventListener(Event event, Listener listener) {
        xioc.eventListenerUtil.addListener(event, listener);
        return xioc;
    }


    /**
     * 自动添加注解处理器， 可以使用这个特性自定义注解
     *
     * @param annotationHandler
     */
    public Xioc addAnnoHandler(AnnotationHandler<? extends Annotation> annotationHandler) {
        xioc.contextUtil.getAnnoUtil().addAnnotationHandler(annotationHandler);
        return xioc;
    }

}
