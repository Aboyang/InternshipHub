package careerhub;

import careerhub.models.Student;
import careerhub.storage.DataManager;

// student services
import careerhub.services.student.StudentViewAllInternshipsService;
import careerhub.services.student.StudentFilterInternshipsService;
import careerhub.services.student.StudentManageApplicationsService;

/**
 * Menu interface for authenticated Student users.  
 * 
 * <p>This menu provides the student-facing functionality required by the
 * Internship Hub system, including:</p>
 *
 * <ul>
 *     <li>Viewing all available internship opportunities</li>
 *     <li>Filtering and searching internships</li>
 *     <li>Managing submitted applications</li>
 *     <li>Changing passwords</li>
 *     <li>Logging out</li>
 * </ul>
 *
 * <p>This class acts as a Boundary component, handling console interaction,
 * while delegating all business logic to dedicated Control-layer services
 * such as {@link StudentViewAllInternshipsService},
 * {@link StudentFilterInternshipsService}, and
 * {@link StudentManageApplicationsService}. This aligns with SOLID and the
 * assignment’s required B-C-E structure.</p>
 */
public class StudentMenu extends BaseMenu {

    /** DataManager for retrieving, updating, and saving system state. */
    private final DataManager dm;

    /** The authenticated Student currently using the menu. */
    private final Student student;

    /** Service for listing all internships and applying to them. */
    private final StudentViewAllInternshipsService viewAllService;

    /** Service for filtered internship searching. */
    private final StudentFilterInternshipsService filterService;

    /** Service that manages student application actions (apply, withdraw, accept). */
    private final StudentManageApplicationsService appsService;

    /**
     * Constructs the StudentMenu and injects all student-related services.
     *
     * @param dm      shared DataManager instance
     * @param student authenticated student user
     */
    public StudentMenu(DataManager dm, Student student) {
        this.dm = dm;
        this.student = student;

        this.viewAllService = new StudentViewAllInternshipsService(dm, sc);
        this.filterService = new StudentFilterInternshipsService(dm, sc);
        this.appsService = new StudentManageApplicationsService(dm, sc);
    }

    /**
     * Starts the Student menu loop.
     *
     * <p>Students may repeatedly view internships, apply, withdraw, and check
     * their application statuses until they log out or change their password.
     * Upon a password change, the menu terminates immediately to force
     * re-authentication.</p>
     */
    @Override
    public void start() {

        while (true) {
            System.out.println("\nStudent Menu:");
            System.out.println("1) View and Apply all available internships");
            System.out.println("2) Search internships (with filters)");
            System.out.println("3) My applications");
            System.out.println("4) Change password");
            System.out.println("5) Logout");
            System.out.print("> ");

            String o = sc.nextLine().trim();

            switch (o) {
                case "1" -> viewAllService.viewAll(student);

                case "2" -> filterService.filter(student);

                case "3" -> appsService.manage(student);

                case "4" -> {
                    changePasswordFlow(student);  // logout afterwards
                    return;
                }

                case "5" -> { return; }

                default -> System.out.println("Invalid.");
            }
        }
    }
}
