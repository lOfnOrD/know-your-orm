package se.waymark.orm.jpa;

import java.util.Set;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se.waymark.orm.model.Person;
import se.waymark.orm.model.Role;
import se.waymark.orm.model.User;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.junit.matchers.JUnitMatchers.hasItems;

public class UserEntityPersistTest {

    private InMemoryPersistence persistence;
    private Person.PersonID person1ID;

    public UserEntityPersistTest() {
        super();
        // Mute SqlExceptionHelper log
        Logger.getLogger(SqlExceptionHelper.class).setLevel(Level.FATAL);
    }

    @Before
    public void setUp() throws Exception {
        persistence = new InMemoryPersistence();
        OrganizationEntity organization = new OrganizationEntity("Org");
        PersonEntity person1 = new PersonEntity("Some One", organization);
        PersonEntity person2 = new PersonEntity("You Two", organization);
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            tx.persist(organization);
            tx.persist(person1);
            tx.persist(person2);
            tx.commit();
        }
        person1ID = person1.getPersonID();
        persistence.resetForTest();
    }

    @After
    public void tearDown() throws Exception {
        persistence.close();
    }


    @Test
    public void testPersist_happyPath() throws Exception {
        String userName = "a UserName";
        PersonEntity person = findPerson(person1ID);

        //SUT
        UserEntity toPersist = new UserEntity(userName, person);
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            tx.persist(toPersist);
            tx.commit();
        }

        User.UserID id = toPersist.getUserID();
        persistence.resetForVerification();

        UserEntity reRead = persistence.find(UserEntity.class, id.getID());
        assertThat(reRead.getUserName(), is(userName));
        assertThat(reRead.getPerson(), is(person));
        assertThat(reRead.isActive(), is(true));
    }

    @Test
    public void testRoles_duplicateRoleOnlyStoredOnce() throws Exception {
        UserEntity user = new UserEntity("a UserName", findPerson(person1ID));
        RoleEntity sameRole = new RoleEntity(1, "same RoleEnum");
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

        User.UserID id = user.getUserID();
        persistence.resetForVerification();

        UserEntity reRead = persistence.find(UserEntity.class, id.getID());
        Set<RoleEntity> actualRoles = reRead.getRoles();
        assertThat(actualRoles.size(), is(1));
        assertThat(actualRoles, hasItem(sameRole));
    }

    @Test
    public void testRoles_happyPath() throws Exception {

        UserEntity user = new UserEntity("a UserName", findPerson(person1ID));
        RoleEntity role1 = new RoleEntity(1, "role1");
        RoleEntity role2 = new RoleEntity(2, "role2");
        RoleEntity role3 = new RoleEntity(3, "role3");
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            tx.persist(role1);
            tx.persist(role2);
            tx.persist(role3);
            user.getRoles().add(role3);
            user.getRoles().add(role1);
            tx.persist(user);
            tx.commit();
        }

        User.UserID id = user.getUserID();
        Role.RoleID role1id = role1.getRoleID();
        Role.RoleID role2id = role2.getRoleID();
        Role.RoleID role3id = role3.getRoleID();
        persistence.resetForVerification();

        UserEntity storedUser = persistence.find(UserEntity.class, id.getID());
        RoleEntity storedRole1 = persistence.find(RoleEntity.class, role1id.getID());
        RoleEntity storedRole2 = persistence.find(RoleEntity.class, role2id.getID());
        RoleEntity storedRole3 = persistence.find(RoleEntity.class, role3id.getID());

        Set<RoleEntity> actualRoles = storedUser.getRoles();
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
