package com.xm.recommend.service;

import com.xm.recommend.model.Crypto;
import com.xm.recommend.model.PriceRecord;
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
    }

    @Test
    void getHighestNormalizedRange() {
    }

    Map<String, List<PriceRecord>> cryptos = Map.of(
            "BTC", Arrays.asList(
                    new PriceRecord("BTC", 1000, 1641020400000L),
                    new PriceRecord("BTC", 1500, 1741020400000L),
                    new PriceRecord("BTC", 2000, 1841020400000L))
    );
}