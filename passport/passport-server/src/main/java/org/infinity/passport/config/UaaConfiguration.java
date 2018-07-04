package org.infinity.passport.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.infinity.passport.config.oauth2.MongoApprovalStore;
import org.infinity.passport.config.oauth2.MongoAuthorizationCodeServices;
import org.infinity.passport.config.oauth2.MongoClientDetailsService;
import org.infinity.passport.config.oauth2.MongoTokenStore;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.repository.OAuth2AccessTokenRepository;
import org.infinity.passport.repository.OAuth2ApprovalRepository;
import org.infinity.passport.repository.OAuth2ClientDetailsRepository;
import org.infinity.passport.repository.OAuth2CodeRepository;
import org.infinity.passport.repository.OAuth2RefreshTokenRepository;
import org.infinity.passport.security.AjaxLogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Refer 
 * http://projects.spring.io/spring-security-oauth/docs/oauth2.html
 * https://stackoverflow.com/questions/33812805/spring-security-oauth2-not-working-without-jwt
 * https://spring.io/guides/tutorials/spring-security-and-angular-js/#_oauth2_logout_angular_js_and_spring_security_part_ix
 * https://blog.csdn.net/a82793510/article/details/53509427
 * https://github.com/spring-guides/tut-spring-security-and-angular-js/issues/121
 */
@Configuration
public class UaaConfiguration {

    public static final String INTERNAL_CLIENT_ID        = "internal_client";

    public static final String INTERNAL_CLIENT_SECRET    = "7GF-td8-98s-9hq-HU8";

    public static final String THIRD_PARTY_CLIENT_ID     = "third_party_client";

    public static final String THIRD_PARTY_CLIENT_SECRET = "3fP-efd-40g-4Re-fvG";

    @Bean
    public TokenStore tokenStore(OAuth2AccessTokenRepository oAuth2AccessTokenRepository,
            OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository) {
        return new MongoTokenStore(oAuth2AccessTokenRepository, oAuth2RefreshTokenRepository);
    }

    /**
     * Resource server is used to process API calls
     */
    @Configuration
    @EnableResourceServer
    @SessionAttributes("authorizationRequest")
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Autowired
        private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;

        @Override
        public void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access Denied"))
            .and()
                .logout()
                .logoutUrl("/api/account/logout")
                .logoutSuccessHandler(ajaxLogoutSuccessHandler)
            .and()
//                    .csrf().disable()
                .headers()
                .frameOptions()
                .disable()
            .and()
                .authorizeRequests()
                // Do not need authentication
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/management/health").permitAll()
                // Need authentication
                .antMatchers("/api/**").authenticated()
                // Need 'DEVELOPER' authority
                .antMatchers("/v2/api-docs/**").hasAuthority(Authority.DEVELOPER)
                .antMatchers("/management/**").hasAuthority(Authority.DEVELOPER);
            // @formatter:on
        }
    }

    /**
     * Authorization server负责获取用户的授权并且发布token
     * AuthorizationServerEndpointsConfiguration
     */
    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        @Autowired
        private AuthenticationManager         authenticationManager;

        @Autowired
        private TokenStore                    tokenStore;

        @Autowired
        private UserDetailsService            userDetailsService;

        @Autowired
        private OAuth2ApprovalRepository      oAuth2ApprovalRepository;

        @Autowired
        private OAuth2CodeRepository          oAuth2CodeRepository;

        @Autowired
        private OAuth2ClientDetailsRepository oAuth2ClientDetailsRepository;

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.withClientDetails(new MongoClientDetailsService(oAuth2ClientDetailsRepository));
            // @formatter:off
//            clients.inMemory()
//                .withClient(INTERNAL_CLIENT_ID)
//                .secret(INTERNAL_CLIENT_SECRET)
//                .scopes("internal-app")
//                .autoApprove(true)
//                .authorizedGrantTypes("password", "authorization_code", "refresh_token") // 如果没有refresh_token的话，获取token时不会返回refresh_token
//                .accessTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(7))
//                .refreshTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(7))
//            .and()
//                .withClient(THIRD_PARTY_CLIENT_ID)
//                .secret(THIRD_PARTY_CLIENT_SECRET)
//                .scopes("openid")
//                .autoApprove(false)
//                .authorizedGrantTypes("implicit", "refresh_token", "password", "authorization_code")
//                .accessTokenValiditySeconds((int) TimeUnit.MINUTES.toSeconds(5))
//                .refreshTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(7));
            // @formatter:on
        }

        @Bean
        public ApprovalStore approvalStore() {
            return new MongoApprovalStore(oAuth2ApprovalRepository);
        }

        @Bean
        protected AuthorizationCodeServices authorizationCodeServices() {
            return new MongoAuthorizationCodeServices(oAuth2CodeRepository);
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            // Note: authenticationManager, tokenStore, userDetailsService must be injected here
            // 如果没有userDetailsService在使用refresh token刷新access token时报错
            // @formatter:off
            endpoints
                .authenticationManager(authenticationManager)
                .authorizationCodeServices(authorizationCodeServices())
                .approvalStore(approvalStore())
                .tokenStore(tokenStore)
                .userDetailsService(userDetailsService);
            // @formatter:on
            endpoints.addInterceptor(new HandlerInterceptorAdapter() {
                @Override
                public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                        ModelAndView modelAndView) throws Exception {
                    if (modelAndView != null && modelAndView.getView() instanceof RedirectView) {
                        RedirectView redirect = (RedirectView) modelAndView.getView();
                        String url = redirect.getUrl();
                        if (url.contains("code=") || url.contains("error=")) {
                            HttpSession session = request.getSession(false);
                            if (session != null) {
                                session.invalidate();
                            }
                        }
                    }
                }
            });
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
            // 如果没有下面一条语句会在使用authorization code获取access token时报Full
            // authentication is required to access this resource错误
            oauthServer.allowFormAuthenticationForClients();

            // 下面语句好像没起作用
            oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
        }
    }
}
