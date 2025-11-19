package careerhub.utils;

import careerhub.IMenu;
import careerhub.StudentMenu;
import careerhub.CompanyRepMenu;
import careerhub.StaffMenu;

import careerhub.models.*;
import careerhub.storage.DataManager;

/**
 * Factory class responsible for creating the correct role-specific menu
 * implementation based on the authenticated user's type.
 *
 * <p>This class follows the Factory Method pattern and supports the
 * Boundary–Control–Entity architecture used throughout the system.
 * By centralizing menu creation here, the rest of the system remains
 * decoupled from the concrete menu classes, following SOLID
 * (especially DIP and OCP).</p>
 *
 * <p>The factory inspects the runtime type of the given {@link User}
 * using pattern matching and returns the correct {@link IMenu}
 * implementation:</p>
 *
 * <ul>
 *     <li>{@link StudentMenu} for {@link Student}</li>
 *     <li>{@link CompanyRepMenu} for {@link CompanyRep}</li>
 *     <li>{@link StaffMenu} for {@link Staff}</li>
 * </ul>
 *
 * <p>If an unknown type is encountered (which should not happen),
 * an {@link IllegalArgumentException} is thrown.</p>
 */
public class MenuFactory {

    /**
     * Creates the appropriate menu for the given authenticated user.
     *
     * @param user the authenticated user whose role determines the menu to load
     * @param dm   shared DataManager instance for use by menu services
     * @return the corresponding role-specific {@link IMenu} implementation
     * @throws IllegalArgumentException if the user type is not recognized
     */
    public static IMenu createMenu(User user, DataManager dm) {

        if (user instanceof Student s) {
            return new StudentMenu(dm, s);
        }

        if (user instanceof CompanyRep r) {
            return new CompanyRepMenu(dm, r);
        }

        if (user instanceof Staff st) {
            return new StaffMenu(dm, st);
        }

        throw new IllegalArgumentException("Unknown user type: " + user.getType());
    }
}
