package ru.lakidemon.store.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.lakidemon.store.configuration.UnitpayConfiguration;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class UnitpayIpFilter implements Filter {
    private final UnitpayConfiguration unitpayConfig;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        var httpRequest = (HttpServletRequest) request;
        var httpResponse = (HttpServletResponse) response;
        if (!httpRequest.getServletPath().equals("/unitpay") || unitpayConfig.getAllowedIPs()
                .contains(httpRequest.getRemoteHost())) {
            chain.doFilter(request, response);
        } else {
            log.warn("Got request to payment handler from unknown source {}", httpRequest.getRemoteHost());
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Not allowed IP");
        }
    }
}
