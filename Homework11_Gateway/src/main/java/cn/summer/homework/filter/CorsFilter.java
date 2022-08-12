package cn.summer.homework.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author VHBin
 * @date 2022/8/12-14:36
 */
@Component
public class CorsFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST");
        response.addHeader("Access-Control-Allow-Headers",
                "Content-Type,X-CAF-Authorization-Token,sessionToken,X-TOKEN,Authorization,token");
        if (((HttpServletRequest) servletRequest).getMethod().equals("OPTIONS")) {
            PrintWriter writer = response.getWriter();
            writer.printf("OK");
            writer.close();
            return;
        }
        filterChain.doFilter(servletRequest, response);
    }
}
