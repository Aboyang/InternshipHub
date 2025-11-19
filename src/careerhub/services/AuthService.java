package careerhub.services;

import careerhub.models.User;
import careerhub.storage.DataManager;

import java.util.Optional;

/**
 * Provides authentication services for the system.
 *
 * <p>This class is responsible for validating user login attempts.
 * It retrieves user records from the {@link DataManager}, checks that the
 * user exists, and verifies that the supplied password matches the stored
 * password.</p>
 *
 * <p>It follows SRP (Single Responsibility Principle) by containing only
 * authentication logic, and DIP (Dependency Inversion Principle) because
 * it depends on the high-level {@link DataManager} interface rather than
 * any concrete storage implementation.</p>
 */
public class AuthService {

    /** Shared DataManager used to retrieve stored user credentials. */
    private final DataManager dm;

    /**
     * Constructs the authentication service.
     *
     * @param dm the shared {@link DataManager} instance used for user lookup
     */
    public AuthService(DataManager dm) {
        this.dm = dm;
    }

    /**
     * Attempts to authenticate a user using the provided ID and password.
     *
     * <p>Authentication succeeds only if:</p>
     * <ul>
     *     <li>A user with the given ID exists</li>
     *     <li>The provided password matches the stored password</li>
     * </ul>
     *
     * @param userId   the ID entered at login
     * @param password the password entered at login
     * @return an {@link Optional} containing the authenticated {@link User},
     *         or an empty Optional if authentication fails
     */
    public Optional<User> authenticate(String userId, String password) {
        Optional<User> uo = dm.findUserById(userId);
        if (uo.isEmpty()) return Optional.empty();

        User user = uo.get();
        if (!user.checkPassword(password)) return Optional.empty();

        return Optional.of(user);
    }
}
