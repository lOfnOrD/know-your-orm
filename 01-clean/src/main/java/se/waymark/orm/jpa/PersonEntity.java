package se.waymark.orm.jpa;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import se.waymark.orm.jpa.fields.IDImpl;
import se.waymark.orm.model.Person;

@Entity
public class PersonEntity extends BaseMappedSuperclass implements Person {
    @Id
    @GeneratedValue
    private long personID;

    @Basic(optional = false)
    private String fullName;

    private String email;

    @ManyToOne(optional = false)
    private OrganizationEntity organization;

    public PersonEntity(String fullName, OrganizationEntity organization) {
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
    public String getFullName() {
        return fullName;
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

    public void setOrganization(OrganizationEntity organization) {
        this.organization = organization;
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
