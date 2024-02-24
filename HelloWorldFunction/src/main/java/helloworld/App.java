package helloworld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import entities.EntityEntity;

/**
 * Handler for requests to Lambda function.
 */
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        // Cambios para insertar entidades
        final String entitiesJson = input.getBody();
        boolean success = insertEntities(entitiesJson);

        String output;
        int statusCode;
        if (success) {
            output = "{ \"message\": \"Entities inserted successfully\" }";
            statusCode = 200;
        } else {
            output = "{ \"message\": \"Error inserting entities\" }";
            statusCode = 500;
        }

        return response
                .withStatusCode(statusCode)
                .withBody(output);
    }

    // Método para insertar entidades en la base de datos
    private boolean insertEntities(String entitiesJson) {
        Gson gson = new Gson();
        EntityEntity[] entities = gson.fromJson(entitiesJson, EntityEntity[].class);

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/your_database", "username", "password")) {
            for (EntityEntity entity : entities) {
                String query = "INSERT INTO ENTITY (EntityName, EntityCode, Web, Email) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = con.prepareStatement(query)) {
                    pstmt.setString(1, entity.getEntityName());
                    pstmt.setString(2, entity.getEntityCode());
                    pstmt.setString(3, entity.getWeb());
                    pstmt.setString(4, entity.getEmail());
                    pstmt.executeUpdate();
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
