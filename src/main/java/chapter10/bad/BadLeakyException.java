package chapter10.bad;

import java.sql.SQLException;

public final class BadLeakyException {

    public int loadConfig(String key) {
        try {
            return queryDatabase(key);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int queryDatabase(String key) throws SQLException {
        throw new SQLException("integrity constraint violated on " + key);
    }
}