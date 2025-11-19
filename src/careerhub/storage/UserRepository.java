package careerhub.storage;

import careerhub.models.User;

import java.util.Collection;
import java.util.Optional;

/**
 * Repository interface for managing {@link User} entities.
 *
 * <p>This abstraction defines basic CRUD-style operations for storing and
 * retrieving user accounts (Students, Staff, Company Representatives).
 * Implementations (e.g., in-memory, CSV-backed, or database-backed)
 * provide the concrete storage mechanism.</p>
 *
 * <p>This interface is used by {@code UserDataService} and {@code DataManager}
 * to keep the system decoupled from the underlying persistence layer
 * (supports SOLID: DIP and SRP).</p>
 */
public interface UserRepository {

    /**
     * Finds a user by ID.
     *
     * @param id the unique user ID (e.g., email for CompanyRep)
     * @return an Optional containing the user if found, otherwise empty
     */
    Optional<User> findById(String id);

    /**
     * Saves or updates a user.
     *
     * <p>If a user with the same ID already exists, it should be replaced.</p>
     *
     * @param user the user to store
     */
    void save(User user);

    /**
     * Retrieves all users stored in the repository.
     *
     * @return a collection of all users
     */
    Collection<User> findAll();

    /**
     * Removes all users from the repository.
     *
     * <p>Mainly used when resetting data or performing full reloads
     * from CSV.</p>
     */
    void clear();
}
