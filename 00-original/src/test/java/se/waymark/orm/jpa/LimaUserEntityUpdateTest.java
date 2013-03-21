package se.waymark.orm.jpa;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se.waymark.orm.model.LimaUser;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

public class LimaUserEntityUpdateTest {

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
        CountryEntity residence = new CountryEntity(4, "LL", "Country", "Land");
        OrganizationEntity organization = new OrganizationEntity("Org", residence);
        PersonEntity person = new PersonEntity("A.N. Oob", "Oob", "A.N.", organization);
        LimaUserEntity neverVisited = new LimaUserEntity("NeverVisited", person);

        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            tx.persist(residence);
            tx.persist(organization);
            tx.persist(person);
            tx.persist(neverVisited);
            tx.commit();
        }
        LimaUser.LimaUserID id = neverVisited.getLimaUserID();

        persistence.resetForVerification();
        LimaUserEntity verifyNeverVisited = persistence.find(LimaUserEntity.class, id.getID());

        assertThat(verifyNeverVisited.getLastVisit(), nullValue());
        assertThat(verifyNeverVisited.getCurrentVisit(), nullValue());

        persistence.resetForTest();
        LimaUserEntity visitOnce = persistence.find(LimaUserEntity.class, id.getID());

        // Visit once
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            visitOnce.updateVisitedTimestamps();
            tx.commit();
        }

        persistence.resetForVerification();
        LimaUserEntity verifyVisitedOnce = persistence.find(LimaUserEntity.class, id.getID());

        long millisAfterFirstVisit = System.currentTimeMillis();
        long actualMillisFirstVisit = verifyVisitedOnce.getCurrentVisit().getMillis();
        assertThat(verifyVisitedOnce.getLastVisit(), nullValue());
        assertThat(actualMillisFirstVisit, lessThanOrEqualTo(millisAfterFirstVisit));

        persistence.resetForTest();
        LimaUserEntity visitAgain = persistence.find(LimaUserEntity.class, id.getID());

        // Visit again
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            visitAgain.updateVisitedTimestamps();
            tx.commit();
        }

        persistence.resetForVerification();
        LimaUserEntity verifyVisitedAgain = persistence.find(LimaUserEntity.class, id.getID());

        long millisAfterSecondVisit = System.currentTimeMillis();
        long actualMillisLastVisit = verifyVisitedAgain.getLastVisit().getMillis();
        long actualMillisSecondVisit = verifyVisitedAgain.getCurrentVisit().getMillis();

        assertThat(actualMillisLastVisit, is(actualMillisFirstVisit));
        assertThat(actualMillisLastVisit, lessThan(actualMillisSecondVisit));
        assertThat(actualMillisSecondVisit, lessThanOrEqualTo(millisAfterSecondVisit));
    }

    @Test
    public void testUpdateLastFailureTimestamp_happyPath() throws Exception {
        CountryEntity residence = new CountryEntity(4, "LL", "Country", "Land");
        OrganizationEntity organization = new OrganizationEntity("Org", residence);
        PersonEntity person = new PersonEntity("Epic F. Ail", "Ail", "Epic F.", organization);
        LimaUserEntity neverFailed = new LimaUserEntity("NeverFailed", person);

        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            tx.persist(residence);
            tx.persist(organization);
            tx.persist(person);
            tx.persist(neverFailed);
            tx.commit();
        }
        LimaUser.LimaUserID id = neverFailed.getLimaUserID();

        persistence.resetForVerification();
        LimaUserEntity verifyNeverFailed = persistence.find(LimaUserEntity.class, id.getID());

        assertThat(verifyNeverFailed.getLastFailure(), nullValue());

        persistence.resetForTest();
        LimaUserEntity failMe = persistence.find(LimaUserEntity.class, id.getID());

        //SUT
        try (InMemoryPersistence.Tx tx = persistence.beginTx()) {
            failMe.updateLastFailureTimestamp();
            tx.commit();
        }

        persistence.resetForVerification();
        LimaUserEntity verifyFailed = persistence.find(LimaUserEntity.class, id.getID());

        long millisAfterFirstVisit = System.currentTimeMillis();
        long actualMillis = verifyFailed.getLastFailure().getMillis();
        assertThat(actualMillis, lessThanOrEqualTo(millisAfterFirstVisit));
    }

}
