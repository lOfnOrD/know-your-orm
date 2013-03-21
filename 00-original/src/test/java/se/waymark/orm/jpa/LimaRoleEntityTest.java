package se.waymark.orm.jpa;

import java.lang.annotation.Annotation;
import javax.persistence.EntityExistsException;
import javax.persistence.RollbackException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se.waymark.orm.model.LimaRole;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static se.waymark.orm.jpa.verifiers.BeanValidationViolationVerifier.verifySingleViolation;
import static se.waymark.orm.jpa.verifiers.PersistenceConstraintViolationVerifier.verifyUniqueConstraintViolation;

public class LimaRoleEntityTest {

    private InMemoryPersistence persistence;

    public LimaRoleEntityTest() {
        super();
        // Mute SqlExceptionHelper log
        Logger.getLogger(SqlExceptionHelper.class).setLevel(Level.FATAL);
    }

    @Before
    public void setUp() throws Exception {
        persistence = new InMemoryPersistence();
    }

    @After
    public void tearDown() throws Exception {
        persistence.close();
    }

    @Test
    public void testPersist_nullName() throws Throwable {
        String nullName = null;
        LimaRoleEntity toPersist = new LimaRoleEntity(1, nullName);

        //SUT
        persistWithViolation(toPersist, "roleName", nullName, NotNull.class);
    }

    @Test
    public void testPersist_emptyName() throws Throwable {
        String emptyName = "";
        LimaRoleEntity toPersist = new LimaRoleEntity(1, emptyName);

        //SUT
        persistWithViolation(toPersist, "roleName", emptyName, Size.class);
    }

    private void persistWithViolation(LimaRoleEntity toPersist,
                                      String propertyPath,
                                      String invalidValue,
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
    public void testPersist_nonUniqueID() throws Exception {
        int sameID = 1;
        LimaRoleEntity toPersist1 = new LimaRoleEntity(sameID, "a RoleName");
        LimaRoleEntity toPersist2 = new LimaRoleEntity(sameID, "another RoleName");

        // Persist first
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            tx.persist(toPersist1);
            tx.commit();
        }

        //SUT
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            tx.persist(toPersist2);
            tx.commit();
            fail("Expected exception");
        } catch (EntityExistsException e) {
        }
    }

    @Test
    public void testPersist_nonUniqueName() throws Exception {
        String sameName = "a RoleName";
        LimaRoleEntity toPersist1 = new LimaRoleEntity(1, sameName);
        LimaRoleEntity toPersist2 = new LimaRoleEntity(2, sameName);

        //SUT
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            tx.persist(toPersist1);
            tx.persist(toPersist2);
            tx.commit();
            fail("Expected rollback");
        } catch (RollbackException e) {
            String columnName = "RoleName";
            verifyUniqueConstraintViolation(e, columnName);
        }
    }

    @Test
    public void testPersist_happyPath() throws Exception {
        String roleName = "a RoleName";
        String roleDescription = "Some description";

        LimaRoleEntity toPersist = new LimaRoleEntity(1, roleName);
        toPersist.setRoleDescription(roleDescription);

        //SUT
        try (InMemoryPersistence.Tx emw = persistence.beginTx()) {
            emw.persist(toPersist);
            emw.commit();
        }

        LimaRole.LimaRoleID id = toPersist.getLimaRoleID();
        persistence.resetForVerification();

        LimaRoleEntity reRead = persistence.find(LimaRoleEntity.class, id.getID());
        assertThat(reRead.getRoleName(), is(roleName));
        assertThat(reRead.getRoleDescription(), is(roleDescription));
    }


    @Test(expected = UnsupportedOperationException.class)
    public void testUsers_unmodifiable() throws Exception {
        LimaRoleEntity role = new LimaRoleEntity(1, "a RoleName");
        LimaUserEntity user1 = new LimaUserEntity("user1", mock(PersonEntity.class));

        //SUT
        role.getUsers().add(user1);
    }
}
