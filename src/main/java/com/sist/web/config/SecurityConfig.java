package com.sist.web.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.token.KeyBasedPersistenceTokenService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.sist.web.security.LoginFailHandler;
import com.sist.web.security.LoginSuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration // xml => 자바 (설정을 쉽게) => 보안  
@EnableWebSecurity // Security => 인터셉트 
@RequiredArgsConstructor // lombok => 생성자를 통해 @Autowired (자동 주입)
// => 객체 => final
/*
 *   1. Spring Security 
 *      = 보안을 담당하는 프레임워크 
 *        ---
 *        | 인증 / 인가 
 *          ---   ---
 *                Authorization : 사용자가 누구인지 확인 절차 = 로그인 
 *          Authentication :  인증된 사용자가 사이트에 접근 가능한지 권한 확인 
 *        | 저장 (인증 = 권한 = 저장 = Session)
 *                          -----
 *                          서버가 종료 => 메모리 해제 
 *                          | => Cookie 기반 : JWT 
 *          1) 인증 => 회원에 가입된 자 / 게스트 / 관리자 
 *             |= DispatcherServlet => HandlerMapping
 *              = 인터셉트 
 *              |
 *              사용자 요청 ====== DispatcherServlet => HandlerMapping
 *                        | preHandle()                  | => postHandle()
 *                                                    ViewResolver
 *                                                         | => 
 *                                                       JSP
 *          2) Authentication Filter 
 *               | 책임 전가 
 *          3) Authentication Manager
 *               | 인증 방법 
 *          4) Authentication Provider
 *             DataBase연동 => 5) UserDetailService : 결과값 return
 *                |
 *                4-1) User비교 / 암호 
 *                     | PasswordEncoding 
 *         
 *         /login => permitAll
 *         /admin => hasRole("ROLE_ADMIN")
 *         /user  => hasRole("ROLE_USER")
 *         /board => permitAll
 *         /member => permitAll
 */
/*
 *    1. Authentication (인증) => 로그인 / 사용자 확인 
 *    2. Authorization (인가) => 권한 확인 
 *    
 *    동작 (실행 순서)
 *    사용자 
 *      /member/login 
 *          |
 *      /member/login_process
 *      ----------------------
 *          |
 *       Security FilterChain
 *          |
 *          UsernamePasswordAuthenticationFilter
 *             => .usernameParameter("userid")
 *                                   --------- <input type=text name="">
 *                 => String username=request.getParameter("userid");
	              .passwordParameter("userpwd")
	               => 암호화된 비밀번호 / 비밀번호 비교
	                     |              |
	                     ---------------- > match encoder() 
	                                          |      |
	                                          --------
	         |
	      AuthenticationManager
	         |
	      AuthenticationProvider 
	         |
	      JdbcUserDetailsManager
	         | DB 조회
	           springmember / authority
	         |
	      UserDetails 생성 
	         |
	      BCryptPasswordEncoder
	         | 비밀번호 비교 
	      인증 성공 
	         |
	    -------------------
	    |                 |
	   성공               실패
	                     LoginFailHandler 
	   LoginSuccessHandler
	          |
	       SecurityContext
	          |
	       Session에 저장 
	          |
	      사용자 인증 완료
	   /member/login_process 
	   ---------------------- Controller 처리가 
	             아니라 SecurityContext 인터셉트를 해서 처리
 */
public class SecurityConfig {
   private final LoginSuccessHandler loginSuccessHandler;
   private final LoginFailHandler  loginFailHandler;
   private final DataSource dataSource;
   
