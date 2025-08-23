package Main;

import Main.Controllers.MainControllers.AdminController;
import Main.Controllers.MainControllers.SelectController;
import Main.FormUtils.Validators;
import javafx.event.ActionEvent;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class AiConnection {

    public static String naturalPrompt = "";

    public static boolean validatePrompt(String prompt, ActionEvent event){
        String[] blockedWords = {
                "create", "insert", "add", "make", "build", "construct", "register", "sign up", "signup", "enroll",
                "update", "modify", "change", "edit", "alter", "revise", "adjust", "patch", "correct",
                "delete", "remove", "erase", "drop", "clear", "discard", "truncate", "destroy", "wipe",
                "replace", "overwrite", "substitute", "merge", "combine",
                "grant", "revoke", "give access", "allow", "permit", "authorize", "deny", "block", "restrict"
        };

        HashSet<String> invalidWords = new HashSet<>(Arrays.asList(blockedWords));
        String[] promptValues = prompt.split(" ");

        for(String word: promptValues){
            if(invalidWords.contains(word.toLowerCase()))
                return false;
        }
        naturalPrompt = prompt;
        generateSqlFromPrompt(event);
        return true;
    }

    public static void generateSqlFromPrompt(ActionEvent event) {
        try {

            String schemaJson = "["
                    + "{ \"table_name\": \"users\", \"columns\": ["
                    + "{\"name\":\"id\",\"type\":\"serial\"},"
                    + "{\"name\":\"username\",\"type\":\"varchar(50)\"},"
                    + "{\"name\":\"email\",\"type\":\"varchar(100)\"},"
                    + "{\"name\":\"password\",\"type\":\"varchar(255)\"},"
                    + "{\"name\":\"created_at\",\"type\":\"timestamp\"}"
                    + "]},"
                    + "{ \"table_name\": \"bank_account\", \"columns\": ["
                    + "{\"name\":\"status\",\"type\":\"varchar(20)\"},"
                    + "{\"name\":\"user_id\",\"type\":\"integer\"},"
                    + "{\"name\":\"account_number\",\"type\":\"bigint\"},"
                    + "{\"name\":\"account_type\",\"type\":\"varchar(20)\"},"
                    + "{\"name\":\"balance\",\"type\":\"numeric(12,2)\"},"
                    + "{\"name\":\"open_date\",\"type\":\"timestamp\"}"
                    + "]},"
                    + "{ \"table_name\": \"city\", \"columns\": ["
                    + "{\"name\":\"id\",\"type\":\"integer\"},"
                    + "{\"name\":\"name\",\"type\":\"varchar(100)\"},"
                    + "{\"name\":\"state_id\",\"type\":\"integer\"}"
                    + "]},"
                    + "{ \"table_name\": \"states\", \"columns\": ["
                    + "{\"name\":\"id\",\"type\":\"integer\"},"
                    + "{\"name\":\"name\",\"type\":\"varchar(100)\"}"
                    + "]},"
                    + "{ \"table_name\": \"transactions\", \"columns\": ["
                    + "{\"name\":\"transaction_id\",\"type\":\"bigint\"},"
                    + "{\"name\":\"sender_account\",\"type\":\"bigint\"},"
                    + "{\"name\":\"receiver_account\",\"type\":\"bigint\"},"
                    + "{\"name\":\"amount\",\"type\":\"numeric(15,2)\"},"
                    + "{\"name\":\"timestamp\",\"type\":\"timestamp\"},"
                    + "{\"name\":\"transaction_type\",\"type\":\"varchar(20)\"}"
                    + "]},"
                    + "{ \"table_name\": \"uuids\", \"columns\": ["
                    + "{\"name\":\"email\",\"type\":\"varchar(255)\"},"
                    + "{\"name\":\"uuid\",\"type\":\"varchar(255)\"},"
                    + "{\"name\":\"timestamp\",\"type\":\"bigint\"},"
                    + "{\"name\":\"user_id\",\"type\":\"integer\"}"
                    + "]},"
                    + "{ \"table_name\": \"admin_accounts\", \"columns\": ["
                    + "{\"name\":\"id\",\"type\":\"integer\"},"
                    + "{\"name\":\"email\",\"type\":\"varchar(100)\"},"
                    + "{\"name\":\"name\",\"type\":\"varchar(100)\"},"
                    + "{\"name\":\"password_hash\",\"type\":\"varchar(255)\"},"
                    + "{\"name\":\"created_at\",\"type\":\"timestamp\"}"
                    + "]},"
                    + "{ \"table_name\": \"deletion_logs\", \"columns\": ["
                    + "{\"name\":\"id\",\"type\":\"serial\"},"
                    + "{\"name\":\"account_number\",\"type\":\"bigint\"},"
                    + "{\"name\":\"deleted_at\",\"type\":\"timestamp\"}"
                    + "]}"
                    + "]";


            String jsonBody = "{"
                    + "\"prompt\":\"" + naturalPrompt + "\","
                    + "\"model\":\"oa_v3_16k\","
                    + "\"data_source_type\":\"postgresql\","
                    + "\"data_schema\":" + schemaJson
                    + "}";


            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://ai-query2.p.rapidapi.com/generate_sql/"))
                    // Expired keys
                    //00d6f250c8msh62aaa5a0be9609fp1491d2jsn2efc3aad4b3a
                    //989a75a89amsh08127dfd8208191p17c43cjsnb60e5869df90
                    //acfb49b74bmsh9605499ba2f221dp197143jsn48b7940026e1

                    // Valid keys
                    //b0fe425b53msh6994283b6b9f4cbp16234ajsn724a9a9070f4


                    .header("x-rapidapi-key", "7e6d2491dfmsh4628591f7652f9fp1ebc7ejsn3c4a1956a8da")
                    .header("x-rapidapi-host", "ai-query2.p.rapidapi.com")
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();


            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body();
            String[] split = body.split(":");
            executeQuery( split[1].substring(1, split[1].length() - 2).trim(), event);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void executeQuery(String sql, ActionEvent event) throws Exception {
        System.out.println(sql);
        ResultSet set = getResultSet(sql);

        if(set == null){
            Validators.showInfo("Invalid sql query","There was a problem with your prompt");
        }else{
            AdminController adminController = new AdminController();
            SelectController.rs = set;
            adminController.loadSelectScene(event);
        }

    }

    public static ResultSet getResultSet(String sql) throws Exception{
        Connection con = DBconnect.getConnection();
        PreparedStatement statement = con.prepareStatement(sql);
        ResultSet set;
        try{
           set  = statement.executeQuery();
        }catch (SQLException e){
            Validators.showInfo("Invalid sql","Please enter a valid sql query");
            return null;
        }

        if(set.next())
            return set;
        return null;
    }

//    public static void main(String[] args) {
//        generateSqlFromPrompt();
//    }


}
