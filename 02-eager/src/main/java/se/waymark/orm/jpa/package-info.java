@org.hibernate.annotations.GenericGenerators({
        @org.hibernate.annotations.GenericGenerator(
                name = IDImpl.GENERATOR_NAME,
                strategy = IDImpl.HIBERNATE_GENERATOR_STRATEGY)
})

/**
 * JPA <code>@Entity</code> implementations of <code>model</code> interfaces
 */
package se.waymark.orm.jpa;

import se.waymark.orm.jpa.fields.IDImpl;