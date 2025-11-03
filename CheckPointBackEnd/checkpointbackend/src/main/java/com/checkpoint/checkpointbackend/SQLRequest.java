package com.checkpoint.checkpointbackend;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class SQLRequest {
        @Autowired
        private DataSource dataSource;

        private Connection conn = null;
    
        @PostConstruct
        private void setConnection() {
            try {
                conn = dataSource.getConnection();
                System.out.println("sucessfully connected and running");
                System.out.println(conn.toString());
                
            } catch (SQLException e) {
                System.err.println("Database connection failed: " + e.getMessage());
            }

        }

        public void createUser(String userName) {
            String sqlCheckIfIDExist = "SELECT COUNT(*) FROM appUser WHERE userID = ?";
            String userID = IDGenerator.generateID();
            try (PreparedStatement stmt = conn.prepareStatement(sqlCheckIfIDExist)){
                while(true) {
                    stmt.setString(1, userID);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            int count = rs.getInt(1);
                            if (count == 0) {
                                break;
                            } else {
                                userID = IDGenerator.generateID();
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("Database operation failed: " + e.getMessage());
            }
            createUser(userID, userName);
        }

        private void createUser(String userID, String userName){
            String sqlRequest = "INSERT INTO appUser VALUES (?, ?, ?)";
            String userJson = String.format(
            """
            {
                "userID": "%s"
                "userName": "%s"
            }
            """, userID, userName);
            try (PreparedStatement stmt = conn.prepareStatement(sqlRequest)){
                stmt.setString(1, userID);           // project ID
                stmt.setString(2, userName);         // project name
                stmt.setString(3, userJson); 
                int rows = stmt.executeUpdate();
                System.out.println("Inserted " + rows + " row(s) into appUser table.");
            } catch (SQLException e) {
                System.err.println("Database operation failed: " + e.getMessage());
            }
        }

        public void createProject(String projectName, String ownerID) {
            String sqlCheckIfIDExist = "SELECT COUNT(*) FROM Project WHERE projectID = ?";
            String projectID = IDGenerator.generateID();
            try (PreparedStatement stmt = conn.prepareStatement(sqlCheckIfIDExist)){
                while(true) {
                    stmt.setString(1, projectID);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            int count = rs.getInt(1);
                            if (count == 0) {
                                break;
                            } else {
                                projectID = IDGenerator.generateID();
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("Database operation failed: " + e.getMessage());
            }
            createProject(projectID, projectName, ownerID);
        }

        private void createProject(String projectID, String projectName, String ownerID){
            String sqlRequest = "INSERT INTO Project VALUES (?, ?, ?, ?, ?)";

            String projectJson = String.format(
            """
            {
                "projectID": "%s"
                "projectName": "%s",
                "users": [
                    {"userID": "%s", "permissionLevel": "o"}
                ]
            }
            """, projectID, projectName, ownerID);

            String boardsJSON = "{}";

            try (PreparedStatement stmt = conn.prepareStatement(sqlRequest)){
                stmt.setString(1, projectID);           // project ID
                stmt.setString(2, projectName);         // project name
                stmt.setString(3, ownerID);             // owner
                stmt.setString(4, projectJson);         // JSON column
                stmt.setString(5, boardsJSON);          // JSON column
                int rows = stmt.executeUpdate();
                System.out.println("Inserted " + rows + " row(s) into Project table.");
            } catch (SQLException e) {
                System.err.println("Database operation failed: " + e.getMessage());
            }
        }

        public void deleteProject(String projectID) {
            String sqlRequest = "CALL deleteProject(?)";
            try (CallableStatement  stmt = conn.prepareStatement(sqlRequest)){
                stmt.setString(1, projectID); 
                int rows = stmt.executeUpdate();
                System.out.println("Inserted " + rows + " row(s) into Project table.");
            } catch (SQLException e) {
                System.err.println("Database operation failed: " + e.getMessage());
            }

        }

}
