package careerhub;

import careerhub.models.*;
import careerhub.storage.DataManager;
import careerhub.utils.MenuFactory;
import careerhub.services.AuthService;

import java.util.Optional;
import java.util.Scanner;

/**
 * Represents the unauthenticated entry menu of the Internship Hub system.
 * This menu allows users to:
 * <ul>
 *     <li>Log in using an existing account</li>
 *     <li>Register a new Company Representative account</li>
 *     <li>Exit the application</li>
 * </ul>
 *
 * <p>This class acts as the top-level Boundary component in the
 * Boundary–Control–Entity architecture. It delegates authentication
 * and menu routing to dedicated services such as {@link AuthService}
 * and {@link MenuFactory}, following SOLID principles (DIP, SRP).</p>
 */
public class MainMenu implements IMenu {

    /** DataManager responsible for loading, finding, and persisting all system data. */
    private final DataManager dm;

    /** Authentication service used to validate credentials and load User objects. */
    private final AuthService authService;

    /** Scanner for console input within the main menu. */
    private final Scanner sc = new Scanner(System.in);

    /**
     * Constructs the MainMenu and initializes the authentication service.
     *
     * @param dm shared DataManager instance
     */
    public MainMenu(DataManager dm) {
        this.dm = dm;
        this.authService = new AuthService(dm);   // SOLID: DIP-compliant
    }

    /**
     * Starts the main unauthenticated menu loop.
     *
     * <p>This loop runs until the user chooses to exit. It provides
     * the initial interaction point for all users before routing them
     * to their appropriate role-specific menu (Student, Staff, CompanyRep).</p>
     */
    @Override
    public void start() {
        while (true) {
            System.out.println("\n=== Internship Hub ===");
            System.out.println("1) Login");
            System.out.println("2) Register Company Representative");
            System.out.println("3) Exit");
            System.out.print("> ");

            String cmd = sc.nextLine().trim();

            switch (cmd) {
                case "1" -> loginFlow();
                case "2" -> registerCompanyRepFlow();
                case "3" -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    /**
     * Handles the login workflow for all user types.
     *
     * <p>This method prompts for user credentials, delegates validation to
     * {@link AuthService#authenticate(String, String)}, and then passes the
     * authenticated {@link User} instance to the role-based menu created by
     * {@link MenuFactory}.</p>
     *
     * <p>If authentication fails, an error message is shown and the menu returns
     * to the main screen.</p>
     */
    private void loginFlow() {

        System.out.print("User ID: ");
        String id = sc.nextLine().trim();

        System.out.print("Password: ");
        String pw = sc.nextLine().trim();

        // ==============================================
        // SOLID AuthService used here (DIP + SRP)
        // ==============================================
        Optional<User> uo = authService.authenticate(id, pw);

        if (uo.isEmpty()) {
            System.out.println("Invalid username or password.");
            return;
        }

        User u = uo.get();
        System.out.println("Welcome, " + u.getName() + " (" + u.getType() + ")");

        // ====================================================
        // SOLID Menu routing using MenuFactory + IMenu
        // ====================================================
        try {
            IMenu userMenu = MenuFactory.createMenu(u, dm);
            userMenu.start();
        }
        catch (IllegalArgumentException e) {
            System.out.println("Unknown user type. Cannot route to a menu.");
        }
    }

    /**
     * Handles the workflow for registering a new Company Representative.
     *
     * <p>This method collects user input, verifies that the ID does not already
     * exist, creates a {@link CompanyRep} instance, and registers it through
     * {@link DataManager#registerCompanyRep(CompanyRep)}. Newly registered
     * Company Representatives must wait for Staff approval before being able
     * to create internships.</p>
     */
    private void registerCompanyRepFlow() {
        System.out.println("Register Company Representative (needs staff approval later).");

        System.out.print("ID (use your company email): ");
        String id = sc.nextLine().trim();

        if (dm.findUserById(id).isPresent()) {
            System.out.println("ID already exists.");
            return;
        }

        System.out.print("Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Company name: ");
        String company = sc.nextLine().trim();

        System.out.print("Department: ");
        String dept = sc.nextLine().trim();

        System.out.print("Position: ");
        String pos = sc.nextLine().trim();

        CompanyRep rep = new CompanyRep(id, name, "password", company, dept, pos);
        dm.registerCompanyRep(rep);

        System.out.println("Registered. Wait for Career Center Staff approval before you can create internships.");
    }
}
