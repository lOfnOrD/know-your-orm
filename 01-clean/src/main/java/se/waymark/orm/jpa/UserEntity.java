package se.waymark.orm.jpa;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import se.waymark.orm.jpa.fields.IDImpl;
import se.waymark.orm.model.User;

@Entity
public class UserEntity extends BaseMappedSuperclass implements User {

    @Id
    @GeneratedValue
    private long userID;

    @Basic(optional = false)
    @Column(unique = true)
    private String userName;

    private String encryptedPassword;

    private boolean active;

    private Date lastVisit;
    private Date lastFailure;
    private Date currentVisit;

    @ManyToMany
    private Set<RoleEntity> roles;

    @ManyToOne(optional = false)
    private PersonEntity person;

    public UserEntity(String userName, PersonEntity person) {
        this.userName = userName;
        this.person = person;
        this.active = true;
        this.roles = new HashSet<>();
    }

    protected UserEntity() {
    }

    @Override
    public UserID getUserID() {
        return new UserIDImpl();
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public Date getLastVisit() {
        return lastVisit;
    }

    @Override
    public Date getLastFailure() {
        return lastFailure;
    }

    @Override
    public Date getCurrentVisit() {
        return currentVisit;
    }

    @Override
    public Set<RoleEntity> getRoles() {
        return roles;
    }
    
    @Override
    public PersonEntity getPerson() {
        return person;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User that = (User) o;

        UserID thatUserID = that.getUserID();
        return userID == thatUserID.getID();

    }

    @Override
    public int hashCode() {
        return (int) (userID ^ (userID >>> 32));
    }

    public void setRoles(Set<RoleEntity> roles) {
        this.roles = new HashSet<>();

        if (roles != null) {
            roles.addAll(roles);
        }
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void updateVisitedTimestamps() {
        this.lastVisit = currentVisit;
        this.currentVisit = new Date();
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "userID=" + userID +
                ", userName='" + userName + '\'' +
                ", active=" + active +
                ", lastVisit=" + lastVisit +
                ", lastFailure=" + lastFailure +
                ", currentVisit=" + currentVisit +
                ", roles=" + roles +
                ", person=" + person +
                ", encryptedPassword='" + encryptedPassword + '\'' +
                '}';
    }

    public void updateLastFailureTimestamp() {
        this.lastFailure = new Date();
    }

    private class UserIDImpl extends IDImpl implements UserID {
        public UserIDImpl() {
            super(UserEntity.this.userID);
        }
    }
}
