package se.waymark.orm.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Note: <code>Role.*_STRING</code> constants (for use in annotations etc)
 * <em>must</em> correspond to enum names!
 *
 * Note: <code>Role.limaRoleID</code> <em>must</em> correspond to database <code>LimaRole.LimaRoleID</code>.
 */
public enum Role {
    READ_ONLY(1, "", 0),
    ANALYST(34, "Analyst", 4),
    APPLICANT(39, "Applicant", 1),
    CONTROLLER(38, "Controller", 128),
    // TODO: Remove safely (users with role exists in db)
    // no longer used, but keep here for reference:
    DECISION_MAKER(35, "Decision Maker", 16),
    LIMA_ADMINISTRATOR(26, "Lima Administrator", 64),
    MEETING_ADMINISTRATOR(36, "Meeting Administrator", 8),
    PREPARATOR(33, "Preparator", 2),
    SYSTEM_MANAGER(40, "System Manager", 256),
    VERIFIER(37, "Verifier", 32);

    public static final String READ_ONLY_STRING = "READ_ONLY";
    public static final String ANALYST_STRING = "ANALYST";
    public static final String APPLICANT_STRING = "APPLICANT";
    public static final String CONTROLLER_STRING = "CONTROLLER";
    public static final String LIMA_ADMINISTRATOR_STRING = "LIMA_ADMINISTRATOR";
    public static final String MEETING_ADMINISTRATOR_STRING = "MEETING_ADMINISTRATOR";
    public static final String PREPARATOR_STRING = "PREPARATOR";
    public static final String SYSTEM_MANAGER_STRING = "SYSTEM_MANAGER";
    public static final String VERIFIER_STRING = "VERIFIER";

    private final long limaRoleID;
    private final String legacyName;
    private final int legacyBitMask;


    Role(long limaRoleID, String legacyName, int legacyBitMask) {
        this.limaRoleID = limaRoleID;

        this.legacyName = legacyName;
        this.legacyBitMask = legacyBitMask;
    }

    public long getLimaRoleID() {
        return limaRoleID;
    }

    public String getLegacyName() {
        return legacyName;
    }

    public int getLegacyBitMask() {
        return legacyBitMask;
    }

    private static Map<Long, Role> idToRoleEnumMap;

    static {
        HashMap<Long, Role> map = new HashMap<>();
        for (Role value : Role.values()) {
            map.put(value.getLimaRoleID(), value);
        }
        idToRoleEnumMap = Collections.unmodifiableMap(map);
    }

    public static Role findById(long limaRoleID) {
        return idToRoleEnumMap.get(limaRoleID);
    }
}
