package careerhub.models;

/**
 * Represents a Career Center Staff user in the Internship Management System.
 * Staff members are responsible for approving or rejecting Company Representative
 * accounts, reviewing internship opportunities, and processing student withdrawal
 * requests. They also have access to all system-wide reporting and filtering tools.
 *
 * <p>This class stores the staff member’s department and their last-used filter
 * preferences so that menu navigation can restore the user's previous filtering
 * context.</p>
 */
public class Staff extends User {

    /** Department the staff member belongs to. */
    private String department;

    // -------------------- Saved Filter Preferences --------------------

    /** Last selected status filter applied by this staff user. */
    private String lastStatusFilter = "";

    /** Last selected major filter applied. */
    private String lastMajorFilter = "";

    /** Last selected internship level filter applied. */
    private String lastLevelFilter = "";

    /** Last selected company-name filter applied. */
    private String lastCompanyFilter = "";

    /** Last selected visibility filter applied. */
    private String lastVisibilityFilter = "";

    /** Last selected closing-date filter applied. */
    private String lastCloseFilter = "";

    /**
     * Constructs a new Staff user with department information.
     *
     * @param id         staff login ID (NTU account)
     * @param name       staff member’s name
     * @param password   default or updated password
     * @param department the staff member’s department
     */
    public Staff(String id, String name, String password, String department) {
        super(id, name, password);
        this.department = department;
    }

    /**
     * Retrieves the staff member’s department.
     *
     * @return department name
     */
    public String getDepartment(){ return department; }

    /**
     * Identifies the user role type for system routing and menu construction.
     *
     * @return "Staff"
     */
    @Override
    public String getType() { return "Staff"; }

    // -------------------- Filter Getters & Setters --------------------

    /** @return the last applied status filter */
    public String getLastStatusFilter() { return lastStatusFilter; }

    /**
     * Updates the last used status filter.
     *
     * @param v status filter value
     */
    public void setLastStatusFilter(String v) { lastStatusFilter = v; }

    /** @return the last applied major filter */
    public String getLastMajorFilter() { return lastMajorFilter; }

    /**
     * Updates the last used major filter.
     *
     * @param v major filter value
     */
    public void setLastMajorFilter(String v) { lastMajorFilter = v; }

    /** @return the last applied level filter */
    public String getLastLevelFilter() { return lastLevelFilter; }

    /**
     * Updates the last used internship level filter.
     *
     * @param v level filter value
     */
    public void setLastLevelFilter(String v) { lastLevelFilter = v; }

    /** @return the last applied company-name filter */
    public String getLastCompanyFilter() { return lastCompanyFilter; }

    /**
     * Updates the last used company filter.
     *
     * @param v company filter value
     */
    public void setLastCompanyFilter(String v) { lastCompanyFilter = v; }

    /** @return the last applied visibility filter */
    public String getLastVisibilityFilter() { return lastVisibilityFilter; }

    /**
     * Updates the last used visibility filter.
     *
     * @param v visibility filter value
     */
    public void setLastVisibilityFilter(String v) { lastVisibilityFilter = v; }

    /** @return the last applied closing-date filter */
    public String getLastCloseFilter() { return lastCloseFilter; }

    /**
     * Updates the last used closing-date filter.
     *
     * @param v closing-date filter value
     */
    public void setLastCloseFilter(String v) { lastCloseFilter = v; }
}
