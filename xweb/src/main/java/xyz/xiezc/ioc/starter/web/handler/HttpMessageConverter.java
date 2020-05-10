/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.xiezc.ioc.starter.web.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ClassUtil;
import xyz.xiezc.ioc.definition.MethodDefinition;
import xyz.xiezc.ioc.definition.ParamDefinition;
import xyz.xiezc.ioc.starter.web.common.ContentType;
import xyz.xiezc.ioc.starter.web.entity.HttpRequest;

import java.util.Collection;
import java.util.List;

/**
 * Strategy interface that specifies a converter that can convert from and to HTTP requests and responses.
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.0
 */
public interface HttpMessageConverter {


    List<ContentType> getSupportContentType();

    /**
     *
     */
    Object[] doRead(MethodDefinition methodDefinition, ContentType contentType, HttpRequest request);

    default Object paramMapping(List<String> params, ParamDefinition paramDefinition) {
        Class paramType = paramDefinition.getParamType();
        //如果是基本类型， 则把list中的第一个值转换类型，赋值进去
        if (ClassUtil.isSimpleValueType(paramType)) {
            return Convert.convert(paramType, params.get(0));
        }
        //如果是数组类型，
        if (paramType.isArray()) {
            return params.toArray();
        }
        //如果是集合类型
        if (ClassUtil.isAssignable(Collection.class, paramType)) {
            return CollUtil.toCollection(params);
        }
        //如果是其他类型
        if (params.size() == 1) {
            return Convert.convert(paramType, params.get(0));
        }
        return Convert.convert(paramType, params);
    }
}
