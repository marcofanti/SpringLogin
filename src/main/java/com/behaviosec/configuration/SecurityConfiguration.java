package com.behaviosec.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.behaviosec.handler.SimpleAuthenticationSuccessHandler;
import com.behaviosec.login.filter.BehavioSecAuthenticationEntryPoint;
import com.behaviosec.login.filter.BehavioSecAuthenticationFilter;
import com.behaviosec.login.filter.BehavioSecAuthenticationFilter2;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private DataSource dataSource;

	@Value("${spring.queries.users-query}")
	private String usersQuery;

	@Value("${spring.queries.roles-query}")
	private String rolesQuery;

	@Autowired
	private SimpleAuthenticationSuccessHandler successHandler;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().usersByUsernameQuery(usersQuery).authoritiesByUsernameQuery(rolesQuery)
				.dataSource(dataSource).passwordEncoder(bCryptPasswordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.sessionManagement().maximumSessions(2);
		BehavioSecAuthenticationFilter customFilter = new BehavioSecAuthenticationFilter();
		http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);

		http.authorizeRequests()
			.antMatchers("/").permitAll()
			.antMatchers("/login").permitAll()
			.antMatchers("/success").permitAll()
			.antMatchers("/jwtvalidation").permitAll()
			.antMatchers("/registration").permitAll()
			.antMatchers("/registrationga").permitAll()
			.antMatchers("/authorizationregistration").permitAll()
			.antMatchers("/redirectPostToPost").permitAll()
			.antMatchers("/redirectedPostToPost").permitAll()
			.antMatchers("/saml/SSO").permitAll()
			.antMatchers("/home/**").hasAuthority("USER").anyRequest().authenticated().and().csrf().disable()
			.formLogin().loginPage("/login").failureUrl("/login?error=true").defaultSuccessUrl("/home/homepage")
			.usernameParameter("username").passwordParameter("password").and().formLogin()
			.successHandler(successHandler).and().logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/success")
			.deleteCookies("auth_code", "token").deleteCookies("auth_code", "JSESSIONID").and().exceptionHandling()
			.accessDeniedPage("/access-denied");
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
	}

}
