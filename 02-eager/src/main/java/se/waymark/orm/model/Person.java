package se.waymark.orm.model;

import se.waymark.orm.model.fields.ID;

public interface Person extends Base {

    PersonID getPersonID();
    String getFullName();
    String getEmail();

    Organization getOrganization();

    interface PersonID extends ID {
    }
}
