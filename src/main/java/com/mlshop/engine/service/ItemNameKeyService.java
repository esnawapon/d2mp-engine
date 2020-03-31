package com.mlshop.engine.service;

import com.mlshop.engine.Constant;
import com.mlshop.engine.model.ItemNameKey;
import com.mlshop.engine.model.OneHotAttributeMapping;
import com.mlshop.engine.model.Record;
import com.mlshop.engine.util.FileUtils;
import com.mlshop.engine.util.KeyMatchers;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ItemNameKeyService {
    private ItemNameKey itemNameKey;
    private Map<String, String> keyNameAndAttributeNames;
    private List<String> attributeNames;

    public void reloadResource() throws IOException {
        File file = new File(Constant.FILE_NAME_ITEM_NAME_KEY);
        if (file.exists()) {
            itemNameKey = FileUtils.readJsonFile(Constant.FILE_NAME_ITEM_NAME_KEY, ItemNameKey.class);
        } else {
            itemNameKey = new ItemNameKey(new ArrayList());
        }

        keyNameAndAttributeNames = getItemNameKey().getKeys().stream()
                .collect(Collectors.toMap(OneHotAttributeMapping::getName, OneHotAttributeMapping::getAttributeName));
        attributeNames = getItemNameKey().getKeys().stream()
                .map(e -> e.getAttributeName())
                .collect(Collectors.toList());

    }

    private void writeToFile(ItemNameKey keys) throws IOException {
        FileUtils.writeReplaceJsonFile(Constant.FILE_NAME_ITEM_NAME_KEY, keys);
        reloadResource();
    }

    public ItemNameKey getItemNameKey() throws IOException {
        if (itemNameKey == null) {
            reloadResource();
        }
        return itemNameKey;
    }

    private Map<String, String> getKeyNameAndAttributeNames() throws IOException {
        if (keyNameAndAttributeNames == null) {
            reloadResource();
        }
        return keyNameAndAttributeNames;
    }

    public List<String> getAttributeNames() throws IOException {
        if (attributeNames == null) {
            reloadResource();
        }
        return attributeNames;
    }

    public synchronized List<Record> updateItemNameKeys(List<Record> records) throws IOException {
        final ItemNameKey newKeys = new ItemNameKey(new ArrayList(getItemNameKey().getKeys()));
        final Map<String, String> newKeyNameAndAttributeNames = new HashMap(getKeyNameAndAttributeNames());

        for (Record record: records) {
            Set<String> keys = splitItemName(record.getItemName());
            record.setActiveKeyAttributes(new HashSet());

            for (String key: keys) {
                String attributeName = newKeyNameAndAttributeNames.get(key);
                if (attributeName == null) {
                    attributeName = "keyName" + newKeyNameAndAttributeNames.size();
                    newKeyNameAndAttributeNames.put(key, attributeName);
                    newKeys.getKeys().add(new OneHotAttributeMapping(attributeName, key));
                }
                record.getActiveKeyAttributes().add(attributeName);
            }
        }
        writeToFile(newKeys);
        return records;
    }

    public Set<String> getActivateKeys(String itemName) throws IOException {
        final Map<String, String> newKeyNameAndAttributeNames = new HashMap(getKeyNameAndAttributeNames());
        Set<String> keys = splitItemName(itemName);
        return keys.stream().map(e -> newKeyNameAndAttributeNames.get(e)).collect(Collectors.toSet());
    }

    public int keyLength() throws IOException {
        return getItemNameKey().getKeys().size();
    }

    private Set<String> splitItemName(String itemName) {
        //transform
        itemName = KeyMatchers.COLOR.shiftMatchPrefixRight(itemName);

        // split
        String[] splited = itemName.split("\\s+");
        splited = KeyMatchers.COLOR.addPrefix(splited);

        Set<String> keys = new HashSet(Arrays.asList(splited));
        return keys;
    }
}
