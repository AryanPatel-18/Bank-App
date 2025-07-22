package Main.FormUtils;

import Main.DBconnect;
import Main.Models.BankAccount;
import Main.Models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// Create file would contain all the methods that would be used to create objects for all the classes


public class Create {

    private void createUser(User obj) throws SQLException {
        Connection con = DBconnect.getConnection();

        LocalDate date = obj.getBirth_date(); // or get from DatePicker
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // or any pattern
        String formattedDate = date.format(formatter);

        String query = "INSERT INTO users (full_name, email, password_hash, phone_number, address, user_state, city, birthdate) VALUES (?,?,?,?,?,?,?,?)";
        PreparedStatement statement = con.prepareStatement(query);
        statement.setString(1, obj.getFirst_name());
        statement.setString(2, obj.getEmail());
        statement.setString(3, obj.getPassword_hash());
        statement.setString(4, obj.getPhone_number());
        statement.setString(5, obj.getAddress());
        statement.setString(6, obj.getState());
        statement.setString(7, obj.getCity());
        statement.setString(8, formattedDate);

        int rows = statement.executeUpdate();

        if(rows > 0){
            System.out.println("The values were successfully added");
        }else{
            System.out.println("The values were not added");
        }
    }

    private void createBankInfo(BankAccount account) throws SQLException{
        Connection connection = DBconnect.getConnection();

        String query = " INSERT INTO bank_account (account_number,user_id,account_type,status, balance) VALUES (?,?,?,?,?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, account.getAccount_number());
        statement.setInt(2, account.getUser_id());
        statement.setString(3, account.getAccount_type());
        statement.setString(4, account.getStatus());
        statement.setDouble(5, 0);

        int row = statement.executeUpdate();

        System.out.println(row>0?"Added the values":"Did not add the values");

    }

    public void createBankInfoCall(BankAccount account) throws SQLException{
        createBankInfo(account);
    }

    public void createUserCall(User obj) throws SQLException {
        createUser(obj);
    }

}

