package com.checkpoint.checkpointbackend;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Component
public class SQLRequest {
        @Autowired
        private DataSource dataSource;

        private Connection conn = null;

        @Autowired
        JSONProjectConverter projectConverter;
        @Autowired
        JSONProjectBoardConverter projectBoardConverter;
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

        // public String createUser(String userName, String password) {
            
        //     createUser(userName, password);
        //     return userID;
        // }

        public void createUser(String username, String password){
            String sqlCheckIfIDExist = "SELECT COUNT(*) FROM appUser WHERE username = ?";
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
            String sqlRequest = "INSERT INTO appUser VALUES (?, ?, ?)";
            String userJson = String.format(
            """
            {
                "userName": "%s",
                "projects": []
            }
            """, username);
            try (PreparedStatement stmt = conn.prepareStatement(sqlRequest)){
                stmt.setString(1, username);           // userID
                stmt.setString(2, password);         // username
                stmt.setString(3, userJson); 
                int rows = stmt.executeUpdate();
                System.out.println("Inserted " + rows + " row(s) into appUser table.");
            } catch (SQLException e) {
                System.err.println("Database operation failed: " + e.getMessage());
            }
        }

        public String createProject(String projectName, String ownerID) {
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
            return projectID;
        }

        private void createProject(String projectID, String projectName, String ownerID){
            String sqlRequest = "INSERT INTO Project (projectID, projectName, OwnerID, projectJSON, boardsJSON) VALUES (?, ?, ?, ?, ?)";

            // String projectJson = String.format(
            // """
            // {
            //     "projectID": "%s",
            //     "projectName": "%s",
            //     "users": [
            //         {"userID": "%s", "permissionLevel": "o"}
            //     ]
            // }
            // """, projectID, projectName, ownerID);
            
            String projectJson = String.format(
                "{\"projectID\":\"%s\",\"projectName\":\"%s\",\"users\":[{\"username\":\"%s\",\"permissionLevel\":\"o\"}]}",
                projectID, projectName, ownerID
            );
            String boardsJSON = "{ \"sections\": [] }";

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
            addUserToProject(projectID, ownerID, "o");
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

        public void removeUserFromProject(String projectID, String username) {
            
            String sqlRequest = "{CALL removeUserFromProject(?, ?)}";
            try (CallableStatement  stmt = conn.prepareCall(sqlRequest)){
                stmt.setString(1, projectID); 
                stmt.setString(2, username); 

                stmt.execute();
                System.out.println("Removed" +username +"from" + projectID + ".");
            } catch (SQLException e) {
                System.err.println("Database operation failed: " + e.getMessage());
            }
        }

        public void addUserToProject(String projectID, String username, String permLevel) {
            if (!(permLevel.equals("r") || permLevel.equals("w") || permLevel.equals("o")) ){
                System.out.println("invalid permmissionLevel");
                return;
            }

            String sqlRequest = "{CALL addUserToProject(?, ?, ?)}";
            try (CallableStatement  stmt = conn.prepareCall(sqlRequest)){
                stmt.setString(1, projectID);
                stmt.setString(2, username);
                stmt.setString(3, permLevel);
                stmt.execute();
                System.out.println("added " +username +" to  "+ projectID +" with permission level "+ permLevel+".");
            } catch (SQLException e) {
                System.err.println("addUserToProject operation failed: " + e.getMessage());
            }
        }

        public void changePermissionLevel(String projectID, String username, String permLevel) {
            if (!(permLevel.equals("r") || permLevel.equals("w") || permLevel.equals("o")) ){
                System.out.println("invalid permmissionLevel");
                return;
            }

            String sqlRequest = "{CALL changePermissionLevel(?, ?, ?)}";
            try (CallableStatement  stmt = conn.prepareCall(sqlRequest)){
                stmt.setString(1, projectID);
                stmt.setString(2, username);
                stmt.setString(3, permLevel);
                stmt.execute();
                System.out.println("changed" +username +"permission level in" + projectID + "to "+ permLevel+".");
            } catch (SQLException e) {
                System.err.println("Database operation failed: " + e.getMessage());
            }
        }
        public String checkPermissionLevel(String projectID, String username) {
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
                            if (user.getUsername().equals(username)) {
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

        public Map<String,Object> requestProjectBoard(String projectID) {
            String sql = "SELECT boardsJSON FROM Project WHERE projectID = ?";
            ObjectMapper mapper = new ObjectMapper();

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, projectID);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) return Map.of("sections", List.of());

                    String jsonStr = rs.getString("boardsJSON");
                    if (jsonStr == null || jsonStr.isBlank()) return Map.of("sections", List.of());

                    return mapper.readValue(jsonStr, Map.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return Map.of("error", e.getMessage());
            }
        }
    public Map<String,Object> requestUserJson(String username) {
        String sql = "SELECT userJSON FROM appUser WHERE username = ?";
        ObjectMapper mapper = new ObjectMapper();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return Map.of(); // no user found

                String jsonStr = rs.getString("userJSON");
                if (jsonStr == null || jsonStr.isBlank()) return Map.of();

                return mapper.readValue(
                    jsonStr,
                    Map.class
                    );
                }
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", e.getMessage());
        }
    }
    public void addBoardToProject(String username /*for checking permission level */, String projectID, String boardName, String content) {
        if(!(checkPermissionLevel(projectID, username).equals("o") || checkPermissionLevel(projectID, username).equals("w"))) {
            return;
        }
        String sql =
            "UPDATE Project " +
            "SET boardsJSON = JSON_ARRAY_APPEND( " +
            "    boardsJSON, " +
            "    '$.sections', " +
            "    JSON_OBJECT('boardName', ?, 'content', ?) " +
            ") " +
            "WHERE projectID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, boardName);
            stmt.setString(2, content); // prolly going to be nothing most the time
            stmt.setString(3, projectID);
            stmt.executeUpdate();
        } catch (SQLException ex) {
        }
    }
    public void updateBoardSection(String username /*for checking permission level */, String projectID, String boardName, String content) {
        if(!(checkPermissionLevel(projectID, username).equals("o") || checkPermissionLevel(projectID, username).equals("w"))) {
            return;
        }
        String sqlRequest = "SELECT boardsJSON FROM Project WHERE projectID = ?";
        try (CallableStatement stmt = conn.prepareCall(sqlRequest)) {
                stmt.setString(1, projectID);
                try (ResultSet rs = stmt.executeQuery()) {
                    if(!rs.next()) {
                        return;
                    }
                    String json = rs.getString("boardsJSON");
                    JSONProjectBoard projectBoard = projectBoardConverter.convertToEntityAttribute(json);
                    if (projectBoard.getSections() == null) {
                        return;
                    }
                    int index = -1;
                    List<JSONProjectBoardSection> sections = projectBoard.getSections();
                    for (int i = 0; i < sections.size(); i++) {
                        if (sections.get(i).getBoardName().equals(boardName)) {
                            index = i;
                            sections.get(i).setContent(content);
                            break;
                        }
                    }
                    String sqlUpdate =
                        "UPDATE Project " +
                        "SET boardsJSON = JSON_SET(boardsJSON, CONCAT('$.sections[', ?, '].content'), ?) " +
                        "WHERE projectID = ?";

                    try (PreparedStatement updateStmt = conn.prepareStatement(sqlUpdate)) {
                        updateStmt.setInt(1, index);
                        updateStmt.setString(2, content);
                        updateStmt.setString(3, projectID);
                        updateStmt.executeUpdate();
                    }
                }
            }
            catch (SQLException e) {
                System.err.println("Database operation failed: " + e.getMessage());
            }
    }
    public String getBoardSection(String projectID, String boardName) {
        String sqlRequest = "SELECT boardsJSON FROM Project WHERE projectID = ?";
        String contenta = null;
        try (CallableStatement stmt = conn.prepareCall(sqlRequest)) {
                stmt.setString(1, projectID);
                try (ResultSet rs = stmt.executeQuery()) {
                    if(!rs.next()) {
                        return null;
                    }
                    String json = rs.getString("boardsJSON");
                    JSONProjectBoard projectBoard = projectBoardConverter.convertToEntityAttribute(json);
                    if (projectBoard.getSections() == null) {
                        return null;
                    }
                    int index = -1;
                    List<JSONProjectBoardSection> sections = projectBoard.getSections();
                    for (int i = 0; i < sections.size(); i++) {
                        if (sections.get(i).getBoardName().equals(boardName)) {
                            index = i;
                            contenta = sections.get(i).getContent();
                        }
                    }
                }
            }
        catch (SQLException e) {
            System.err.println("Database operation failed: " + e.getMessage());
        }
        return contenta;
    }
    private void updateUserJSON(String userID) {

    }
}
