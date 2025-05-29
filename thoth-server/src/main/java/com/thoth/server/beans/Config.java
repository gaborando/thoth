package com.thoth.server.beans;

import com.thoth.server.model.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean IAuthenticationFacade authenticationFacade(UserRepository userRepository){
        return new AuthenticationFacade(userRepository);
    }
}
