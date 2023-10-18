package ru.fisunov.http.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcProvider {
    private static final String DB_URL = "jdbc:postgresql://localhost/java-http";
    private static final String DB_LOGIN = "postgres";
    private static final String DB_PASSWORD = "postgres";
    private final Connection databaseConnection;
    private static final String SELECT_ALL = "select * from items";
    private final List<Item> itemList;

    public JdbcProvider() throws SQLException {
        this.databaseConnection = DriverManager.getConnection(DB_URL, DB_LOGIN, DB_PASSWORD);
        this.itemList = new ArrayList<>();
    }

    public List<Item> getItemsFromDatabase() {
        try (ResultSet rs = sqlQuery(SELECT_ALL)) {
            while (rs.next()) {
                itemList.add(new Item(rs.getLong("id"), rs.getString("title")));
            }
            return itemList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ResultSet sqlQuery(String query) {
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(query);
            return ps.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}