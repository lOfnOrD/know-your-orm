package se.waymark.orm.model;

import java.util.Set;
import se.waymark.orm.model.fields.GlobalID;

/**
 * Represents role stored in database, identities must match those of hardcoded <code>RoleEnum</code>s.
 */
public interface Role extends Base {

    LimaRoleID getLimaRoleID();

    String getRoleName();

    String getRoleDescription();

    Set<? extends User> getUsers();

    RoleEnum getRoleEnum();

    interface LimaRoleID extends GlobalID {
    }
}
