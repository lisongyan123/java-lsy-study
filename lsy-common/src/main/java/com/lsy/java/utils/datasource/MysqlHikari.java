package com.lsy.java.utils.datasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * github 文档
 * https://github.com/brettwooldridge/HikariCP
 */
public class MysqlHikari {
    public static void main(String[] args) {
        try {
            HikariConfig config = new HikariConfig("/db.properties");
            try (HikariDataSource dataSource = new HikariDataSource(config);
                 Connection conn = dataSource.getConnection()) {
                createTable(conn);
                insertData(conn, 1, "A1");
                insertData(conn, 2, "A2");
                selectData(conn);
                updateData(conn, "test_update", 1);
                selectData(conn);
                deleteData(conn, 2);
                selectData(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS test_hikaricp (id INT, name VARCHAR(50))";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
            System.out.println("Table created successfully.");
        }
    }

    private static void insertData(Connection conn, int id, String name) throws SQLException {
        String sql = "INSERT INTO test_hikaricp (id, name) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.executeUpdate();
            System.out.println("Data inserted successfully.");
        }
    }

    private static void selectData(Connection conn) throws SQLException {
        String sql = "SELECT * FROM test_hikaricp";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            System.out.println("User Data:");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                System.out.println("ID: " + id + ", Name: " + name);
            }
            System.out.println();
        }
    }

    private static void updateData(Connection conn, String name, int id) throws SQLException {
        String sql = "UPDATE test_hikaricp SET name = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            System.out.println("Data updated successfully.");
        }
    }

    private static void deleteData(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM test_hikaricp WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Data deleted successfully.");
        }
    }
}