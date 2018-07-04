package org.infinity.passport.config.oauth2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.infinity.passport.domain.OAuth2AuthenticationClientDetails;
import org.infinity.passport.repository.OAuth2ClientDetailsRepository;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

public class MongoClientDetailsService implements ClientDetailsService, ClientRegistrationService {

    private PasswordEncoder               passwordEncoder = NoOpPasswordEncoder.getInstance();

    private OAuth2ClientDetailsRepository oAuth2ClientDetailsRepository;

    public MongoClientDetailsService(OAuth2ClientDetailsRepository oAuth2ClientDetailsRepository) {
        this.oAuth2ClientDetailsRepository = oAuth2ClientDetailsRepository;
    }

    /**
     * @param passwordEncoder the password encoder to set.
     */
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        OAuth2AuthenticationClientDetails mongoClientDetails = oAuth2ClientDetailsRepository.findOneByClientId(clientId)
                .orElseThrow(() -> new NoSuchClientException("No client with requested id: " + clientId));
        return mongoClientDetails;
    }

    @Override
    public void addClientDetails(ClientDetails clientDetails) throws ClientAlreadyExistsException {
        Optional<OAuth2AuthenticationClientDetails> mongoClientDetails = oAuth2ClientDetailsRepository
                .findOneByClientId(clientDetails.getClientId());
        if (mongoClientDetails.isPresent()) {
            throw new ClientAlreadyExistsException("Client already exists: " + clientDetails.getClientId());
        }
        saveClientDetails(mongoClientDetails.get(), clientDetails);
    }

    @Override
    public void updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {
        if (oAuth2ClientDetailsRepository.findOneByClientId(clientDetails.getClientId()) == null) {
            throw new NoSuchClientException("No client found with id = " + clientDetails.getClientId());
        }
        saveClientDetails(new OAuth2AuthenticationClientDetails(), clientDetails);
    }

    @Override
    public void updateClientSecret(String clientId, String secret) throws NoSuchClientException {
        OAuth2AuthenticationClientDetails mongoClientDetails = oAuth2ClientDetailsRepository.findOneByClientId(clientId)
                .orElseThrow(() -> new NoSuchClientException("No client with requested id: " + clientId));
        mongoClientDetails.setClientSecret(passwordEncoder.encode(secret));
        oAuth2ClientDetailsRepository.save(mongoClientDetails);
    }

    @Override
    public void removeClientDetails(String clientId) throws NoSuchClientException {
        OAuth2AuthenticationClientDetails mongoClientDetails = oAuth2ClientDetailsRepository.findOneByClientId(clientId)
                .orElseThrow(() -> new NoSuchClientException("No client with requested id: " + clientId));
        oAuth2ClientDetailsRepository.delete(mongoClientDetails);
    }

    @Override
    public List<ClientDetails> listClientDetails() {
        return new ArrayList<>(oAuth2ClientDetailsRepository.findAll());
    }

    public void saveClientDetails(OAuth2AuthenticationClientDetails mongoClientDetails, ClientDetails clientDetails) {
        mongoClientDetails.setClientId(clientDetails.getClientId());
        mongoClientDetails.setClientSecret(
                clientDetails.getClientSecret() != null ? passwordEncoder.encode(clientDetails.getClientSecret())
                        : null);
        mongoClientDetails.setScope(clientDetails.getScope());
        mongoClientDetails.setResourceIds(clientDetails.getResourceIds());
        mongoClientDetails.setAuthorizedGrantTypes(clientDetails.getAuthorizedGrantTypes());
        mongoClientDetails.setRegisteredRedirectUri(clientDetails.getRegisteredRedirectUri());
        mongoClientDetails.setAuthorities(clientDetails.getAuthorities());
        mongoClientDetails.setAccessTokenValiditySeconds(clientDetails.getAccessTokenValiditySeconds());
        mongoClientDetails.setRefreshTokenValiditySeconds(clientDetails.getRefreshTokenValiditySeconds());
        mongoClientDetails.setAdditionalInformation(clientDetails.getAdditionalInformation());
        if (clientDetails instanceof BaseClientDetails) {
            mongoClientDetails.setAutoApproveScopes(((BaseClientDetails) clientDetails).getAutoApproveScopes());
        }
        oAuth2ClientDetailsRepository.save(mongoClientDetails);
    }
}