   // 접근 권한 => SecurityFilterChain
   /*
    *   HTTP요청이 있는 경우 Spring Security가 어떻게 처리 할지 지시 (설정)
    */
   @Bean
   public SecurityFilterChain filterChain(HttpSecurity http)
   throws Exception
   {
	   // 공격 방어 
	   /*
	    *     CSRF 
	    *       Cross site Request forgery 
	    *       공격자가 인증된 브라우저에서 
	    *       저장된 쿠키나 세션정보를 활용해서 
	    *       => 다른 사이트 요청값 전달 : 위조 
	    *       => 일반 보안 =: csrf.disable()
	    */
	   http
	    .csrf(csrf-> csrf.disable())
	    // 접근 권한 설정 (URL)
	    .authorizeHttpRequests(auth-> auth
	          .requestMatchers("/","/member/**").permitAll()
	          // 로그인 없이 접근 가능 
	          .requestMatchers("/admin/**").hasRole("ADMIN")
	          // ADMIN권한이 있는 사람만 접근이 가능 
	          .anyRequest().permitAll()
	          //.anyRequest().authenticated()
	          // 지정이 안된 URL 주소 
	          // /comment/** => authenticated()
	    )
	    // 로그인 설정 => login_ok
	   
	    .formLogin(form -> form 
	          .loginPage("/member/login")
	          //로그인 화면창 설정 => 설정이 없는 경우 default login
	          .loginProcessingUrl("/member/login_process")
	          // 가장 중요 부분 
	          // 로그인 처리를 담당하는 URL => 가장 => Security에서 인터셉트가 가능
	          // Controller에서 처리하는 것이 아니다 / SecurityFilter로 처리
	          /*
	           *   Security 처리 
	           *   개발자 처리 => Controller / RestController
	           */
	          .usernameParameter("userid")
	          .passwordParameter("userpwd")
	          // => 로그인 처리를 위해서 id,pwd를 전송
	          // => id = username , pwd => password
	          /*
	           *   UsernamePasswordAuthenticationFilter
	           *   => String username=request.getParameter("userid");
	           *   => String password=request.getParameter("userpwd");
	           *   => 인증 객체로 전송 
	           *      Authentication
	           *        |- username=hong
	           *        |- password=1234
	           *   => AuthenticationManager
	           *      => authenticationManager() 메소드 호출
	           *          |
	           *         .userDetailsService(jdbcUserDetailsService())
	           *                             | 데이터베이스 검색 
	           *                             | SQL
	           *             | - 사용자 정보 => UserDetailsService
	                     .passwordEncoder(passwordEncoder());
	                         | - 비밀번호 비교 => PasswordEncoder
	                  => UserDetails에 정보 저장 
	                     --------------------- Session형식 => Principal
	                     
	                  인증 성공 
	                    |
	                  userid 존재 
	                    |
	                  UserDetails생성 
	                    |
	                  권한 확인 
	                    |
	                  비밀번호 확인 
	                     |
	                   인증 성공 / 인증 실패
	                   
	                 ==> 
	                   SecuirtyContext
	                       |
	                        Authentication 
	                          | - Principal (UserDetails)
	                          | - authorities
	                          | - authenticated = true 
	                
	           */
	          .defaultSuccessUrl("/",false)
	          .successHandler(loginSuccessHandler)
	          .failureHandler(loginFailHandler)
	          .permitAll() 
	    )
	    // 자동 로그인 
	    .rememberMe(remember-> remember
	         .key("my-secret-key")
	         .rememberMeParameter("remember-me")
	         .tokenValiditySeconds(60*60*24)
	         // 저장 기간 : 1일 
	         .tokenRepository(persistentTokenRepository())
	         // persistent_logins 테이블에 저장 
	         /*
	          *   로그인 
	          *     |
	          *   remember-me 체크 => true 
	          *     |
	          *   토큰 생성 => 구분자 
	          *     |
	          *   JdbcTokenRepositoryImpl 
	          *     |
	          *    DB 저장 
	          *    ---- persistent_logins
	          *         username / series / token / last_used
	          */
	    )
	    /*
	     *    /member/logout 
	     *        |
	     *    Spring Security LogoutFilter
	     *        |
	     *    SecurityContext 제거
	     *        |
	     *      Session 제거 
	     *        |
	     *      Cookie 삭제
	     */
	    .logout(logout -> logout 
	          .logoutUrl("/member/logout")
	          .logoutSuccessUrl("/")
	          .invalidateHttpSession(true)
	          .deleteCookies("remember-me","JSESSIONID")
	    );
	    // remember-me
	    return http.build();
	    
   }
   /*
    *   권한 
    *     => permitAll
    *     => hasRole('ROLE_ADMIN')
    *     
    *   login 
    *   logout 
    *   remember-me 
    *   
    */
   // 인증 관리자 
   /*@Bean
   public AuthenticationManager authenticationManager(
       HttpSecurity http,
       BCryptPasswordEncoder passwordEncoder
   ) throws Exception
   {
	   return null;
   }
   @Bean
   public JdbcUserDetailsManager jdbcUserDetailsSevice()
   {
	   return null;
   }
   // 비밀번호 암호화 
   @Bean
   public BCryptPasswordEncoder passwordEncoder() {
	   return new BCryptPasswordEncoder();
   }
   // PersistentLogins 등록
   @Bean
   public PersistentTokenRepository persistentTokenRepository() {
	   return null;
   }*/
   @Bean
   public AuthenticationManager authenticationManager(
      HttpSecurity http,
      BCryptPasswordEncoder passwordEncoder
   ) throws Exception
   {
	   AuthenticationManagerBuilder builder=
			   http.getSharedObject(AuthenticationManagerBuilder.class);
	   builder
	     .userDetailsService(jdbcUserDetailsService())
	     .passwordEncoder(passwordEncoder());
	   return builder.build();
   }
   @Bean
   public JdbcUserDetailsManager jdbcUserDetailsService() {
	   JdbcUserDetailsManager manager=
			   new JdbcUserDetailsManager(dataSource);
	   manager.setUsersByUsernameQuery(
			   "SELECT userid as username,userpwd as password,enable "
			   +"FROM springmember WHERE userid=?"
	   );
	   manager.setAuthoritiesByUsernameQuery(
			   "SELECT userid as username , authority "
			  +"FROM authority WHERE userid=?"
	   );
	   return manager;
   }
   @Bean
   public BCryptPasswordEncoder passwordEncoder() {
	   return new BCryptPasswordEncoder();
   }
   @Bean
   public PersistentTokenRepository persistentTokenRepository() {
       JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
       repo.setDataSource(dataSource);
       return repo;
   }
   
}
/*
 *     [사용자]
 *       | POST => /member/login_procss
 *     ------------------------------
 *       Spring Security FilterChain 
 *     ------------------------------
 *              |
 *     UsernamePasswordAuthenticationFilter
 *              |  username
 *              |  password
 *                 .usernameParameter("userid")
	               .passwordParameter("userpwd")
	            |
	     -------------------------
	       AuthenticationManager
	     -------------------------
	            |
	       AuthenticationProvider
	            |
	       JdbcUserDetailsManager  
	            | DB 검색 
	      ------------------------
	      |                      |
	    springmember           authority => username이 존재
	    (기본 사용자 정보)         (권한 정보)
	         |                    |
	         ----------------------
	                  |
 *                 UserDetails
 *                    |
 *               BCrytPasswordEncoder
 *                    |
 *                 비밀번호 검증 
 *                    |
 *        -------------------------------
 *        |                             |
 *       성공                           실패
 *        |                             |
 *      LoginSeccessHandler         LoginFailHandler
 *        |
 *       SecurityContext 
 *        |
 *       Session에 저장 
 *        |
 *       인증 완료
 *       
 *       ------------------------------------
 *       라이브러리 역할 
 *       @EnableWebSecurity => Spring Security 활성화 
 *                             => 사용하기 위해 
 *       1. SecurityConfig : 보안 전체 설정을 담당 
 *                           => 사용자 정의 
 *       2. HttpSecurity : 로그인 / 로그아웃 /권한 / CSRF보안 설정 구성 
 *       3. SecurityFilterChain : HTTP 요청에 대한 Spring Security 필처 처리 순서 정의
 *       4. AuthenticationManager : 사용자의 인증 과정을 총괄 
 *       5. AuthenticationProvider : 실제 사용자 인증을 수행하는 객체 
 *             => login_ok
 *       6. UserDetailsService : 로그인한 사용자의 정보를 조회 
 *       7. JdbcUserDetailsManager
 *             => DB에서 사용자/권한 정보를 조회해서 
 *                저장하는 역할 : SQL 사용 
 *       8. UserDetails : 사용자 정보가 저장된 객체 
 *       9. BCryptPasswordEncoder : 비밀번호 암호화 처리 
 *       10. JdbcTokenRepositoryImpl : 자동 로그인시 사용자 구분 토큰을 저장하는 역할 
 *       11. LoginSuccessHandler : 로그인 성공시 처리 
 *       12. LoginFailHandler : 로그인 실패시 처리 
 *       13. SecurityContext : 인증 정보를 보관하는 클래스
 *       14. formLogin : 로그인시 처리 방법 설정 
 *       15. rememberMe: 자동로그인 설정 
 *       16. logout : => session해제 / cookie 삭제 
 *       
 *       Filter - Manager = UserDetailsService - DB
 *              - PasswordEncoder 
 *              - Authentication 
 *              - SecurityContext
 *        => DB 
 *           회원정보 / 권한 
 *              |
 *           Authentication 
 *              |-로그인된 사용자 정보
 *           SecurityContext 
 *              | -  Authentication 보관 
 *           Session 
 *              로그인 상태 유지 
 *              
 *       ---------------------------------
 *       인증 AuthenticationManager
 *       성공 Authentication -> SecurityContext
 *       유지 Session (remember-me)
 *                     | cookie + DB
*/