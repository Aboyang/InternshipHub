package service;

import repository.UserRepository;
import repository.InternshipRepository;
import repository.ApplicationRepository;
import model.Student;
import model.Staff;
import util.Utils;

import java.nio.file.*;
import java.util.*;

public class CSVLoader {
    private UserRepository userRepo;
    private InternshipRepository internshipRepo;
    private ApplicationRepository applicationRepo;

    public CSVLoader(UserRepository ur, InternshipRepository ir, ApplicationRepository ar){
        this.userRepo = ur; this.internshipRepo = ir; this.applicationRepo = ar;
    }

    public void loadStudentsFromCsv(String path) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get(path));
        boolean header = true;
        for (String l : lines){
            if (header) { header = false; continue; }
            String[] parts = Utils.safeSplit(l, ',');
            if (parts.length < 5) continue;
            String id = parts[0].trim();
            String name = parts[1].trim();
            String major = parts[2].trim();
            int year = Integer.parseInt(parts[3].trim());
            String email = parts[4].trim();
            Student s = new Student(id, name, "password", major, year, email); // updated
            userRepo.addUser(s);
        }
    }

    public void loadStaffFromCsv(String path) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get(path));
        boolean header = true;
        for (String l : lines){
            if (header) { header = false; continue; }
            String[] parts = Utils.safeSplit(l, ',');
            if (parts.length < 5) continue;
            String id = parts[0].trim();
            String name = parts[1].trim();
            String role = parts[2].trim();
            String dept = parts[3].trim();
            String email = parts[4].trim();
            Staff st = new Staff(id, name, "password", dept, email); // updated
            userRepo.addUser(st);
        }
    }

}
