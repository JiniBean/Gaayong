package com.gaayong.api;

import com.gaayong.entity.User;
import com.gaayong.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final SecurityContextRepository securityContextRepository;
    private final RememberMeServices rememberMeServices;

    public AuthController(AuthenticationManager authenticationManager,
                          UserService userService,
                          SecurityContextRepository securityContextRepository,
                          RememberMeServices rememberMeServices) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.securityContextRepository = securityContextRepository;
        this.rememberMeServices = rememberMeServices;
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(toUserMap(user));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> body,
                                                     HttpServletRequest request,
                                                     HttpServletResponse response) {
        String userNm = (String) body.get("userNm");
        String pwd = (String) body.get("pwd");
        boolean rememberMe = Boolean.TRUE.equals(body.get("rememberMe"));

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userNm, pwd));

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            if (rememberMe) {
                HttpServletRequest rememberRequest = new HttpServletRequestWrapper(request) {
                    @Override
                    public String getParameter(String name) {
                        if ("rememberMe".equals(name)) {
                            return "true";
                        }
                        return super.getParameter(name);
                    }
                };
                rememberMeServices.loginSuccess(rememberRequest, response, authentication);
            }

            User user = (User) authentication.getPrincipal();
            return ResponseEntity.ok(toUserMap(user));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "아이디 또는 비밀번호가 올바르지 않습니다."));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request,
                                                      HttpServletResponse response,
                                                      Authentication authentication) {
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        return ResponseEntity.ok(Map.of("message", "로그아웃 되었습니다."));
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody Map<String, String> body) {
        Map<String, String> user = new HashMap<>();
        user.put("userNm", body.get("userNm"));
        user.put("name", body.get("name"));
        user.put("pwd", body.get("pwd"));
        user.put("email", body.get("email"));

        try {
            if (userService.addUser(user)) {
                return ResponseEntity.status(201).body(Map.of("message", "회원가입이 완료되었습니다."));
            }
            return ResponseEntity.status(500).body(Map.of("error", "회원가입에 실패했습니다."));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of("error", "이미 사용하고 있는 아이디입니다."));
        } catch (Exception e) {
            log.error("Signup failed: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "회원가입에 실패했습니다."));
        }
    }

    private Map<String, Object> toUserMap(User user) {
        String theme = user.getTheme() != null ? user.getTheme() : "light";
        return Map.of("userNm", user.getUserNm(), "name", user.getName(), "theme", theme);
    }
}
