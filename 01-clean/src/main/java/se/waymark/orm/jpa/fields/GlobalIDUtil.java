package se.waymark.orm.jpa.fields;

import java.io.Serializable;
import java.util.Objects;
import se.waymark.orm.model.fields.GlobalID;

public class GlobalIDUtil implements Serializable {
    /**
     * Type-checked equality for <code>GlobalID</code> implementations.
     */
    public static <I extends GlobalID> boolean equals(I id1, I id2) {
        return Objects.equals(id1, id2);
    }
}