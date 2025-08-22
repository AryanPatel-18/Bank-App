package Main;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;

public class AiConnection {

    public static String naturalPrompt = "";

    public static void generateSqlFromPrompt() {
        try {

            String schemaJson = "["
                    + "{ \"table_name\": \"users\", \"columns\": ["
                    + "{\"name\":\"id\",\"type\":\"serial\"},"
                    + "{\"name\":\"username\",\"type\":\"varchar(50)\"},"
                    + "{\"name\":\"email\",\"type\":\"varchar(100)\"},"
                    + "{\"name\":\"password\",\"type\":\"varchar(255)\"},"
                    + "{\"name\":\"created_at\",\"type\":\"timestamp\"}"
                    + "]},"
                    + "{ \"table_name\": \"accounts\", \"columns\": ["
                    + "{\"name\":\"id\",\"type\":\"serial\"},"
                    + "{\"name\":\"user_id\",\"type\":\"integer\"},"
                    + "{\"name\":\"account_number\",\"type\":\"bigint\"},"
                    + "{\"name\":\"account_type\",\"type\":\"varchar(20)\"},"
                    + "{\"name\":\"balance\",\"type\":\"numeric(12,2)\"},"
                    + "{\"name\":\"created_at\",\"type\":\"timestamp\"}"
                    + "]},"
                    + "{ \"table_name\": \"transactions\", \"columns\": ["
                    + "{\"name\":\"id\",\"type\":\"serial\"},"
                    + "{\"name\":\"sender_account\",\"type\":\"bigint\"},"
                    + "{\"name\":\"receiver_account\",\"type\":\"bigint\"},"
                    + "{\"name\":\"amount\",\"type\":\"numeric(12,2)\"},"
                    + "{\"name\":\"timestamp\",\"type\":\"timestamp\"}"
                    + "]},"
                    + "{ \"table_name\": \"admin_accounts\", \"columns\": ["
                    + "{\"name\":\"id\",\"type\":\"serial\"},"
                    + "{\"name\":\"email\",\"type\":\"varchar(100)\"},"
                    + "{\"name\":\"password\",\"type\":\"varchar(255)\"}"
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
                    //00d6f250c8msh62aaa5a0be9609fp1491d2jsn2efc3aad4b3a
                    .header("x-rapidapi-key", "989a75a89amsh08127dfd8208191p17c43cjsnb60e5869df90")
                    .header("x-rapidapi-host", "ai-query2.p.rapidapi.com")
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();


            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body();
            String[] split = body.split(":");
            executeQuery( split[1].substring(1, split[1].length() - 2).trim());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void executeQuery(String sql) {
        System.out.println("running");
        System.out.println(sql);
        try (Connection con = DBconnect.getConnection();
             Statement stmt = con.createStatement()) {

            boolean hasResultSet = stmt.execute(sql);

            if (hasResultSet) {

                System.out.println("has result set");
                try (ResultSet rs = stmt.getResultSet()) {
                    ResultSetMetaData meta = rs.getMetaData();
                    int columnCount = meta.getColumnCount();


                    for (int i = 1; i <= columnCount; i++) {
                        System.out.print(meta.getColumnName(i) + "\t");
                    }
                    System.out.println();

                    // Print rows
                    while (rs.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            System.out.print(rs.getString(i) + "\t");
                        }
                        System.out.println();
                    }
                }
            } else {

                int rows = stmt.getUpdateCount();
                System.out.println("Update rows : " + rows);
            }

        } catch (SQLException e) {
            System.out.println("There was a problem in executing the sql statement");
        }
    }

    public static void main(String[] args) {
        generateSqlFromPrompt();
    }
}
