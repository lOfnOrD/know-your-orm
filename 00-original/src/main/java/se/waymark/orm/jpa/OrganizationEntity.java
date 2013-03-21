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
import se.waymark.orm.jpa.fields.GlobalIDImpl;
import se.waymark.orm.model.Organization;

@Entity
@Table(name = "Organization")
@org.hibernate.validator.constraints.ScriptAssert(
        lang = "javascript",
        script = "!_this.equals(_this.motherOrganization)",
        message = "Organization cannot be the mother of itself"
)
public class OrganizationEntity extends LimaBaseMappedSuperclass implements Organization {
    @Id
    @GeneratedValue(generator = GlobalIDImpl.GENERATOR_NAME)
    private long organizationID;

    @Basic(optional = false)
    @Column(unique = true)
    @NotNull
    @Size(min = 1, max = 255)
    private String organizationName;

    @Size(max = 2000)
    private String organizationDescription;

    @ManyToOne(optional = false)
    @JoinColumn(name = "CountryID")
    @NotNull
    private CountryEntity residence;

    @ManyToOne(optional = true)
    @JoinColumn(name = "MotherOrganizationID")
    private OrganizationEntity motherOrganization;

    @ManyToOne(optional = true) // cannot create first organization without this being optional
    @JoinColumn(name = "PersonID")
    private PersonEntity organizationManager;

    public OrganizationEntity(String organizationName, CountryEntity residence) {
        this.organizationName = organizationName;
        this.residence = residence;
    }

    protected OrganizationEntity() {
    }


    @Override
    public OrganizationID getOrganizationID() {
        return new OrganizationIDImpl();
    }

    @Override
    public String getOrganizationName() {
        return organizationName;
    }

    @Override
    public String getOrganizationDescription() {
        return organizationDescription;
    }

    @Override
    public CountryEntity getResidence() {
        return residence;
    }

    @Override
    public OrganizationEntity getMotherOrganization() {
        return motherOrganization;
    }

    @Override
    public PersonEntity getOrganizationManager() {
        return organizationManager;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Organization)) return false;

        Organization that = (Organization) o;

        OrganizationID thatOrganizationID = that.getOrganizationID();
        return organizationID == thatOrganizationID.getID();

    }

    @Override
    public int hashCode() {
        return (int) (organizationID ^ (organizationID >>> 32));
    }
    
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public void setOrganizationDescription(String organizationDescription) {
        this.organizationDescription = organizationDescription;
    }

    public void setMotherOrganization(OrganizationEntity motherOrganization) {
        this.motherOrganization = motherOrganization;
    }

    public void setOrganizationManager(PersonEntity organizationManager) {
        this.organizationManager = organizationManager;
    }

    public void setResidence(CountryEntity residence) {
        this.residence = residence;
    }

    private class OrganizationIDImpl extends GlobalIDImpl implements OrganizationID {
        public OrganizationIDImpl() {
            super(OrganizationEntity.this.organizationID);
        }
    }
}
