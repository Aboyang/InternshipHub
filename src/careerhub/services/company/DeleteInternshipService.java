package careerhub.services.company;

import careerhub.models.CompanyRep;
import careerhub.models.Internship;
import careerhub.storage.DataManager;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Service responsible for deleting internship opportunities created
 * by a Company Representative.
 *
 * <p>This class belongs to the Control layer in the B-C-E architecture.
 * It is invoked by the CompanyRepMenu and handles:</p>
 *
 * <ul>
 *     <li>Listing all internships created by the representative</li>
 *     <li>Validating the internship ID entered by the user</li>
 *     <li>Confirming deletion to prevent accidental removal</li>
 *     <li>Deleting the internship from the system</li>
 *     <li>Updating the representative’s internal list of created internships</li>
 * </ul>
 *
 * <p>Deletion removes the internship entirely from the DataManager and cannot
 * be undone. This aligns with the assignment rule that internships pending
 * approval or not yet open may be deleted by their creators.</p>
 */
public class DeleteInternshipService {

    /** Shared DataManager handling storage and retrieval operations. */
    private final DataManager dm;

    /** Shared scanner passed from the menu layer for console input. */
    private final Scanner sc;

    /**
     * Constructs the deletion service with its required dependencies.
     *
     * @param dm shared DataManager instance
     * @param sc shared Scanner from CompanyRepMenu
     */
    public DeleteInternshipService(DataManager dm, Scanner sc) {
        this.dm = dm;
        this.sc = sc;
    }

    /**
     * Executes the internship deletion workflow for a Company Representative.
     *
     * <p>The process is as follows:</p>
     * <ol>
     *     <li>Show all internships created by the representative</li>
     *     <li>Prompt the user to select an internship ID</li>
     *     <li>Verify that the internship exists and belongs to this rep</li>
     *     <li>Ask for a final confirmation (Y/N)</li>
     *     <li>Delete the internship and update the rep’s internal record</li>
     * </ol>
     *
     * @param rep the Company Representative requesting the deletion
     */
    public void delete(CompanyRep rep) {

        List<Internship> mine = dm.getInternshipsByCompanyRep(rep.getId());
        if (mine.isEmpty()) {
            System.out.println("You have no internships to delete.");
            return;
        }

        System.out.println("\nYour Internships:");
        for (Internship it : mine) {
            System.out.printf(
                "%s | %s | Status:%s | Visible:%s | Confirmed:%d/%d\n",
                it.getId(), it.getTitle(), it.getStatus(),
                it.isVisible(), it.getConfirmedCount(), it.getSlots()
            );
        }

        System.out.print("Enter internship ID to delete (blank to cancel): ");
        String iid = sc.nextLine().trim();
        if (iid.isBlank()) return;

        Optional<Internship> io = dm.getInternshipById(iid);
        if (io.isEmpty()) {
            System.out.println("Invalid ID.");
            return;
        }

        System.out.print("Are you sure? DELETE cannot be undone. (Y/N): ");
        String confirm = sc.nextLine().trim();
        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        dm.deleteInternship(iid);
        rep.removeCreatedInternship(iid);

        System.out.println("Internship removed.");
    }
}
