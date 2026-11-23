package com.foodorder.util;

import org.mindrot.jbcrypt.BCrypt;

/** Hash & verifikasi password dengan BCrypt (prefiks $2a$). */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static String hash(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(10));
    }

    public static boolean verify(String plain, String hash) {
        if (plain == null || hash == null || hash.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(plain, hash);
        } catch (IllegalArgumentException e) { // hash tidak valid / prefiks tidak dikenali
            return false;
        }
    }

    /** Pembuat hash manual untuk seed: java -cp target/classes:jbcrypt.jar com.foodorder.util.PasswordUtil admin123 */
    public static void main(String[] args) {
        for (String a : args) {
            System.out.println(a + " -> " + hash(a));
        }
    }
}
