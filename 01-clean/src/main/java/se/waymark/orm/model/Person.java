package se.waymark.orm.model;

import se.waymark.orm.model.fields.ID;

public interface Person extends Base {

    PersonID getPersonID();
    String getSwedbankPersonID();
    String getFullName();
    String getLastName();
    String getFirstName();
    String getTelephone();
    String getEmail();

    Organization getOrganization();

    interface PersonID extends ID {
    }
}
