package com.mlshop.engine.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mlshop.engine.Constant;
import com.mlshop.engine.model.RangeConfig;
import com.mlshop.engine.model.Record;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuantityRangeService {
    private List<RangeConfig> configs;
    public void reloadResource() throws IOException {
        try (FileReader reader = new FileReader(Constant.FILE_NAME_QUANTITY_RANGE)) {
            configs = new Gson().fromJson(reader, new TypeToken<List<RangeConfig>>() {}.getType());
        } catch (IOException e) {
            throw e;
        }
    }

    public List<RangeConfig> getConfigs() throws IOException {
        if (configs == null) {
            reloadResource();
        }
        return configs;
    }

    public Integer classifyRange(Double quantity) throws IOException {
        for (RangeConfig config: getConfigs()) {
            if (config.getEq() != null && config.getEq() != quantity) continue;
            if (config.getGte() != null && config.getGte() > quantity) continue;
            if (config.getLte() != null && config.getLte() < quantity) continue;
            return config.getIndex();
        }
        return null;
    }

    public List<Record> classifyRange(List<Record> records) throws IOException {
        for (Record record: records) {
            record.setQuantityRange(classifyRange(record.getQuantity()));
        }
        return records;
    }

    public List<String> getIndexes() throws IOException {
        return getConfigs().stream().map(e -> e.getIndex().toString()).collect(Collectors.toList());
    }
}
