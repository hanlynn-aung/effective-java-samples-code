package chapter2.item2.good;

import java.util.Objects;

public final class GoodBuilderUser {
    private final String name;
    private final String email;
    private final String phone;
    private final boolean admin;

    private GoodBuilderUser(Builder builder) {
        this.name = Objects.requireNonNull(builder.name, "name");
        this.email = builder.email;
        this.phone = builder.phone;
        this.admin = builder.admin;
    }

    public static Builder builder(String name) { return new Builder(name); }

    public static final class Builder {
        private final String name;
        private String email;
        private String phone;
        private boolean admin;

        private Builder(String name) { this.name = name; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder admin(boolean admin) { this.admin = admin; return this; }
        public GoodBuilderUser build() { return new GoodBuilderUser(this); }
    }

    public String name() { return name; }
    public String email() { return email; }
    public String phone() { return phone; }
    public boolean admin() { return admin; }
}
