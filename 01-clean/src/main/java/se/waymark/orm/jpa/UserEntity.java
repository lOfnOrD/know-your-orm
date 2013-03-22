package se.waymark.orm.jpa;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.joda.time.DateTime;
import se.waymark.orm.jpa.fields.GlobalIDImpl;
import se.waymark.orm.model.User;

@Entity
@Table(name = "User")
public class UserEntity extends BaseMappedSuperclass implements User {
    @Id
    @GeneratedValue(generator = GlobalIDImpl.GENERATOR_NAME)
    private long limaUserID;

    @Basic(optional = false)
    @Column(unique = true)
    @NotNull
    @Size(min = 1, max = 20)
    private String userName;

    //TODO: Custom passwordEncryptor implementation matching existing VB algorithm, alt. double passwords!?
//    @Size(max = 20)
    private String encryptedPassword;

    @Column(columnDefinition = "NUMBER", precision = 1, scale = 0)
    private boolean active;

    @org.hibernate.annotations.Type(type=USERTYPE_DATETIME)
    private DateTime lastVisit;
    @org.hibernate.annotations.Type(type=USERTYPE_DATETIME)
    private DateTime lastFailure;
    @org.hibernate.annotations.Type(type=USERTYPE_DATETIME)
    private DateTime currentVisit;

    @ManyToMany
    @JoinTable(
            name="LimaUser_LimaRole",
            joinColumns=@JoinColumn(name="LimaUserID"),
            inverseJoinColumns=@JoinColumn(name="LimaRoleID")
    )
    private Set<RoleEntity> roles;

    @ManyToOne(optional = false)
    @JoinColumn(name = "PersonID")
    @NotNull
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
    public LimaUserID getLimaUserID() {
        return new LimaUserIDImpl();
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
    public DateTime getLastVisit() {
        return lastVisit;
    }

    @Override
    public DateTime getLastFailure() {
        return lastFailure;
    }

    @Override
    public DateTime getCurrentVisit() {
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

        LimaUserID thatLimaUserID = that.getLimaUserID();
        return limaUserID == thatLimaUserID.getID();

    }

    @Override
    public int hashCode() {
        return (int) (limaUserID ^ (limaUserID >>> 32));
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
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
        this.currentVisit = DateTime.now();
    }

    public void updateLastFailureTimestamp() {
        this.lastFailure = DateTime.now();
    }

    public void setPerson(PersonEntity person) {
        this.person = person;
    }

    private class LimaUserIDImpl extends GlobalIDImpl implements LimaUserID {
        public LimaUserIDImpl() {
            super(UserEntity.this.limaUserID);
        }
    }
}
