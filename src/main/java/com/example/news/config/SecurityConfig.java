package com.example.news.config;



import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers(
                        "/h2-console/**"
                        ,"/favicon.ico"
                        ,"/error"
                        ,"/swagger-ui.html/**"
                        ,"/swagger-ui/**"

                );
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity

                // token을 사용하는 방식이기 때문에 csrf를 disable합니다.
                .csrf().disable()

                //.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)

                // 인증, 허가 에러 시 공통적으로 처리해주는 부분
                .exceptionHandling()

                // enable h2-console
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                // JWT를 쓰려면 Spring Security에서 기본적으로 지원하는 Session 설정을 해제해야 한다.
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                //컨트롤러 url 추가할 때 마다 이곳에 추가해야함.
                //요청에 따른 권한 체크 설정 부분 /api/** 경로로 들어오는 부분은 인증이 필요없고, 그 외 모든 요청들은 인증을 거치도록 설정
//                .and()
//
//                .authorizeRequests()
//
//                .antMatchers("/test").permitAll()
//                .antMatchers("/api/user/**").permitAll()
//                .antMatchers("/api/inquiry/**").permitAll()
//                .antMatchers("/api/order/**").permitAll()
//                .antMatchers("/api/item/**").permitAll()
//                .antMatchers("/api/test/**").permitAll()
//                .antMatchers("/api/admin/**").permitAll()


                .and();
                //.apply(new JwtSecurityConfig(tokenProvider));
    }
}