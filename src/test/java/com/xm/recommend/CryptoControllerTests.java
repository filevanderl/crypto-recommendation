package com.xm.recommend;

import com.xm.recommend.controller.CryptoController;
import com.xm.recommend.model.Crypto;
import com.xm.recommend.model.PriceRecord;
import com.xm.recommend.service.RecommendationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@GraphQlTest(CryptoController.class)
public class CryptoControllerTests {

    @Autowired
    private GraphQlTester graphQlTester;
    @MockBean
    private RecommendationService recommendationService;

    @Test
    void cryptoBySymbolTest() {
        Mockito.when(recommendationService.getBySymbol("BTC")).thenReturn(BTC);
        this.graphQlTester
                .documentName("cryptoBySymbol")
                .variable("symbol", "BTC")
                .execute()
                .path("cryptoBySymbol")
                .matchesJson("""
                              {
                                  "symbol": "BTC",
                                  "minPrice": 33276.59,
                                  "maxPrice": 47722.66,
                                  "normalizedRange": 0.43412110435594536,
                                  "newestRecord": {
                                    "timestamp": 1643659200000,
                                    "price": 38415.79
                                  },
                                  "oldestRecord": {
                                    "timestamp": 1641009600000,
                                    "price": 46813.21
                                  }
                                }
                              }
                        """);
    }

    @Test
    void shouldReturnSortedCryptoTest() {
        List<Crypto> cryptos = Arrays.asList(
                ETH, BTC
        );
        Mockito.when(recommendationService.getAllSortedByNormalizedRange()).thenReturn(cryptos);
        this.graphQlTester
                .documentName("getall")
                .execute()
                .path("getAllSortedByNormalizedRange")
                .matchesJson("""
                        [
                                                  {
                            "symbol": "ETH",
                            "normalizedRange": 1.0
                          },
                          {
                            "symbol": "BTC",
                            "normalizedRange": 0.43412110435594536
                          }
                        ]
                        """);
    }

    private static final Crypto BTC = new Crypto("BTC", 33276.59, 47722.66,
            0.43412110435594536, new PriceRecord(38415.79, "BTC", 1643659200000L),
            new PriceRecord(46813.21, "BTC", 1641009600000L), Collections.EMPTY_MAP);

    private static final Crypto ETH = new Crypto("ETH", 1, 2,
            1, new PriceRecord(1, "ETH", 1643659200000L),
            new PriceRecord(2, "ETH", 1641009600000L), Collections.EMPTY_MAP);
}
