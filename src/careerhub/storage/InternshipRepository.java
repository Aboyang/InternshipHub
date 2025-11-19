package careerhub.storage;

import careerhub.models.Internship;

import java.util.Collection;
import java.util.Optional;

/**
 * Repository interface for managing {@link Internship} entities.
 *
 * <p>This abstraction defines the CRUD operations available for storing,
 * retrieving, and deleting internships. Different implementations (e.g.,
 * in-memory, file-based, database-based) can provide their own storage
 * mechanisms.</p>
 *
 * <p>The system primarily uses this interface through {@code DataManager} and
 * {@code InternshipDataService} to decouple the business logic from the
 * underlying storage implementation (promoting DIP/SOLID principles).</p>
 */
public interface InternshipRepository {

    /**
     * Creates a new internship entry in the repository.
     *
     * <p>If the internship does not have an ID, the implementing class must
     * assign a generated ID (e.g., I1, I2, ...). If an ID already exists,
     * it should be respected and stored.</p>
     *
     * @param internship the internship object to store
     * @return the ID assigned to the internship
     */
    String create(Internship internship);

    /**
     * Saves (inserts or updates) an existing internship that already has a valid ID.
     *
     * <p>Used mainly by CSV load operations, where internships are restored
     * with predefined IDs and must not trigger new ID generation.</p>
     *
     * @param internship the internship to save
     */
    void save(Internship internship);

    /**
     * Retrieves an internship by its ID.
     *
     * @param id the internship ID
     * @return an Optional containing the internship if found, otherwise empty
     */
    Optional<Internship> findById(String id);

    /**
     * Retrieves all internships stored in the repository.
     *
     * @return a collection of all internships
     */
    Collection<Internship> findAll();

    /**
     * Deletes an internship from storage.
     *
     * @param id the internship ID to remove
     */
    void delete(String id);

    /**
     * Clears all stored internships.
     *
     * <p>Mainly used during initialization or when resetting data.</p>
     */
    void clear();
}
