package se.waymark.orm.jpa;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import se.waymark.orm.jpa.fields.IDImpl;
import se.waymark.orm.model.Organization;

@Entity
public class OrganizationEntity extends BaseMappedSuperclass implements Organization {
    @Id
    @GeneratedValue
    private long organizationID;

    @Basic(optional = false)
    @Column(unique = true)
    private String organizationName;

    private String organizationDescription;

    @ManyToOne(optional = true)
    private OrganizationEntity motherOrganization;

    @ManyToOne(optional = true) // cannot create first organization without this being optional
    private PersonEntity organizationManager;

    public OrganizationEntity(String organizationName) {
        this.organizationName = organizationName;
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

    private class OrganizationIDImpl extends IDImpl implements OrganizationID {
        public OrganizationIDImpl() {
            super(OrganizationEntity.this.organizationID);
        }
    }
}
