package org.infinity.passport.repository;

import java.util.List;

import org.infinity.passport.domain.OAuth2AuthenticationAccessToken;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the OAuth2AuthenticationAccessToken entity.
 */
public interface OAuth2AccessTokenRepository extends MongoRepository<OAuth2AuthenticationAccessToken, String> {

    OAuth2AuthenticationAccessToken findByTokenId(String tokenId);

    OAuth2AuthenticationAccessToken findByRefreshToken(String refreshToken);

    OAuth2AuthenticationAccessToken findByAuthenticationId(String authenticationId);

    List<OAuth2AuthenticationAccessToken> findByClientIdAndUserName(String clientId, String userName);

    List<OAuth2AuthenticationAccessToken> findByClientId(String clientId);
}
