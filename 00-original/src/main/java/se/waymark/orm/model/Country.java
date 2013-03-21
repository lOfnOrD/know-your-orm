package se.waymark.orm.model;

import java.util.Set;
import org.joda.time.LocalDate;
import se.waymark.orm.model.fields.GlobalID;

public interface Country extends LimaBase {

    CountryID getCountryID();

    int getSwedbankCountryId();

    String getSwedbankCountryCode();

    String getNameEnglish();

    String getNameSwedish();

    String getCountryDescription();

    String getInternalRating();

    LocalDate getAnalysisDate();

    Person getCountryManager();

    boolean isCaseByCase();

    Set<? extends Person> getAssistantCountryManagers();

    interface CountryID extends GlobalID {
    }
}
