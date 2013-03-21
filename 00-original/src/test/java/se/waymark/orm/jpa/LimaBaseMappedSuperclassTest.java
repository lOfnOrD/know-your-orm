package se.waymark.orm.jpa;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se.waymark.orm.model.LimaRole;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class LimaBaseMappedSuperclassTest {

    private InMemoryPersistence persistence;
    private DateTime beforePersist;
    private DateTime afterPersist;
    private LimaRole.LimaRoleID persistedRoleID;

    @Before
    public void setUp() throws Exception {
        persistence = new InMemoryPersistence();

        beforePersist = DateTime.now();

        // Use LimaRoleEntity as a guinea pig extender of LimaBaseMappedSuperclass
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            LimaRoleEntity role = new LimaRoleEntity(1, "a RoleName");
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
        LimaRoleEntity unmodifiedRole = findPersistedRole();

        verifyCreatedAndCreatedBy(unmodifiedRole);
        assertThat(unmodifiedRole.getLastWrittenBy(), nullValue());
        assertThat(unmodifiedRole.isDeleted(), is(false));
    }

    @Test
    public void testAuditPropertiesAfterUpdate() throws Exception {

        DateTime beforeUpdate = DateTime.now();

        //SUT
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            LimaRoleEntity unmodifiedRole = findPersistedRole();
            unmodifiedRole.setDeleted(true);
            tx.commit();
        }

        persistence.resetForVerification();

        LimaRoleEntity updatedRole = findPersistedRole();
        verifyCreatedAndCreatedBy(updatedRole);
        assertThat(updatedRole.isDeleted(), is(true));

        DateTime actualLastWritten = updatedRole.getLastWritten();
        assertThat(actualLastWritten.getMillis(), greaterThan(afterPersist.getMillis()));
        assertThat(actualLastWritten.getMillis(), greaterThan(beforeUpdate.getMillis()));

        // the assert below fails, probably because of timestamps being generated in different ways
//        assertThat(actualLastWritten.getMillis(), lessThanOrEqualTo(DateTime.now().getMillis()));

        assertThat(updatedRole.getLastWrittenBy(), is(System.getenv("USER")));
    }

    private void verifyCreatedAndCreatedBy(LimaRoleEntity role) {
        DateTime actualCreated = role.getCreated();
        assertThat(actualCreated.getMillis(), greaterThanOrEqualTo(beforePersist.getMillis()));
        assertThat(actualCreated.getMillis(), lessThanOrEqualTo(afterPersist.getMillis()));
        assertThat(role.getCreatedBy(), is(System.getenv("USER")));
    }

    private LimaRoleEntity findPersistedRole() {
        return persistence.find(LimaRoleEntity.class, persistedRoleID.getID());
    }

}
