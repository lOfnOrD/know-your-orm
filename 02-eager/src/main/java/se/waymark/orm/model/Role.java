package se.waymark.orm.model;

import java.util.Set;
import se.waymark.orm.model.fields.ID;

/**
 * Represents role stored in database, identities must match those of hardcoded <code>RoleEnum</code>s.
 */
public interface Role extends Base {

    RoleID getRoleID();

    String getRoleName();

    String getRoleDescription();

    Set<? extends User> getUsers();

    RoleEnum getRoleEnum();

    interface RoleID extends ID {
    }
}
