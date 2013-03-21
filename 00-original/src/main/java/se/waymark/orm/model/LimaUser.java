package se.waymark.orm.model;

import java.util.Set;
import org.joda.time.DateTime;
import se.waymark.orm.model.fields.GlobalID;

public interface LimaUser extends LimaBase {

    LimaUserID getLimaUserID();
    String getUserName();
    String getEncryptedPassword();
    boolean isActive();
    DateTime getLastVisit();
    DateTime getLastFailure();
    DateTime getCurrentVisit();

    Set<? extends LimaRole> getRoles();

    Person getPerson();

    interface LimaUserID extends GlobalID {
    }
}
