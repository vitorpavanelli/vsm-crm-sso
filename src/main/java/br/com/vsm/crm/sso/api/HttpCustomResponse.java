package br.com.vsm.crm.sso.api;

import lombok.Data;

@Data
public class HttpCustomResponse {

    private String message;

    private Long userId;

    private HttpCustomResponseStatus status;
}
