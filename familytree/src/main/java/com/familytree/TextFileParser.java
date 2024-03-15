package com.familytree;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TextFileParser {

    public static void main(String[] args) {
        parseTextFileAndPrintResults("data.txt");
    }

    public static List<String[]> parseTextFile(String filePath) {
        List<String[]> parsedData = new ArrayList<>();
        try (InputStream inputStream = TextFileParser.class.getClassLoader().getResourceAsStream(filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(", ")) {
                    String[] tokens = line.split(", ");
                    if (tokens.length == 3 && tokens[2].matches("\\d{1,2} \\d{1,2} \\d{4}")) {
                        parsedData.add(new String[]{"Event", line});
                    } else if (tokens.length == 3) {
                        parsedData.add(new String[]{"Relationship", line});
                    } else if (tokens.length == 2 && tokens[1].equals("marriedto")) {
                        parsedData.add(new String[]{"Relationship", line});
                    } else if (tokens.length == 2 && tokens[1].equals("parentof")) {
                        parsedData.add(new String[]{"Relationship", line});
                    } else {
                        parsedData.add(new String[]{"Address", line});
                    }
                } else {
                    parsedData.add(new String[]{"FamilyMember", line});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parsedData;
    }

    public static void parseTextFileAndPrintResults(String filePath) {
        List<String[]> parsedData = parseTextFile(filePath);

        // Print the parsed data
        for (String[] data : parsedData) {
            System.out.println(data[0] + ": " + data[1]);
        }
    }
}
