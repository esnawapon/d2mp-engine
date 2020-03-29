package com.mlshop.engine.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mlshop.engine.Constant;
import com.mlshop.engine.model.MetaData;
import com.mlshop.engine.model.Record;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MetaDataService {
    private MetaData metaData;
    private Map<String, Integer> nameAndIndexes;

    public void reloadResource() throws IOException {
        File file = new File(Constant.FILE_NAME_META_DATA);
        if (file.exists()) {
            try (FileReader reader = new FileReader(Constant.FILE_NAME_META_DATA)) {
                metaData = new Gson().fromJson(reader, MetaData.class);

            } catch (IOException e) {
                throw e;
            }
        } else {
            metaData = new MetaData();
        }

        if (metaData.getItems() == null) {
            metaData.setItems(new ArrayList());
        }

        nameAndIndexes = metaData.getItems().stream().collect(Collectors.toMap(MetaData.Item::getName, MetaData.Item::getIndex));
    }

    private void writeToFile(MetaData metaData) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(Constant.FILE_NAME_META_DATA)) {
            gson.toJson(metaData, writer);
        } catch (IOException e) {
            throw e;
        }
        reloadResource();
    }

    public MetaData getMetaData() throws IOException {
        if (metaData == null) {
            reloadResource();
        }
        return metaData;
    }

    private Map<String, Integer> getNameAndIndexes() throws IOException {
        if (nameAndIndexes == null) {
            reloadResource();
        }
        return nameAndIndexes;
    }

    public Integer getIndex(String itemName) {
        try {
            return getNameAndIndexes().get(itemName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized void updateMetaData(List<Record> records) throws IOException {
        List<MetaData.Item> newItems = new ArrayList(getMetaData().getItems());
        Map<String, Integer> newNameAndIndexes = new HashMap(getNameAndIndexes());
        for (Record record: records) {
            Integer index = newNameAndIndexes.get(record.getItemName());
            MetaData.Item item;
            if (index == null) {
                item = new MetaData.Item(newItems.size(), record.getItemName(), new HashMap());
                newItems.add(item);
                newNameAndIndexes.put(item.getName(), item.getIndex());
            } else {
                item = newItems.get(index);
            }
            item.getSizeAndPrice().put(record.getSize(), record.getUnitPrice());
            record.setItemNameIndex(index);
        }
        MetaData newMetaData = new MetaData();
        newMetaData.setItems(newItems);
        writeToFile(newMetaData);
    }

    public int itemLength() throws IOException {
        return getMetaData().getItems().size();
    }
}
