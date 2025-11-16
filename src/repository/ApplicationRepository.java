package repository;

import model.Application;
import java.util.*;

public class ApplicationRepository {
    private Map<String, Application> applications = new HashMap<>();

    public void add(Application a){ applications.put(a.getId(), a); }
    public Optional<Application> findById(String id){ return Optional.ofNullable(applications.get(id)); }
    public Collection<Application> findAll(){ return applications.values(); }
    public List<Application> findByStudent(String studentId){
        List<Application> out = new ArrayList<>();
        for (Application a: applications.values()) if (a.getStudentId().equals(studentId)) out.add(a);
        return out;
    }
    public List<Application> findByInternship(String internshipId){
        List<Application> out = new ArrayList<>();
        for (Application a: applications.values()) if (a.getInternshipId().equals(internshipId)) out.add(a);
        return out;
    }
}
