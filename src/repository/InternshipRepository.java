package repository;

import model.Internship;
import java.util.*;

public class InternshipRepository {
    private Map<String, Internship> internships = new HashMap<>();

    public void add(Internship i){ internships.put(i.getId(), i); }
    public Optional<Internship> findById(String id){ return Optional.ofNullable(internships.get(id)); }
    public Collection<Internship> findAll(){ return internships.values(); }
    public void remove(String id){ internships.remove(id); }
}
