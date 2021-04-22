package persistence;

import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;



public class PersistenceHandler {

    private File credit = new File("src/main/resources/credits.txt");
    private File episode = new File("src/main/resources/episode.txt");
    private File tvSeries = new File("src/main/resources/TvSeries.txt");
    private File person = new File("src/main/resources/person.txt");
    private File transmission = new File("src/main/resources/transmission.txt");


    public PersistenceHandler() {
    }

    private void write(File file, String s) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileWriter fileWriter = new FileWriter(file, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(s);
            printWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeCredit(String s) {
        write(credit, s);
    }

    public void writePerson(String s) {
        write(person, s);
    }

    public void writeTvSeries(String s) {
        write(tvSeries, s);
    }

    public void writeTransmission(String s) {
        write(transmission, s);
    }

    public void writeEpisode(String s) {
        write(episode, s);
    }

    public void deleteFiles() {

        credit.delete();
        person.delete();
        tvSeries.delete();
        transmission.delete();
        episode.delete();

    }


    public ArrayList<String[]> readCredit() {
        return read(credit);
    }

    public ArrayList<String[]> readEpisode() {
        return read(episode);
    }

    public ArrayList<String[]> readPerson() {
        return read(person);
    }

    public ArrayList<String[]> readTvSeries() {
        return read(tvSeries);
    }

    public ArrayList<String[]> readTransmission() {
        return read(transmission);
    }

    public ArrayList<String[]> read(File file) {
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
