package se.waymark.orm.jpa.verifiers;

import javax.persistence.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

public class BeanValidationViolationVerifier {
    public static void verifySingleViolation(RollbackException e,
                                             String propertyPath,
                                             Object invalidValue,
                                             Class<? extends Annotation> annotationClass) {
        Throwable maybeConstraintViolationException = e.getCause();

        assertThat(maybeConstraintViolationException, instanceOf(ConstraintViolationException.class));
        ConstraintViolationException cve = (ConstraintViolationException) maybeConstraintViolationException;

        Set<ConstraintViolation<?>> violations = cve.getConstraintViolations();
        assertThat(violations.size(), is(1));

        ConstraintViolation<?> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString(), is(propertyPath));
        assertThat(violation.getInvalidValue(), is(invalidValue));

        ConstraintDescriptor<?> constraintDescriptor = violation.getConstraintDescriptor();
        assertThat(constraintDescriptor.getAnnotation(), is(annotationClass));
    }
}
