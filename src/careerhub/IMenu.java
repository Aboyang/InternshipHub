package careerhub;

/**
 * Represents a generic menu interface in the Internship Management System.
 * All menu types (StudentMenu, CompanyRepMenu, StaffMenu, and BaseMenu subclasses)
 * implement this interface to provide a standard entry point for launching
 * interactive menu loops.
 *
 * <p>The {@link #start()} method defines the main lifecycle of the menu,
 * handling user input and delegating operations until the user exits
 * or the menu terminates its own session (e.g., after a password change).</p>
 */
public interface IMenu {

    /**
     * Starts the interactive menu flow. Implementations should provide
     * a continuous input loop, presenting menu options and executing the
     * appropriate operations until the user chooses to exit.
     */
    void start();
}
