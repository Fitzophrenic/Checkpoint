package com.checkpointfrontend;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;

//stores the clientInstance
public class httpClientInstance {
     public static final CloseableHttpClient CLIENT = HttpClients.createDefault();
     public static final HttpPost POSTURL = new HttpPost("http://localhost:8080/api/files/upload");

}
