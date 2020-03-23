package com.mlshop.engine.util;

import com.mlshop.engine.model.Record;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ArffUtils {
    public static String genNominalTypeFromZeroTo(int length) {
        return "{" + IntStream.range(0, length).mapToObj(e -> String.valueOf(e)).collect(Collectors.joining(",")) + "}";
    }

    public static String genNominalTypeFromArray(String... types) {
        return "{" + Arrays.stream(types).collect(Collectors.joining(",")) + "}";
    }

    public static String toArffRecords(List<Record> records) {
        return records.stream().map(record -> toArffRecord(record)).collect(Collectors.joining("\n"));
    }

    public static String toArffRecord(Record record) {
        StringBuilder sb = new StringBuilder();
        sb.append(record.getDate()).append(",")
            .append(record.getItemName()).append(",")
            .append(record.getSize() == null ? "" : record.getSize()).append(",")
            .append(record.getQuantity() == null ? "?" : record.getQuantity());
        return sb.toString();
    }
}
