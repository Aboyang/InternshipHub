package careerhub.models;

/**
 * Abstract base class for all user types in the Internship Management System.
 * Common attributes such as ID, name, and password are stored here, and
 * subclasses (Student, Staff, CompanyRep) extend this class to include
 * additional role-specific behavior and profile data.
 *
 * <p>This class also defines shared authentication behavior such as password
 * checking and updating. Each concrete subclass must provide an implementation
 * of {@link #getType()} to identify the user's role for menu routing.</p>
 */
public abstract class User {

    /** Unique identifier for the user. */
    protected String id;

    /** Full name of the user. */
    protected String name;

    /** Login password used for authentication. */
    protected String password;

    /**
     * Optional container storing last-used filter settings.
     * Subclasses may choose to use this or store filters individually.
     */
    protected FilterSettings filterSettings;

    /**
     * Constructs a User with basic profile information.
     *
     * @param id       unique identifier for the user
     * @param name     the user's name
     * @param password initial password for login
     */
    public User(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    /**
     * Retrieves the user ID.
     *
     * @return user ID string
     */
    public String getId() { return id; }

    /**
     * Retrieves the user's name.
     *
     * @return name string
     */
    public String getName() { return name; }

    /**
     * Checks whether the given password matches the stored password.
     *
     * @param p password to validate
     * @return true if the password matches; false otherwise
     */
    public boolean checkPassword(String p) {
        return password != null && password.equals(p);
    }

    /**
     * Retrieves the current password.
     * Mainly used by DataManager when persisting user information.
     *
     * @return password string
     */
    public String getPassword() {
        return password;
    }

    /**
     * Updates the user's password.
     *
     * @param newPw the new password to set
     */
    public void changePassword(String newPw) { this.password = newPw; }

    /**
     * Identifies the specific user type (e.g., "Student", "Staff", "CompanyRep").
     * Used throughout the system for menu routing and role-specific logic.
     *
     * @return string representing the user role
     */
    public abstract String getType();
}
