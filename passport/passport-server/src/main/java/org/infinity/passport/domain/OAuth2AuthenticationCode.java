package org.infinity.passport.domain;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

@Document(collection = "OAuth2AuthenticationCode")
public class OAuth2AuthenticationCode implements Serializable {

    private static final long    serialVersionUID = 1L;

    @Id
    private String               id;

    private String               code;

    private OAuth2Authentication authentication;

    @PersistenceConstructor
    public OAuth2AuthenticationCode(String code, OAuth2Authentication authentication) {
        this.code = code;
        this.authentication = authentication;
    }

    public String getCode() {
        return code;
    }

    public OAuth2Authentication getAuthentication() {
        return authentication;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OAuth2AuthenticationCode that = (OAuth2AuthenticationCode) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
