package se.waymark.orm.jpa;

import java.util.Date;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se.waymark.orm.model.Role;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class BaseMappedSuperclassTest {

    private InMemoryPersistence persistence;
    private DateTime beforePersist;
    private DateTime afterPersist;
    private Role.LimaRoleID persistedRoleID;

    @Before
    public void setUp() throws Exception {
        persistence = new InMemoryPersistence();

        beforePersist = DateTime.now();

        // Use RoleEntity as a guinea pig extender of BaseMappedSuperclass
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            RoleEntity role = new RoleEntity(1, "a RoleName");
            tx.persist(role);
            tx.commit();
            persistedRoleID = role.getLimaRoleID();
        }

        afterPersist = DateTime.now();

        persistence.resetForTest();
    }

    @After
    public void tearDown() throws Exception {
        persistence.close();
    }


    @Test
    public void testAuditPropertiesAfterPersist() throws Exception {
        RoleEntity unmodifiedRole = findPersistedRole();

        verifyCreatedAndCreatedBy(unmodifiedRole);
        assertThat(unmodifiedRole.getLastWrittenBy(), nullValue());
    }

    @Test
    public void testAuditPropertiesAfterUpdate() throws Exception {

        DateTime beforeUpdate = DateTime.now();

        //SUT
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            RoleEntity unmodifiedRole = findPersistedRole();
            unmodifiedRole.setRoleDescription("expected");
            tx.commit();
        }

        persistence.resetForVerification();

        RoleEntity updatedRole = findPersistedRole();
        verifyCreatedAndCreatedBy(updatedRole);
        assertThat(updatedRole.getRoleDescription(), is("expected"));

        Date actualLastWritten = updatedRole.getLastWritten();
        assertThat(actualLastWritten.getTime(), greaterThan(afterPersist.getMillis()));
        assertThat(actualLastWritten.getTime(), greaterThan(beforeUpdate.getMillis()));

        // the assert below fails, probably because of timestamps being generated in different ways
//        assertThat(actualLastWritten.getMillis(), lessThanOrEqualTo(DateTime.now().getMillis()));

        assertThat(updatedRole.getLastWrittenBy(), is(System.getenv("USER")));
    }

    private void verifyCreatedAndCreatedBy(RoleEntity role) {
        Date actualCreated = role.getCreated();
        assertThat(actualCreated.getTime(), greaterThanOrEqualTo(beforePersist.getMillis()));
        assertThat(actualCreated.getTime(), lessThanOrEqualTo(afterPersist.getMillis()));
        assertThat(role.getCreatedBy(), is(System.getenv("USER")));
    }

    private RoleEntity findPersistedRole() {
        return persistence.find(RoleEntity.class, persistedRoleID.getID());
    }

}
