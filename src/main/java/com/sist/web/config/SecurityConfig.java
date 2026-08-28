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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor

public class SecurityConfig {
	private final LoginSuccessHandler loginSuccessHandler;
	private final LoginFailHandler loginFailHandler;
	private final DataSource dataSource;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> 
				auth.requestMatchers("/", "/member/**").permitAll()
				.requestMatchers("/admin/**").hasRole("ADMIN").anyRequest().permitAll())
				.formLogin(form -> form.loginPage("/member/login").loginProcessingUrl("/member/login_process")
				.usernameParameter("userid").passwordParameter("userpwd").defaultSuccessUrl("/", false)
				.successHandler(loginSuccessHandler).failureHandler(loginFailHandler).permitAll())
				.rememberMe(remember -> remember.key("my-secret-key").rememberMeParameter("remember-me")
				.tokenValiditySeconds(60 * 60 * 24).tokenRepository(persistentTokenRepository()))
				.logout(logout -> logout.logoutUrl("/member/logout").logoutSuccessUrl("/").invalidateHttpSession(true)
				.deleteCookies("remember-me", "JSESSIONID"));

		return http.build();

	}

	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder passwordEncoder)
			throws Exception {
		AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
		builder.userDetailsService(jdbcUserDetailsService()).passwordEncoder(passwordEncoder());
		return builder.build();
	}

	@Bean
	public JdbcUserDetailsManager jdbcUserDetailsService() {
		JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
		manager.setUsersByUsernameQuery(
				"SELECT userid as username,userpwd as password,enable " + "FROM springmember WHERE userid=?");
		manager.setAuthoritiesByUsernameQuery(
				"SELECT userid as username , authority " + "FROM authority WHERE userid=?");
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
