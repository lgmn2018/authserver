package com.lgmn.authserver.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class LgmnUserDetails implements Serializable {

    private String password;
    private  String username;
    private  Set<String> authorities;
    private  boolean accountNonExpired;
    private  boolean accountNonLocked;
    private  boolean credentialsNonExpired;
    private  boolean enabled;

}
