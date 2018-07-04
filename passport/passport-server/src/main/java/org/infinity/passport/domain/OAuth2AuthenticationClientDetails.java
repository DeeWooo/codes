package org.infinity.passport.domain;

import org.infinity.passport.dto.OAuth2AuthenticationClientDetailsDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

@Document(collection = "OAuth2AuthenticationClientDetails")
public class OAuth2AuthenticationClientDetails extends BaseClientDetails implements ClientDetails {

    private static final long serialVersionUID = 1L;

    @Id
    private String            id;

    @PersistenceConstructor
    public OAuth2AuthenticationClientDetails() {
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

        OAuth2AuthenticationClientDetails that = (OAuth2AuthenticationClientDetails) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public OAuth2AuthenticationClientDetailsDTO asDTO() {
        OAuth2AuthenticationClientDetailsDTO dest = new OAuth2AuthenticationClientDetailsDTO();
        BeanUtils.copyProperties(this, dest);
        return dest;
    }

    public static OAuth2AuthenticationClientDetails fromDTO(OAuth2AuthenticationClientDetailsDTO dto) {
        OAuth2AuthenticationClientDetails dest = new OAuth2AuthenticationClientDetails();
        BeanUtils.copyProperties(dto, dest);
        return dest;
    }
}
