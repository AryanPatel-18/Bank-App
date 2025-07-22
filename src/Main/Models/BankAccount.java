package Main.Models;

import Main.DBconnect;

import java.util.Random;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BankAccount {
    private final long account_number;
    private final int user_id;
    private final String account_type;
    private final String status;
    private final double balance;

    public BankAccount(String account_type) throws SQLException {
        this.user_id = setUserId();
        this.account_number = setAccountNumber();
        this.account_type = account_type;
        this.status = "active";
        this.balance = 0;

    }

    private int setUserId() throws SQLException {
        Connection connection = DBconnect.getConnection();
        String query = "SELECT last_value FROM users_user_id_seq";
        Statement statement = connection.createStatement();
        ResultSet set = statement.executeQuery(query);

        if(set.next()){
            return set.getInt(1);
        }
        return 0;
    }

    private long setAccountNumber(){
        Random random = new Random();
        long min = 1_000_000_000_000L;   // smallest 12-digit number
        long max = 9_999_999_999_999L;   // largest 12-digit number

        return min + (long)(random.nextDouble() * (max - min));
    }

    public static void main(String[] args) throws Exception{
        BankAccount object = new BankAccount( "current");
        System.out.println(object.account_number);
    }

    public int getUser_id() {
        return user_id;
    }

    public String getStatus() {
        return status;
    }

    public double getBalance() {
        return balance;
    }

    public String getAccount_type() {
        return account_type;
    }

    public long getAccount_number() {
        return account_number;
    }
}
