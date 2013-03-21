package se.waymark.orm.model;

import java.util.Set;
import se.waymark.orm.model.fields.GlobalID;

/**
 * Represents role stored in database, identities must match those of hardcoded <code>Role</code>s.
 */
public interface LimaRole extends LimaBase {

    LimaRoleID getLimaRoleID();

    String getRoleName();

    String getRoleDescription();

    Set<? extends LimaUser> getUsers();

    Role getRoleEnum();

    interface LimaRoleID extends GlobalID {
    }
}
