package se.waymark.orm.jpa;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;
import javax.validation.constraints.Size;
import org.joda.time.DateTime;
import se.waymark.orm.model.Base;

@MappedSuperclass
public abstract class BaseMappedSuperclass implements Base {

    protected static final String USERTYPE_DATETIME = "org.jadira.usertype.dateandtime.joda.PersistentDateTime";
    protected static final String USERTYPE_LOCAL_DATE = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate";
    protected static final String USERTYPE_LOCAL_TIME = "org.jadira.usertype.dateandtime.joda.PersistentLocalTime";

    @org.hibernate.annotations.Type(type= USERTYPE_DATETIME)
    private DateTime created;

    @Column(name = "Creator")
    @Size(max = 20)
    private String createdBy;

    @SuppressWarnings("UnusedDeclaration") // annotated with @Version => updated automatically
    @Version
    @org.hibernate.annotations.Type(type=USERTYPE_DATETIME)
    private DateTime lastWritten;

    @Size(max = 20)
    private String lastWrittenBy;

    @Basic(optional = true)
    @Column(name = "IsDeleted", columnDefinition = "NUMBER", precision = 1, scale = 0)
    private Boolean deleted;


    protected BaseMappedSuperclass() {
    }

    @PrePersist
    void prePersist() {
        this.created = DateTime.now();
        this.createdBy = System.getenv("USER");
        if(this.deleted == null) {
            this.deleted = false;
        }
    }

    @PreUpdate
    void preUpdate() {
        // Timestamp "lastWritten" annotated with @Version => updated automatically
        this.lastWrittenBy = System.getenv("USER");
    }

    @Override
    public DateTime getCreated() {
        return created;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public DateTime getLastWritten() {
        return lastWritten;
    }

    @Override
    public String getLastWrittenBy() {
        return lastWrittenBy;
    }

    @Override
    public boolean isDeleted() {
        return deleted != null ? deleted.booleanValue() : false;
    }


    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
