package chapter2.item2.bad;

public final class BadTelescopingUser {
    private final String name;
    private final String email;
    private final String phone;
    private final boolean admin;

    public BadTelescopingUser(String name) { this(name, null, null, false); }
    public BadTelescopingUser(String name, String email) { this(name, email, null, false); }
    public BadTelescopingUser(String name, String email, String phone) { this(name, email, phone, false); }
    public BadTelescopingUser(String name, String email, String phone, boolean admin) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.admin = admin;
    }

    public String name() { return name; }
    public String email() { return email; }
    public String phone() { return phone; }
    public boolean admin() { return admin; }
}
