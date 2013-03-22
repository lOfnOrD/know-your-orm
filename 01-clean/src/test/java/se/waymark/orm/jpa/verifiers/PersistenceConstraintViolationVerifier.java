package se.waymark.orm.jpa.verifiers;

import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

public class PersistenceConstraintViolationVerifier {
    public static void verifyUniqueConstraintViolation(RollbackException e, String... columnNames) {
        Throwable maybePersistenceException = e.getCause();
        assertThat(maybePersistenceException, instanceOf(PersistenceException.class));
        PersistenceException persistenceException = (PersistenceException) maybePersistenceException;

        Throwable maybeHibernateConstraintViolationException = persistenceException.getCause();
        assertThat(maybeHibernateConstraintViolationException, instanceOf(org.hibernate.exception.ConstraintViolationException.class));

        ConstraintViolationException constraintViolationException = (ConstraintViolationException) maybeHibernateConstraintViolationException;
        assertThat(constraintViolationException.getSQLException().getMessage().toLowerCase(), containsString("unique index"));
        for (String columnName : columnNames) {
            assertThat(constraintViolationException.getConstraintName().toLowerCase(), containsString(columnName.toLowerCase()));
        }
    }
}
