package com.checkpoint.checkpointbackend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

//mockito - no real db required
@ExtendWith(MockitoExtension.class)
public class SQLRequestTest {

    @Test
    public void createProject_whenNewIdInserted_returnsGeneratedIdAndInsertsProject() throws Exception {
        SQLRequest sqlRequest = spy(new SQLRequest());

        // Mocks for JDBC
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockSelectStmt = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);
        PreparedStatement mockInsertStmt = mock(PreparedStatement.class);

        // When a SELECT COUNT... is prepared return the select statement
        when(mockConn.prepareStatement(startsWith("SELECT COUNT"))).thenReturn(mockSelectStmt);
        when(mockSelectStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true);
        // Simulate ID DNE
        when(mockRs.getInt(1)).thenReturn(0);

        // When an INSERT INTO Project is prepared return the insert statement
        when(mockConn.prepareStatement(startsWith("INSERT INTO Project"))).thenReturn(mockInsertStmt);
        when(mockInsertStmt.executeUpdate()).thenReturn(1);

        // Prevent addUserToProject from performing more DB operations in this unit test
        doNothing().when(sqlRequest).addUserToProject(anyString(), anyString(), anyString());

        // Inject mock connection into private field 'conn'
        Field connField = SQLRequest.class.getDeclaredField("conn");
        connField.setAccessible(true);
        connField.set(sqlRequest, mockConn);

        // Run method under test
        String projectId = sqlRequest.createProject("My Project", "owner123");

        assertNotNull(projectId, "Returned project ID should not be null");
        assertFalse(projectId.isBlank(), "Returned project ID should not be blank");

        // Capture the ID passed into the SELECT prepared statement's setString call
        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockSelectStmt).setString(eq(1), idCaptor.capture());
        String idUsedInSelect = idCaptor.getValue();
        assertEquals(projectId, idUsedInSelect, "The project ID checked for existence should match the returned ID");

        // Verify that the insert statement was executed
        verify(mockInsertStmt).executeUpdate();

        // Verify prepared insert received parameters (we expect at least setString called for projectID)
        verify(mockInsertStmt).setString(eq(1), eq(projectId));
    }
}
