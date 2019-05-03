package br.com.vsm.crm.sso.api;

import br.com.vsm.crm.sso.api.resource.repository.Resource;
import br.com.vsm.crm.sso.api.user.repository.User;
import lombok.Data;

@Data
public class HttpAuthenticatedUserResponse {

    private User user;

    private Iterable<Resource> resources;
}
