package Main;

import java.io.File;
import java.io.FileInputStream;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Images {
    public static void main(String[] args) throws Exception{
        Connection connection = DBconnect.getConnection();
        connection.setAutoCommit(false);  // ✅ Disable auto-commit

        String query = "INSERT INTO profile_photos (photo) VALUES (?)";
        PreparedStatement statement = connection.prepareStatement(query);

        for(int i = 1; i <= 6; i++) {
            String filename = "photo" + i + ".jpg";
            File file = new File("D:\\ProfilePhotos\\" + filename);
            FileInputStream fis = new FileInputStream(file);

            statement.setBinaryStream(1, fis, (int) file.length());  // ✅ Use setBinaryStream for BYTEA
            int row = statement.executeUpdate();
            System.out.println(row > 0 ? "Entered" : "Not Entered");

            fis.close();
        }

        connection.commit();
    }
}
