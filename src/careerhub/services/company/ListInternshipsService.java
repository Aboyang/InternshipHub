package careerhub.services.company;

import careerhub.models.CompanyRep;
import careerhub.models.Internship;
import careerhub.storage.DataManager;

import java.util.List;

/**
 * Service responsible for listing all internships created by
 * a specific Company Representative.
 *
 * <p>This class belongs to the Control layer and is invoked from
 * {@code CompanyRepMenu} whenever the representative requests
 * to view their created internships.</p>
 *
 * <p>The service retrieves internships using the {@link DataManager}
 * and displays key information such as:</p>
 *
 * <ul>
 *     <li>Internship ID</li>
 *     <li>Title</li>
 *     <li>Status (Pending / Approved / Rejected / Filled)</li>
 *     <li>Visibility (true/false)</li>
 *     <li>Confirmed student count vs total slots</li>
 * </ul>
 *
 * <p>If the representative has created no internships,
 * a simple message is printed.</p>
 */
public class ListInternshipsService {

    /** Shared DataManager used for retrieving internship records. */
    private final DataManager dm;

    /**
     * Constructs the service with a required DataManager dependency.
     *
     * @param dm shared DataManager instance used for internship queries
     */
    public ListInternshipsService(DataManager dm) {
        this.dm = dm;
    }

    /**
     * Displays all internships created by the given Company Representative.
     *
     * <p>If no internships exist, the method prints a message and returns.
     * Otherwise, each internship is printed on its own line with basic
     * information (ID, title, status, visibility, and slot usage).</p>
     *
     * @param rep the representative whose internships will be displayed
     */
    public void show(CompanyRep rep) {
        List<Internship> list = dm.getInternshipsByCompanyRep(rep.getId());
        if (list.isEmpty()) {
            System.out.println("No internships created.");
            return;
        }

        for (Internship i : list) {
            System.out.printf("%s | %s | Status:%s | Visible:%s | Confirmed:%d/%d\n",
                    i.getId(), i.getTitle(), i.getStatus(),
                    i.isVisible(), i.getConfirmedCount(), i.getSlots());
        }
    }
}
