package com.gaayong.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails, Serializable {
    @Serial
    private static final long serialVersionUID = 5110076519184586297L;

    private String id;
    private String userNm;
    private String name;
    private String pwd;
    private String email;
    private String joinDt;
    private String cxlDt;
    private String theme;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return this.pwd;
    }

    @Override
    public String getUsername() {
        return this.name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserNm(String userNm) {
        this.userNm = userNm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJoinDt() {
        return joinDt;
    }

    public void setJoinDt(String joinDt) {
        this.joinDt = joinDt;
    }

    public String getCxlDt() {
        return cxlDt;
    }

    public void setCxlDt(String cxlDt) {
        this.cxlDt = cxlDt;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}