package se.waymark.orm.jpa;

import java.lang.annotation.Annotation;
import java.util.Set;
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
import se.waymark.orm.model.LimaUser;
import se.waymark.orm.model.Person;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static se.waymark.orm.jpa.verifiers.BeanValidationViolationVerifier.verifySingleViolation;
import static se.waymark.orm.jpa.verifiers.PersistenceConstraintViolationVerifier.verifyUniqueConstraintViolation;

public class LimaUserEntityPersistTest {

    private InMemoryPersistence persistence;
    private Person.PersonID person1ID;
    private Person.PersonID person2ID;

    public LimaUserEntityPersistTest() {
        super();
        // Mute SqlExceptionHelper log
        Logger.getLogger(SqlExceptionHelper.class).setLevel(Level.FATAL);
    }

    @Before
    public void setUp() throws Exception {
        persistence = new InMemoryPersistence();
        OrganizationEntity organization = new OrganizationEntity("Org");
        PersonEntity person1 = new PersonEntity("Some One", "One", "Some", organization);
        PersonEntity person2 = new PersonEntity("You Two", "Two", "You", organization);
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            tx.persist(organization);
            tx.persist(person1);
            tx.persist(person2);
            tx.commit();
        }
        person1ID = person1.getPersonID();
        person2ID = person2.getPersonID();
        persistence.resetForTest();
    }

    @After
    public void tearDown() throws Exception {
        persistence.close();
    }

    @Test
    public void testPersist_nullName() throws Throwable {
        String nullName = null;
        LimaUserEntity toPersist = new LimaUserEntity(nullName, findPerson(person1ID));

        //SUT
        persistWithViolation(toPersist, "userName", nullName, NotNull.class);
    }

    @Test
    public void testPersist_nullPerson() throws Throwable {
        PersonEntity nullPerson = null;
        LimaUserEntity toPersist = new LimaUserEntity("a UserName", nullPerson);

        //SUT
        persistWithViolation(toPersist, "person", nullPerson, NotNull.class);
    }

    @Test
    public void testPersist_emptyName() throws Throwable {
        String emptyName = "";
        LimaUserEntity toPersist = new LimaUserEntity(emptyName, findPerson(person1ID));

        //SUT
        persistWithViolation(toPersist, "userName", emptyName, Size.class);
    }

    @Test
    public void testPersist_nonUniqueName() throws Exception {
        String sameName = "a UserName";
        LimaUserEntity toPersist1 = new LimaUserEntity(sameName, findPerson(person1ID));
        LimaUserEntity toPersist2 = new LimaUserEntity(sameName, findPerson(person2ID));

        //SUT
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            tx.persist(toPersist1);
            tx.persist(toPersist2);
            tx.commit();
            fail("Expected rollback");
        } catch (RollbackException e) {
            verifyUniqueConstraintViolation(e, "UserName");
        }
    }

    private void persistWithViolation(LimaUserEntity toPersist,
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
        String userName = "a UserName";
        PersonEntity person = findPerson(person1ID);

        //SUT
        LimaUserEntity toPersist = new LimaUserEntity(userName, person);
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            tx.persist(toPersist);
            tx.commit();
        }

        LimaUser.LimaUserID id = toPersist.getLimaUserID();
        persistence.resetForVerification();

        LimaUserEntity reRead = persistence.find(LimaUserEntity.class, id.getID());
        assertThat(reRead.getUserName(), is(userName));
        assertThat(reRead.getPerson(), is(person));
        assertThat(reRead.isActive(), is(true));
    }

    @Test
    public void testRoles_duplicateRoleOnlyStoredOnce() throws Exception {
        LimaUserEntity user = new LimaUserEntity("a UserName", findPerson(person1ID));
        LimaRoleEntity sameRole = new LimaRoleEntity(1, "same Role");
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            tx.persist(user);
            tx.persist(sameRole);
            user.getRoles().add(sameRole);
            tx.commit();
        }
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            user.getRoles().add(sameRole);
            tx.commit();
        }

        LimaUser.LimaUserID id = user.getLimaUserID();
        persistence.resetForVerification();

        LimaUserEntity reRead = persistence.find(LimaUserEntity.class, id.getID());
        Set<LimaRoleEntity> actualRoles = reRead.getRoles();
        assertThat(actualRoles.size(), is(1));
        assertThat(actualRoles, hasItem(sameRole));
    }

    @Test
    public void testRoles_happyPath() throws Exception {

        LimaUserEntity user = new LimaUserEntity("a UserName", findPerson(person1ID));
        LimaRoleEntity role1 = new LimaRoleEntity(1, "role1");
        LimaRoleEntity role2 = new LimaRoleEntity(2, "role2");
        LimaRoleEntity role3 = new LimaRoleEntity(3, "role3");
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            tx.persist(role1);
            tx.persist(role2);
            tx.persist(role3);
            user.getRoles().add(role3);
            user.getRoles().add(role1);
            tx.persist(user);
            tx.commit();
        }

        LimaUser.LimaUserID id = user.getLimaUserID();
        LimaRole.LimaRoleID role1id = role1.getLimaRoleID();
        LimaRole.LimaRoleID role2id = role2.getLimaRoleID();
        LimaRole.LimaRoleID role3id = role3.getLimaRoleID();
        persistence.resetForVerification();

        LimaUserEntity storedUser = persistence.find(LimaUserEntity.class, id.getID());
        LimaRoleEntity storedRole1 = persistence.find(LimaRoleEntity.class, role1id.getID());
        LimaRoleEntity storedRole2 = persistence.find(LimaRoleEntity.class, role2id.getID());
        LimaRoleEntity storedRole3 = persistence.find(LimaRoleEntity.class, role3id.getID());

        Set<LimaRoleEntity> actualRoles = storedUser.getRoles();
        assertThat(actualRoles.size(), is(2));
        assertThat(actualRoles, hasItems(storedRole1, storedRole3));
        assertThat(storedRole1.getUsers(), hasItem(storedUser));
        assertThat(storedRole3.getUsers(), hasItem(storedUser));

        assertThat(actualRoles, not(hasItem(storedRole2)));
        assertThat(storedRole2.getUsers(), not(hasItem(storedUser)));

    }


    private PersonEntity findPerson(Person.PersonID id) {
        return persistence.find(PersonEntity.class, id.getID());
    }
}
