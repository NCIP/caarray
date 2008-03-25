package gov.nih.nci.caarray.web.action.registration;

/**
 * @author Akhil Bhaskar (Amentra, Inc.)
 *
 */
public enum UserRole {
    /** System administration role. */
    SYSTEM_ADMINISTRATOR (1, "System Administrator", "SystemAdministrator"),
    /** PI role. */
    PRINCIPAL_INVESTIGATOR (2, "Principal Investigator", "PrincipalInvestigator"),
    /** Lab admin role. */
    LAB_ADMINISTRATOR (3, "Lab Administrator", "LabAdministrator"),
    /** Lab scientist role. */
    LAB_SCIENTIST (4, "Lab Scientist", "LabScientist"),
    /** Biostatisticial role. */
    BIOSTATISTICIAN (5, "Biostatistician", "Biostatistician");

    /** the id of the role. */
    private final int id;
    /** the name of the role. */
    private final String name;
    /** name of db role. */
    private final String roleName;

    /**
     * the constructor.
     */
    UserRole(int id, String name, String roleName) {
        this.id = id;
        this.name = name;
        this.roleName = roleName;
    }

    /**
     * @return db id
     */
    public int getId() {
        return this.id;
    }

    /**
     * @return display name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the db role name
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * @return variable, in session scope.
     */
    public String getSessionVar() {
        return "is" + getRoleName();
    }
}
