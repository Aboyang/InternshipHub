package careerhub.models;

public abstract class User {
    protected String id;
    protected String name;
    protected String password;
    protected FilterSettings filterSettings;

    public User(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public String getId() { return id; }
    public String getName() { return name; }

    public boolean checkPassword(String p) {
        return password != null && password.equals(p);
    }

    public void changePassword(String newPw) { this.password = newPw; }

    public abstract String getType();
}
