package se.waymark.orm.jpa;

import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import static se.waymark.orm.jpa.InMemoryPersistenceProperties.getInMemoryPersistenceProperties;

public class InMemoryPersistence implements AutoCloseable {

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    public InMemoryPersistence() throws Exception {

        // Make hbm2ddl shut up...
        Logger.getLogger("org.hibernate.tool.hbm2ddl").setLevel(Level.FATAL);

        Properties override = getInMemoryPersistenceProperties();

        entityManagerFactory = Persistence.createEntityManagerFactory(LimaPersistenceUnit.PERSISTENCE_UNIT_NAME, override);
        entityManager = entityManagerFactory.createEntityManager();
    }

    @Override
    public void close() throws Exception {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }

    private void reset() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.clear();
            entityManager.close();
        }
        entityManager = entityManagerFactory.createEntityManager();
    }

    public EntityManager resetForTest() {
        reset();
        return entityManager;
    }

    public EntityManager resetForVerification() {
        reset();
        return entityManager;
    }

    public <T> T find(Class<T> entityClass, Object primaryKey) {
        return entityManager.find(entityClass, primaryKey);
    }

    public Tx beginTx() {
        return new Tx();
    }

    public class Tx implements AutoCloseable {
        private EntityTransaction tx;
        private boolean committed = false;

        public Tx() {
            tx = InMemoryPersistence.this.entityManager.getTransaction();
            tx.begin();
        }

        public void persist(Object entity) {
            entityManager.persist(entity);
        }

        public void remove(Object entity) {
            entityManager.remove(entity);
        }

        public <T> T find(Class<T> entityClass, Object primaryKey) {
            return InMemoryPersistence.this.find(entityClass, primaryKey);
        }

        public void flush() {
            entityManager.flush();
        }

        public void commit() {
            tx.commit();
            committed = true;
        }

        @Override
        public void close() throws Exception {
            if (tx.isActive()) {
                if (!committed && !tx.getRollbackOnly()) {
                    tx.commit();
                } else {
                    tx.rollback();
                }
            }
        }
    }
}
