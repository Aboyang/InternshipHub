package careerhub;

import careerhub.storage.DataManager;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Internship Hub (CLI) — Java");
        // data folder relative to project root
        String dataDir = "data";
        DataManager dm = new DataManager(dataDir);
        dm.loadStudentsFromCsv("students.csv"); // expects data/students.csv
        dm.loadStaffFromCsv("staff.csv");       // expects data/staff.csv

        Menu menu = new Menu(dm);
        menu.start();
        System.out.println("Exiting Internship Hub. Goodbye!");
    }
}

