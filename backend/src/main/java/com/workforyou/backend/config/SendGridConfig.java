package com.workforyou.backend.config;

import com.sendgrid.SendGrid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SendGridConfig {

    @Bean
    public SendGrid sendGrid(@Value("${SENHA_SENDGRID}") String apiKey){
        return new SendGrid(apiKey);
    }
}
