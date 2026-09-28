package com.gaayong.api;

import com.gaayong.entity.User;
import com.gaayong.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController("apiUserController")
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService service;

    @PatchMapping("/theme")
    public ResponseEntity<Void> theme(@RequestBody Map<String, String> body,
                                      @AuthenticationPrincipal User user,
                                      HttpServletRequest request) {
        String mode = body.get("theme");

        try {
            if (!"light".equals(mode) && !"dark".equals(mode)) {
                return ResponseEntity.unprocessableEntity().build();
            }

            boolean result = service.editTheme(user.getId(), mode);
            if (!result) {
                return ResponseEntity.internalServerError().build();
            }

            user.setTheme(mode);
            HttpSession session = request.getSession(false);
            if (session != null) {
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
                );
                session.setAttribute(
                        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                        SecurityContextHolder.getContext()
                );
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error occurred: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}
