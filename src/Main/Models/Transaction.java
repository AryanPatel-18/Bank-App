package Main.Models;

// Transaction object also used for storing information

import Main.DBconnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Transaction {
    private static boolean udpateBalance(double amount, int userId) throws SQLException {
        Connection connection = DBconnect.getConnection();

        String query = "SELECT balance FROM bank_account WHERE user_id = ?";
        String updateQuery = "UPDATE bank_account SET balance = ? WHERE user_id = ?";
        double currentBalance;
        double newBalance;


        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, userId);
        ResultSet set = statement.executeQuery();
        if(set.next())
            currentBalance = set.getDouble(1);
        else
            return false;
        if(amount < 0 && (amount + currentBalance) < 0)
            return false;

        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
        updateStatement.setDouble(1, (currentBalance + amount));
        updateStatement.setInt(2, userId);
        int rows = updateStatement.executeUpdate();

        if(!(rows>0))
            return false;

        updateTransactionLog(userId, amount);
        return true;
    }

    private static void updateTransactionLog(int userId, double amount) throws SQLException{
        String query = "INSERT INTO transactions (user_id, transaction_type, amount ) VALUES (?,?,?)";
        Connection connection = DBconnect.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, userId);
        statement.setString(2, amount>0?"credit":"debit");
        statement.setDouble(3, amount);

        int rows = statement.executeUpdate();

        System.out.println(rows>0?"added log":"couldn't add log");
    }

    public static boolean updateBalanceCall(int user_id, double amount) throws SQLException{

        return udpateBalance(amount, user_id);
    }
}
