package careerhub.services.company;

import careerhub.models.CompanyRep;
import careerhub.models.Internship;
import careerhub.storage.DataManager;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Service class that allows a Company Representative to toggle
 * the visibility of their internship postings.
 *
 * <p>This class belongs to the Control layer and is triggered
 * from the {@code CompanyRepMenu}. It retrieves the representative’s
 * internships, allows selection of one internship, and flips its
 * visibility flag between {@code true} and {@code false}.</p>
 *
 * <p>Visibility determines whether students can see and apply
 * for the internship after it is approved.</p>
 */
public class ToggleVisibilityService {

    /** Shared DataManager used for loading and updating internships. */
    private final DataManager dm;

    /** Shared scanner instance for console input. */
    private final Scanner sc;

    /**
     * Constructs the service with required dependencies.
     *
     * @param dm the shared DataManager instance
     * @param sc the shared Scanner instance
     */
    public ToggleVisibilityService(DataManager dm, Scanner sc) {
        this.dm = dm;
        this.sc = sc;
    }

    /**
     * Toggles the visibility of an internship owned by the given Company Representative.
     *
     * <p>The workflow:</p>
     * <ol>
     *     <li>List all internships created by the representative</li>
     *     <li>Prompt the representative for the internship ID</li>
     *     <li>Validate that the internship exists</li>
     *     <li>Flip visibility from {@code true → false} or {@code false → true}</li>
     *     <li>Display the new visibility status</li>
     * </ol>
     *
     * <p>If the representative has no internships, or enters an invalid ID,
     * the method prints an appropriate message and exits gracefully.</p>
     *
     * @param rep the Company Representative requesting the toggle
     */
    public void toggle(CompanyRep rep) {

        List<Internship> mine = dm.getInternshipsByCompanyRep(rep.getId());
        if (mine.isEmpty()) {
            System.out.println("You have no internships to toggle.");
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

        System.out.print("Enter internship ID to toggle visibility: ");
        String iid = sc.nextLine().trim();

        Optional<Internship> io = dm.getInternshipById(iid);
        if (io.isEmpty()) {
            System.out.println("Invalid ID.");
            return;
        }

        Internship it = io.get();
        it.setVisible(!it.isVisible());
        System.out.println("Visibility updated. Now: " + it.isVisible());
    }
}
