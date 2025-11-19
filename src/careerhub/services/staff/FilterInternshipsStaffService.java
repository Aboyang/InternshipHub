package careerhub.services.staff;

import careerhub.models.Internship;
import careerhub.models.Staff;
import careerhub.storage.DataManager;

import java.util.List;
import java.util.Scanner;

/**
 * Service that enables Career Center Staff to apply filters and view
 * internships based on multiple criteria such as status, major, level,
 * company, visibility, and closing date.
 *
 * <p>This class allows Staff to:</p>
 * <ul>
 *     <li>Enter filtering parameters (or reuse previously entered ones)</li>
 *     <li>Persist the filter values inside {@link Staff} for convenience</li>
 *     <li>Retrieve filtered results from {@link DataManager#filterInternships}</li>
 *     <li>Display internships with full details in a staff-specific format</li>
 * </ul>
 *
 * <p>The filtering process does not modify internship records; it only adjusts
 * how they are displayed to the Staff user.</p>
 */
public class FilterInternshipsStaffService {

    /** DataManager used for querying internship records. */
    private final DataManager dm;

    /** Shared Scanner instance used for console input. */
    private final Scanner sc;

    /**
     * Creates a new filtering service for Staff users.
     *
     * @param dm DataManager used to retrieve internships
     * @param sc shared Scanner for reading user input
     */
    public FilterInternshipsStaffService(DataManager dm, Scanner sc) {
        this.dm = dm;
        this.sc = sc;
    }

    /**
     * Starts an interactive filtering process for Staff.
     *
     * <p>For each filter field, the Staff user may:</p>
     * <ul>
     *     <li>Enter a new filter value</li>
     *     <li>Press Enter to retain the previously saved filter value</li>
     * </ul>
     *
     * <p>Filters include:</p>
     * <ul>
     *     <li>Status</li>
     *     <li>Preferred major</li>
     *     <li>Internship level</li>
     *     <li>Company name</li>
     *     <li>Visibility ("visible" or "hidden")</li>
     *     <li>Closing date (exact, &lt;date, or &gt;date)</li>
     * </ul>
     *
     * <p>After collecting filter values, this method performs a query via
     * {@link DataManager#filterInternships(String, String, String, String, String, String)}
     * and displays all matching internships.</p>
     *
     * @param staff the Staff user whose filter preferences are used and updated
     */
    public void filter(Staff staff) {

        System.out.println("\nFilter internships (press Enter to keep previous):");

        System.out.print("Status [" + staff.getLastStatusFilter() + "]: ");
        String status = sc.nextLine().trim();
        if (!status.isBlank()) staff.setLastStatusFilter(status);

        System.out.print("Preferred Major [" + staff.getLastMajorFilter() + "]: ");
        String major = sc.nextLine().trim();
        if (!major.isBlank()) staff.setLastMajorFilter(major);

        System.out.print("Level [" + staff.getLastLevelFilter() + "]: ");
        String level = sc.nextLine().trim();
        if (!level.isBlank()) staff.setLastLevelFilter(level);

        System.out.print("Company Name [" + staff.getLastCompanyFilter() + "]: ");
        String company = sc.nextLine().trim();
        if (!company.isBlank()) staff.setLastCompanyFilter(company);

        System.out.print("Visibility [" + staff.getLastVisibilityFilter() + "]: ");
        String vis = sc.nextLine().trim();
        if (!vis.isBlank()) staff.setLastVisibilityFilter(vis);

        System.out.print("Closing Date [" + staff.getLastCloseFilter() + "]: ");
        String close = sc.nextLine().trim();
        if (!close.isBlank()) staff.setLastCloseFilter(close);

        List<Internship> list = dm.filterInternships(
                staff.getLastStatusFilter(),
                staff.getLastMajorFilter(),
                staff.getLastLevelFilter(),
                staff.getLastCompanyFilter(),
                staff.getLastVisibilityFilter(),
                staff.getLastCloseFilter()
        );

        if (list.isEmpty()) {
            System.out.println("No internships found.");
            return;
        }

        System.out.println("\nFiltered internships (Staff View):");
        for (Internship it : list) {
            System.out.printf(
                "%s | %s | %s | Level:%s | Major:%s | Open:%s | Close:%s | Slots:%d/%d | Status:%s | Visible:%s\n",
                it.getId(),
                it.getTitle(),
                it.getCompanyName(),
                it.getLevel(),
                it.getPreferredMajor(),
                it.getOpenDate(),
                it.getCloseDate(),
                it.getConfirmedCount(),
                it.getSlots(),
                it.getStatus(),
                it.isVisible()
            );
        }
    }
}
