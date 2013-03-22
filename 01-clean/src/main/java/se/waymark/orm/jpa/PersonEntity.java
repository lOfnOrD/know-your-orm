package se.waymark.orm.jpa;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import se.waymark.orm.jpa.fields.IDImpl;
import se.waymark.orm.model.Person;

@Entity
@Table(name = "Person")
@Indexed
public class PersonEntity extends BaseMappedSuperclass implements Person {
    @Id
    @GeneratedValue(generator = IDImpl.GENERATOR_NAME)
    private long personID;

    @Basic(optional = true)
    @Column(name = "FSPA_PersonID")
    @Size(max = 20)
    @Field
    private String swedbankPersonID;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Field
    private String fullName;

    @Basic(optional = true)
    @Size(max = 64)
    @Field
    private String lastName;

    @Basic(optional = true)
    @Size(max = 64)
    @Field
    private String firstName;

    @Basic(optional = true)
    @Size(max = 64)
    private String telephone;

    @Basic(optional = true)
    @org.hibernate.validator.constraints.Email
    @Size(max = 255)
    private String email;

    @ManyToOne(optional = false)
    @JoinColumn(name = "OrganizationID")
    @NotNull
    private OrganizationEntity organization;

    public PersonEntity(String fullName, String lastName, String firstName, OrganizationEntity organization) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.fullName = fullName;
        this.organization = organization;
    }

    protected PersonEntity() {
    }

    @Override
    public PersonID getPersonID() {
        return new PersonIDImpl();
    }

    @Override
    public String getSwedbankPersonID() {
        return swedbankPersonID;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getTelephone() {
        return telephone;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public OrganizationEntity getOrganization() {
        return organization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;

        Person that = (Person) o;

        PersonID thatPersonID = that.getPersonID();
        return personID == thatPersonID.getID();

    }

    @Override
    public int hashCode() {
        return (int) (personID ^ (personID >>> 32));
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setOrganization(OrganizationEntity organization) {
        this.organization = organization;
    }

    public void setSwedbankPersonID(String swedbankPersonID) {
        this.swedbankPersonID = swedbankPersonID;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private class PersonIDImpl extends IDImpl implements PersonID {
        public PersonIDImpl() {
            super(PersonEntity.this.personID);
        }
        
        @Override
        public boolean equals(Object o) {
            if (o instanceof PersonID)
                return getID() == ((PersonID) o).getID();

            return super.equals(o);
        }
    }
}
