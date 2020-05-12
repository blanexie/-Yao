package xyz.xiezc.ioc.starter.web.handler;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ClassUtil;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.QueryStringDecoder;
import xyz.xiezc.ioc.annotation.Component;
import xyz.xiezc.ioc.definition.MethodDefinition;
import xyz.xiezc.ioc.definition.ParamDefinition;
import xyz.xiezc.ioc.starter.web.common.ContentType;
import xyz.xiezc.ioc.starter.web.entity.FileItem;
import xyz.xiezc.ioc.starter.web.entity.HttpRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * application/x-www-form-urlencoded 的post 请求的解析
 */
@Component
public class UrlencodedFormHttpMessageConverter implements HttpMessageConverter {

    List<ContentType> contentTypes = new ArrayList<>() {{
        add(ContentType.FORM_URLENCODED);
    }};

    @Override
    public List<ContentType> getSupportContentType() {
        return contentTypes;
    }

    @Override
    public Object[] doRead(MethodDefinition methodDefinition, ContentType contentType, HttpRequest request) {
        ParamDefinition[] paramDefinitions = methodDefinition.getParamDefinitions();
        ByteBuf body = request.getBody();
        CharSequence charSequence = body.readCharSequence(body.readableBytes(), CharsetUtil.CHARSET_UTF_8);
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder("/xweb?"+charSequence.toString());
        Map<String, List<String>> parameters = queryStringDecoder.parameters();
        Object[] objects = this.parseFormData(null, paramDefinitions, parameters);
        return objects;
    }

}
