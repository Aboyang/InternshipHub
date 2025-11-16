package service;

import repository.UserRepository;
import model.CompanyRep;
import model.Staff;
import model.Student;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class UserService {
    private UserRepository userRepo;

    public UserService(UserRepository ur){ 
        this.userRepo = ur; 
    }

    public void loadStudentsFromCSV(String path) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(path));
        for (int i = 1; i < lines.size(); i++) { // skip header
            String[] parts = lines.get(i).split(",");
            String id = parts[0];
            String name = parts[1];
            String major = parts[2];
            int year = Integer.parseInt(parts[3]);
            String email = parts[4];
            Student s = new Student(id, name, "password", major, year, email);
            userRepo.addUser(s); // store in repository
        }
    }

    public void loadStaffFromCSV(String path) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(path));
        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            String id = parts[0];
            String name = parts[1];
            String role = parts[2];
            String dept = parts[3];
            String email = parts[4];
            Staff st = new Staff(id, name, "password", dept, email);
            userRepo.addUser(st); // store in repository
        }
    }

    public boolean registerCompanyRep(String id, String name, String company, String dept, String pos, String email){
        if (userRepo.findById(id).isPresent()) return false;
        CompanyRep rep = new CompanyRep(id, name, "password", company, dept, pos, email);
        userRepo.addUser(rep);
        return true;
    }

    public List<CompanyRep> pendingCompanyReps(){
        List<CompanyRep> out = new ArrayList<>();
        for (CompanyRep r: userRepo.findAllCompanyReps()) if (!r.isApproved()) out.add(r);
        return out;
    }

    public boolean approveCompanyRep(String repId, boolean approved){
        Optional<CompanyRep> r = userRepo.findCompanyRep(repId);
        if (!r.isPresent()) return false;
        r.get().setApproved(approved);
        return true;
    }
}
