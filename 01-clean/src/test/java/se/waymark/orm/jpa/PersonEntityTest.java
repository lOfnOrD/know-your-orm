package se.waymark.orm.jpa;

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

public class PersonEntityTest {

    public static final String FULL_NAME = "Full Name";
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
    public void testPersist_happyPath() throws Exception {
        OrganizationEntity organization = findOrg(orgID);
        String email = "anyone@example.com";

        PersonEntity toPersist = new PersonEntity(FULL_NAME, organization);
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
        assertThat(reRead.getOrganization(), is(organization));
        assertThat(reRead.getEmail(), is(email));
    }

    private OrganizationEntity findOrg(Organization.OrganizationID id) {
        return persistence.find(OrganizationEntity.class, id.getID());
    }
}
