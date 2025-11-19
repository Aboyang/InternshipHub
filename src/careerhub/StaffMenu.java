package careerhub;

import careerhub.models.Staff;
import careerhub.storage.DataManager;

// staff services
import careerhub.services.staff.ApproveCompanyRepService;
import careerhub.services.staff.ApproveInternshipService;
import careerhub.services.staff.ProcessWithdrawalService;
import careerhub.services.staff.FilterInternshipsStaffService;

/**
 * Menu interface for authenticated Career Center Staff users.
 * This menu grants staff the ability to:
 * <ul>
 *     <li>Approve or reject Company Representative registrations</li>
 *     <li>Approve or reject internship opportunities</li>
 *     <li>Process student withdrawal requests</li>
 *     <li>View and filter all internships in the system</li>
 *     <li>Change their password</li>
 * </ul>
 *
 * <p>As a Boundary component, this menu displays options and delegates work
 * to the corresponding staff service classes. All business logic, approval
 * decisions, and filtering operations are handled through Control-layer
 * services, keeping this class focused on user interaction.</p>
 */
public class StaffMenu extends BaseMenu {

    /** Shared DataManager used for loading, updating, and saving system data. */
    private final DataManager dm;

    /** The authenticated Staff user operating this menu. */
    private final Staff staff;

    // --------------------- Staff Services ---------------------

    /** Handles approval or rejection of Company Representative accounts. */
    private final ApproveCompanyRepService approveRepService;

    /** Handles approval or rejection of internship postings. */
    private final ApproveInternshipService approveInternshipService;

    /** Handles student withdrawal request processing. */
    private final ProcessWithdrawalService withdrawalService;

    /** Handles filtering of internships specifically for staff users. */
    private final FilterInternshipsStaffService filterService;

    /**
     * Constructs a StaffMenu and injects all necessary services.
     *
     * @param dm    shared DataManager instance
     * @param staff authenticated Staff user
     */
    public StaffMenu(DataManager dm, Staff staff) {
        this.dm = dm;
        this.staff = staff;

        // Inject staff-related services using shared scanner
        this.approveRepService = new ApproveCompanyRepService(dm, sc);
        this.approveInternshipService = new ApproveInternshipService(dm, sc);
        this.withdrawalService = new ProcessWithdrawalService(dm, sc);
        this.filterService = new FilterInternshipsStaffService(dm, sc);
    }

    /**
     * Starts the Staff menu loop.
     *
     * <p>This loop continues until the user logs out or changes their password
     * (which triggers an automatic logout). Each command routes to the
     * appropriate staff service for handling business logic.</p>
     */
    @Override
    public void start() {

        while (true) {
            System.out.println("\nStaff Menu:");
            System.out.println("1) Approve/reject company representatives");
            System.out.println("2) Approve/reject internships");
            System.out.println("3) Process withdrawal requests");
            System.out.println("4) View all internships (with filters)");
            System.out.println("5) Change password");
            System.out.println("6) Logout");
            System.out.print("> ");

            String o = sc.nextLine().trim();

            switch (o) {

                case "1" -> approveRepService.approveOrReject();

                case "2" -> approveInternshipService.approveOrReject();

                case "3" -> withdrawalService.process();

                case "4" -> filterService.filter(staff);

                case "5" -> {
                    changePasswordFlow(staff);
                    return; // logout after password change
                }

                case "6" -> { return; }

                default -> System.out.println("Invalid option.");
            }
        }
    }
}
