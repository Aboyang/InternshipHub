package careerhub.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class providing simple CSV reading and writing functionality.
 * 
 * <p>This helper is intentionally lightweight and designed for the CSV
 * formats used by the Internship Hub application — no quoted values,
 * no escaped commas, and no multiline records. It supports:</p>
 *
 * <ul>
 *     <li>Reading CSV files into {@code List<String[]>}</li>
 *     <li>Skipping an optional header row</li>
 *     <li>Writing rows back to CSV</li>
 * </ul>
 *
 * <p>All values are trimmed, and empty columns are preserved using
 * {@code split(",", -1)}.</p>
 */
public class CSVUtil {

    /**
     * Reads a CSV file and returns all rows as a list of string arrays.
     * Each array corresponds to one row, split by commas.
     *
     * <p>If {@code hasHeader} is true, the first line is skipped.</p>
     *
     * @param path       the file system path to the CSV file
     * @param hasHeader  whether the first row is a header row
     * @return list of rows represented as {@code String[]}
     * @throws IOException if the file cannot be opened or read
     */
    public static List<String[]> readCsv(String path, boolean hasHeader) throws IOException {
        List<String[]> out = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(path))) {

            if (hasHeader) {
                // discard header
                String header = br.readLine();
                if (header == null) return out;
            }

            String line;
            while ((line = br.readLine()) != null) {
                out.add(splitCsvLine(line));
            }
        }
        return out;
    }

    /**
     * Reads a CSV file while assuming the file contains a header row.
     * This method is shorthand for {@link #readCsv(String, boolean)} with
     * {@code hasHeader = true}.
     *
     * @param path path to the CSV file
     * @return list of parsed CSV rows
     * @throws IOException if the file cannot be read
     */
    public static List<String[]> readCsv(String path) throws IOException {
        return readCsv(path, true);   // default behavior
    }

    /**
     * Splits a single CSV line into columns.
     *
     * <p>This implementation uses a simple comma split and therefore does
     * not support quoted commas or escaped values. All columns are trimmed
     * for convenience.</p>
     *
     * @param line the raw CSV line
     * @return array of trimmed column values
     */
    private static String[] splitCsvLine(String line) {
        // Very simple split on comma — works for your provided CSVs (no quoted commas)
        String[] parts = line.split(",", -1);
        for (int i = 0; i < parts.length; i++) parts[i] = parts[i].trim();
        return parts;
    }

    /**
     * Writes a list of string-array rows to a CSV file. Each array is joined
     * using commas. No header row is added automatically.
     *
     * @param path the output CSV file path
     * @param rows list of string arrays representing CSV rows
     */
    public static void writeCsv(String path, List<String[]> rows) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            for (String[] row : rows) {
                pw.println(String.join(",", row));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
