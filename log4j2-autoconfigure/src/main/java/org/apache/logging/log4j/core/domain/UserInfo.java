package org.apache.logging.log4j.core.domain;

import lombok.Data;

@Data
public class UserInfo {

    private String remoteIp;

    private String requestUri;

    private String userAgent;

    private String username;
}
