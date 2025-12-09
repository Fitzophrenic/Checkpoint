package com.checkpointfrontend;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
public class httpClientCheckPoint {
    public Map<String, Object> createUser(String userName, String password) { //not going to be useable until login screen is created
        String request = String.format("username: %s %n create-user: %s", userName, password);
        File sendFile = new File("last-call.txt");
        return compileSend(request, sendFile);
    }
    public Map<String, Object> createProject(String projectName, String userName) {
        String request = String.format("username: %s %n create-project: %s", userName, projectName);
        File sendFile = new File("last-call.txt");
        return compileSend(request, sendFile);
    }
    public Map<String, Object> deleteProject(String projectID, String username) {
        String request = String.format("username: %s %n projectID: %s %n delete-project", username, projectID);
        File sendFile = new File("last-call.txt");
        return compileSend(request, sendFile);
    }
    public Map<String, Object> removeUserFromProject (String projectID, String username, String ownerID) {
        String request = String.format("username: %s %n projectID: %s %n remove-user-from-project: %s", ownerID, projectID, username);
        File sendFile = new File("last-call.txt");
        return compileSend(request, sendFile);
    }
    public Map<String, Object> addUserToProject(String projectID, String username, String ownerID, String permissionLevel) {
        String request = String.format("username: %s %n projectID: %s %n add-user-to-project: %s : %s", ownerID, projectID, username, permissionLevel);
        File sendFile = new File("last-call.txt");
        return compileSend(request, sendFile);
    }
    public Map<String, Object> changePermissionLevel(String projectID, String username, String ownerID, String permissionLevel) {
        String request = String.format("username: %s %n projectID: %s %n change-permission-level: %s : %s", ownerID, projectID, username, permissionLevel);
        File sendFile = new File("last-call.txt");
        return compileSend(request, sendFile);
    }
    public Map<String, Object> requestUserJson(String username) {
        String request = String.format("username: %s %n get-user-json", username);
        File sendFile = new File("last-call.txt");
        return compileSend(request, sendFile);
    }
    public Map<String, Object> getProjectBoards(String projectID) {
        String request = String.format("projectID: %s %n get-project-boards", projectID);
        File sendFile = new File("last-call.txt");
        return compileSend(request, sendFile);
    }
    public Map<String, Object> addBoardToProject(String username /*for checking permission level */, String projectID, String boardName, String content) {
        String request = String.format("username: %s %n projectID: %s %n add-board-to-project: %s : %s", username, projectID, boardName, content);
        File sendFile = new File("last-call.txt");
        return compileSend(request, sendFile);
    }
    public Map<String, Object> loginUser(String userName, String password) {
        String request = String.format("username: %s %n login: %s", userName, password);
        File sendFile = new File("last-call.txt");
        return compileSend(request, sendFile);
    }
    public Map<String, Object> updateBoardSection(String username /*for checking permission level */, String projectID, String boardName, String content) {
        // String request = String.format("userID: %s %n projectID: %s %n update-board-section: %s : %s", userID, projectID, boardName, content);
        
        ObjectMapper mapper = new ObjectMapper();
        String escapedContent = content;
        try {
            escapedContent = mapper.writeValueAsString(content); // converts newlines to \n, wraps in quotes
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    String request = "username: " + username + "\n" +
                        "projectID: " + projectID + "\n" +
                        "update-board-section: " + boardName + " : " + escapedContent;
        File sendFile = new File("last-call.txt");
        return compileSend(request, sendFile);
    }
    public Map<String, Object> getBoardSection(String projectID, String boardName) {
        String request = String.format("projectID: %s %n get-board-data: %s",projectID, boardName);
        File sendFile = new File("last-call.txt");
        return compileSend(request, sendFile);
    }
    public Map<String, Object> getUserCalendar(String username) {
        String request = String.format("username: %s %n request-user-calander",username);
        File sendFile = new File("last-call.txt");
        return compileSend(request, sendFile);
    }
    public Map<String, Object> updateUserCalendar(String username, String content) {
        String request = String.format("username: %s %n update-user-calander: %s",username, content);
        File sendFile = new File("last-call.txt");
        return compileSend(request, sendFile);
    }
    public Map<String, Object> getProjectCalendar(String projectID) {
        String request = String.format("projectID: %s %n request-project-calander",projectID);
        File sendFile = new File("last-call.txt");
        return compileSend(request, sendFile);
    }
    public Map<String, Object> updateProjectCalendar(String username, String projectID, String content) {
        String request = String.format("username: %s %n projectID: %s %n update-project-calander: %s",username, projectID, content);
        File sendFile = new File("last-call.txt");
        return compileSend(request, sendFile);
    }
    public Map<String, Object> compileSend(String request, File sendFile) {
        CloseableHttpClient client = httpClientInstance.CLIENT;
        HttpPost post = new HttpPost("http://localhost:8080/api/files/upload");

        try (FileWriter fw = new FileWriter(sendFile)) {
            fw.write(request);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        try {
            HttpEntity entity = MultipartEntityBuilder.create()
                    .addBinaryBody("file", sendFile)
                    .build();

            post.setEntity(entity);

            ObjectMapper mapper = new ObjectMapper();
            
            Map<String, Object> response = client.execute(
                post,
                httpResponse -> {
                    String json = EntityUtils.toString(httpResponse.getEntity());
                    return mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
                }
            );
            System.out.println(response.toString());

            return response;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public void httpTest() throws Exception { // test function can ignore
        CloseableHttpClient client = httpClientInstance.CLIENT;
        HttpPost post = httpClientInstance.POSTURL;
        try {

            HttpEntity entity = MultipartEntityBuilder.create()
                    .addBinaryBody("file", new File("create-user.txt"))
                    .build();

            post.setEntity(entity);

            String response = client.execute(post, httpResponse ->
                    EntityUtils.toString(httpResponse.getEntity())
            );

            System.out.println(response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}