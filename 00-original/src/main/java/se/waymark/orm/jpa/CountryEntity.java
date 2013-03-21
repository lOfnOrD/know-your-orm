package se.waymark.orm.jpa;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.joda.time.LocalDate;
import se.waymark.orm.jpa.fields.GlobalIDImpl;
import se.waymark.orm.model.Country;

@Entity
@Table(name = "Country")
@Indexed
public class CountryEntity extends LimaBaseMappedSuperclass implements Country {

    // TODO: DB columns not implemented: MOODYS, ANNUALREVIEWDATE. Are these
    // used at all?

    @Id
    @GeneratedValue(generator = GlobalIDImpl.GENERATOR_NAME)
    private long countryID;

    @Basic(optional = false)
    @Column(unique = true, name ="BNKT_LAND_LAND_ID", columnDefinition = "number", precision = 8, scale = 0)
    @NotNull
    @Field
    private int swedbankCountryId;

    @Basic(optional = false)
    @Column(unique = true, name = "FSPA_CountryCode", length = 20)
    @NotNull
    @Size(min = 2, max = 2) // TODO: field is VARCHAR2(20)..
    @Field
    private String swedbankCountryCode;

    @Basic(optional = false)
    @Column(unique = true)
    @NotNull
    @Size(min = 1, max = 255)
    @Field
    private String nameEnglish;

    @Basic(optional = false)
    @Column(unique = true)
    @NotNull
    @Size(min = 1, max = 255)
    @Field
    private String nameSwedish;

    @Size(max = 2000)
    private String countryDescription;

    @Column(name = "INTERN_LANDRATING")
    @Size(max = 64)
    private String internalRating;

    @Column(name = "LANDANALYSDATUM")
    @org.hibernate.annotations.Type(type=USERTYPE_LOCAL_DATE)
    private LocalDate analysisDate;

    @ManyToOne(optional = true) // cannot create first country without this being optional
    @JoinColumn(name = "PersonID")
    private PersonEntity countryManager;

    @Basic(optional = true)
    @Column(name = "IsCaseByCase", columnDefinition = "NUMBER", precision = 1, scale = 0)
    private Boolean caseByCase;

    @ManyToMany
    //TODO: IsSecondCountryManager //used?
    @JoinTable(
            name="Country_AssistantManager",
            joinColumns=@JoinColumn(name="CountryID"),
            inverseJoinColumns=@JoinColumn(name="PersonID")
    )
    private Set<PersonEntity> assistantCountryManagers;


    public CountryEntity(int swedbankCountryId, String swedbankCountryCode, String nameEnglish, String nameSwedish) {
        this.swedbankCountryId = swedbankCountryId;
        this.swedbankCountryCode = swedbankCountryCode;
        this.nameEnglish = nameEnglish;
        this.nameSwedish = nameSwedish;
        this.assistantCountryManagers = new HashSet<>();
    }

    protected CountryEntity() {
    }

    @Override
    public CountryID getCountryID() {
        return new CountryIDImpl();
    }

    @Override
    public int getSwedbankCountryId() {
        return swedbankCountryId;
    }

    @Override
    public String getSwedbankCountryCode() {
        return swedbankCountryCode;
    }

    @Override
    public String getNameEnglish() {
        return nameEnglish;
    }

    @Override
    public String getNameSwedish() {
        return nameSwedish;
    }

    @Override
    public String getCountryDescription() {
        return countryDescription;
    }

    @Override
    public String getInternalRating() {
        return internalRating;
    }

    @Override
    public LocalDate getAnalysisDate() {
        return analysisDate;
    }

    @Override
    public PersonEntity getCountryManager() {
        return countryManager;
    }

    @Override
    public Set<PersonEntity> getAssistantCountryManagers() {
        return assistantCountryManagers;
    }

    @Override
    public boolean isCaseByCase() {
        return caseByCase != null ? caseByCase.booleanValue() : false;
    }

    public void setSwedbankCountryId(int swedbankCountryId) {
        this.swedbankCountryId = swedbankCountryId;
    }

    public void setSwedbankCountryCode(String swedbankCountryCode) {
        this.swedbankCountryCode = swedbankCountryCode;
    }

    public void setNameEnglish(String nameEnglish) {
        this.nameEnglish = nameEnglish;
    }

    public void setNameSwedish(String nameSwedish) {
        this.nameSwedish = nameSwedish;
    }

    public void setAssistantCountryManagers(Set<PersonEntity> assistantCountryManagers) {
        this.assistantCountryManagers = assistantCountryManagers;
    }

    public void setCountryDescription(String countryDescription) {
        this.countryDescription = countryDescription;
    }

    public void setInternalRating(String internalCountryRating) {
        this.internalRating = internalCountryRating;
    }

    public void setAnalysisDate(LocalDate countryAnalysisDate) {
        this.analysisDate = countryAnalysisDate;
    }

    public void setCountryManager(PersonEntity countryManager) {
        this.countryManager = countryManager;
    }

    public void setCaseByCase(Boolean caseByCase) {
        this.caseByCase = caseByCase;
    }

    private class CountryIDImpl extends GlobalIDImpl implements CountryID {
        public CountryIDImpl() {
            super(CountryEntity.this.countryID);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Country)) return false;

        Country that = (Country) o;

        CountryID thatCountryID = that.getCountryID();
        return countryID == thatCountryID.getID();

    }

    @Override
    public int hashCode() {
        return (int) (countryID ^ (countryID >>> 32));
    }
}
