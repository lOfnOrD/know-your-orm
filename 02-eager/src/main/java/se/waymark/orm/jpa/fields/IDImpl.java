package se.waymark.orm.jpa.fields;

import se.waymark.orm.model.fields.ID;

/**
 * Cannot use {@code @EmbeddedId } with generated values
 * (see https://forum.hibernate.org/viewtopic.php?p=2393962 )
 * and {@code @IdClass } is EJB-2-horrible,
 * so wrap id field in subclass of this POJO.
 */
public abstract class IDImpl implements ID {

    /**
     * NameField of generator specified in package-info
     */
    public static final String GENERATOR_NAME = "ID";

    /**
     * Strategy of generator specified in package-info
     */
    public static final String HIBERNATE_GENERATOR_STRATEGY = "org.hibernate.id.IncrementGenerator";

    private long id;

    public IDImpl(long id) {
        this.id = id;
    }

    @Override
    public long getID() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ID)) return false;

        ID that = (ID) o;

        return id == that.getID();

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
