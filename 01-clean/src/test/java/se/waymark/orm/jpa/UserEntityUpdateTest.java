package se.waymark.orm.jpa;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se.waymark.orm.model.User;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

public class UserEntityUpdateTest {

    private InMemoryPersistence persistence;

    @Before
    public void setUp() throws Exception {
        persistence = new InMemoryPersistence();
    }

    @After
    public void tearDown() throws Exception {
        persistence.close();
    }


    @Test
    public void testUpdateVisitedTimestamps_happyPath() throws Exception {
        OrganizationEntity organization = new OrganizationEntity("Org");
        PersonEntity person = new PersonEntity("A.N. Oob", organization);
        UserEntity neverVisited = new UserEntity("NeverVisited", person);

        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            tx.persist(organization);
            tx.persist(person);
            tx.persist(neverVisited);
            tx.commit();
        }
        User.UserID id = neverVisited.getUserID();

        persistence.resetForVerification();
        UserEntity verifyNeverVisited = persistence.find(UserEntity.class, id.getID());

        assertThat(verifyNeverVisited.getLastVisit(), nullValue());
        assertThat(verifyNeverVisited.getCurrentVisit(), nullValue());

        persistence.resetForTest();
        UserEntity visitOnce = persistence.find(UserEntity.class, id.getID());

        // Visit once
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            visitOnce.updateVisitedTimestamps();
            tx.commit();
        }

        persistence.resetForVerification();
        UserEntity verifyVisitedOnce = persistence.find(UserEntity.class, id.getID());

        long millisAfterFirstVisit = System.currentTimeMillis();
        long actualMillisFirstVisit = verifyVisitedOnce.getCurrentVisit().getTime();
        assertThat(verifyVisitedOnce.getLastVisit(), nullValue());
        assertThat(actualMillisFirstVisit, lessThanOrEqualTo(millisAfterFirstVisit));

        persistence.resetForTest();
        UserEntity visitAgain = persistence.find(UserEntity.class, id.getID());

        // Visit again
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            visitAgain.updateVisitedTimestamps();
            tx.commit();
        }

        persistence.resetForVerification();
        UserEntity verifyVisitedAgain = persistence.find(UserEntity.class, id.getID());

        long millisAfterSecondVisit = System.currentTimeMillis();
        long actualMillisLastVisit = verifyVisitedAgain.getLastVisit().getTime();
        long actualMillisSecondVisit = verifyVisitedAgain.getCurrentVisit().getTime();

        assertThat(actualMillisLastVisit, is(actualMillisFirstVisit));
        assertThat(actualMillisLastVisit, lessThan(actualMillisSecondVisit));
        assertThat(actualMillisSecondVisit, lessThanOrEqualTo(millisAfterSecondVisit));
    }

    @Test
    public void testUpdateLastFailureTimestamp_happyPath() throws Exception {
        OrganizationEntity organization = new OrganizationEntity("Org");
        PersonEntity person = new PersonEntity("Epic F. Ail", organization);
        UserEntity neverFailed = new UserEntity("NeverFailed", person);

        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            tx.persist(organization);
            tx.persist(person);
            tx.persist(neverFailed);
            tx.commit();
        }
        User.UserID id = neverFailed.getUserID();

        persistence.resetForVerification();
        UserEntity verifyNeverFailed = persistence.find(UserEntity.class, id.getID());

        assertThat(verifyNeverFailed.getLastFailure(), nullValue());

        persistence.resetForTest();
        UserEntity failMe = persistence.find(UserEntity.class, id.getID());

        //SUT
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            failMe.updateLastFailureTimestamp();
            tx.commit();
        }

        persistence.resetForVerification();
        UserEntity verifyFailed = persistence.find(UserEntity.class, id.getID());

        long millisAfterFirstVisit = System.currentTimeMillis();
        long actualMillis = verifyFailed.getLastFailure().getTime();
        assertThat(actualMillis, lessThanOrEqualTo(millisAfterFirstVisit));
    }

}
