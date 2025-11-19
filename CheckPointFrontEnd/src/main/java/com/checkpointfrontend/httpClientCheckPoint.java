package com.checkpointfrontend;
import java.io.File;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;

public class httpClientCheckPoint {
    public void http() throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("http://localhost:8080/api/files/upload");

            HttpEntity entity = MultipartEntityBuilder.create()
                    .addBinaryBody("file", new File("test.txt"))
                    .build();

            post.setEntity(entity);

            String response = client.execute(post, httpResponse ->
                    new String(httpResponse.getEntity().getContent().readAllBytes())
            );

            System.out.println(response);
        }
    }
}