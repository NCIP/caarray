package gov.nih.nci.caarray.web.action.registration;

/**
 * @author Akhil Bhaskar (Amentra, Inc.)
 *
 */
public enum UserRole
{
    SYSTEM_ADMINISTRATOR (1, "System Administrator"),
    PRINCIPAL_INVESTIGATOR (2, "Principal Investigator"),
    LAB_ADMINISTRATOR (3, "Lab Administrator"),
    LAB_SCIENTIST (4, "Lab Scientist"),
    BIOSTATISTICIAN (5, "Biostatistician");

    /** the id of the role */
    private final int id;
    /** the name of the role */
    private final String name;

    /**
     * the constructor
     */
    UserRole(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
