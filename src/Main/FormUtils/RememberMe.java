package Main.FormUtils;

import Main.DBconnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.prefs.Preferences;


public class RememberMe {

    // This is the object that would save the value in the local machine
    public static final Preferences preferences = Preferences.userRoot().node("BankApp/rememberMe");
    public static final String uuid_name = "uuid";
    public static final String stored_email = "email";
    public static final String time_refresh = "begin";
    public static final String time_created = "create";
    private static final long duration = 1140; // Number of minutes in  a day
    private static final Connection connection = DBconnect.getConnection();

    public static void createUUID(String email) throws Exception {

        // Will be used to Create UUID
        UUID uuid = UUID.randomUUID();
        long current_time = System.currentTimeMillis();

        if(preferences.get(uuid_name, null) == null){
            // Creates a refresh timer of 48 hours
            // Basically after 48 hours the user would be forces to log in once again
            preferences.putLong(time_refresh, current_time + (48*60*60*1000));
        }

        preferences.put(uuid_name, uuid.toString());
        preferences.put(stored_email, email);
        preferences.putLong(time_created, current_time);

        // Adding these values to the database
        String query = "INSERT INTO uuids VALUES (?,?,?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, email);
        statement.setString(2, BCrypt.hashpw(uuid.toString(), BCrypt.gensalt(12)));
        statement.setLong(3, current_time);

        int rows = statement.executeUpdate();
        deleteToken(email);


//        System.out.println(rows>0?"Added Values":"Not added values");
    }

    public static boolean checkUUID(String email) throws SQLException {
        String query = "SELECT uuid FROM uuids WHERE email = ? ORDER BY timestamp DESC LIMIT 1";
        String latest_uuid_hash = "";
        String stored_uuid = preferences.get(uuid_name, null);

        // Returning false if there is not uuid stored in the local machine
        if(stored_uuid == null)
            return false;

        if(!validRefreshTime())
            return false;

        // Fetching the latest uuid from the database
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, email);
        ResultSet set = statement.executeQuery();

        if(set.next())
            latest_uuid_hash = set.getString("uuid");
        else
            return false;
        return BCrypt.checkpw(stored_uuid, latest_uuid_hash);
    }

    public static void deleteToken(String email) throws SQLException{
        // Periodically delete the tokens from the database
        // To only keep 10 tokens at a time
        ArrayList<String> uuid_hashes = new ArrayList<>();
        String query = "SELECT * FROM uuids WHERE email  = ? ORDER BY timestamp DESC";
        String deleteQuery = "DELETE FROM uuids WHERE email = ? AND uuid NOT IN (?,?,?,?,?,?,?,?,?,?)";


        PreparedStatement statement = connection.prepareStatement(query);
        PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);

        statement.setString(1, email);
        ResultSet set = statement.executeQuery();

        // Getting the top 10 uuids from the database
        int count = 0;
        while(set.next()){
            uuid_hashes.add(set.getString("uuid"));
            count++;
            if(count == 10) break;
        }

        // Deleting the older uuids ( Besides the top 10 )
        if(uuid_hashes.size() == 10){
            // Setting all the uuids in the prepared statement
            deleteStatement.setString(1, email);
            for(int i = 2;  i <= 11 ; i++){
                deleteStatement.setString(i,uuid_hashes.get(i-2));
            }
            int rows = deleteStatement.executeUpdate();
//            System.out.println(rows>0?"Deleted the older values":"Did not delete the older values");
        }


    }

    // Just checking the refresh time that is present in the local storage is valid or not
    public static boolean validRefreshTime(){
        return !(preferences.getLong(time_refresh, 0) < System.currentTimeMillis());
    }

    public static void main(String[] args) throws Exception {
//        System.out.println(preferences.get(stored_email, null));
//        preferences.clear();
        System.out.println(preferences.get(uuid_name, null));
        System.out.println(preferences.get(stored_email, null));
        System.out.println(preferences.getLong(time_refresh, 0));
        System.out.println(preferences.getLong(time_created, 0));
//        deleteToken("aryanpatel2593@gmail.com");
    }


}
