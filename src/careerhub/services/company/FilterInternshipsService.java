package careerhub.services.company;

import careerhub.models.CompanyRep;
import careerhub.models.Internship;
import careerhub.storage.DataManager;

import java.util.List;
import java.util.Scanner;

/**
 * Service class that handles internship filtering for Company Representatives.
 *
 * <p>This class belongs to the Control layer and retrieves filter preferences
 * from the {@link CompanyRep} object, allowing persistent filtering across
 * multiple menu sessions. It prompts the user for updated filter criteria
 * and delegates the actual filtering logic to the {@link DataManager}.</p>
 *
 * <p>Supported filter fields:</p>
 * <ul>
 *     <li>Status</li>
 *     <li>Preferred major</li>
 *     <li>Level (Basic / Intermediate / Advanced)</li>
 *     <li>Company name</li>
 *     <li>Visibility (true / false)</li>
 *     <li>Closing date (YYYY-MM-DD)</li>
 * </ul>
 *
 * <p>Any field left blank continues using the previously applied filter stored
 * inside the CompanyRep object.</p>
 */
public class FilterInternshipsService {

    /** Shared DataManager used to query internship records. */
    private final DataManager dm;

    /** Shared Scanner instance used for console input. */
    private final Scanner sc;

    /**
     * Creates the filtering service with injected dependencies.
     *
     * @param dm the shared DataManager instance
     * @param sc the shared Scanner instance
     */
    public FilterInternshipsService(DataManager dm, Scanner sc) {
        this.dm = dm;
        this.sc = sc;
    }

    /**
     * Executes the full filtering workflow for a Company Representative.
     *
     * <p>The method prompts the user for filter values. Any blank input will
     * preserve the previously saved filter stored in the representative's
     * attributes. After updating the filters, the method calls
     * {@link DataManager#filterInternships(String, String, String, String, String, String)}
     * to retrieve matching internships.</p>
     *
     * <p>If no results are found, a message is displayed. Otherwise,
     * the matching internships are printed in a formatted list.</p>
     *
     * @param rep the Company Representative requesting the filter operation
     */
    public void filter(CompanyRep rep) {

        System.out.println("\nFilter internships (press Enter to keep previous values):");

        System.out.print("Status [" + rep.getLastStatusFilter() + "]: ");
        String status = sc.nextLine().trim();
        if (!status.isBlank()) rep.setLastStatusFilter(status);

        System.out.print("Preferred Major [" + rep.getLastMajorFilter() + "]: ");
        String major = sc.nextLine().trim();
        if (!major.isBlank()) rep.setLastMajorFilter(major);

        System.out.print("Level [" + rep.getLastLevelFilter() + "]: ");
        String level = sc.nextLine().trim();
        if (!level.isBlank()) rep.setLastLevelFilter(level);

        System.out.print("Company Name [" + rep.getLastCompanyFilter() + "]: ");
        String company = sc.nextLine().trim();
        if (!company.isBlank()) {
            rep.setLastCompanyFilter(company);
        }
        else if (rep.getLastCompanyFilter().isBlank()) {
            // default to own company name on first use
            rep.setLastCompanyFilter(rep.getCompanyName());
        }

        System.out.print("Visibility [" + rep.getLastVisibilityFilter() + "]: ");
        String vis = sc.nextLine().trim();
        if (!vis.isBlank()) rep.setLastVisibilityFilter(vis);

        System.out.print("Closing Date [" + rep.getLastCloseFilter() + "]: ");
        String close = sc.nextLine().trim();
        if (!close.isBlank()) rep.setLastCloseFilter(close);

        List<Internship> list = dm.filterInternships(
                rep.getLastStatusFilter(),
                rep.getLastMajorFilter(),
                rep.getLastLevelFilter(),
                rep.getLastCompanyFilter(),
                rep.getLastVisibilityFilter(),
                rep.getLastCloseFilter()
        );

        if (list.isEmpty()) {
            System.out.println("No internships found.");
            return;
        }

        System.out.println("\nFiltered internships:");
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
