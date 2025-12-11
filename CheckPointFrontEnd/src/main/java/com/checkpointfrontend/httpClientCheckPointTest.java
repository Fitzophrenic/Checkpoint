package com.checkpointfrontend;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for httpClientCheckPoint that verify constructed request strings and that compileSend is invoked.
 * compileSend is stubbed so tests do not touch the filesystem or network.
 */
public class httpClientCheckPointTest {

    @Test
    public void getProjectBoards_buildsExpectedRequestAndCallsCompileSend() throws Exception {
        httpClientCheckPoint client = spy(new httpClientCheckPoint());

        // Stub compileSend to return a simple map without doing I/O
        when(client.compileSend(anyString(), any(File.class))).thenReturn(Map.of("status", "ok"));

        String projectID = "223i8n6Y2Cy8";
        Map<String, Object> resp = client.getProjectBoards(projectID);

        assertNotNull(resp);
        assertEquals("ok", resp.get("status"));

        // Capture the request string and file passed to compileSend
        ArgumentCaptor<String> reqCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<File> fileCap = ArgumentCaptor.forClass(File.class);
        verify(client).compileSend(reqCap.capture(), fileCap.capture());

        String requestSent = reqCap.getValue();
        File sentFile = fileCap.getValue();

        assertTrue(requestSent.contains("get-project-boards"), "Request should contain operation token");
        assertTrue(requestSent.contains(projectID), "Request should contain projectID value");
        assertEquals("last-call.txt", sentFile.getName(), "Should send last-call.txt as the request file");
    }

    @Test
    public void changePermissionLevel_buildsExpectedRequestAndCallsCompileSend() throws Exception {
        httpClientCheckPoint client = spy(new httpClientCheckPoint());
        when(client.compileSend(anyString(), any(File.class))).thenReturn(Map.of("status", "ok"));

        Map<String, Object> resp = client.changePermissionLevel("proj1", "alice", "ownerA", "r");
        assertEquals("ok", resp.get("status"));

        ArgumentCaptor<String> reqCap = ArgumentCaptor.forClass(String.class);
        verify(client).compileSend(reqCap.capture(), any(File.class));

        String requestSent = reqCap.getValue();
        assertTrue(requestSent.contains("change-permission-level"));
        assertTrue(requestSent.contains("alice"));
        assertTrue(requestSent.contains("ownerA"));
        assertTrue(requestSent.contains("proj1"));
    }
}
