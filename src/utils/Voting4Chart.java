/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


public class Voting4Chart {

    private WritableCellFormat timesBoldUnderline;
    private WritableCellFormat times;

    private WritableSheet excelSheet;
    private WritableWorkbook workbook;

    private int rowOffset;

        public static void main(String[] args) throws WriteException, IOException {
//       Voting4Chart excel = new Voting4Chart("V:\\Federico\\fileSocialCrawler\\ExperimentResults\\aa.xls");
//
//        excel.writeNumber(0.001,7,1);
//
//
//
//         excel.writeNumber(3.12,2,1);
//          excel.writeString("test",2,1);
//         excel.close();


    }

    public void setRowOffset(int offset) {
        this.rowOffset = offset;
    }



   public Voting4Chart(String inputFile) throws IOException{
               File file = new File(inputFile);
        WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setLocale(new Locale("en", "EN"));

        workbook = Workbook.createWorkbook(file, wbSettings);
        workbook.createSheet("PerformanceReport", 0);
         excelSheet = workbook.getSheet(0);
         rowOffset=0;

   }

   public void close() throws IOException, WriteException{
                     workbook.write();
        workbook.close();
   }



    public void writeNumber(double number,int col) throws IOException, WriteException {

       createLabel(excelSheet);

//        addLabel(excelSheet, col, row, label);
            addNumber(excelSheet, col, rowOffset, number);


    }

        public void writeString(String s, int col) throws IOException, WriteException {

       createLabel(excelSheet);

        addLabel(excelSheet, col, rowOffset, s);



    }

    private void createLabel(WritableSheet sheet)
            throws WriteException {
        // Lets create a times font
        WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
        // Define the cell format
        times = new WritableCellFormat(times10pt);
        // Lets automatically wrap the cells
        times.setWrap(true);

        // Create create a bold font with unterlines
        WritableFont times10ptBoldUnderline = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false,
                UnderlineStyle.SINGLE);
        timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
        // Lets automatically wrap the cells
        timesBoldUnderline.setWrap(true);

        CellView cv = new CellView();
        cv.setFormat(times);
        cv.setFormat(timesBoldUnderline);
        cv.setAutosize(true);

        //    // Write a few headers
        //    addCaption(sheet, 0, 0, "Header 1");
        //    addCaption(sheet, 1, 0, "This is another header");


    }



    private void addCaption(WritableSheet sheet, int column, int row, String s)
            throws RowsExceededException, WriteException {
        Label label;
        label = new Label(column, row, s, timesBoldUnderline);
        sheet.addCell(label);
    }

    private void addNumber(WritableSheet sheet, int column, int row,
            Double integer) throws WriteException, RowsExceededException {
        Number number;
        number = new Number(column, row, integer, times);
        sheet.addCell(number);
    }

    private void addLabel(WritableSheet sheet, int column, int row, String s)
            throws WriteException, RowsExceededException {


        Label label;
        label = new Label(column, row, s, times);
        sheet.addCell(label);
    }



    public String readFile(String filepath) throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new FileReader(filepath));

        StringBuilder sb = new StringBuilder();
        String line = br.readLine();

        while (line != null) {

            sb.append(line);
            sb.append("\n");

            line = br.readLine();
        }

        br.close();

        return sb.toString();
    }


}