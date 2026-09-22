package com.smartwms.common.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class OrderNumberGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static String generate(String prefix) {
        String date = LocalDate.now().format(DATE_FORMAT);
        long timestamp = System.currentTimeMillis() % 10000;
        return String.format("%s%s%04d", prefix, date, timestamp);
    }

    public static String generateInboundOrder() {
        return generate("IN");
    }

    public static String generateOutboundOrder() {
        return generate("OUT");
    }

    public static String generateTransferOrder() {
        return generate("TR");
    }

    public static String generateStocktake() {
        return generate("ST");
    }

    public static String generatePickingTask() {
        return generate("PK");
    }

    public static String generatePutawayTask() {
        return generate("PUT");
    }

    public static String generateTaskOrder() {
        return generate("TK");
    }
}
