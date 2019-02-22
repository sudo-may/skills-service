package skills.service.auth.form

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor
import org.springframework.security.web.context.SecurityContextPersistenceFilter
import org.springframework.security.web.util.matcher.RequestMatcher

import javax.servlet.http.HttpServletRequest

import static skills.service.auth.SecurityConfiguration.FormAuth
import static skills.service.auth.SecurityConfiguration.PortalWebSecurityHelper

@Conditional(FormAuth)
@Configuration
@EnableResourceServer
class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Value('#{"${security.oauth2.resource.id:skills-service-oauth}"}')
    private String resourceId

    // The DefaultTokenServices bean provided at the AuthorizationConfig
    @Autowired
    private DefaultTokenServices tokenServices

    // The TokenStore bean provided at the AuthorizationConfig
    @Autowired
    private TokenStore tokenStore

    @Autowired
    PortalWebSecurityHelper portalWebSecurityHelper

    @Autowired
    SecurityContextPersistenceFilter securityContextPersistenceFilter

    // To allow the ResourceServerConfigurerAdapter to understand the token,
    // it must share the same characteristics with AuthorizationServerConfigurerAdapter.
    // So, we must wire it up the beans in the ResourceServerSecurityConfigurer.
    @Override
    void configure(ResourceServerSecurityConfigurer resources) {
        resources
                .resourceId(resourceId)
                .tokenServices(tokenServices)
                .tokenStore(tokenStore)
    }

    @Override
    void configure(HttpSecurity http) throws Exception {
        portalWebSecurityHelper.configureHttpSecurity(
                http
                        .requestMatcher(new OAuthRequestedMatcher())
                        .addFilterBefore(securityContextPersistenceFilter, FilterSecurityInterceptor)
        )
    }

    private static class OAuthRequestedMatcher implements RequestMatcher {
        boolean matches(HttpServletRequest request) {
            String auth = request.getHeader("Authorization")
            // Determine if the client request contained an OAuth Authorization
            boolean haveOauth2Token = (auth != null) && auth.startsWith("Bearer")
            boolean haveAccessToken = request.getParameter("access_token") != null
            return haveOauth2Token || haveAccessToken
        }
    }
}
