package io.github.nandandesai.secure.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.nandandesai.secure.security.models.JwtUserInfo;
import io.github.nandandesai.secure.security.models.UserSecurityDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private UserSecurityDetailsService userSecurityDetailsService;

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String token = null; //jwt
        JwtUserInfo jwtUserInfo = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            try {
                jwtUserInfo = jwtService.decodeToken(token);
            }catch (JWTVerificationException jwtAuthException){
                /*
                * This exception cannot be handled by our usual CommonExceptionHandler as we are in the filter.
                * Hence we're modifying the response object and stopping the filter chain here.
                * */
                TokenException ex = new TokenException(jwtAuthException.getMessage());

                //response status and content-type
                response.setStatus(ex.getResponseEntity().getStatusCodeValue());
                response.setContentType("application/json");

                //set response body as json
                ObjectMapper mapper = new ObjectMapper();
                response.getWriter().write(mapper.writeValueAsString(ex.getResponseEntity().getBody()));

                //break the chain
                return;
            }
        }
        if (jwtUserInfo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserSecurityDetails userSecurityDetails = (UserSecurityDetails) userSecurityDetailsService
                                                        .loadUserByUsername(jwtUserInfo.getEmail());
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    userSecurityDetails, token, userSecurityDetails.getAuthorities());
            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
        //continue the chain
        filterChain.doFilter(request, response);
    }
}
