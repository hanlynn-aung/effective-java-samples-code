package chapter10.good;

import java.sql.SQLException;

/**
 * Translates a low-level SQLException into a higher-level, caller-appropriate
 * exception so the abstraction boundary is not violated.
 */
public final class GoodTranslation {

    public static final class ConfigLoaderException extends Exception {
        public ConfigLoaderException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public int loadConfig(String key) throws ConfigLoaderException {
        try {
            return queryDatabase(key);
        } catch (SQLException e) {
            throw new ConfigLoaderException("failed loading config for " + key, e);
        }
    }

    private int queryDatabase(String key) throws java.sql.SQLException {
        throw new java.sql.SQLException("integrity constraint violated on " + key);
    }
}