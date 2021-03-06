import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se.waymark.orm.jpa.OrganizationEntity;
import se.waymark.orm.jpa.PersistenceUnit;
import se.waymark.orm.jpa.PersonEntity;
import se.waymark.orm.jpa.RoleEntity;
import se.waymark.orm.jpa.UserEntity;
import se.waymark.orm.model.User;
import static se.waymark.orm.jpa.InMemoryPersistenceProperties.getInMemoryPersistenceProperties;

public class DemoTest {

    private EntityManagerFactory entityManagerFactory;
    private EntityManager emTest;
    private User.UserID userID;

    @Before
    public void setUp() throws Exception {
        Properties override = getInMemoryPersistenceProperties();
        Logger.getLogger("org.hibernate.tool.hbm2ddl").setLevel(Level.FATAL);
        entityManagerFactory = Persistence.createEntityManagerFactory(PersistenceUnit.PERSISTENCE_UNIT_NAME, override);

        EntityManager emSetup = entityManagerFactory.createEntityManager();
        emSetup.getTransaction().begin();


        OrganizationEntity organization = new OrganizationEntity("Waymark");
        emSetup.persist(organization);

        PersonEntity person = new PersonEntity("Pelle Elgh", organization);
        emSetup.persist(person);

        UserEntity user = new UserEntity("pelle", person);

        RoleEntity role1 = new RoleEntity(1, "master");
        RoleEntity role2 = new RoleEntity(2, "servant");

        user.getRoles().add(role1);
        user.getRoles().add(role2);

        emSetup.persist(user);

        emSetup.getTransaction().commit();

        userID = user.getUserID();
        emSetup.close();

        emTest = entityManagerFactory.createEntityManager();
    }

    @Test
    public void testSomeSQL() throws Exception {
        System.out.println("*** BEGIN find()");
        UserEntity user = emTest.find(UserEntity.class, userID.getID());
        System.out.println("*** END find()");

        System.out.println("*** BEGIN toString()");
        String s = user.toString();
        System.out.println("*** END toString()");

        System.out.println("Found: " + s);
    }

    @Test
    public void testSomeJPQL() throws Exception {
        Query query = emTest.createQuery(
                "select u from se.waymark.orm.jpa.UserEntity u " +
                        "join fetch u.person p " +
//                        "join fetch p.organization " +
                        "join fetch u.roles " +
                        "where u.userName = :usr")
                .setParameter("usr", "pelle");

        System.out.println("*** BEGIN JPQL");
        UserEntity user = (UserEntity) query.getSingleResult();
        System.out.println("*** END JPQL");

        System.out.println("*** BEGIN toString()");
        String s = user.toString();
        System.out.println("*** END toString()");

        System.out.println("Found: " + s);
    }

    @Test
    public void testNamedQuery() throws Exception {
        Query query = emTest.createNamedQuery("findByUserName")
                .setParameter("usr", "pelle");

        System.out.println("*** BEGIN named JPQL query");
        UserEntity user = (UserEntity) query.getSingleResult();
        System.out.println("*** END named JPQL query");

        System.out.println("*** BEGIN toString()");
        String s = user.toString();
        System.out.println("*** END toString()");

        System.out.println("Found: " + s);
    }


    @After
    public void tearDown() throws Exception {
        entityManagerFactory.close();
    }
}
