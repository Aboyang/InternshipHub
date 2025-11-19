package careerhub.services.company;

import careerhub.models.CompanyRep;
import careerhub.models.Internship;
import careerhub.storage.DataManager;

import java.time.LocalDate;
import java.util.Scanner;

/**
 * Service responsible for handling the creation of new internship opportunities
 * by a Company Representative.  
 *
 * <p>This class belongs to the Control layer in the Boundary–Control–Entity
 * architecture. It receives raw input from the {@link careerhub.CompanyRepMenu}
 * (Boundary) and performs validation, formatting, business rule enforcement, and
 * final creation of {@link Internship} entities through the {@link DataManager}.</p>
 *
 * <p>Main responsibilities include:</p>
 * <ul>
 *     <li>Validating input fields (title, description, level, major, date range)</li>
 *     <li>Checking CompanyRep eligibility (approval + max internship limit)</li>
 *     <li>Clamping and validating slot counts</li>
 *     <li>Constructing the Internship object</li>
 *     <li>Delegating persistence to the DataManager</li>
 * </ul>
 */
public class CreateInternshipService {

    /** Reference to the DataManager for creating and storing internship data. */
    private final DataManager dm;

    /** Shared scanner for user input, passed from menu layer. */
    private final Scanner sc;

    /**
     * Constructs the service with dependencies injected.
     *
     * @param dm shared DataManager instance
     * @param sc shared Scanner from the CompanyRepMenu
     */
    public CreateInternshipService(DataManager dm, Scanner sc) {
        this.dm = dm;
        this.sc = sc;
    }

    /**
     * Guides the Company Representative through the internship creation workflow.
     *
     * <p>This method interacts with the user to gather all required internship
     * attributes. It validates each field step-by-step, applies assignment rules
     * (maximum 5 internships per representative, date consistency, allowed levels,
     * and slot limits), and ensures the final Internship object is constructed
     * correctly before passing it to the DataManager for ID assignment and storage.</p>
     *
     * @param rep the authenticated Company Representative creating the internship
     */
    public void create(CompanyRep rep) {

        if (!rep.isApproved()) {
            System.out.println("Your account is not approved yet.");
            return;
        }

        if (!rep.canCreateMoreInternships()) {
            System.out.println("Reached max 5 internships.");
            return;
        }

        // ========== Title ==========
        String title;
        while (true) {
            System.out.print("Title: ");
            title = sc.nextLine().trim();
            if (title.isBlank()) System.out.println("Title cannot be blank.");
            else break;
        }

        // ========== Description ==========
        String desc;
        while (true) {
            System.out.print("Description: ");
            desc = sc.nextLine().trim();
            if (desc.isBlank()) System.out.println("Description cannot be blank.");
            else break;
        }

        // ========== Level ==========
        String level;
        while (true) {
            System.out.print("Level (Basic/Intermediate/Advanced): ");
            level = sc.nextLine().trim();
            if (!(level.equalsIgnoreCase("Basic") ||
                  level.equalsIgnoreCase("Intermediate") ||
                  level.equalsIgnoreCase("Advanced"))) {
                System.out.println("Invalid level.");
            } else break;
        }

        // ========== Major ==========
        String pref;
        while (true) {
            System.out.print("Preferred major: ");
            pref = sc.nextLine().trim();
            if (pref.isBlank()) System.out.println("Preferred major cannot be blank.");
            else break;
        }

        // ========== Dates ==========
        LocalDate open = null, close = null;

        System.out.print("Open date (YYYY-MM-DD) or blank: ");
        String o = sc.nextLine().trim();
        if (!o.isBlank()) open = LocalDate.parse(o);

        System.out.print("Close date (YYYY-MM-DD) or blank: ");
        String c = sc.nextLine().trim();
        if (!c.isBlank()) close = LocalDate.parse(c);

        if (open != null && close != null && close.isBefore(open)) {
            System.out.println("Closing date cannot be earlier than opening date.");
            return;
        }

        // ========== Slots ==========
        int slots;
        while (true) {
            System.out.print("Slots (1–10): ");
            try {
                slots = Integer.parseInt(sc.nextLine().trim());
                if (slots >= 1 && slots <= 10) break;
            } catch (Exception ignored) {}
            System.out.println("Invalid slot count.");
        }

        // Construct the internship object
        Internship it = new Internship(
                null, title, desc, level, pref, open, close,
                rep.getCompanyName(), rep.getId(), slots
        );

        // Persist via DataManager
        String id = dm.createInternship(it);

        // Track internship ID under this CompanyRep
        rep.addCreatedInternship(id);

        System.out.println("Created internship " + id + " (Pending staff approval).");
    }
}
