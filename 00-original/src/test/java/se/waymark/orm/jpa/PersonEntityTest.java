package se.waymark.orm.jpa;

import java.lang.annotation.Annotation;
import javax.persistence.RollbackException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se.waymark.orm.model.Organization;
import se.waymark.orm.model.Person;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static se.waymark.orm.jpa.verifiers.BeanValidationViolationVerifier.verifySingleViolation;

public class PersonEntityTest {

    public static final String FIRST_NAME = "FirstName";
    public static final String FULL_NAME = "Full Name";
    public static final String LAST_NAME = "LastName";
    private InMemoryPersistence persistence;
    private Organization.OrganizationID orgID;

    public PersonEntityTest() {
        super();
        // Mute SqlExceptionHelper log
        Logger.getLogger(SqlExceptionHelper.class).setLevel(Level.FATAL);
    }

    @Before
    public void setUp() throws Exception {
        persistence = new InMemoryPersistence();
        OrganizationEntity organization = new OrganizationEntity("Org");
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            tx.persist(organization);
            tx.commit();
        }
        orgID = organization.getOrganizationID();
        persistence.resetForTest();
    }

    @After
    public void tearDown() throws Exception {
        persistence.close();
    }

    @Test
    public void testPersist_nullFullName() throws Throwable {
        String nullName = null;
        PersonEntity toPersist = new PersonEntity(nullName, LAST_NAME, FIRST_NAME, findOrg(orgID));

        //SUT
        persistWithViolation(toPersist, "fullName", nullName, NotNull.class);
    }

    @Test
    public void testPersist_nullOrganization() throws Throwable {
        OrganizationEntity nullOrg = null;
        PersonEntity toPersist = new PersonEntity(FULL_NAME, LAST_NAME, FIRST_NAME, nullOrg);

        //SUT
        persistWithViolation(toPersist, "organization", nullOrg, NotNull.class);
    }

    @Test
    public void testPersist_emptyFullName() throws Throwable {
        String emptyName = "";
        PersonEntity toPersist = new PersonEntity(emptyName, LAST_NAME, FIRST_NAME, findOrg(orgID));

        //SUT
        persistWithViolation(toPersist, "fullName", emptyName, Size.class);
    }

    @Test
    public void testPersist_invalidEmail() throws Exception {
        String invalidEmail = "not an email";
        PersonEntity toPersist = new PersonEntity(FULL_NAME, LAST_NAME, FIRST_NAME, findOrg(orgID));
        toPersist.setEmail(invalidEmail);

        //SUT
        persistWithViolation(toPersist, "email", invalidEmail, org.hibernate.validator.constraints.Email.class);
    }

    private void persistWithViolation(PersonEntity toPersist,
                                      String propertyPath,
                                      Object invalidValue,
                                      Class<? extends Annotation> annotationClass) throws Exception {
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            tx.persist(toPersist);
            tx.commit();
            fail("Expected rollback");
        } catch (RollbackException e) {
            verifySingleViolation(e, propertyPath, invalidValue, annotationClass);
        }
    }

    @Test
    public void testPersist_happyPath() throws Exception {
        OrganizationEntity organization = findOrg(orgID);
        String swedbankPersonID = "some ID";
        String telephone = "some number";
        String email = "anyone@example.com";

        PersonEntity toPersist = new PersonEntity(FULL_NAME, LAST_NAME, FIRST_NAME, organization);
        toPersist.setSwedbankPersonID(swedbankPersonID);
        toPersist.setTelephone(telephone);
        toPersist.setEmail(email);

        //SUT
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            tx.persist(toPersist);
            tx.commit();
        }

        Person.PersonID id = toPersist.getPersonID();
        persistence.resetForVerification();

        PersonEntity reRead = persistence.find(PersonEntity.class, id.getID());
        assertThat(reRead.getFullName(), is(FULL_NAME));
        assertThat(reRead.getLastName(), is(LAST_NAME));
        assertThat(reRead.getFirstName(), is(FIRST_NAME));
        assertThat(reRead.getOrganization(), is(organization));
        assertThat(reRead.getSwedbankPersonID(), is(swedbankPersonID));
        assertThat(reRead.getTelephone(), is(telephone));
        assertThat(reRead.getEmail(), is(email));
    }

    private OrganizationEntity findOrg(Organization.OrganizationID id) {
        return persistence.find(OrganizationEntity.class, id.getID());
    }
}
