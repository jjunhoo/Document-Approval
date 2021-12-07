package com.croquis.documentapproval.config;

import com.croquis.documentapproval.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfiguraion extends WebSecurityConfigurerAdapter {

    private final LoginService loginService;

    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
        webSecurity.ignoring().antMatchers("/h2-console/**", "/css/**", "/js/**");
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests() // 권한 요청 처리 설정
                    // 페이지 권한 설정
                    .antMatchers("/user/login", "/h2-console/**").permitAll() // h2-console 접속 허용
                    .antMatchers("/documents").hasRole("USER")
                    .anyRequest().authenticated()
                .and() // 로그인 설정
                    .formLogin()
                    .loginPage("/user/login")
                    .defaultSuccessUrl("/documents/proceedingDocumentList/proceeding")
                    .usernameParameter("userId")
                    .permitAll()
                .and()
                    .logout()
                    .logoutSuccessUrl("/user/logout")
                    .invalidateHttpSession(true);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(loginService).passwordEncoder(new BCryptPasswordEncoder());
    }
}
