package org.infinity.passport.repository;

import java.util.Optional;

import org.infinity.passport.domain.OAuth2AuthenticationClientDetails;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the OAuth2AuthenticationClientDetails entity.
 */
public interface OAuth2ClientDetailsRepository extends MongoRepository<OAuth2AuthenticationClientDetails, String> {

    Optional<OAuth2AuthenticationClientDetails> findOneByClientId(String clientId);
}
