package se.waymark.orm.model;

import java.util.Date;
import java.util.Set;
import se.waymark.orm.model.fields.ID;

public interface User extends Base {

    UserID getUserID();
    String getUserName();
    String getEncryptedPassword();
    boolean isActive();
    Date getLastVisit();
    Date getLastFailure();
    Date getCurrentVisit();

    Set<? extends Role> getRoles();

    Person getPerson();

    interface UserID extends ID {
    }
}
