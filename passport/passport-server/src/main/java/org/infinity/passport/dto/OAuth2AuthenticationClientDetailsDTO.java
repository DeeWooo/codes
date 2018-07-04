package org.infinity.passport.dto;

import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

import io.swagger.annotations.ApiModel;

@ApiModel("单点登录客户端信息DTO")
public class OAuth2AuthenticationClientDetailsDTO extends BaseClientDetails implements ClientDetails {

    private static final long serialVersionUID = 1L;

}
