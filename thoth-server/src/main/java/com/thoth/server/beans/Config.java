package com.thoth.server.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean IAuthenticationFacade authenticationFacade(){
        return new AuthenticationFacade();
    }
}
