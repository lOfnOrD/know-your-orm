package se.waymark.orm.model;

import se.waymark.orm.model.fields.GlobalID;

public interface Organization extends LimaBase {

    OrganizationID getOrganizationID();
    String getOrganizationName();
    String getOrganizationDescription();

    Organization getMotherOrganization();
    Person getOrganizationManager();

    interface OrganizationID extends GlobalID {
    }
}
