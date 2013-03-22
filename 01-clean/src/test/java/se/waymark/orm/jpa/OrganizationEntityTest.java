package se.waymark.orm.jpa;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se.waymark.orm.model.Organization;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OrganizationEntityTest {

    private InMemoryPersistence persistence;

    public OrganizationEntityTest() {
        super();
        // Mute SqlExceptionHelper log
        Logger.getLogger(SqlExceptionHelper.class).setLevel(Level.FATAL);
    }

    @Before
    public void setUp() throws Exception {
        persistence = new InMemoryPersistence();

        persistence.resetForTest();
    }

    @After
    public void tearDown() throws Exception {
        persistence.close();
    }

    @Test
    public void testPersist_happyPath() throws Exception {
        String orgName = "an OrgName";
        String orgDescription = "some description";
        OrganizationEntity mother = new OrganizationEntity("Mama");
        PersonEntity manager = new PersonEntity("Mama Boss", mother);

        OrganizationEntity toPersist = new OrganizationEntity(orgName);
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
        assertThat(reRead.getOrganizationDescription(), is(orgDescription));
        assertThat(reRead.getMotherOrganization(), is(mother));
        assertThat(reRead.getOrganizationManager(), is(manager));
    }

}
