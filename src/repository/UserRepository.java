package repository;

import model.*;
import java.util.*;

public class UserRepository {
    private Map<String, User> users = new HashMap<>();

    public void addUser(User u){ users.put(u.getId(), u); }
    public Optional<User> findById(String id){ return Optional.ofNullable(users.get(id)); }
    public Optional<Student> findStudent(String id){
        User u = users.get(id);
        if (u instanceof Student) return Optional.of((Student)u);
        return Optional.empty();
    }
    public Optional<CompanyRep> findCompanyRep(String id){
        User u = users.get(id);
        if (u instanceof CompanyRep) return Optional.of((CompanyRep)u);
        return Optional.empty();
    }
    public Optional<Staff> findStaff(String id){
        User u = users.get(id);
        if (u instanceof Staff) return Optional.of((Staff)u);
        return Optional.empty();
    }
    public Collection<User> findAll(){ return users.values(); }
    public Collection<Student> findAllStudents(){
        List<Student> out = new ArrayList<>();
        for (User u: users.values()) if (u instanceof Student) out.add((Student)u);
        return out;
    }
    public Collection<CompanyRep> findAllCompanyReps(){
        List<CompanyRep> out = new ArrayList<>();
        for (User u: users.values()) if (u instanceof CompanyRep) out.add((CompanyRep)u);
        return out;
    }
    public Collection<Staff> findAllStaff(){
        List<Staff> out = new ArrayList<>();
        for (User u: users.values()) if (u instanceof Staff) out.add((Staff)u);
        return out;
    }
}
