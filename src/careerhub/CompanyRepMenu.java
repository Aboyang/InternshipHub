package careerhub;

import careerhub.models.CompanyRep;
import careerhub.storage.DataManager;

// Company services
import careerhub.services.company.CreateInternshipService;
import careerhub.services.company.FilterInternshipsService;
import careerhub.services.company.ListInternshipsService;
import careerhub.services.company.ToggleVisibilityService;
import careerhub.services.company.ReviewApplicationsService;
import careerhub.services.company.EditInternshipService;
import careerhub.services.company.DeleteInternshipService;

/**
 * Menu interface for authenticated Company Representative users.
 * Provides access to all CompanyRep-related functionalities such as:
 * <ul>
 *     <li>Creating internship opportunities</li>
 *     <li>Filtering and viewing internships</li>
 *     <li>Managing visibility</li>
 *     <li>Reviewing and deciding on applications</li>
 *     <li>Editing or deleting internships (before approval)</li>
 *     <li>Changing password</li>
 * </ul>
 *
 * <p>This class acts as a Boundary component in the Boundary–Control–Entity
 * architecture. It delegates business logic to specialized service classes,
 * while handling user interaction and menu navigation.</p>
 */
public class CompanyRepMenu extends BaseMenu {

    /** Handles all persistent data operations. */
    private final DataManager dm;

    /** The authenticated Company Representative using this menu. */
    private final CompanyRep rep;

    // Injected services for CompanyRep operations
    private final CreateInternshipService createService;
    private final FilterInternshipsService filterService;
    private final ListInternshipsService listService;
    private final ToggleVisibilityService visibilityService;
    private final ReviewApplicationsService reviewService;
    private final EditInternshipService editService;
    private final DeleteInternshipService deleteService;

    /**
     * Constructs a CompanyRepMenu and initializes all related services.
     * Each service receives the required DataManager and shared Scanner
     * from {@link BaseMenu}.
     *
     * @param dm  the system-wide DataManager
     * @param rep the authenticated Company Representative
     */
    public CompanyRepMenu(DataManager dm, CompanyRep rep) {
        this.dm = dm;
        this.rep = rep;

        // Initialize services using shared scanner from BaseMenu
        this.createService = new CreateInternshipService(dm, sc);
        this.filterService = new FilterInternshipsService(dm, sc);
        this.listService = new ListInternshipsService(dm);
        this.visibilityService = new ToggleVisibilityService(dm, sc);
        this.reviewService = new ReviewApplicationsService(dm, sc);
        this.editService = new EditInternshipService(dm, sc);
        this.deleteService = new DeleteInternshipService(dm, sc);
    }

    /**
     * Starts the Company Representative menu loop.
     *
     * <p>The menu remains active until the user selects Logout or changes
     * their password (which forces an immediate logout). This method handles
     * input prompts and delegates each operation to the corresponding service.</p>
     *
     * <p>If the representative's account has not yet been approved by Career
     * Center Staff, the menu informs the user and exits immediately.</p>
     */
    @Override
    public void start() {

        if (!rep.isApproved()) {
            System.out.println("Your account is not approved yet by Career Center Staff. You cannot create internships.");
            return;
        }

        while (true) {
            System.out.println("\nCompany Rep Menu:");
            System.out.println("1) Create internship");
            System.out.println("2) View all internships (with filters)");
            System.out.println("3) View my internships");
            System.out.println("4) Toggle internship visibility");
            System.out.println("5) View applications & approve/reject");
            System.out.println("6) Edit internship");
            System.out.println("7) Delete internship");
            System.out.println("8) Change password");
            System.out.println("9) Logout");
            System.out.print("> ");

            String o = sc.nextLine().trim();

            switch (o) {
                case "1" -> createService.create(rep);

                case "2" -> filterService.filter(rep);

                case "3" -> listService.show(rep);

                case "4" -> visibilityService.toggle(rep);

                case "5" -> reviewService.review(rep);

                case "6" -> editService.edit(rep);

                case "7" -> deleteService.delete(rep);

                case "8" -> {
                    changePasswordFlow(rep);
                    return; // logout after password change
                }

                case "9" -> {
                    return;
                }

                default -> System.out.println("Invalid.");
            }
        }
    }
}
