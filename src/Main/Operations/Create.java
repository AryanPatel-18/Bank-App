package Main.Operations;

import Main.DBconnect;
import Main.ObjectFiles.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Create {

    private void createUser(User obj) throws SQLException {
        Connection con = DBconnect.getConnection();

        String query = "INSERT INTO users (full_name, email, password_hash, phone_number, address, user_state, city, birthdate) VALUES (?,?,?,?,?,?,?,?)";
        PreparedStatement statement = con.prepareStatement(query);
        statement.setString(1, obj.getFirst_name());
        statement.setString(2, obj.getEmail());
        statement.setString(3, obj.getPassword_hash());
        statement.setString(4, obj.getPhone_number());
        statement.setString(5, obj.getAddress());
        statement.setString(6, obj.getState());
        statement.setString(7, obj.getCity());
        statement.setString(8, obj.getBirth_date());

        int rows = statement.executeUpdate();

        if(rows > 0){
            System.out.println("The values were successfully added");
        }else{
            System.out.println("The values were not added");
        }
    }

    public void createUserCall(User obj) throws SQLException {
        createUser(obj);
    }

}

