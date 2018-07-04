package org.infinity.passport.domain;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.provider.approval.Approval;

@Document(collection = "OAuth2AuthenticationApproval")
public class OAuth2AuthenticationApproval extends Approval implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String            id;

    @PersistenceConstructor
    public OAuth2AuthenticationApproval() {
        super();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OAuth2AuthenticationApproval that = (OAuth2AuthenticationApproval) o;

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
