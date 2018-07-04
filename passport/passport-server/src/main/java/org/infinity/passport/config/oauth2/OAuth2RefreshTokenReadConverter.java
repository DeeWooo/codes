package org.infinity.passport.config.oauth2;

import java.util.Date;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

import com.mongodb.DBObject;

/**
 * Converter to deserialize back into an OAuth2RefreshToken Object made necessary because
 * Spring Mongo can't map oAuth2RefreshToken to OAuth2RefreshToken.
 */
public class OAuth2RefreshTokenReadConverter implements Converter<DBObject, OAuth2RefreshToken> {

    @Override
    public OAuth2RefreshToken convert(DBObject source) {
        DefaultExpiringOAuth2RefreshToken oAuth2RefreshToken = new DefaultExpiringOAuth2RefreshToken(
                (String) source.get("value"), (Date) source.get("expiration"));
        return oAuth2RefreshToken;
    }
}
