package se.waymark.orm.model;

import java.io.Serializable;
import java.util.Date;

public interface Base extends Serializable {
    Date getCreated();
    String getCreatedBy();
    Date getLastWritten();
    String getLastWrittenBy();
}
