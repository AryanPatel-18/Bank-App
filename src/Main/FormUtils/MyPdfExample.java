package Main.FormUtils;



public class MyPdfExample {
    public static void main(String[] args) {
        try {
            String command = "\"C:\\Program Files\\LibreOffice\\program\\soffice.exe\" --headless --convert-to pdf \"C:\\Users\\aryan\\OneDrive\\Desktop\\transaction-data.xlsx\" --outdir \"C:\\Users\\aryan\\OneDrive\\Desktop\"";
            Process p = Runtime.getRuntime().exec(command);

            p.waitFor();
            System.out.println("✅ Converted Excel to PDF successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
