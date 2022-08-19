//package cn.summer.homework.filter;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.Ordered;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//
///**
// * @author VHBin
// * @date 2022/8/12-14:36
// */
//@Configuration
//public class CorsFilter implements GlobalFilter, Ordered {
//    private static final Logger logger = LoggerFactory.getLogger(CorsFilter.class);
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        logger.info("Exchange: {}", exchange);
//        HttpServletRequest request = (HttpServletRequest) exchange.getRequest();
//        HttpServletResponse response = (HttpServletResponse) exchange.getResponse();
//        response.addHeader("Access-Control-Allow-Credentials", "true");
//        response.addHeader("Access-Control-Allow-Origin", "*");
//        response.addHeader("Access-Control-Allow-Methods", "GET, POST");
//        response.addHeader("Access-Control-Allow-Headers", "Content-Type, token");
//        response.addHeader("Access-Control-Max-Age", "18000");
//        if (request.getMethod().equals("OPTIONS")) {
//            PrintWriter writer;
//            try {
//                writer = response.getWriter();
//            } catch (IOException e) {
//                logger.error("Response 获得 PrintWriter 失败: {}", e.getMessage(), e);
//                throw new RuntimeException(e);
//            }
//            writer.printf("OK");
//            writer.close();
//            return chain.filter(exchange);
//        }
//        Mono<Void> filter = chain.filter(exchange);
//        logger.info("Filter: {}", filter);
//        return filter;
//    }
//
//    @Override
//    public int getOrder() {
//        return -1;
//    }
////    @Override
////    public void doFilter(ServletRequest servletRequest,
////                         ServletResponse servletResponse,
////                         FilterChain filterChain)
////            throws IOException, ServletException {
////        HttpServletResponse response = (HttpServletResponse) servletResponse;
////        response.addHeader("Access-Control-Allow-Credentials", "true");
////        response.addHeader("Access-Control-Allow-Origin", "*");
////        response.addHeader("Access-Control-Allow-Methods", "GET, POST");
////        response.addHeader("Access-Control-Allow-Headers",
////                "Content-Type, token");
////        if (((HttpServletRequest) servletRequest).getMethod().equals("OPTIONS")) {
////            PrintWriter writer = response.getWriter();
////            writer.printf("OK");
////            writer.close();
////            return;
////        }
////        filterChain.doFilter(servletRequest, response);
////    }
//}
