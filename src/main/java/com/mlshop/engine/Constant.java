package com.mlshop.engine;

public class Constant {
    public static final String DIR_NAME_MAIN = "/Users/es/mlshop2";
    public static final String DIR_NAME_ARFF = DIR_NAME_MAIN + "/arff";
    public static final String DIR_NAME_MODEL = DIR_NAME_MAIN + "/model";
    public static final String DIR_NAME_PREDICT = DIR_NAME_MAIN + "/predict";
    public static final String DIR_NAME_MAPPING = DIR_NAME_MAIN + "/mapping";
    public static final String FILE_NAME_TEMPLATE = DIR_NAME_ARFF + "/template.arff";
    public static final String FILE_NAME_META_DATA = DIR_NAME_MAPPING + "/meta-data.json";
    public static final String FILE_NAME_ITEM_NAME_KEY = DIR_NAME_MAPPING + "/item-name-key.json";
    public static final String FILE_NAME_QUANTITY_RANGE = DIR_NAME_MAPPING + "/quantity-range.json";
    public static final String FILE_NAME_MAIN_MODEL = DIR_NAME_MODEL + "/main-model.mlshop";
    public static final String FILE_NAME_MAIN_PREDICT = DIR_NAME_PREDICT + "/main-predict.arff";
    public static final String[] SIZES = new String[] {"s","m","l","xl","2xl","3xl","4xl"};
    public static final int MONTH_PREDICTION_LENGTH = 3;

    public final class ColumnIndex {
        public static final int TIMESTAMP = 5;
        public static final int ITEM_NAME = 13;
        public static final int OPTION = 15;
        public static final int BASE_PRICE = 16;
        public static final int SELL_PRICE = 17;
        public static final int QUANTITY = 18;
    }

    // data transform
    public static final String KEYS_COLOR_PREFIX = "สี";
    public static final String[] KEYS_COLOR_INCLUDE = new String[] {"ขาว", "เทาฟ้า", "เทา", "กรม", "แดง", "เหลือง", "ฟ้า", "ยีนส์ขาว"};

}
