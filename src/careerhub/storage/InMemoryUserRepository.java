package careerhub.storage;

import careerhub.models.User;

import java.util.*;

/**
 * In-memory implementation of {@link UserRepository}.
 *
 * <p>This repository stores all {@link User} objects inside a {@code HashMap},
 * indexed by their unique User ID. It is used by the DataManager and related
 * services to read/write user data during runtime.</p>
 *
 * <p>This class contains no business logic; it only handles CRUD operations
 * on the in-memory user collection.</p>
 */
public class InMemoryUserRepository implements UserRepository {

    /**
     * Internal storage of users mapped by their user ID.
     */
    private final Map<String, User> users = new HashMap<>();

    /**
     * Finds a user by ID.
     *
     * @param id the unique user ID
     * @return an Optional containing the user if found, otherwise empty
     */
    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    /**
     * Saves or updates a user object.
     *
     * <p>If a user with the same ID already exists, it will be replaced.</p>
     *
     * @param user the user object to store
     */
    @Override
    public void save(User user) {
        users.put(user.getId(), user);
    }

    /**
     * Returns all stored users.
     *
     * @return a collection of all users
     */
    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    /**
     * Clears all stored users from memory.
     */
    @Override
    public void clear() {
        users.clear();
    }

    /**
     * Returns the internal map for debugging purposes.
     *
     * @return map of ID → User
     */
    public Map<String, User> getInternalMap() {
        return users;
    }
}
