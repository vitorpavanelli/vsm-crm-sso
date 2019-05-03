package br.com.vsm.crm.sso.api.user.repository;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = {"accesses"})
@ToString(exclude = {"accesses"})
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id", scope = User.class
)
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String email;

    private boolean admin;

    private String login;

    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private String password;

    private boolean active;

    private boolean locked;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Column(name = "locking_reason")
    private String lockingReason;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Access> accesses;

}
