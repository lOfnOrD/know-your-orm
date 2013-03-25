package se.waymark.orm.jpa.fields;

import java.io.Serializable;
import java.util.Objects;
import se.waymark.orm.model.fields.ID;

public class GlobalIDUtil implements Serializable {
    /**
     * Type-checked equality for <code>ID</code> implementations.
     */
    public static <I extends ID> boolean equals(I id1, I id2) {
        return Objects.equals(id1, id2);
    }
}