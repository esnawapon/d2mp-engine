package com.mlshop.engine.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mlshop.engine.model.CsvSavedAble;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;

public class FileUtils {
    public static File writeReplaceFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        try (FileWriter writer = new FileWriter(filePath, false)) {
            writer.write(content);
        } catch (IOException e) {
            System.out.println("Cannot write " + filePath);
            throw e;
        }
        return file;
    }

    public static File writeReplaceCsvFile(String filePath, CsvSavedAble csvModel) throws IOException {
        StringBuilder content = new StringBuilder();
        try (CSVPrinter csvPrinter = new CSVPrinter(content, CSVFormat.DEFAULT.withHeader(csvModel.getCsvHeader()))) {
            for (Object[] row: csvModel.getRows()) {
                csvPrinter.printRecord(row);
            }
            csvPrinter.flush();
        }
        return FileUtils.writeReplaceFile(filePath, content.toString());
    }

    public static void writeReplaceJsonFile(String filePath, Object model) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(model, writer);
        } catch (IOException e) {
            throw e;
        }
    }

    public static <T> T readJsonFile(String filePath, Class<T> classOfT) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            return new Gson().fromJson(reader, classOfT);
        } catch (IOException e) {
            throw e;
        }
    }

    public static <T> T readJsonFile(String filePath, Type typeOfT) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            return new Gson().fromJson(reader, typeOfT);
        } catch (IOException e) {
            throw e;
        }
    }
}
