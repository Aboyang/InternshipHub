package careerhub.storage;

import careerhub.models.Application;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository abstraction for managing {@link Application} entities.
 *
 * <p>This interface defines all persistence-level operations related to
 * internship applications, regardless of the underlying storage mechanism
 * (CSV, in-memory, database, etc.). It follows the Repository pattern,
 * enabling high-level services to depend on this abstraction rather than
 * concrete implementations.</p>
 */
public interface ApplicationRepository {

    /**
     * Creates and stores a new {@link Application} using a system-generated
     * application ID (e.g., A1, A2, A3).
     *
     * @param internshipId ID of the internship being applied for
     * @param studentId    ID of the student submitting the application
     * @return the generated application ID
     */
    String create(String internshipId, String studentId);

    /**
     * Saves an existing {@link Application} object into the repository.
     * This is typically used during CSV loading, where the ID is already
     * known and should not be regenerated.
     *
     * @param app the application instance to persist
     */
    void save(Application app);

    /**
     * Retrieves an application by its identifier.
     *
     * @param id application ID
     * @return an {@link Optional} containing the application if found,
     *         or empty if not found
     */
    Optional<Application> findById(String id);

    /**
     * Retrieves all applications currently stored.
     *
     * @return a collection of all {@link Application} objects
     */
    Collection<Application> findAll();

    /**
     * Clears all stored applications.
     * Typically used during system initialization or data resets.
     */
    void clear();

    /**
     * Retrieves all applications submitted by a specific student.
     *
     * @param studentId student ID
     * @return list of matching {@link Application} objects
     */
    List<Application> findByStudentId(String studentId);

    /**
     * Retrieves all applications submitted for a specific internship.
     *
     * @param internshipId internship ID
     * @return list of matching {@link Application} objects
     */
    List<Application> findByInternshipId(String internshipId);

    /**
     * Deletes all applications associated with a specific internship.
     * Used when an internship is removed from the system.
     *
     * @param internshipId ID of the internship whose applications should be deleted
     */
    void deleteByInternshipId(String internshipId);
}
