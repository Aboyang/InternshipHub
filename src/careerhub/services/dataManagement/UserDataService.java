package careerhub.services.dataManagement;

import careerhub.models.CompanyRep;
import careerhub.models.User;
import careerhub.storage.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer responsible for domain-level operations on {@link User}
 * entities. This class wraps {@link UserRepository} and provides higher-level
 * behaviours used by Staff, Company Representatives, and the main application.
 *
 * <p>Responsibilities include:</p>
 * <ul>
 *     <li>User lookup by ID</li>
 *     <li>Registration and approval of Company Representatives</li>
 *     <li>Retrieval of pending CompanyRep accounts</li>
 *     <li>Access to all users for persistence</li>
 * </ul>
 *
 * <p>This service helps maintain SRP by keeping user-related business logic
 * separate from the repository and menu layers.</p>
 */
public class UserDataService {

    /** Repository storing and managing all User objects. */
    private final UserRepository userRepo;

    /**
     * Constructs a UserDataService backed by the given repository.
     *
     * @param userRepo repository used for persistence operations
     */
    public UserDataService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Retrieves a user by their unique ID.
     *
     * @param id the user's ID
     * @return an Optional containing the user if found
     */
    public Optional<User> findUserById(String id) {
        return userRepo.findById(id);
    }

    /**
     * Registers a new company representative in the system.
     * This does not automatically approve the representative.
     *
     * @param rep the CompanyRep to register
     */
    public void registerCompanyRep(CompanyRep rep) {
        userRepo.save(rep);
    }

    /**
     * Approves or rejects a company representative account.
     *
     * @param id      the representative's user ID
     * @param approve true to approve, false to reject
     */
    public void approveCompanyRep(String id, boolean approve) {
        userRepo.findById(id).ifPresent(u -> {
            if (u instanceof CompanyRep r) {
                r.setApproved(approve);
            }
        });
    }

    /**
     * Retrieves all Company Representatives who have not yet been approved.
     * Used by Career Center Staff to manage pending account requests.
     *
     * @return list of unapproved CompanyRep users
     */
    public List<CompanyRep> getPendingCompanyReps() {
        return userRepo.findAll().stream()
                .filter(u -> u instanceof CompanyRep)
                .map(u -> (CompanyRep) u)
                .filter(r -> !r.isApproved())
                .collect(Collectors.toList());
    }

    /**
     * Returns all users stored in the system.
     * Used for saving data into CSV files.
     *
     * @return iterable collection of all users
     */
    public Iterable<User> getAllUsers() {
        return userRepo.findAll();
    }
}
