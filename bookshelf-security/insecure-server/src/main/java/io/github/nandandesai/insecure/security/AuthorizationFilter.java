package io.github.nandandesai.insecure.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.nandandesai.insecure.security.models.JwtUserInfo;
import io.github.nandandesai.insecure.security.models.UserSecurityDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthorizationFilter extends OncePerRequestFilter {

    private UserSecurityDetailsService userSecurityDetailsService;

    private JwtService jwtService;

    public AuthorizationFilter(UserSecurityDetailsService userSecurityDetailsService, JwtService jwtService){
        this.jwtService = jwtService;
        this.userSecurityDetailsService=userSecurityDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        try {
            token = request.getCookies()[0].getValue();
        }catch (Exception e){
            /*
             * This exception cannot be handled by our usual CommonExceptionHandler as we are in the filter.
             * Hence we're modifying the response object and stopping the filter chain here.
             * */
            TokenException ex = new TokenException("Token not found or there was some error retrieving that.");

            //response status and content-type
            response.setStatus(ex.getResponseEntity().getStatusCodeValue());
            response.setContentType("application/json");

            //set response body as json
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(ex.getResponseEntity().getBody()));

            //break the chain
            return;
        }
        JwtUserInfo jwtUserInfo = null;
        if (token != null) {
            try {
                jwtUserInfo = jwtService.decodeToken(token);
            }catch (JWTVerificationException jwtAuthException){
                TokenException ex = new TokenException(jwtAuthException.getMessage());
                response.setStatus(ex.getResponseEntity().getStatusCodeValue());
                response.setContentType("application/json");
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
