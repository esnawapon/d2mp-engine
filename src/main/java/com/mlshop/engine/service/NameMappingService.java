package com.mlshop.engine.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mlshop.engine.Constant;
import com.mlshop.engine.model.ItemNameMapping;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class NameMappingService {
    private ItemNameMapping mappings;
    private List<String> itemNameMappings;
    private Map<String, Integer> reverseItemNameMappings;

    public void reloadResource() throws IOException {
        try (FileReader reader = new FileReader(Constant.FILE_NAME_ITEM_MAPPING)) {
            mappings = new Gson().fromJson(reader, ItemNameMapping .class);
        } catch (IOException e) {
            throw e;
        }
        itemNameMappings = mappings.getItemNames();
        reverseItemNameMappings = new HashMap();
        for (int i = 0; i < itemNameMappings.size(); i++) {
            reverseItemNameMappings.put(itemNameMappings.get(i), i);
        }
    }

    private void writeResource(List<String> itemNames) throws IOException {
        ItemNameMapping mappings = new ItemNameMapping(itemNames);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(Constant.FILE_NAME_ITEM_MAPPING)) {
            gson.toJson(mappings, writer);
        } catch (IOException e) {
            throw e;
        }
        reloadResource();
    }

    private List<String> getItemNameMappings() throws IOException {
        if (itemNameMappings == null) {
            reloadResource();
        }
        return itemNameMappings;
    }

    private Map<String, Integer> getReverseNameMapping() throws IOException {
        if (reverseItemNameMappings == null) {
            reloadResource();
        }
        return reverseItemNameMappings;
    }

    public synchronized Map<String, Integer> mapItemNameToResource(Set<String> itemNames) throws IOException {
        Map<String, Integer> results = new HashMap();
        List<String> newItemNames = new ArrayList();
        for (String itemName: itemNames) {
            Integer existItemName = getReverseNameMapping().get(itemName);
            if (existItemName == null) {
                existItemName = getItemNameMappings().size() + newItemNames.size();
                newItemNames.add(itemName);
            }
            results.put(itemName, existItemName);
        }
        List<String> newResource = new ArrayList();
        newResource.addAll(getItemNameMappings());
        newResource.addAll(newItemNames);
        writeResource(newResource);
        return results;
    }

    public int itemNameSize() throws IOException {
        return getItemNameMappings().size();
    }
}
