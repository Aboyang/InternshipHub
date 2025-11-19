package careerhub;

import careerhub.storage.DataManager;

/**
 * Entry point for the Internship Hub CLI application.
 * 
 * <p>This class initializes the data directory, loads all persisted data
 * through {@link DataManager}, launches the main menu, and finally saves
 * all system state upon exit. It serves as the top-level coordinator of
 * system startup and shutdown.</p>
 *
 * <p>The flow is:</p>
 * <ol>
 *     <li>Start application and display greeting</li>
 *     <li>Create the DataManager for loading/saving system data</li>
 *     <li>Load users, internships, and applications</li>
 *     <li>Launch {@link MainMenu} (the primary unauthenticated menu)</li>
 *     <li>On program exit, save all updated data back to storage</li>
 * </ol>
 */
public class Main {

    /**
     * Program entry point for the Internship Hub system.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {

        System.out.println("Starting Internship Hub (CLI) — Java");

        String dataDir = "data";
        DataManager dm = new DataManager(dataDir);

        // Load persisted state (or seed from CSV files on first run)
        dm.loadAll();
        
        // Create and run the main menu (unauthenticated)
        IMenu mainMenu = new MainMenu(dm);
        mainMenu.start();

        // Save all changes back to the data directory
        dm.saveUsers();
        dm.saveInternships();
        dm.saveApplications();

        System.out.println("Exiting Internship Hub. Goodbye!");
    }
}
