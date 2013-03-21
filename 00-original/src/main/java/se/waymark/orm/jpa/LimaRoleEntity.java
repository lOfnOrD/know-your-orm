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
import se.waymark.orm.jpa.fields.GlobalIDImpl;
import se.waymark.orm.model.LimaRole;
import se.waymark.orm.model.Role;

@Entity
@Table(name = "LimaRole")
public class LimaRoleEntity extends LimaBaseMappedSuperclass implements LimaRole {

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
    private Set<LimaUserEntity> users;

    public LimaRoleEntity(long limaRoleID, String roleName) {
        this.limaRoleID = limaRoleID;
        this.roleName = roleName;
        this.users = new HashSet<>();
    }

    protected LimaRoleEntity() {
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
    public Set<LimaUserEntity> getUsers() {
        return Collections.unmodifiableSet(users); // owned by LimaUser => changes will not be stored
    }

    @Override
    public Role getRoleEnum() {
        return Role.findById(limaRoleID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LimaRole)) return false;

        LimaRole that = (LimaRole) o;

        return limaRoleID == that.getLimaRoleID().getID();
    }

    @Override
    public int hashCode() {
        return (int) (limaRoleID ^ (limaRoleID >>> 32));
    }

    private class LimaRoleIDImpl extends GlobalIDImpl implements LimaRoleID {
        public LimaRoleIDImpl() {
            super(LimaRoleEntity.this.limaRoleID);
        }
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }
}
