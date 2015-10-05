package net.cassite.rbac;

public interface RestrictionUR<User> {
        boolean distributabled(String role, User user);
}
