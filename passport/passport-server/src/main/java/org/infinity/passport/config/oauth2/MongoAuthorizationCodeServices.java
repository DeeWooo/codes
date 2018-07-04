package org.infinity.passport.config.oauth2;

import org.infinity.passport.domain.OAuth2AuthenticationCode;
import org.infinity.passport.repository.OAuth2CodeRepository;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;

public class MongoAuthorizationCodeServices extends RandomValueAuthorizationCodeServices {

    private final OAuth2CodeRepository oAuth2CodeRepository;

    public MongoAuthorizationCodeServices(OAuth2CodeRepository oAuth2CodeRepository) {
        this.oAuth2CodeRepository = oAuth2CodeRepository;
    }

    @Override
    protected void store(String code, OAuth2Authentication authentication) {
        this.oAuth2CodeRepository.save(new OAuth2AuthenticationCode(code, authentication));
    }

    public OAuth2Authentication remove(String code) {
        OAuth2AuthenticationCode oAuth2AuthenticationCode = oAuth2CodeRepository.findOneByCode(code);
        if (oAuth2AuthenticationCode != null && oAuth2AuthenticationCode.getAuthentication() != null) {
            oAuth2CodeRepository.delete(oAuth2AuthenticationCode);
            return oAuth2AuthenticationCode.getAuthentication();
        }
        return null;
    }
}
