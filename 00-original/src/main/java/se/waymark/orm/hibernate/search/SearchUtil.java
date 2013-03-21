package se.waymark.orm.hibernate.search;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import java.util.LinkedList;
import java.util.List;

public class SearchUtil {

    public static String[] getWildcardStrings(String searchString) {
        String[] empty = new String[0];
        if (searchString == null) {
            return empty;
        }
        // Default analyzer indexes lower-case keywords + ensure wildcard
        Iterable<String> split = Splitter.on(CharMatcher.WHITESPACE).trimResults().omitEmptyStrings().split(searchString.toLowerCase());

        List<String> result = new LinkedList<>();
        for (String needsWildcard : split) {
            result.add(needsWildcard + "*");
        }

        return result.toArray(empty);
    }

    // Util class
    private SearchUtil() {
    }
}
