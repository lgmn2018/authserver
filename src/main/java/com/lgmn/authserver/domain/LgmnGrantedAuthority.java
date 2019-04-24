package com.lgmn.authserver.domain;

import java.io.Serializable;

public interface LgmnGrantedAuthority extends Serializable {
    String getAuthority();
}
