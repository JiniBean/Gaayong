package com.gaayong.api;

import com.gaayong.entity.User;
import com.gaayong.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("apiUserController")
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService service;

    @GetMapping("/{m}")
    public ResponseEntity<String> theme(@PathVariable(name = "m") String mode,
                                        @AuthenticationPrincipal User user, HttpServletRequest request) {

        try {
            if (!mode.equals("light") && !mode.equals("dark")) return ResponseEntity.status(422).build();

            boolean result = service.editTheme(user.getId(), mode);

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
            if(!result) return ResponseEntity.status(500).build();
            else return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            log.error("Error occurred: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}
