import ui.CLI;
import service.CSVLoader;
import repository.UserRepository;
import repository.InternshipRepository;
import repository.ApplicationRepository;
import service.AuthService;
import service.UserService;
import service.InternshipService;
import service.ApplicationService;
import service.ReportService;
import model.User;

public class Main {
    public static void main(String[] args) {
        // CSV paths (adjusted for your folder)
        String studentsCsv = "src/data/students.csv";
        String staffCsv = "src/data/staff.csv";

        // Initialize repositories
        UserRepository userRepo = new UserRepository();
        InternshipRepository internshipRepo = new InternshipRepository();
        ApplicationRepository applicationRepo = new ApplicationRepository();

        // Load data from CSV
        CSVLoader loader = new CSVLoader(userRepo, internshipRepo, applicationRepo);
        try {
            loader.loadStudentsFromCsv(studentsCsv);
            loader.loadStaffFromCsv(staffCsv);

            System.out.println("Loaded users:");
            for (User u : userRepo.findAll()) {
                System.out.println(u.getId() + " | " + u.getName() + " | " + u.getRole());
            }
        } catch (Exception e) {
            System.out.println("Warning loading CSVs: " + e.getMessage());
        }

        // Initialize services
        AuthService authService = new AuthService(userRepo);
        UserService userService = new UserService(userRepo);
        InternshipService internshipService = new InternshipService(internshipRepo, userRepo);
        ApplicationService applicationService = new ApplicationService(applicationRepo, internshipRepo, userRepo);
        ReportService reportService = new ReportService(internshipRepo, applicationRepo);

        // Start CLI
        CLI cli = new CLI(authService, userService, internshipService, applicationService, reportService);
        cli.start();
    }
}
