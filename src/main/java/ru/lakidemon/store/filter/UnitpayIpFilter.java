package ru.lakidemon.store.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UnitpayIpFilter implements Filter {
    @Qualifier("allowedIPs")
    private final List<String> allowedIPs;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        var httpRequest = (HttpServletRequest) request;
        var httpResponse = (HttpServletResponse) response;
        if (!httpRequest.getServletPath().equals("/unitpay") || allowedIPs.contains(httpRequest.getRemoteHost())) {
            chain.doFilter(request, response);
        } else {
            log.warn("Got request to payment handler from unknown source {}", httpRequest.getRemoteHost());
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Not allowed IP");
        }
    }
}
