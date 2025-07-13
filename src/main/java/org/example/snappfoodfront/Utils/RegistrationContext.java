package org.example.snappfoodfront.Utils;


import lombok.Getter;
import lombok.Setter;

public class RegistrationContext {
    @Getter
    @Setter
    private static String phone;
    @Getter
    @Setter
    private static String password;
    @Getter
    @Setter
    private static String role;

    public static void clear() {
        phone = null;
        password = null;
        role = null;
    }

}