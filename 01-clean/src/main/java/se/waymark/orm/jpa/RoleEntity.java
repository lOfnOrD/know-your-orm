package se.waymark.orm.jpa;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import se.waymark.orm.jpa.fields.IDImpl;
import se.waymark.orm.model.Role;
import se.waymark.orm.model.RoleEnum;

@Entity
public class RoleEntity extends BaseMappedSuperclass implements Role {

    @Id // Not generated!
    private long roleID;

    @Basic(optional = false)
    @Column(unique = true)
    private String roleName;

    private String roleDescription;

    @ManyToMany(mappedBy = "roles")
    private Set<UserEntity> users;

    public RoleEntity(long roleID, String roleName) {
        this.roleID = roleID;
        this.roleName = roleName;
        this.users = new HashSet<>();
    }

    protected RoleEntity() {
    }

    @Override
    public RoleID getRoleID() {
        return new RoleIDImpl();
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
        return RoleEnum.findById(roleID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;

        Role that = (Role) o;

        return roleID == that.getRoleID().getID();
    }

    @Override
    public int hashCode() {
        return (int) (roleID ^ (roleID >>> 32));
    }

    private class RoleIDImpl extends IDImpl implements RoleID {
        public RoleIDImpl() {
            super(RoleEntity.this.roleID);
        }
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }
}
