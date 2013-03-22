package se.waymark.orm.model;

import java.io.Serializable;
import org.joda.time.DateTime;

public interface Base extends Serializable {
    DateTime getCreated();
    String getCreatedBy();
    DateTime getLastWritten();
    String getLastWrittenBy();
    boolean isDeleted();
}
