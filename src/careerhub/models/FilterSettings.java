package careerhub.models;

/**
 * Represents a set of filter preferences used when viewing or sorting lists of
 * internship opportunities. These filter fields mirror the filtering criteria
 * required by the assignment: status, major, internship level, and sorting
 * preference (e.g., alphabetical or by closing date).
 *
 * <p>This class acts as a simple data container, allowing menus or user profiles
 * to remember and restore the last-used filter settings for consistent user
 * experience.</p>
 */
public class FilterSettings {

    /** Filter by internship status (e.g., Approved, Pending, Filled). */
    private String statusFilter;

    /** Filter by preferred major of interns (e.g., CSC, EEE, MAE). */
    private String majorFilter;

    /** Filter by internship level (Basic, Intermediate, Advanced). */
    private String levelFilter;

    /** Sorting order used when listing internships (e.g., alphabetical). */
    private String sortOrder;

    /**
     * Constructs a FilterSettings object with the indicated criteria.
     *
     * @param status the status filter value
     * @param major  the major filter value
     * @param level  the internship level filter value
     * @param sort   the sort order to apply
     */
    public FilterSettings(String status, String major, String level, String sort) {
        this.statusFilter = status;
        this.majorFilter = major;
        this.levelFilter = level;
        this.sortOrder = sort;
    }

    /**
     * Retrieves the currently selected status filter.
     *
     * @return the status filter string
     */
    public String getStatusFilter() {
        return statusFilter;
    }

    /**
     * Updates the status filter used when viewing internship listings.
     *
     * @param statusFilter the new status filter value
     */
    public void setStatusFilter(String statusFilter) {
        this.statusFilter = statusFilter;
    }

    /**
     * Retrieves the currently selected major filter.
     *
     * @return the major filter string
     */
    public String getMajorFilter() {
        return majorFilter;
    }

    /**
     * Updates the major filter used when viewing internship opportunities.
     *
     * @param majorFilter the new major filter value
     */
    public void setMajorFilter(String majorFilter) {
        this.majorFilter = majorFilter;
    }

    /**
     * Retrieves the currently selected internship level filter.
     *
     * @return the internship level filter string
     */
    public String getLevelFilter() {
        return levelFilter;
    }

    /**
     * Updates the internship level filter used when listing opportunities.
     *
     * @param levelFilter the new internship level filter value
     */
    public void setLevelFilter(String levelFilter) {
        this.levelFilter = levelFilter;
    }

    /**
     * Retrieves the sorting order applied to the internship list.
     *
     * @return the sort order string
     */
    public String getSortOrder() {
        return sortOrder;
    }

    /**
     * Updates the sorting order used for internship listings.
     *
     * @param sortOrder the new sort order value
     */
    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
