package xyz.xiezc.ioc.starter.annotationHandler;

import cn.hutool.core.util.StrUtil;
import xyz.xiezc.ioc.AnnotationHandler;
import xyz.xiezc.ioc.annotation.Inject;
import xyz.xiezc.ioc.common.ContextUtil;
import xyz.xiezc.ioc.definition.BeanDefinition;
import xyz.xiezc.ioc.definition.FieldDefinition;
import xyz.xiezc.ioc.definition.MethodDefinition;

import java.lang.annotation.Annotation;

public class InjectAnnotationHandler extends AnnotationHandler<Inject> {

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    @Override
    public Class<Inject> getAnnotationType() {
        return Inject.class;
    }

    @Override
    public void processClass(Inject annotation, Class clazz, ContextUtil contextUtil) {

    }

    @Override
    public void processMethod(MethodDefinition methodDefinition, Inject annotation, BeanDefinition beanDefinition, ContextUtil contextUtil) {

    }

    /**
     * 这个方法就是设置fieldDefinition中的beanName字段值。
     *
     * @param fieldDefinition 被注解的字段
     * @param annotation      这个类上的所有注解
     * @param beanDefinition  被注解的类
     * @param contextUtil     容器中已有的所有bean信息
     */
    @Override
    public void processField(FieldDefinition fieldDefinition, Inject annotation, BeanDefinition beanDefinition, ContextUtil contextUtil) {
        String beanName = annotation.value();
        if (StrUtil.isBlank(beanName)) {
            beanName = fieldDefinition.getFieldName();
        }
        fieldDefinition.setBeanName(beanName);
    }
}
