package com.mlshop.engine.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class KeyMatcher {
    private String prefix;
    private Set<String> include;
    protected KeyMatcher(String prefix, String[] include) {
        this.prefix = prefix;
        this.include = new HashSet(Arrays.asList(include));
    }

    public String shiftMatchPrefixRight(String itemName) {
        return itemName.replaceAll(prefix, " " + prefix);
    }

    public String[] addPrefix(String[] keys) {
        return Arrays.stream(keys)
                .map(e -> include.contains(e) ? prefix + e : e)
                .filter(e -> !e.equals(prefix))
                .toArray(String[]::new);
    }
}
