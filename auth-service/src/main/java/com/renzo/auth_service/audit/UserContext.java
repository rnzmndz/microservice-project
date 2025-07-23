package com.renzo.auth_service.audit;

import java.util.Optional;

public class UserContext {
    private static final ThreadLocal<String> currentUser = new ThreadLocal<>();

    public static void setUser(String user) {
        currentUser.set(user);
    }

    public static Optional<String> getUser() {
        return Optional.ofNullable(currentUser.get());
    }

    public static void clear() {
        currentUser.remove();
    }
}