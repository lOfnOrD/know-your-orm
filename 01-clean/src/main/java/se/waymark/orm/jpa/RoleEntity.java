package se.waymark.orm.jpa;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import se.waymark.orm.jpa.fields.IDImpl;
import se.waymark.orm.model.Role;
import se.waymark.orm.model.RoleEnum;

@Entity
@Table(name = "Role")
public class RoleEntity extends BaseMappedSuperclass implements Role {

    @Id // Not generated!
    private long limaRoleID;

    @Basic(optional = false)
    @Column(unique = true)
    @NotNull
    @Size(min = 1, max = 255)
    private String roleName;

    @Size(max = 2000)
    private String roleDescription;

    @ManyToMany(mappedBy = "roles")
    private Set<UserEntity> users;

    public RoleEntity(long limaRoleID, String roleName) {
        this.limaRoleID = limaRoleID;
        this.roleName = roleName;
        this.users = new HashSet<>();
    }

    protected RoleEntity() {
    }

    @Override
    public LimaRoleID getLimaRoleID() {
        return new LimaRoleIDImpl();
    }

    @Override
    public String getRoleName() {
        return roleName;
    }

    @Override
    public String getRoleDescription() {
        return roleDescription;
    }

    @Override
    public Set<UserEntity> getUsers() {
        return Collections.unmodifiableSet(users); // owned by User => changes will not be stored
    }

    @Override
    public RoleEnum getRoleEnum() {
        return RoleEnum.findById(limaRoleID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;

        Role that = (Role) o;

        return limaRoleID == that.getLimaRoleID().getID();
    }

    @Override
    public int hashCode() {
        return (int) (limaRoleID ^ (limaRoleID >>> 32));
    }

    private class LimaRoleIDImpl extends IDImpl implements LimaRoleID {
        public LimaRoleIDImpl() {
            super(RoleEntity.this.limaRoleID);
        }
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }
}
