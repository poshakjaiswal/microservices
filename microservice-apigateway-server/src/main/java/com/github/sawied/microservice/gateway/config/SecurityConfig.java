package com.github.sawied.microservice.gateway.config;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.github.sawied.microservice.gateway.security.CompoundTokenExtractor;

@Configuration
@EnableResourceServer
@EnableWebSecurity(debug=true)
public class SecurityConfig extends ResourceServerConfigurerAdapter{

	@Autowired
	@Qualifier("tokenService")
	private ResourceServerTokenServices tokenService;
	
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenServices(tokenService).tokenExtractor(new CompoundTokenExtractor()).resourceId("API_GATEWAY_RESOURCE");
	}
	
	

	
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		//super.configure(http);
		http.requestMatcher(new NotOauth2RequestMatcher()).authorizeRequests().antMatchers("/oauth2/**").permitAll().antMatchers("/**").authenticated()
		.and().sessionManagement().sessionFixation().none();
		;
	}


    



	@Bean
	public TokenStore jwtTokenStore(JwtAccessTokenConverter jwtTokenConverter) {
		return new JwtTokenStore(jwtTokenConverter);
	}




	@Bean
	public JwtAccessTokenConverter jwtTokenConverter(@Value("${jwt.token.key:secret}") String key ) {
		JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
		jwtAccessTokenConverter.setSigningKey(key);
		return jwtAccessTokenConverter;
	}

	
	@Bean
	@Primary
	public ResourceServerTokenServices tokenService(TokenStore jwtTokenStore) {
		DefaultTokenServices tokenService = new DefaultTokenServices();
		tokenService.setTokenStore(jwtTokenStore);
		return tokenService;
	}
	
	private static class NotOauth2RequestMatcher implements RequestMatcher{

		@Override
		public boolean matches(HttpServletRequest request) {
			return true;
		}
		
	}
	
	
}