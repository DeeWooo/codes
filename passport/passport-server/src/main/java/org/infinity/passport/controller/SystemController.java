package org.infinity.passport.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.infinity.passport.config.ApplicationProperties;
import org.infinity.passport.domain.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemController {

    @Autowired
    private ApplicationProperties applicationProperties;

    @GetMapping(value = "/system/redis/admin", produces = MediaType.TEXT_HTML_VALUE)
    @Secured(Authority.DEVELOPER)
    public void redirectToRedisAdmin(HttpServletResponse response) throws IOException {
        response.sendRedirect(applicationProperties.getRedis().getAdminUrl());
    }

    @GetMapping(value = "/system/scheduler/admin", produces = MediaType.TEXT_HTML_VALUE)
    @Secured(Authority.DEVELOPER)
    public void redirectToScheduler(HttpServletResponse response) throws IOException {
        response.sendRedirect(applicationProperties.getScheduler().getAdminUrl());
    }
}
