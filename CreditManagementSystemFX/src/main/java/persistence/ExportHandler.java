package persistence;

import java.awt.Desktop;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class ExportHandler {
    //File representing the export document
    private File krediteringDanmarkReport = new File("src/main/resources/krediteringDanmarkReport.txt");

    public Writer fileWriter;

    public ExportHandler() {
    }

    //Contains the general write functionality that is used to write the files
    private void write(File file, String s) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            fileWriter = new FileWriter(file, false);
            fileWriter.write(s);
            fileWriter.close();
        } catch(FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void exportKrediteringDanmarkReport(String s) {
        write(krediteringDanmarkReport, s);
        Desktop desktop = Desktop.getDesktop();
        //opens the file in the OS default text editor
        try {
            desktop.open(krediteringDanmarkReport);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Contains the general read functionality that is used to read the files
    public ArrayList<String[]> read(File file) {
        //
        ArrayList<String[]> list = new ArrayList<>();

        try {
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()){
                String string = scanner.nextLine();
                String[] data = string.split(";");
                list.add(data);
            }
        } catch (FileNotFoundException e) {
//            e.printStackTrace();
        }
        return list;
    }

}
