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

        @Autowired
        JSONProjectConverter projectConverter;

        @PostConstruct
        private void testConnection() {
            try {
                conn = dataSource.getConnection();
                System.out.println("sucessfully connected and running");
                System.out.println(conn.toString());
                
            } catch (SQLException e) {
                System.err.println("Database connection failed: " + e.getMessage());
            }
        }

        public Connection getConnection() {
            return conn;
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
                "userID": "%s",
                "userName": "%s"
            }
            """, userID, userName);
            try (PreparedStatement stmt = conn.prepareStatement(sqlRequest)){
                stmt.setString(1, userID);           // userID
                stmt.setString(2, userName);         // username
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
                "projectID": "%s",
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

        public void deleteProject(String projectID, String userID) {
            if(!checkPermissionLevel(projectID, userID).equals("o")){
                return;
            }
            String sqlRequest = "{CALL deleteProject(?)}";
            try (CallableStatement  stmt = conn.prepareCall(sqlRequest)){
                stmt.setString(1, projectID); 
                stmt.execute();
                System.out.println("Deleted" + projectID + "from Project table.");
            } catch (SQLException e) {
                System.err.println("Database operation failed: " + e.getMessage());
            }
        }

        public void removeUserFromProject(String projectID, String userID) {
            
            String sqlRequest = "{CALL removeUserFromProject(?, ?)}";
            try (CallableStatement  stmt = conn.prepareCall(sqlRequest)){
                stmt.setString(1, projectID); 
                stmt.setString(2, userID); 

                stmt.execute();
                System.out.println("Removed" +userID +"from" + projectID + ".");
            } catch (SQLException e) {
                System.err.println("Database operation failed: " + e.getMessage());
            }
        }

        public void addUserToProject(String projectID, String userID, String permLevel) {
            if (!(permLevel.equals("r") || permLevel.equals("w") || permLevel.equals("o")) ){
                System.out.println("invalid permmissionLevel");
                return;
            }

            String sqlRequest = "{CALL addUserToProject(?, ?, ?)}";
            try (CallableStatement  stmt = conn.prepareCall(sqlRequest)){
                stmt.setString(1, projectID);
                stmt.setString(2, userID);
                stmt.setString(3, permLevel);
                stmt.execute();
                System.out.println("added" +userID +" to  "+ projectID +" with permission level "+ permLevel+".");
            } catch (SQLException e) {
                System.err.println("Database operation failed: " + e.getMessage());
            }
        }

        public void changePermissionLevel(String projectID, String userID, String permLevel) {
            if (!(permLevel.equals("r") || permLevel.equals("w") || permLevel.equals("o")) ){
                System.out.println("invalid permmissionLevel");
                return;
            }

            String sqlRequest = "{CALL changePermissionLevel(?, ?, ?)}";
            try (CallableStatement  stmt = conn.prepareCall(sqlRequest)){
                stmt.setString(1, projectID);
                stmt.setString(2, userID);
                stmt.setString(3, permLevel);
                stmt.execute();
                System.out.println("changed" +userID +"permission level in" + projectID + "to "+ permLevel+".");
            } catch (SQLException e) {
                System.err.println("Database operation failed: " + e.getMessage());
            }
        }
        public String checkPermissionLevel(String projectID, String userID) {
            String sqlRequest = "SELECT projectJSON FROM Project WHERE projectID = ?";
            try (CallableStatement stmt = conn.prepareCall(sqlRequest)) {
                stmt.setString(1, projectID);
                try (ResultSet rs = stmt.executeQuery()) {
                    if(!rs.next()) {
                        return null;
                    }
                    String json = rs.getString("projectJSON");
                    JSONProjectJSON project = projectConverter.convertToEntityAttribute(json);
                    if (project.getUsers() != null) {
                        for (JSONProjectUserPerm user : project.getUsers()) {
                            if (user.getUserID().equals(userID)) {
                                return user.getPermissionLevel();
                            }
                        }
                    }
                }
            }
            catch (SQLException e) {
                System.err.println("Database operation failed: " + e.getMessage());
            }

            return null;
        }
}
