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
import se.waymark.orm.model.Country;
import se.waymark.orm.model.Organization;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static se.waymark.orm.jpa.verifiers.BeanValidationViolationVerifier.verifySingleViolation;
import static se.waymark.orm.jpa.verifiers.PersistenceConstraintViolationVerifier.verifyUniqueConstraintViolation;

public class OrganizationEntityTest {

    public static final String CLASS_LEVEL_CONSTRAINT_PROPERTY_PATH = "";
    private InMemoryPersistence persistence;
    private Country.CountryID residenceCountryID;

    public OrganizationEntityTest() {
        super();
        // Mute SqlExceptionHelper log
        Logger.getLogger(SqlExceptionHelper.class).setLevel(Level.FATAL);
    }

    @Before
    public void setUp() throws Exception {
        persistence = new InMemoryPersistence();

        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            CountryEntity residence = new CountryEntity(4, "LL", "Country", "Land");
            tx.persist(residence);
            tx.commit();
            residenceCountryID = residence.getCountryID();
        }
        persistence.resetForTest();
    }

    @After
    public void tearDown() throws Exception {
        persistence.close();
    }

    @Test
    public void testPersist_nullName() throws Throwable {
        String nullName = null;
        OrganizationEntity toPersist = new OrganizationEntity(nullName, findResidence());

        //SUT
        persistWithViolation(toPersist, "organizationName", nullName, NotNull.class);
    }

    @Test
    public void testPersist_nullCountry() throws Throwable {
        CountryEntity nullCountry = null;
        OrganizationEntity toPersist = new OrganizationEntity("any OrgName", nullCountry);

        //SUT
        persistWithViolation(toPersist, "residence", nullCountry, NotNull.class);
    }

    @Test
    public void testPersist_emptyName() throws Throwable {
        String emptyName = "";
        OrganizationEntity toPersist = new OrganizationEntity(emptyName, findResidence());

        //SUT
        persistWithViolation(toPersist, "organizationName", emptyName, Size.class);
    }

    private void persistWithViolation(OrganizationEntity toPersist,
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
    public void testPersist_nonUniqueName() throws Exception {
        String sameName = "an OrgName";
        OrganizationEntity toPersist1 = new OrganizationEntity(sameName, findResidence());
        OrganizationEntity toPersist2 = new OrganizationEntity(sameName, findResidence());

        //SUT
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            tx.persist(toPersist1);
            tx.persist(toPersist2);
            tx.commit();
            fail("Expected rollback");
        } catch (RollbackException e) {
            String columnName = "OrganizationName";
            verifyUniqueConstraintViolation(e, columnName);
        }
    }

    @Test
    public void testPersist_motherOfItself() throws Exception {

        OrganizationEntity toPersist = new OrganizationEntity("Inbred", findResidence());
        toPersist.setMotherOrganization(toPersist);

        //SUT
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            tx.persist(toPersist);
            tx.commit();
            fail("Expected exception");
        } catch (RollbackException e) {
            verifySingleViolation(e, CLASS_LEVEL_CONSTRAINT_PROPERTY_PATH, toPersist, org.hibernate.validator.constraints.ScriptAssert.class);
        }
    }

    @Test
    public void testPersist_happyPath() throws Exception {
        String orgName = "an OrgName";
        CountryEntity residence = findResidence();
        String orgDescription = "some description";
        OrganizationEntity mother = new OrganizationEntity("Mama", residence);
        PersonEntity manager = new PersonEntity("Mama Boss", "Boss", "Mama", mother);

        OrganizationEntity toPersist = new OrganizationEntity(orgName, residence);
        toPersist.setOrganizationDescription(orgDescription);
        toPersist.setMotherOrganization(mother);
        toPersist.setOrganizationManager(manager);

        //SUT
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            tx.persist(mother);
            tx.persist(manager);
            tx.persist(toPersist);
            tx.commit();
        }
        Organization.OrganizationID id = toPersist.getOrganizationID();
        persistence.resetForVerification();

        OrganizationEntity reRead = persistence.find(OrganizationEntity.class, id.getID());
        assertThat(reRead.getOrganizationName(), is(orgName));
        assertThat(reRead.getResidence(), is(residence));
        assertThat(reRead.getOrganizationDescription(), is(orgDescription));
        assertThat(reRead.getMotherOrganization(), is(mother));
        assertThat(reRead.getOrganizationManager(), is(manager));
    }

    private CountryEntity findResidence() {
        return persistence.find(CountryEntity.class, residenceCountryID.getID());
    }
}
