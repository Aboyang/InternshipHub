package careerhub.models;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Represents a Company Representative user in the Internship Management System.
 * A Company Representative can create internship opportunities (up to a maximum
 * of five), review student applications, approve or reject them, and toggle the
 * visibility of their opportunities.
 *
 * <p>This class stores profile information (company name, department, position),
 * approval status from Career Center Staff, and a list of internship IDs created
 * by this representative. It also tracks the representative’s last-used filter
 * settings for convenience when navigating menus.</p>
 */
public class CompanyRep extends User {

    /** Name of the company this representative belongs to. */
    private String companyName;

    /** Department within the company. */
    private String department;

    /** Position or job title held by the representative. */
    private String position;

    /**
     * Indicates whether this representative has been approved by
     * Career Center Staff and is allowed to log into the system.
     */
    private boolean approved = false;

    /**
     * List of internship opportunity IDs created by this representative.
     * Maximum size is enforced externally (at most 5 opportunities).
     */
    private List<String> internships = new ArrayList<>();

    // ---------------- Saved Filter Preferences ----------------

    /** Last selected status filter applied in internship listings. */
    private String lastStatusFilter = "";

    /** Last selected preferred-major filter applied. */
    private String lastMajorFilter = "";

    /** Last selected internship-level filter applied. */
    private String lastLevelFilter = "";

    /** Last selected visibility filter applied. */
    private String lastVisibilityFilter = "";

    /** Last selected company-name filter applied. */
    private String lastCompanyFilter = "";

    /** Last selected closing-date filter applied. */
    private String lastCloseFilter = "";

    /**
     * Constructs a new CompanyRep user with company details.
     *
     * @param id          the representative's login ID (company email)
     * @param name        the representative's name
     * @param password    the login password
     * @param companyName the name of the associated company
     * @param department  the representative's department
     * @param position    the representative's role or job title
     */
    public CompanyRep(String id, String name, String password,
                      String companyName, String department, String position) {
        super(id, name, password);
        this.companyName = companyName;
        this.department = department;
        this.position = position;
    }

    /**
     * Removes duplicate internship IDs from the internal list.
     * This is primarily used during data loading to ensure consistency
     * when merging records from persistent storage.
     */
    public void dedupeInternships() {
        this.internships = new ArrayList<>(new LinkedHashSet<>(this.internships));
    }

    // ---------------- Getters ----------------

    /**
     * Retrieves the company name of this representative.
     *
     * @return the company name
     */
    public String getCompanyName() { return companyName; }

    /**
     * Retrieves the department of this representative.
     *
     * @return the department name
     */
    public String getDepartment() { return department; }

    /**
     * Retrieves the job position or title of this representative.
     *
     * @return the position title
     */
    public String getPosition() { return position; }

    /**
     * Indicates whether this representative's account has been approved
     * by Career Center Staff.
     *
     * @return true if approved, false otherwise
     */
    public boolean isApproved() { return approved; }

    /**
     * Sets the approval status of this representative.
     *
     * @param v true to approve the representative; false to revoke approval
     */
    public void setApproved(boolean v) { approved = v; }

    /**
     * Determines whether this representative is allowed to create more internship
     * opportunities. According to assignment rules, each Company Representative
     * may create at most five internship listings.
     *
     * @return true if fewer than five internships have been created; false otherwise
     */
    public boolean canCreateMoreInternships() {
        return internships.size() < 5;
    }

    /**
     * Adds a new internship ID to the list of opportunities created by this
     * representative. Duplicate IDs are ignored.
     *
     * @param id the ID of the newly created internship
     */
    public void addCreatedInternship(String id) {
        if (!internships.contains(id)) {
            internships.add(id);
        }
    }

    /**
     * Removes a created internship ID from this representative's list.
     *
     * @param id the ID of the internship to remove
     */
    public void removeCreatedInternship(String id) {
        internships.remove(id);
    }

    /**
     * Retrieves the list of internship IDs created by this representative.
     *
     * @return a list of internship IDs
     */
    public List<String> getCreatedInternshipIds() { return internships; }

    /**
     * Returns the type identifier used by the system to distinguish user roles.
     *
     * @return "CompanyRep"
     */
    @Override
    public String getType() { return "CompanyRep"; }

    // ---------------- Filter Getters ----------------

    /** @return the last applied status filter */
    public String getLastStatusFilter() { return lastStatusFilter; }

    /** @return the last applied preferred-major filter */
    public String getLastMajorFilter() { return lastMajorFilter; }

    /** @return the last applied internship-level filter */
    public String getLastLevelFilter() { return lastLevelFilter; }

    /** @return the last applied visibility filter */
    public String getLastVisibilityFilter() { return lastVisibilityFilter; }

    /** @return the last applied company-name filter */
    public String getLastCompanyFilter() { return lastCompanyFilter; }

    /** @return the last applied closing-date filter */
    public String getLastCloseFilter() { return lastCloseFilter; }

    // ---------------- Filter Setters ----------------

    /**
     * Sets the last used status filter.
     *
     * @param s the status filter string
     */
    public void setLastStatusFilter(String s) { lastStatusFilter = s; }

    /**
     * Sets the last used preferred-major filter.
     *
     * @param s the major filter string
     */
    public void setLastMajorFilter(String s) { lastMajorFilter = s; }

    /**
     * Sets the last used internship-level filter.
     *
     * @param s the level filter string
     */
    public void setLastLevelFilter(String s) { lastLevelFilter = s; }

    /**
     * Sets the last used visibility filter.
     *
     * @param s the visibility filter string
     */
    public void setLastVisibilityFilter(String s) { lastVisibilityFilter = s; }

    /**
     * Sets the last used company-name filter.
     *
     * @param s the company filter string
     */
    public void setLastCompanyFilter(String s) { lastCompanyFilter = s; }

    /**
     * Sets the last used closing-date filter.
     *
     * @param s the closing-date filter string
     */
    public void setLastCloseFilter(String s) { lastCloseFilter = s; }
}
