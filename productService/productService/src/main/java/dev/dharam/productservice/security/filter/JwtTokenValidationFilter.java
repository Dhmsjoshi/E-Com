package dev.dharam.productservice.security.filter;


import dev.dharam.productservice.appConfig.ApplicationConstants;
import dev.dharam.productservice.security.config.SecurityPathRegistry;
import dev.dharam.productservice.security.util.JwtUtil;
import dev.dharam.productservice.security.util.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JwtTokenValidationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final SecurityPathRegistry pathRegistry;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(ApplicationConstants.JWT_HEADER);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String jwt = authHeader.substring(7);
                Claims claims = jwtUtil.extractAllClaims(jwt);

                UUID userId = jwtUtil.getUserIdFromToken(claims);
                String email = jwtUtil.getEmailFromToken(claims);
                String rolesString = jwtUtil.getRolesFromToken(claims);

                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    List<String> rolesListForDto = new ArrayList<>();

                    // Saara logic ek hi safety check ke andar
                    if (rolesString != null && !rolesString.isEmpty()) {
                        String[] rolesArray = rolesString.split(",");
                        for (String r : rolesArray) {
                            String formattedRole = "ROLE_" + r.trim();
                            authorities.add(new SimpleGrantedAuthority(formattedRole));
                            rolesListForDto.add(formattedRole);
                        }
                    }

                    UserDto userDto = new UserDto(userId, email, rolesListForDto);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userDto, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (ExpiredJwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token Expired");
                return;
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid Token: " + e.getMessage());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method =  request.getMethod();
        if(HttpMethod.GET.name().equalsIgnoreCase(method)){
            return Arrays.stream(pathRegistry.PUBLIC_GET_URLS)
                    .anyMatch(pattern -> pathMatcher.match(pattern, path));
        }
        return false;
    }
}
