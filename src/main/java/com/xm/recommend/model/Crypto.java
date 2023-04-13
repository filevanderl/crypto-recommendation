package com.xm.recommend.model;

import java.util.Map;

public record Crypto(String symbol, double minPrice, double maxPrice, double normalizedRange, PriceRecord newestRecord,
                     PriceRecord oldestRecord, Map<String, Double> dailyRanges) {
}

