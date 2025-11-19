package careerhub.services.company;

import careerhub.models.CompanyRep;
import careerhub.models.Internship;
import careerhub.storage.DataManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Service responsible for editing internship opportunities created
 * by a Company Representative.
 *
 * <p>This class belongs to the Control layer of the B-C-E architecture.
 * It receives user input from the CompanyRepMenu (Boundary) and performs:</p>
 *
 * <ul>
 *     <li>Listing all internships owned by the representative</li>
 *     <li>Ensuring that only the creator of an internship can edit it</li>
 *     <li>Allowing edits only if the internship is still {@code PENDING}</li>
 *     <li>Validating updated fields (level, dates, and slot range)</li>
 *     <li>Applying updates directly to the Internship entity</li>
 * </ul>
 *
 * <p>Once an internship has been approved or rejected by Staff, it can no
 * longer be edited. This enforces assignment rules regarding workflow
 * immutability after approval.</p>
 */
public class EditInternshipService {

    /** Shared DataManager for retrieving and updating internship data. */
    private final DataManager dm;

    /** Shared scanner instance for reading console input. */
    private final Scanner sc;

    /**
     * Constructs the editing service with injected dependencies.
     *
     * @param dm shared DataManager instance
     * @param sc shared Scanner instance
     */
    public EditInternshipService(DataManager dm, Scanner sc) {
        this.dm = dm;
        this.sc = sc;
    }

    /**
     * Executes the editing workflow for a Company Representative.
     *
     * <p>The process includes:</p>
     * <ol>
     *     <li>Listing the representative’s internships</li>
     *     <li>Prompting for an internship ID</li>
     *     <li>Validating ownership and status</li>
     *     <li>Allowing updates to title, description, level, major, dates, and slots</li>
     *     <li>Ensuring all edits conform to assignment constraints</li>
     * </ol>
     *
     * <p>Only internships in {@code PENDING} state may be edited.</p>
     *
     * @param rep the Company Representative requesting the edit
     */
    public void edit(CompanyRep rep) {

        List<Internship> mine = dm.getInternshipsByCompanyRep(rep.getId());
        if (mine.isEmpty()) {
            System.out.println("You have no internships.");
            return;
        }

        System.out.println("\n=== Your Internships (Pending Only) ===");
        for (Internship it : mine) {
            System.out.printf(
                "%s | %s | Status:%s | Visible:%s\n",
                it.getId(), it.getTitle(), it.getStatus(), it.isVisible()
            );
        }

        System.out.print("Enter internship ID to edit: ");
        String iid = sc.nextLine().trim();

        Optional<Internship> io = dm.getInternshipById(iid);
        if (io.isEmpty()) {
            System.out.println("Invalid ID.");
            return;
        }

        Internship target = io.get();

        // Ownership validation
        if (!target.getCompanyRepId().equals(rep.getId())) {
            System.out.println("You cannot edit internships you did not create.");
            return;
        }

        // Only Pending internships may be edited
        if (!target.getStatus().equalsIgnoreCase("Pending")) {
            System.out.println("Only Pending internships can be edited.");
            return;
        }

        // ===================== Editing Workflow =====================

        System.out.println("\n=== Editing Internship ===");

        // ----- Title -----
        System.out.println("Current Title: " + target.getTitle());
        System.out.print("New Title: ");
        String t = sc.nextLine().trim();
        if (!t.isBlank()) target.setTitle(t);

        // ----- Description -----
        System.out.println("Current Description: " + target.getDescription());
        System.out.print("New Description: ");
        String d = sc.nextLine().trim();
        if (!d.isBlank()) target.setDescription(d);

        // ----- Level -----
        while (true) {
            System.out.println("Current Level: " + target.getLevel());
            System.out.print("New Level (Basic/Intermediate/Advanced) or blank: ");
            String lvl = sc.nextLine().trim();
            if (lvl.isBlank()) break;

            if (lvl.equalsIgnoreCase("Basic") ||
                lvl.equalsIgnoreCase("Intermediate") ||
                lvl.equalsIgnoreCase("Advanced")) {
                target.setLevel(lvl);
                break;
            }
            System.out.println("Invalid level.");
        }

        // ----- Preferred Major -----
        System.out.println("Current Preferred Major: " + target.getPreferredMajor());
        System.out.print("New Preferred Major: ");
        String major = sc.nextLine().trim();
        if (!major.isBlank()) target.setPreferredMajor(major);

        // ----- Opening Date -----
        while (true) {
            System.out.println("Current Open Date: " + target.getOpenDate());
            System.out.print("New Open Date (YYYY-MM-DD) or blank: ");
            String od = sc.nextLine().trim();
            if (od.isBlank()) break;

            try {
                target.setOpenDate(LocalDate.parse(od));
                break;
            } catch (Exception e) {
                System.out.println("Invalid date.");
            }
        }

        // ----- Closing Date -----
        while (true) {
            System.out.println("Current Close Date: " + target.getCloseDate());
            System.out.print("New Close Date (YYYY-MM-DD) or blank: ");
            String cd = sc.nextLine().trim();
            if (cd.isBlank()) break;

            try {
                target.setCloseDate(LocalDate.parse(cd));
                break;
            } catch (Exception e) {
                System.out.println("Invalid date.");
            }
        }

        // ----- Slot Count -----
        while (true) {
            System.out.println("Current Slots: " + target.getSlots());
            System.out.print("New Slots (1–10) or blank: ");
            String sl = sc.nextLine().trim();
            if (sl.isBlank()) break;

            try {
                int n = Integer.parseInt(sl);
                if (n >= 1 && n <= 10) {
                    target.setSlots(n);
                    break;
                }
            } catch (Exception ignored) {}
            System.out.println("Invalid slot count.");
        }

        System.out.println("Internship updated successfully.");
    }
}
