package se.waymark.orm.model;

import se.waymark.orm.model.fields.GlobalID;

public interface Person extends LimaBase {

    PersonID getPersonID();
    String getSwedbankPersonID();
    String getFullName();
    String getLastName();
    String getFirstName();
    String getTelephone();
    String getEmail();

    Organization getOrganization();

    interface PersonID extends GlobalID {
    }
}
