// config/JwtAuthFilter.java
package com.pahana.edu.billing.config;

import com.pahana.edu.billing.repository.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component @RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final UserRepository userRepo;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    return path.equals("/api/health") || 
           path.startsWith("/api/auth/") || 
           path.equals("/error");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    
    String header = req.getHeader("Authorization");
    if(StringUtils.hasText(header) && header.startsWith("Bearer ")){
      String token = header.substring(7);
      try {
        String username = jwtService.extractUsername(token);
        var user = userRepo.findByUsername(username).orElse(null);
        if(user != null){
          var auth = new UsernamePasswordAuthenticationToken(
              username, null, List.of(new SimpleGrantedAuthority("ROLE_" + user.getUserType().name())));
          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      } catch(Exception ignored) {}
    }
    chain.doFilter(req, res);
  }
}
