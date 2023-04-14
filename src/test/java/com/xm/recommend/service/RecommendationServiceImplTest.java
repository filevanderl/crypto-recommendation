package com.xm.recommend.service;

import com.xm.recommend.model.Crypto;
import com.xm.recommend.model.DailyNormalizedRange;
import com.xm.recommend.model.PriceRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceImplTest {
    @Mock
    private ParsingService parsingService;

    @Test
    void getBySymbol() {
        Mockito.when(parsingService.parseCSVFiles("prices")).thenReturn(cryptos);
        RecommendationService recommendationService = new RecommendationServiceImpl(parsingService);
        Crypto crypto = recommendationService.getBySymbol("BTC");
        assertNotNull("Return is not null", crypto);
        assertEquals("symbol", "BTC", crypto.symbol());
        assertEquals("max price ", 2000.0, crypto.maxPrice());
        assertEquals("min price", 1000.0, crypto.minPrice());
        assertEquals("normalized range ", 1.0, crypto.normalizedRange());
        assertEquals("newest record timestamp ", 1841020400000L, crypto.newestRecord().getTimestamp());
        assertEquals("oldest record timestamp ", 1641020400000L, crypto.oldestRecord().getTimestamp());
    }

    @Test
    void getAllSortedByNormalizedRange() {
        Mockito.when(parsingService.parseCSVFiles("prices")).thenReturn(cryptos);
        RecommendationService recommendationService = new RecommendationServiceImpl(parsingService);
        List<Crypto> cryptos = recommendationService.getAllSortedByNormalizedRange();
        assertEquals("count", 2, cryptos.size());
        assertEquals("highest normalized ", "ETH", cryptos.get(0).symbol());
        assertEquals("second highest normalized ", "BTC", cryptos.get(1).symbol());
    }

    @Test
    void getHighestNormalizedRange() {
        Mockito.when(parsingService.parseCSVFiles("prices")).thenReturn(cryptos);
        RecommendationService recommendationService = new RecommendationServiceImpl(parsingService);
        DailyNormalizedRange highestCrypto = recommendationService.getHighestNormalizedRange("01.01.2022");
        assertNotNull("Return is not null", highestCrypto);
        assertEquals("highest normalized for day", "ETH", highestCrypto.symbol());
        assertEquals("highest normalized value", 4.0, highestCrypto.value());
    }

    @Test
    void getHighestNormalizedRangeInvalidDay() {
        Mockito.when(parsingService.parseCSVFiles("prices")).thenReturn(cryptos);
        RecommendationService recommendationService = new RecommendationServiceImpl(parsingService);
        Assertions.assertThrows(DailyRangeNotFoundException.class, () -> recommendationService.getHighestNormalizedRange("01.01.2023"));
    }

    Map<String, List<PriceRecord>> cryptos = Map.of(
            "BTC", Arrays.asList(
                    new PriceRecord("BTC", 1000, 1641020400000L),
                    new PriceRecord("BTC", 1500, 1741020400000L),
                    new PriceRecord("BTC", 2000, 1841020400000L)),
            "ETH", Arrays.asList(
                    new PriceRecord("ETH", 1000, 1641020400000L),
                    new PriceRecord("ETH", 4000, 1641020500000L),
                    new PriceRecord("ETH", 5000, 1641020600000L))
    );
}