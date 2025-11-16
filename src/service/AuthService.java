package service;

import repository.UserRepository;
import model.User;
import java.util.Optional;

public class AuthService {
    private UserRepository userRepo;
    private User currentUser = null;

    public AuthService(UserRepository ur){ this.userRepo = ur; }

    public Optional<User> login(String id, String pw){
        Optional<User> u = userRepo.findById(id);
        if (!u.isPresent()) return Optional.empty();
        if (!u.get().checkPassword(pw)) return Optional.empty();
        currentUser = u.get();
        return u;
    }

    public void logout(){ currentUser = null; }
    public Optional<User> currentUser(){ return Optional.ofNullable(currentUser); }
    public boolean changePassword(String id, String oldPw, String newPw){
        Optional<User> u = userRepo.findById(id);
        if (!u.isPresent()) return false;
        if (!u.get().checkPassword(oldPw)) return false;
        u.get().setPassword(newPw);
        return true;
    }
}
