package com.xm.recommend.service;

import com.xm.recommend.model.Crypto;
import com.xm.recommend.model.DailyNormalizedRange;
import com.xm.recommend.model.PriceRecord;
import graphql.util.Pair;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    private static final String PATTERN_FORMAT = "dd.MM.yyyy";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT)
            .withZone(ZoneId.systemDefault());
    private final List<Crypto> cryptos = new ArrayList<>();

    public RecommendationServiceImpl(ParsingService parseService) {
        Map<String, List<PriceRecord>> priceRecordMap = parseService.parseCSVFiles("prices");
        priceRecordMap.forEach((k, v) -> {
            Crypto crypto = calculateRecommendation(k, v);
            cryptos.add(crypto);
        });
    }

    /**
     * Return single crypto record with min/max/newest/oldest values.
     */
    public Crypto getBySymbol(String symbol) {
        return cryptos.stream().filter(crypto -> crypto.symbol().equals(symbol))
                .findFirst()
                .orElseThrow(() -> new CryptoNotFoundException("Could not find requested symbol"));
    }

    /**
     * Return descending list of crypto records for a full period of available price values. Sorted by highest normalized range.
     */
    public List<Crypto> getAllSortedByNormalizedRange() {
        return cryptos.stream().sorted(Comparator.comparingDouble(Crypto::normalizedRange).reversed()).toList();
    }

    /**
     * Search and get records for a specific day for every crypto and return highest representation of daily range.
     */
    public DailyNormalizedRange getHighestNormalizedRange(String day) {
        List<DailyNormalizedRange> dailyNormalizedRanges = cryptos.stream().filter(crypto -> crypto.dailyRanges().get(day) != null).map(crypto ->
                new DailyNormalizedRange(day, crypto.symbol(), crypto.dailyRanges().get(day))).sorted(Comparator.comparingDouble(DailyNormalizedRange::value).reversed()).toList();
        if (dailyNormalizedRanges.isEmpty())
            throw new DailyRangeNotFoundException("Unable to find a highest daily normalized range for the provided day. Please provide day in 'dd.mm.yyyy' format (27.01.2022)");
        else
            return dailyNormalizedRanges.get(0);
    }

    private static Crypto calculateRecommendation(String symbol, List<PriceRecord> priceRecords) {
        var priceComparator = Comparator.comparing(PriceRecord::getPrice);
        var dateComparator = Comparator.comparing(PriceRecord::getTimestamp);

        Pair<PriceRecord, PriceRecord> minMaxPrice = getMinMax(priceRecords, priceComparator);
        Pair<PriceRecord, PriceRecord> firstLastDate = getMinMax(priceRecords, dateComparator);

        double min = minMaxPrice.first.getPrice();
        double max = minMaxPrice.second.getPrice();
        double normalizedRange = (max - min) / min;

        Map<String, List<PriceRecord>> dailyRecords = priceRecords.stream().collect(Collectors.groupingBy(e ->
                formatter.format(Instant.ofEpochMilli(e.getTimestamp()).truncatedTo(ChronoUnit.DAYS)))
        );

        Map<String, Double> dailyRanges = new HashMap<>();
        dailyRecords.forEach((k, v) -> {
            Pair<PriceRecord, PriceRecord> dailyMinMax = getMinMax(v, priceComparator);
            double normalizedDailyRange = (dailyMinMax.second.getPrice() - dailyMinMax.first.getPrice()) / dailyMinMax.first.getPrice();
            dailyRanges.put(k, normalizedDailyRange);
        });

        return new Crypto(symbol, min, max, normalizedRange, firstLastDate.second, firstLastDate.first, dailyRanges);
    }

    private static Pair<PriceRecord, PriceRecord> getMinMax(List<PriceRecord> priceRecords, Comparator<PriceRecord> priceComparator) {
        return priceRecords.stream()
                .collect(Collectors.teeing(
                        Collectors.minBy(priceComparator),
                        Collectors.maxBy(priceComparator),
                        (min, max) -> new Pair<>(min.orElse(null), max.orElse(null))
                ));
    }

}
