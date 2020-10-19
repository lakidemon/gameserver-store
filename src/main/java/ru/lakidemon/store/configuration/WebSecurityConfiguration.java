package ru.lakidemon.store.configuration;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.Properties;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2A, new SecureRandom());
    }

    @SneakyThrows
    @Bean
    protected UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        var manager = new InMemoryUserDetailsManager();
        var admins = readAdmins();
        admins.keySet()
                .stream()
                .map(Object::toString)
                .map(name -> User.withUsername(name)
                        .roles("ADMIN")
                        .passwordEncoder(passwordEncoder::encode)
                        .password(admins.getProperty(name))
                        .build())
                .forEach(manager::createUser);
        return manager;
    }

    private Properties readAdmins() throws IOException {
        var file = new File("admins.properties");
        if (!file.exists()) {
            Files.writeString(file.toPath(), "admin=admin", StandardOpenOption.CREATE_NEW);
        }
        Properties properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream(file)) {
            properties.load(inputStream);
        }
        return properties;
    }

    // @formatter:off
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().
                    authorizeRequests()
                        .antMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().permitAll();
        http.httpBasic();
    }
    // @formatter:on

    @Autowired
    void configureAuthentication(AuthenticationManagerBuilder auth, UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }
}
