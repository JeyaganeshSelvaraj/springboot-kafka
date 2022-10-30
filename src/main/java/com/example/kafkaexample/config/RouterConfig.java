package com.example.kafkaexample.config;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.example.kafkaexample.controllers.SalesService;
import com.example.kafkaexample.filters.RequestLogHanlder;
import com.example.kafkaexample.filters.ResponseLogHandler;
import com.example.kafkaexample.filters.SecurityHandler;

@Configuration
public class RouterConfig {
    @Autowired
    private SalesService saleService;
    @Autowired
    private SecurityHandler securityHandler;
    @Autowired
    private ResponseLogHandler responseLogHandler;
    @Autowired
    private RequestLogHanlder requestLogHanlder;

    @Bean    public RouterFunction<ServerResponse> routerFunction() {
        
        RouterFunction<ServerResponse> eventRouter = saleEventRoutes();
        return route().path("",
                b1 -> b1
                        .before(requestLogHanlder::handle)
                        .filter(securityHandler::filter)                        
                        .add(eventRouter)
                        .after(responseLogHandler::handle))
                .build();
    }

    private RouterFunction<ServerResponse> saleEventRoutes() {
        return route()
                .path("/sale", b1 -> b1.nest(accept(APPLICATION_JSON),
                        b2 -> 
                        b2.POST(path("").or(path("/")), saleService::createNewSale)))
                .build();
    }
}
