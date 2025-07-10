package Main.Utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;


public class mailSender {
    final private String username = "aryanpatel2593@gmail.com";
    final private String password = "xqyr vqou viif lrrc";
    private int otp;
    String email;

    public mailSender(String email) throws IOException {
        this.otp = (int)(Math.random() * 9000) + 1000;
        this.email = email;
        storeOtp();
    }

    public mailSender(){}

    public boolean checkOtp(int enteredOtp, String email) throws IOException{
        Path path = Paths.get(String.format("C:\\Users\\aryan\\OneDrive\\Desktop\\Coding\\Java\\Java-2-Project\\BankApp\\src\\Resources\\Otps\\%s.txt",email.split("@")[0]));
        BufferedReader reader = new BufferedReader(new FileReader(String.format("C:\\Users\\aryan\\OneDrive\\Desktop\\Coding\\Java\\Java-2-Project\\BankApp\\src\\Resources\\Otps\\%s.txt",email.split("@")[0])));
        int actualOtp = Integer.parseInt(reader.readLine());
        reader.close();
        try{
            Files.delete(path);
        }catch (IOException e){
            System.out.println("The OTP file was not deleted");
        }
        return actualOtp == enteredOtp;
    }

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

    public void sendMail(String email) {

        // SMTP server settings
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Create a session with authentication
        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            // Create a message
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

            // Send the message
            Transport.send(message);

            System.out.println("Email sent successfully.");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}

class Delete extends Thread{

    private String email;

    Delete(String email){
        this.email = email;
    }

    private File getOtpFile(){
        return new File(String.format("src/Resources/Otps/%s.txt",email.split("@")[0]));
    }

    public void run(){
         File file = getOtpFile();
        try {
            // Wait for 10 minutes (600,000 milliseconds)
//            Thread.sleep(10 * 60 * 1000);
            Thread.sleep(20000);
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