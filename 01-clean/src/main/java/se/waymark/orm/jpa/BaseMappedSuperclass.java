package se.waymark.orm.jpa;

import java.util.Date;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;
import se.waymark.orm.model.Base;

@MappedSuperclass
public abstract class BaseMappedSuperclass implements Base {

    private Date created;

    private String createdBy;

    @Version
    private Date lastWritten;

    private String lastWrittenBy;


    protected BaseMappedSuperclass() {
    }

    @PrePersist
    void prePersist() {
        this.created = new Date();
        this.createdBy = System.getenv("USER");
    }

    @PreUpdate
    void preUpdate() {
        // Timestamp "lastWritten" annotated with @Version => updated automatically
        this.lastWrittenBy = System.getenv("USER");
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public Date getLastWritten() {
        return lastWritten;
    }

    @Override
    public String getLastWrittenBy() {
        return lastWrittenBy;
    }


    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();
}
