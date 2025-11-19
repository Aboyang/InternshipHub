package careerhub.services;

import careerhub.models.User;

import java.util.Scanner;

/**
 * Provides password-changing functionality for any type of user in the system.
 *
 * <p>This service centralizes the password update workflow so that menus
 * (Student, Staff, CompanyRep) do not need to duplicate the same logic.
 * It interacts directly with {@link User#changePassword(String)} and
 * performs a simple console prompt for the new password.</p>
 *
 * <p>The updated password is applied immediately in memory, and the caller
 * (typically a menu class) is expected to log the user out after this
 * operation to ensure proper re-authentication.</p>
 */
public class PasswordService {

    /** Scanner used to read the new password from console input. */
    private final Scanner sc = new Scanner(System.in);

    /**
     * Executes the password change flow for the given user.
     *
     * <p>Steps:</p>
     * <ol>
     *     <li>Prompt the user to enter a new password</li>
     *     <li>Trim the input and update the user's stored password</li>
     *     <li>Display a notice to re-login</li>
     * </ol>
     *
     * @param user the user whose password is being changed
     */
    public void changePassword(User user) {
        System.out.print("Enter new password: ");
        String newPw = sc.nextLine().trim();

        user.changePassword(newPw);

        System.out.println("Password changed (in-memory). You will be logged out. Please login again.");
    }
}
