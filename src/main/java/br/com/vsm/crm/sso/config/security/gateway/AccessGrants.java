package br.com.vsm.crm.sso.config.security.gateway;

import lombok.Data;

import java.util.Set;

@Data
public class AccessGrants {

    private String login;
    private boolean admin;
    private Set<String> accesses;
}
