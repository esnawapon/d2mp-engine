package com.mlshop.engine.util;

import com.mlshop.engine.Constant;

public class KeyMatchers {
    public static final KeyMatcher COLOR = new KeyMatcher(
        Constant.KEYS_COLOR_PREFIX,
        Constant.KEYS_COLOR_INCLUDE
    );
}
