package com.example.try4.config;

import com.example.try4.entity.AppRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.social.security.SpringSocialConfigurer;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(userDetailsService);
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();

        // Pages do not require login
        http.authorizeRequests().antMatchers("/", "/signup", "/login", "/logout","/found","/lost").permitAll();

        http.authorizeRequests().antMatchers("/userInfo","/newApplication").access("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')");

        // For ADMIN only.
        http.authorizeRequests().antMatchers("/admin").access("hasRole('" + AppRole.ROLE_ADMIN + "')");

        // When the user has logged in as XX.
        // But access a page that requires role YY,
        // AccessDeniedException will be thrown.


        // Form Login config
        http.authorizeRequests().and().formLogin()//
                // Submit URL of login page.
                .loginProcessingUrl("/j_spring_security_check") // Submit URL
                .loginPage("/login")//
                .defaultSuccessUrl("/")//
                .failureUrl("/login?error=true")//
                .usernameParameter("username")//
                .passwordParameter("password");

        // Logout Config
        http.authorizeRequests().and().logout().logoutUrl("/logout").logoutSuccessUrl("/");

        // Spring Social Config.
        http.apply(new SpringSocialConfigurer())
                //
                .signupUrl("/signup");

    }

    // This bean is load the user specific data when form login is used.
    @Override
    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }

}