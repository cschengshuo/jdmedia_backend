package com.winsyo.jdmedia.config;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

  private UserDetailsService userDetailsService;
  private JwtTokenUtil jwtTokenUtil;

  @Autowired
  public JwtAuthenticationTokenFilter(@Qualifier("jwtUserDetailsServiceImpl") UserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil) {
    this.userDetailsService = userDetailsService;
    this.jwtTokenUtil = jwtTokenUtil;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
    String authHeader = request.getHeader("Authorization");
    String tokenHead = "Bearer ";
    if (authHeader != null && authHeader.startsWith(tokenHead)) {
      String authToken = authHeader.substring(tokenHead.length());
      String username = jwtTokenUtil.getUsernameFromToken(authToken);
      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        if (jwtTokenUtil.validateToken(authToken, userDetails)) {
          UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      }
    }
    chain.doFilter(request, response);
  }

}