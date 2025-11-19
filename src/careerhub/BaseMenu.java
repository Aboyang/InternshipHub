package careerhub;

import careerhub.models.User;
import java.util.Scanner;

/**
 * Abstract parent class for all authenticated menus in the system
 * (StudentMenu, CompanyRepMenu, StaffMenu). This class provides
 * shared utilities and behaviors commonly needed across all logged-in
 * user menus, such as input handling and password-change functionality.
 *
 * <p>Each concrete menu subclass inherits standardized behaviour to
 * ensure consistency across user roles. This helps reduce duplication
 * and supports a clean Boundary-Control structure aligned with the
 * SC2002 assignment requirements.</p>
 */
public abstract class BaseMenu implements IMenu {

    /**
     * Shared Scanner instance used for reading console input
     * across all authenticated menus.
     */
    protected final Scanner sc = new Scanner(System.in);

    /**
     * Provides a standard password-change workflow for all user types.
     * Prompts the user for a new password, updates it in memory, and
     * notifies the user that they must log in again.
     *
     * <p>This method does not persist changes to file; persistence is handled
     * externally by the DataManager when saving system state.</p>
     *
     * @param user the authenticated user requesting a password change
     */
    protected void changePasswordFlow(User user) {
        System.out.print("Enter new password: ");
        String newPw = sc.nextLine().trim();
        user.changePassword(newPw);
        System.out.println("Password changed (in-memory). You will be logged out. Please login again.");
    }
}
