package xyz.xiezc.ioc.starter.web.entity;

import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.util.concurrent.FastThreadLocal;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class WebContext {

    static FastThreadLocal<WebContext> fastThreadLocal = new FastThreadLocal<>();

    public static TimedCache<String, Map<String, Object>> sessionCache = new TimedCache<>(30 * 60 * 1000);

    public static WebContext get() {
        WebContext webContext = fastThreadLocal.get();
        if (webContext == null) {
            webContext = new WebContext();
            fastThreadLocal.set(webContext);
        }
        return webContext;
    }

    public static WebContext build(FullHttpRequest httpRequest) {
        WebContext webContext = fastThreadLocal.get();
        if (webContext == null) {
            webContext = new WebContext();
            fastThreadLocal.set(webContext);
        }
        webContext.setHttpRequest(httpRequest);
        return webContext;
    }


    public Set<Cookie> getCookies() {
        HttpHeaders headers = httpRequest.headers();
        String cookieStr = headers.get("Cookie");
        if (StrUtil.isEmpty(cookieStr)) {
            cookieStr = headers.get("cookie");
        }
        Set<Cookie> cookies = ServerCookieDecoder.LAX.decode(cookieStr);

        return cookies;
    }

    public Cookie getCookie(String name) {
        HttpHeaders headers = httpRequest.headers();
        String cookieStr = headers.get("Cookie");
        if (StrUtil.isEmpty(cookieStr)) {
            cookieStr = headers.get("cookie");
        }
        Set<Cookie> cookies = ServerCookieDecoder.LAX.decode(cookieStr);
        for (Cookie cookie : cookies) {
            if (Objects.equals(cookie.name(), name)) {
                return cookie;
            }
        }
        return null;
    }

    public Map<String, Object> getSession() {
        Cookie cookie = getCookie("session_id");
        if (cookie == null) {
            HttpHeaders headers = httpRequest.headers();
            String host = headers.get("host");
            if (StrUtil.isEmpty(host)) {
                host = headers.get("Host");
            }
            cookie = new DefaultCookie("session_id", IdUtil.fastSimpleUUID());
            cookie.setDomain(host);
            cookie.setHttpOnly(true);
            sessionCookie.add(cookie);
        }
        Map<String, Object> stringObjectMap = sessionCache.get(cookie.domain() + cookie.value());
        if (stringObjectMap == null) {
            stringObjectMap = new HashMap<>();
            sessionCache.put(cookie.domain() + cookie.value(), stringObjectMap);
        }
        return stringObjectMap;
    }

    @Setter
    FullHttpRequest httpRequest;

    Set<Cookie> sessionCookie;


}
