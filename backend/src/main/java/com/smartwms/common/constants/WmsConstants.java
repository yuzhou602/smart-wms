package com.smartwms.common.constants;

public class WmsConstants {

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_HEADER = "Authorization";
    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USERNAME_HEADER = "X-Username";

    public static final String ORDER_PREFIX_IN = "IN";
    public static final String ORDER_PREFIX_OUT = "OUT";
    public static final String ORDER_PREFIX_TRANSFER = "TR";
    public static final String ORDER_PREFIX_STOCKTAKE = "ST";
    public static final String ORDER_PREFIX_PICK = "PK";
    public static final String ORDER_PREFIX_PUTAWAY = "PUT";

    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    public static final String CACHE_KEY_PREFIX = "smartwms:";
    public static final String CACHE_KEY_USER = CACHE_KEY_PREFIX + "user:";
    public static final String CACHE_KEY_DICT = CACHE_KEY_PREFIX + "dict:";
    public static final String CACHE_KEY_DASHBOARD = CACHE_KEY_PREFIX + "dashboard:";
    public static final String CACHE_KEY_INVENTORY = CACHE_KEY_PREFIX + "inventory:";

    public static final String LOCK_KEY_PREFIX = "lock:";
    public static final String LOCK_KEY_INVENTORY = LOCK_KEY_PREFIX + "inventory:";
    public static final String LOCK_KEY_ORDER = LOCK_KEY_PREFIX + "order:";
}
