package com.foodorder.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DbUtil {

    private static final Properties PROPS = new Properties();
    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;
    private static final Path UPLOAD_DIR;

    static {
        try (InputStream in = DbUtil.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                PROPS.load(in);
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
        URL = get("DB_URL", "db.url",
                "jdbc:mysql://localhost:3306/food_order?useSSL=false&allowPublicKeyRetrieval=true"
                        + "&serverTimezone=Asia/Jakarta&characterEncoding=UTF-8");
        USER = get("DB_USER", "db.user", "root");
        PASSWORD = get("DB_PASSWORD", "db.password", "");
        UPLOAD_DIR = Paths.get(get("UPLOAD_DIR", "upload.dir", "./uploads")).toAbsolutePath().normalize();
    }

    private DbUtil() {
    }

    private static String get(String envName, String propName, String defaultValue) {
        String v = System.getenv(envName);
        if (v == null || v.isBlank()) {
            v = PROPS.getProperty(propName);
        }
        return (v == null || v.isBlank()) ? defaultValue : v.trim();
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static Path getUploadDir() {
        return UPLOAD_DIR;
    }
}
