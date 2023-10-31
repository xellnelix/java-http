package ru.fisunov.http.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemsWebApplication implements MyWebApplication {
    private final String name;
    private final List<Item> items;
    private final JdbcProvider database;
    private final ObjectMapper objectMapper;


    public ItemsWebApplication() throws SQLException {
        this.name = "Items Web Application";
        this.database = new JdbcProvider();
        this.items = database.getItemsFromDatabase();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void execute(Request request, OutputStream output) throws IOException {
        List<String> jsonArray = new ArrayList<>();

        for (Item item : items) {
            jsonArray.add(objectMapper.writeValueAsString(item));
        }

        output.write(("" +
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "Access-Control-Allow-Origin: *\r\n" +
                "\r\n" +
                jsonArray
        ).getBytes(StandardCharsets.UTF_8));
    }
}