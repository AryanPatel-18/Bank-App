package Main.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.prefs.Preferences;
import java.util.UUID;
import Main.DBconnect;
import Main.Utils.BCrypt;

// This class is only used for remember me feature in the login form
// This will create a random number and save it in the local machine along with a timestamp
// When the user open the app again it would check for the existence of that particular random number in the local machine
// If a number exists it would compare the number in the db along with the timestamp at which the random number was created
// With the current timestamp, This is done to allow the user to skip the login form

public class RememberMe {

    // This is the object that would save the value in the local machine
    private static final Preferences preferences = Preferences.userRoot().node("myapp/rememberMe");
    private static final String uuid_name = "uuid";
    private static final long duration = 1140; // Number of minutes in  a day
    private static final Connection connection = DBconnect.getConnection();

    public static void createUUID(String email) throws SQLException {
        // Creating the uuid number and saving it in the system
        UUID uuid = UUID.randomUUID();
        preferences.put(uuid_name, uuid.toString());

        // saving the uuid in number in the database
        String query = "INSERT INTO uuids VALUES (?,?,?)";
        String uuid_hash = BCrypt.hashpw(uuid.toString(), BCrypt.gensalt(12));
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, email);
        statement.setString(2, uuid_hash);
        statement.setLong(3, System.currentTimeMillis());

        int rows = statement.executeUpdate();
        System.out.println(rows>0?"Added the values":"Not added the values");
    }

    public static boolean checkUUID(String email) throws SQLException {
        String query = "SELECT * FROM uuids WHERE email = ?";
        long currentTimestamp = System.currentTimeMillis();
        String localUUID = preferences.get(uuid_name, null);
        String storedUUIDhash = "";
        long storedTimestamp = 0;

        if(localUUID == null || localUUID.isEmpty()) return false;

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, email);
        ResultSet set = statement.executeQuery();

        while(set.next()){
            storedTimestamp = set.getLong("timestamp");
            storedUUIDhash = set.getString("uuid");
        }

        if(BCrypt.checkpw(localUUID, storedUUIDhash)){
            if((currentTimestamp - storedTimestamp) < duration*60*1000){
                return true;
            }
            deleteToken(email);
        }
        return false;
    }

    public static void deleteToken(String email) throws SQLException{

        //Delete from database
        String query = "DELETE FROM uuids WHERE email = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, email);
        statement.executeUpdate();

        // Removing from preferences
        preferences.remove(uuid_name);
    }

    public static boolean hasUUID(){
        String check = preferences.get(uuid_name, null);
        return !(check == null);
    }

    public static void main(String[] args) throws Exception {
//        createUUID("randomemail@gmail.com");
//        deleteToken("randomemail@gmail.com");
        createUUID("aryanpatel2593@gmail.com");
        //21379b1b-379d-4c1b-8e33-6f0ca02d7793
        System.out.println(preferences.get(uuid_name, null));
    }

}
