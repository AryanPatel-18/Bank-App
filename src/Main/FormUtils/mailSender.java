package Main.FormUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;


// This is the main class that sends the email as well as stores the otp that is generated

public class mailSender {
    final private String username = "aryanpatel2593@gmail.com";
    final private String password = "xqyr vqou viif lrrc";
    private int otp;
    String email;

    // Setting the otp as well as saving it with the name of the file
    public mailSender(String email) throws IOException {
        this.otp = (int)(Math.random() * 9000) + 1000;
        this.email = email;
        storeOtp();
    }

    public mailSender(){}


    // Used for checking the otp
    public boolean checkOtp(int enteredOtp, String email){
        int actualOtp = 0;
        try {
            String filename = email.split("@")[0];
            Path path = Paths.get(String.format("src/Resources/Otps/%s.txt",filename));
            BufferedReader reader = new BufferedReader(new FileReader(String.format("src/Resources/Otps/%s.txt",filename)));
            actualOtp = Integer.parseInt(reader.readLine());
            reader.close();
            try{
                Files.delete(path);
            }catch (IOException e){
                System.out.println("There was a problem while deleting the file");
            }
        } catch (IOException e) {
            System.out.println("There was a problem while checking the file");
        }
        return actualOtp == enteredOtp;
    }

    // Used for storing the otp
    private void storeOtp() {
        try {
            Delete delete = new Delete(email);
            File file = new File(String.format("src/Resources/Otps/%s.txt",email.split("@")[0]));
            file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(String.valueOf(otp));
            writer.close();
            delete.start();
        } catch (IOException e) {
            System.out.println("Problem while creating the file");
        }

    }

    // Sending the mail
    public void sendMail(String email) {


        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");


        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(email)
            );
            message.setSubject("Login Otp for bank app");
            message.setText("Dear Customer,\n\n" +
                    "Your One-Time Password (OTP) for completing your transaction/login on BankApp is:\n\n" +
                    "🔐 OTP: " + otp + "\n\n" +
                    "This OTP is valid for the next 10 minutes.\n" +
                    "Please do not share this code with anyone. BankApp will never ask you for your OTP.\n\n" +
                    "If you did not initiate this request, please contact our support team immediately.\n\n" +
                    "Thank you for choosing BankApp.\n\n" +
                    "Sincerely,\n" +
                    "BankApp Security Team");


            Transport.send(message);
            System.out.println("Email sent successfully.");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendMail(String email, String userPassword) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(email)
            );
            message.setSubject("Your Account Password - BankApp");
            message.setText("Dear Customer,\n\n" +
                    "As per your request, here are your login credentials for BankApp:\n\n" +
                    "📧 Email: " + email + "\n" +
                    "🔑 Password: " + userPassword + "\n\n" +
                    "Please keep your credentials safe and do not share them with anyone.\n\n" +
                    "please reset your password immediately for safety purposes.\n\n" +
                    "Best regards,\n" +
                    "BankApp Security Team");

            Transport.send(message);
            System.out.println("Password email sent successfully.");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public static void clearOtpFiles(){
        String directoryPath = "src/Resources/Otps";
        File directory = new File(directoryPath);

        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Invalid directory: " + directoryPath);
            return;
        }

        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    boolean deleted = file.delete();
                    if (deleted) {
                        System.out.println("Deleted: " + file.getName());
                    } else {
                        System.out.println("Failed to delete: " + file.getName());
                    }
                }
            }
        }
    }
}

// Delete class is created to basically delete the otp file after 10 minutes ( time can be changed )
class Delete extends Thread{

    private final String email;

    Delete(String email){
        this.email = email;
    }

    private File getOtpFile(){
        return new File(String.format("src/Resources/Otps/%s.txt",email.split("@")[0]));
    }

    // Simple thread methods that runs the .delete() operation on the otp file
    public void run(){
        File file = getOtpFile();
        try {
            Thread.sleep(10*60*1000);
            // Delete the file
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("OTP file deleted successfully.");
                } else {
                    System.out.println("Failed to delete the OTP file.");
                }
            } else {
                System.out.println("OTP file does not exist.");
            }

        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted before timeout.");
        }
    }


}