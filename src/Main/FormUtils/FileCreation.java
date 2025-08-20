package Main.FormUtils;

import Main.Controllers.FormControllers.LoginController;
import Main.Models.Transaction;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.awt.Color;
import java.text.SimpleDateFormat;

public class FileCreation{
    public void exportToExcel(ObservableList<Transaction> transactions, Stage stage, ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        LoginController login = new LoginController();
        fileChooser.setTitle("Save Transactions Excel File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Transactions");

                // Base font (Sans Serif like Arial)
                Font baseFont = workbook.createFont();
                baseFont.setFontName("Arial");
                baseFont.setFontHeightInPoints((short) 11);

                // Header style (Dark Blue background, White text)
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setFontName("Arial");
                headerFont.setBold(true);
                headerFont.setColor(IndexedColors.WHITE.getIndex());
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                headerStyle.setBorderTop(BorderStyle.MEDIUM);
                headerStyle.setBorderBottom(BorderStyle.MEDIUM);
                headerStyle.setBorderLeft(BorderStyle.MEDIUM);
                headerStyle.setBorderRight(BorderStyle.MEDIUM);

                // Data style (light blue background for all cells)
                CellStyle dataStyle = workbook.createCellStyle();
                dataStyle.setFont(baseFont);
                dataStyle.setBorderTop(BorderStyle.THIN);
                dataStyle.setBorderBottom(BorderStyle.THIN);
                dataStyle.setBorderLeft(BorderStyle.THIN);
                dataStyle.setBorderRight(BorderStyle.THIN);
                dataStyle.setAlignment(HorizontalAlignment.CENTER);
                dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                XSSFColor myColor = new XSSFColor(new Color(239, 239, 239), null);
                dataStyle.setFillForegroundColor(myColor);
                dataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                // Amount style (number format + background)
                CellStyle amountStyle = workbook.createCellStyle();
                amountStyle.cloneStyleFrom(dataStyle);
                amountStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

                // Create header
                Row header = sheet.createRow(0);
                String[] columns = {"Amount", "Timestamp", "Sender", "Receiver"};
                for (int i = 0; i < columns.length; i++) {
                    Cell cell = header.createCell(i);
                    cell.setCellValue(columns[i]);
                    cell.setCellStyle(headerStyle);
                }

                // Fill data
                int rowIndex = 1;
                for (Transaction t : transactions) {
                    Row row = sheet.createRow(rowIndex++);

                    Cell amountCell = row.createCell(0);
                    amountCell.setCellValue(t.getAmount());
                    amountCell.setCellStyle(amountStyle);

                    Cell dateCell = row.createCell(1);
                    dateCell.setCellValue(t.getTimestamp() != null ? t.getTimestamp() : "N/A");
                    dateCell.setCellStyle(dataStyle);

                    Cell senderCell = row.createCell(2);
                    senderCell.setCellValue(String.valueOf(t.getSenderAccount()));
                    senderCell.setCellStyle(dataStyle);

                    Cell receiverCell = row.createCell(3);
                    receiverCell.setCellValue(String.valueOf(t.getReceiverAccount()));
                    receiverCell.setCellStyle(dataStyle);
                }

                // Set wider columns
                sheet.setColumnWidth(0, 5000); // Amount
                sheet.setColumnWidth(1, 8000); // Timestamp
                sheet.setColumnWidth(2, 6000); // Sender
                sheet.setColumnWidth(3, 6000); // Receiver

                // Freeze header
                sheet.createFreezePane(0, 1);

                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }
                login.switchToMainScene(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
