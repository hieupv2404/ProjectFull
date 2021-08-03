package com.nuce.group3.interceptor;

import com.nuce.group3.exception.LogicException;
import com.nuce.group3.utils.TokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class AuthenticationAndLoggingInterceptor extends HandlerInterceptorAdapter {
    private static Logger log = LoggerFactory.getLogger(AuthenticationAndLoggingInterceptor.class);
    private List<String> allowURLs = new ArrayList<>();

    public AuthenticationAndLoggingInterceptor() {
        allowURLs.add("doLogin");
        allowURLs.add("login");
        allowURLs.add("/configuration/ui");
        allowURLs.add("/swagger-resources/**");
        allowURLs.add("/swagger-ui.html");
        allowURLs.add("/swagger-ui.html");
        allowURLs.add("/webjars/**");
        allowURLs.add("/v2/api-docs");
        allowURLs.add("/api/users/forget-password");
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        log.info("[preHandle][" + request + "]" + "[" + request.getMethod()
                + "]" + request.getRequestURI() + getParameters(request));

        // TODO: Thấy mọi request đều gọi 2 lần ?
        // Kiểm tra request thuộc loại gì - OPTIONS trước rồi đến GET sau
        if (request.getMethod().equals(HttpMethod.OPTIONS)) {
            log.info("Allow OPTIONS request");
            return true;
        }
        // TODO: Viết response trả về 1 friendly message như là
        // "Bạn chưa đăng nhập"
        if (allowURLs.contains(request.getRequestURI()))
            return true;

        // Chỉ xử lý mức độ method
        HandlerMethod hm;
        try {
            hm = (HandlerMethod) handler;
        } catch (ClassCastException e) {
            return super.preHandle(request, response, handler);
        }
        Method method = hm.getMethod();

        if (method.isAnnotationPresent(HasRole.class)) {
            if (TokenManager.getInstance().isAuthenticated(request))
                if (TokenManager.getInstance().hasRole(request, method.getAnnotation(HasRole.class))) {
                    return true;
                } else
                    throw new LogicException("You don't have authority to do this action", HttpStatus.FORBIDDEN);
            else
                throw new LogicException("You haven't yet login", HttpStatus.UNAUTHORIZED);
        } else {
            return true; // Not check
        }
    }

    private String getParameters(HttpServletRequest request) {
        StringBuffer posted = new StringBuffer();
        Enumeration<?> e = request.getParameterNames();
        if (e != null) {
            posted.append("?");
        }
        while (e.hasMoreElements()) {
            if (posted.length() > 1) {
                posted.append("&");
            }
            String curr = (String) e.nextElement();
            posted.append(curr + "=");
            if (curr.contains("password")
                    || curr.contains("pass")
                    || curr.contains("pwd")) {
                posted.append("*****");
            } else {
                posted.append(request.getParameter(curr));
            }
        }
        String ip = request.getHeader("X-FORWARDED-FOR");
        String ipAddr = (ip == null) ? getRemoteAddr(request) : ip;
        if (ipAddr != null && !ipAddr.equals("")) {
            posted.append("&_psip=" + ipAddr);
        }
        return posted.toString();
    }

    private String getRemoteAddr(HttpServletRequest request) {
        String ipFromHeader = request.getHeader("X-FORWARDED-FOR");
        if (ipFromHeader != null && ipFromHeader.length() > 0) {
            log.debug("ip from proxy - X-FORWARDED-FOR : " + ipFromHeader);
            return ipFromHeader;
        }
        return request.getRemoteAddr();
    }
}
