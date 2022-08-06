package cn.summer.homework.Interceptor;

import cn.summer.homework.Util.TokenUtil;
import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author VHBin
 * @date 2022/8/5-17:11
 */

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);
    @Value("interceptor.ignore-uri")
    private String ignore;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        String uri = request.getRequestURI();
        logger.info("当前进入拦截器的URI:{}", uri);
        for (String ig : ignore.split(",")) {
            if (ig.trim().equals(uri)) {
                return true;
            }
        }
        try {
            String token = request.getHeader("token");
            if (token == null) {
                token = request.getParameter("token");
                if (token == null) {
                    logger.error("Token 不存在");
                    throw new Exception("无 Token 请重新登录");
                }
            }
            Map<String, String> map = TokenUtil.checkJWToken(token);
            if (map == null) {
                logger.error("Token 有误");
                throw new Exception("Token 被篡改");
            }
            request.setAttribute("userid", map.get("userid"));
            request.setAttribute("account", map.get("account"));
            request.setAttribute("roles", map.get("roles"));
            return true;
        } catch (Exception ex) {
            logger.error("Exception!", ex);
            response.setContentType("application/json;charset=utf-8");
            PrintWriter writer = response.getWriter();
            JSONObject obj = new JSONObject();
            obj.put("Exception", ex.getMessage());
            obj.put("Suggestion", "重新登录");
            writer.print(obj.toJSONString());
            writer.close();
            response.flushBuffer();
            return false;
        }
    }
}
