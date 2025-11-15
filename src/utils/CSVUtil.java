package careerhub.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CSVUtil {
    // Simple CSV reader: returns list of rows (each row is array of columns)
    // Assumes first row is header and will be skipped by caller if needed
    public static List<String[]> readCsv(String path) throws IOException {
        List<String[]> out = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(path))) {
            String header = br.readLine(); // skip header
            if (header == null) return out;
            String line;
            while ((line = br.readLine()) != null) {
                String[] cols = splitCsvLine(line);
                out.add(cols);
            }
        }
        return out;
    }

    private static String[] splitCsvLine(String line) {
        // Very simple split on comma — works for your provided CSVs (no quoted commas)
        String[] parts = line.split(",", -1);
        for (int i = 0; i < parts.length; i++) parts[i] = parts[i].trim();
        return parts;
    }
}

