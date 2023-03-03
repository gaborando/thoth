package com.thoth.server.configuration.security;

import com.thoth.server.configuration.security.token.ApiKetAuthenticationToken;
import com.thoth.server.configuration.security.token.DefaultAuthenticationToken;
import com.thoth.server.configuration.security.token.JwtAuthenticationToken;
import com.thoth.server.configuration.security.token.TempAuthenticationToken;
import com.thoth.server.service.ApiKeyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final ApiKeyService apiKeyService;

    private final SecuredTimestampService securedTimestampService;
    private final boolean oAuthEnabled;

    private final String userSidClaim;

    private final String organizationSidClaim;


    public AuthenticationFilter(
            @Value("${thoth.oauth.enabled}") boolean oAuthEnabled,
            JwtService jwtService, ApiKeyService apiKeyService, SecuredTimestampService securedTimestampService,
            @Value("${thoth.oauth.claim.sid.user}") String userSidClaim,
            @Value("${thoth.oauth.claim.sid.organization}") String organizationSidClaim) {
        this.jwtService = jwtService;
        this.oAuthEnabled = oAuthEnabled;
        this.apiKeyService = apiKeyService;
        this.securedTimestampService = securedTimestampService;
        this.userSidClaim = userSidClaim;
        this.organizationSidClaim = organizationSidClaim;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (!oAuthEnabled) {
                var authentication = new DefaultAuthenticationToken();
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
                return;
            }

            String apiKey = getApiKeyFromRequest(request);
            if (StringUtils.hasText(apiKey)) {
                var key =  apiKeyService.checkKey(apiKey);
                var authentication = new ApiKetAuthenticationToken(key);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
                return;
            }

            String tmpKey = getTmpKetFromRequest(request);
            if (StringUtils.hasText(tmpKey)) {
                var key =  securedTimestampService.parse(tmpKey);
                var authentication = new TempAuthenticationToken(key);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
                return;
            }


            String jwt = getJwtFromRequest(request);
            if (StringUtils.hasText(jwt)) {
                var claims = jwtService.verifyAndGetClaims(jwt);
                var authentication = new JwtAuthenticationToken(
                        claims,userSidClaim,organizationSidClaim);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
                return;
            }

        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
        filterChain.doFilter(request, response);
    }

    private String getTmpKetFromRequest(HttpServletRequest request) {
        String tmpKey = request.getParameter("TMP_KEY");
        if(StringUtils.hasText(tmpKey)){
            return tmpKey;
        }
        return null;
    }

    private String getApiKeyFromRequest(HttpServletRequest request) {
        String apiKey = request.getParameter("API_KEY");
        if(StringUtils.hasText(apiKey)){
            return apiKey;
        }
        return null;
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        String token = request.getParameter("access_token");
        if(StringUtils.hasText(token)){
            return token;
        }
        return null;
    }
}