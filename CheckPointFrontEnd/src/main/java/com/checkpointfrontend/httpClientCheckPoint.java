package com.checkpointfrontend;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

public class httpClientCheckPoint {
    public void createUser(String userName) { //not going to be useable until login screen is created
        String request = String.format("userName: %s %n create-user", userName);
        File sendFile = new File("create-user.txt");
        compileSend(request, sendFile);
    }
    public void createProject(String projectName, String userID) {
        String request = String.format("userID: %s %n create-project: %s", userID, projectName);
        File sendFile = new File("create-project.txt");
        compileSend(request, sendFile);
    }
    public void deleteProject(String projectID, String userID) {
        String request = String.format("userID: %s %n projectID: %s %n delete-project", userID, projectID);
        File sendFile = new File("delete-project.txt");
        compileSend(request, sendFile);
    }
    public void removeUserFromProject (String projectID, String userID, String ownerID) {
        String request = String.format("userID: %s %n projectID: %s %n remove-user-from-project: %s", ownerID, projectID, userID);
        File sendFile = new File("remove-user-from-project.txt");
        compileSend(request, sendFile);
    }
    public void addUserToProject(String projectID, String userID, String ownerID, String permissionLevel) {
        String request = String.format("userID: %s %n projectID: %s %n add-user-to-project: %s : %s", ownerID, projectID, userID, permissionLevel);
        File sendFile = new File("add-user-to-project.txt");
        compileSend(request, sendFile);
    }
    public void changePermissionLevel(String projectID, String userID, String ownerID, String permissionLevel) {
        String request = String.format("userID: %s %n projectID: %s %n change-permission-level: %s : %s", ownerID, projectID, userID, permissionLevel);
        File sendFile = new File("change-permission-level.txt");
        compileSend(request, sendFile);
    }
    public void requestUserJson(String userID) {
        String request = String.format("userID: %s %n get-user-json", userID);
        File sendFile = new File("get-user-json.txt");
        compileSend(request, sendFile);
    }
    public void getProjectBoards(String projectID) {
        String request = String.format("projectID: %s %n get-project-boards", projectID);
        File sendFile = new File("get-project-boards.txt");
        compileSend(request, sendFile);
    }
    public void addBoardToProject(String userID /*for checking permission level */, String projectID, String boardName, String content) {
        String request = String.format("userID: %s %n projectID: %s %n add-board-to-project: %s : %s", userID, projectID, boardName, content);
        File sendFile = new File("add-board-to-project.txt");
        compileSend(request, sendFile);
    }
    public void updateBoardSection(String userID /*for checking permission level */, String projectID, String boardName, String content) {
        String request = String.format("userID: %s %n projectID: %s %n update-board-section: %s : %s", userID, projectID, boardName, content);
        File sendFile = new File("update-board-section.txt");
        compileSend(request, sendFile);
    }
    public void compileSend(String request, File sendFile) {
        CloseableHttpClient client = httpClientInstance.CLIENT;
        HttpPost post = httpClientInstance.POSTURL;

        try (FileWriter fw = new FileWriter(sendFile)) {
            fw.write(request);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        try {
            HttpEntity entity = MultipartEntityBuilder.create()
                    .addBinaryBody("file", sendFile)
                    .build();

            post.setEntity(entity);

            String response = client.execute(
                    post,
                    httpResponse -> EntityUtils.toString(httpResponse.getEntity())
            );

            System.out.println(response);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public void httpTest() throws Exception { // test function can ignore
        CloseableHttpClient client = httpClientInstance.CLIENT;
        HttpPost post = httpClientInstance.POSTURL;
        try {

            HttpEntity entity = MultipartEntityBuilder.create()
                    .addBinaryBody("file", new File("test.txt"))
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