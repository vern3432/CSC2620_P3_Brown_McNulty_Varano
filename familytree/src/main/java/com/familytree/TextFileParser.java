package com.familytree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class TextFileParser {

    public static File convert(InputStream inputStream, String fileName) throws IOException {
        File tempFile = File.createTempFile(fileName, ".tmp");
        tempFile.deleteOnExit(); // Delete the temporary file when the JVM exits

        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return tempFile;
    }

    public static void main(String[] args) throws IOException, ParseException {

        InputStream inputStream = TextFileParser.class.getClassLoader().getResourceAsStream("data.txt");
        File file = convert(inputStream, "data");
        printParsedData(parseTextFile(file));
        FamilyDatabase db = new FamilyDatabase();
        db.addParsedDataToDatabase(parseTextFile(file));

    }

    public static List<String[]> parseTextFile(File file) {
        List<String[]> parsedData = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(", ")) {
                    System.out.println(line);

                    String[] tokens = line.split(", ");
                    if (tokens.length == 3 && tokens[2].matches("\\d{1,2} \\d{1,2} \\d{4}")) {
                        parsedData.add(new String[] { "Dead Person", line });
                    } else if (tokens.length == 3) {
                        parsedData.add(new String[] { "Relationship", line });
                    } else if (tokens.length == 2 && tokens[1].equals("marriedto")) {
                        parsedData.add(new String[] { "Relationship", line });
                    } else if (tokens.length == 2 && tokens[1].equals("parentof")) {
                        parsedData.add(new String[] { "Relationship", line });
                    } else {
                        parsedData.add(new String[] { "Address", line });
                    }
                } else {
                    // parsedData.add(new String[]{"FamilyMember", line});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(parsedData.toString());
        return parsedData;
    }

    public static void printParsedData(List<String[]> parsedData) {
        // Print the parsed data
        for (String[] data : parsedData) {
            System.out.println(data[0] + ": " + data[1]);
        }
    }
}