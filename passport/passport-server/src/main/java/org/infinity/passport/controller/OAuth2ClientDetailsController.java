package org.infinity.passport.controller;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.OAuth2AuthenticationClientDetails;
import org.infinity.passport.dto.OAuth2AuthenticationClientDetailsDTO;
import org.infinity.passport.exception.FieldValidationException;
import org.infinity.passport.exception.NoDataException;
import org.infinity.passport.repository.OAuth2ClientDetailsRepository;
import org.infinity.passport.utils.HttpHeaderCreator;
import org.infinity.passport.utils.PaginationUtils;
import org.infinity.passport.utils.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(tags = "单点登录客户端信息")
public class OAuth2ClientDetailsController {

    private static final Logger           LOGGER = LoggerFactory.getLogger(OAuth2ClientDetailsController.class);

    @Autowired
    private OAuth2ClientDetailsRepository oAuth2ClientDetailsRepository;

    @Autowired
    private HttpHeaderCreator             httpHeaderCreator;

    @ApiOperation("创建单点登录客户端信息")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "成功创建"), @ApiResponse(code = 400, message = "字典名已存在") })
    @RequestMapping(value = "/api/oauth-client/details", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(Authority.ADMIN)
    @Timed
    public ResponseEntity<Void> create(
            @ApiParam(value = "单点登录客户端信息", required = true) @Valid @RequestBody OAuth2AuthenticationClientDetailsDTO dto) {
        LOGGER.debug("REST create oauth client detail: {}", dto);
        oAuth2ClientDetailsRepository.findOneByClientId(dto.getClientId()).ifPresent((existingEntity) -> {
            throw new FieldValidationException("oOAuth2AuthenticationClientDetailsDTO", "clientId", dto.getClientId(),
                    "error.oauth.client.detail.id.exists", dto.getClientId());
        });
        dto.setClientId(StringUtils.defaultIfEmpty(dto.getClientId(), RandomUtils.generateId()));
        dto.setClientSecret(StringUtils.defaultIfEmpty(dto.getClientSecret(), RandomUtils.generateId()));
        oAuth2ClientDetailsRepository.save(OAuth2AuthenticationClientDetails.fromDTO(dto));
        return ResponseEntity.status(HttpStatus.CREATED).headers(
                httpHeaderCreator.createSuccessHeader("notification.oauth.client.detail.created", dto.getClientId()))
                .build();
    }

    @ApiOperation("获取单点登录客户端信息分页列表")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功获取") })
    @RequestMapping(value = "/api/oauth-client/details", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(Authority.ADMIN)
    @Timed
    public ResponseEntity<List<OAuth2AuthenticationClientDetailsDTO>> getClientDetails(Pageable pageable,
            @ApiParam(value = "客户端ID", required = false) @RequestParam(value = "clientDetailId", required = false) String clientId)
            throws URISyntaxException {
        //        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("clientId",
        //                ExampleMatcher.GenericPropertyMatchers.exact());
        OAuth2AuthenticationClientDetails probe = new OAuth2AuthenticationClientDetails();
        probe.setClientId(clientId);
        Page<OAuth2AuthenticationClientDetails> details = oAuth2ClientDetailsRepository.findAll(Example.of(probe),
                pageable);
        List<OAuth2AuthenticationClientDetailsDTO> OAuth2AuthenticationClientDetailsDTOs = details.getContent().stream()
                .map(entity -> entity.asDTO()).collect(Collectors.toList());
        HttpHeaders headers = PaginationUtils.generatePaginationHttpHeaders(details, "/api/oauth-client/details");
        return new ResponseEntity<>(OAuth2AuthenticationClientDetailsDTOs, headers, HttpStatus.OK);
    }

    @ApiOperation("根据客户端ID检索单点登录客户端信息")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功获取"),
            @ApiResponse(code = 400, message = "单点登录客户端信息不存在") })
    @RequestMapping(value = "/api/oauth-client/details/{clientId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<OAuth2AuthenticationClientDetailsDTO> getClientDetail(
            @ApiParam(value = "客户端ID", required = true) @PathVariable String clientId) {
        OAuth2AuthenticationClientDetails entity = oAuth2ClientDetailsRepository
                .findOneByClientId(clientId).orElseThrow(() -> new NoDataException(clientId));
        return new ResponseEntity<>(entity.asDTO(), HttpStatus.OK);
    }

    @ApiOperation("更新单点登录客户端信息")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功更新"),
            @ApiResponse(code = 400, message = "单点登录客户端信息不存在") })
    @RequestMapping(value = "/api/oauth-client/details", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(Authority.ADMIN)
    @Timed
    public ResponseEntity<Void> updateClientDetail(
            @ApiParam(value = "新的单点登录客户端信息", required = true) @Valid @RequestBody OAuth2AuthenticationClientDetailsDTO dto) {
        LOGGER.debug("REST request to update oauth client detail: {}", dto);
        oAuth2ClientDetailsRepository.findOneByClientId(dto.getClientId())
                .orElseThrow(() -> new NoDataException(dto.getClientId()));
        oAuth2ClientDetailsRepository.save(OAuth2AuthenticationClientDetails.fromDTO(dto));
        return ResponseEntity.status(HttpStatus.OK).headers(
                httpHeaderCreator.createSuccessHeader("notification.oauth.client.detail.updated", dto.getClientId()))
                .build();

    }

    @ApiOperation(value = "根据客户端ID删除单点登录客户端信息", notes = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功删除"),
            @ApiResponse(code = 400, message = "单点登录客户端信息不存在") })
    @RequestMapping(value = "/api/oauth-client/details/{clientId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(Authority.ADMIN)
    @Timed
    public ResponseEntity<Void> deleteClientDetail(
            @ApiParam(value = "客户端ID", required = true) @PathVariable String clientId) {
        LOGGER.debug("REST request to delete oauth client detail: {}", clientId);
        oAuth2ClientDetailsRepository.findOneByClientId(clientId).orElseThrow(() -> new NoDataException(clientId));
        oAuth2ClientDetailsRepository.delete(clientId);
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("notification.oauth.client.detail.deleted", clientId))
                .build();
    }
}
