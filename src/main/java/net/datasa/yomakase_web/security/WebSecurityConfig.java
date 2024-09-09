package net.datasa.yomakase_web.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// @Configuration : 클래스를 설정 클래스(configuration class)로 지정하는 데 사용됩니다.
// 설정 클래스는 스프링 IoC 컨테이너가 애플리케이션 컨텍스트를 구성하는 데 필요한 빈(bean) 정의를 포함하는 클래스입니다.
@Configuration

// @EnableWebSecurity : Spring Security를 활성화하기 위한 어노테이션입니다.
// 이 어노테이션은 스프링 애플리케이션에서 웹 보안 기능을 설정하고, 커스터마이징 할 수 있게 합니다.
// 주로 클래스 레벨에 적용되며, 보안 설정을 구성하기 위해 사용하는 클래스에 붙입니다.
@EnableWebSecurity
public class WebSecurityConfig {
    //로그인 없이 접근 가능 경로
    private static final String[] PUBLIC_URLS = {
            "/"                 // 메인화면
            , "/signupForm"       // 로그인없이 접근할 수 있는 페이지들 : 회원가입폼
            , "/loginForm"      // 로그인폼
            , "/css/**"               // css
            , "/img/**"
            , "/jq/**"
            , "/js/**"
            , "/idCheck"      // 로그인폼
    };

    // @Bean : 메서드 레벨에서 사용되며, 해당 메서드의 리턴 값을 스프링 IoC 컨테이너가 관리하는 빈으로 등록하는 역할
    // 이는 주로 설정 클래스(@Configuration으로 어노테이션된 클래스) 내에서 사용되며,
    // Java Config 스타일로 스프링 빈을 정의할 때 유용합니다.
    @Bean
    protected SecurityFilterChain config(HttpSecurity http) throws Exception {
        http
                //요청에 대한 권한 설정
                .authorizeHttpRequests(author -> author
                        .requestMatchers(PUBLIC_URLS).permitAll()   //모두 접근 허용
                        // 원래는 괄호안에 배열이 들어가야 함 // PUBLIC_URLS : 배열 이름
                        .anyRequest().authenticated()               //그 외의 모든 요청은 인증 필요
                )
                //HTTP Basic 인증을 사용하도록 설정
                .httpBasic(Customizer.withDefaults())
                //폼 로그인 설정
                .formLogin(formLogin -> formLogin
                        .loginPage("/loginForm")       // 로그인폼 페이지 경로
                        // 로그인폼을 만들 때, name을 아래와 맞춰야 함.
                        .usernameParameter("id")       // 폼의 ID 파라미터 이름
                        .passwordParameter("password") // 폼의 비밀번호 파라미터 이름
                        .loginProcessingUrl("/loginForm")  // 로그인폼 제출하여 처리할 경로
                        .defaultSuccessUrl("/", true)        // 로그인 성공 시 이동할 경로
                        .permitAll()                   // 로그인 페이지는 모두 접근 허용
                )
                //로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/logout")          // 로그아웃 처리 경로
                        .logoutSuccessUrl("/")         // 로그아웃 성공 시 이동할 경로
                );

        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    //비밀번호 암호화를 위한 인코더를 빈(메모리)으로 등록
    @Bean // BCryptPasswordEncoder 우리가 만든 클래스(우리가 손을 못댐)가 아님
    public BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}