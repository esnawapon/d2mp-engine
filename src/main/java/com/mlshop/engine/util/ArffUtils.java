package com.mlshop.engine.util;

import com.mlshop.engine.model.Record;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ArffUtils {
    public static String genNominalTypeFromZeroTo(int length) {
        return "{" + IntStream.range(0, length).mapToObj(e -> String.valueOf(e)).collect(Collectors.joining(",")) + "}";
    }

    public static String genNominalTypeFromCollection(Collection types) {
        return "{" + types.stream().collect(Collectors.joining(",")) + "}";
    }

    public static String genOneHotAttributesHeader(List<String> attributeNames) {
        return attributeNames.stream().map(e -> "@attribute " + e + " {0,1}").collect(Collectors.joining("\n"));
    }

    public static String genOneHotAttributesRecord(List<String> attributeNames, Collection<String> activeAttributes) {
        return attributeNames.stream().map(e -> activeAttributes.contains(e) ? "1" : "0").collect(Collectors.joining(","));
    }

    public static String toArffRecords(List<Record> records, List<String> attributeNames) {
        return records.stream().map(record -> toArffRecord(record, attributeNames)).collect(Collectors.joining("\n"));
    }

    public static String toArffRecord(Record record, List<String> attributeNames) {
        StringBuilder sb = new StringBuilder();
//        sb.append(record.getDate()).append(",");
        sb.append(record.getSize() == null ? "" : record.getSize()).append(",");
        sb.append(genOneHotAttributesRecord(attributeNames, record.getActiveKeyAttributes())).append(",");
//        sb.append(record.getQuantity() == null ? "?" : record.getQuantity());
        sb.append(record.getQuantityRange() == null ? "?" : record.getQuantityRange());
        return sb.toString();
    }
}
