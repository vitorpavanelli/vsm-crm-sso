package br.com.vsm.crm.sso.api.user.repository;

import br.com.vsm.crm.sso.api.resource.repository.Resource;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString(exclude = {"user"})
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id", scope = Access.class
)
@Entity
public class Access {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "can_write")
    private boolean canWrite;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne(optional = false)
    @JoinColumn(name = "resource_id")
    private Resource resource;

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Access)) {
            return false;
        }

        if (id == null) {
            return false;
        }

        Access access = (Access) obj;

        return access.id.equals(id);
    }

    @Override
    public int hashCode() {
        if (id == null) {
            return 0;
        }

        int result = 17;
        result = 31 * result + id.hashCode();

        return result;
    }
}
