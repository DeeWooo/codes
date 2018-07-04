package org.infinity.passport.repository;

import java.util.List;

import org.infinity.passport.domain.OAuth2AuthenticationApproval;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the OAuth2AuthenticationApproval entity.
 */
public interface OAuth2ApprovalRepository extends MongoRepository<OAuth2AuthenticationApproval, String> {

    List<OAuth2AuthenticationApproval> findByUserIdAndClientId(String userId, String clientId);

    List<OAuth2AuthenticationApproval> findByUserIdAndClientIdAndScope(String userId, String clientId, String scope);
}
