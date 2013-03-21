package se.waymark.orm.model;

import java.io.Serializable;
import org.joda.time.DateTime;

public interface LimaBase extends Serializable {
    DateTime getCreated();
    String getCreatedBy();
    DateTime getLastWritten();
    String getLastWrittenBy();
    boolean isDeleted();
}
