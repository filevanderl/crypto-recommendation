package com.xm.recommend.service;

import com.xm.recommend.model.PriceRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

class ParsingServiceImplTest {

    @BeforeEach
    void setUp() {

    }

    @Test
    void parseCSVFilesForOneCrypto() {
        ParsingService service = new ParsingServiceImpl();
        Map<String, List<PriceRecord>> cryptos = service.parseCSVFiles("prices_1");
        assertEquals("Count of the parsed cryptos", 1, cryptos.size());
        assertTrue("Crypto symbol", cryptos.keySet().contains("BTC"));
        assertEquals("Number of records parsed", 3, cryptos.get("BTC").size());
    }

    @Test
    void parseCSVFilesForMultipleFiles() {
        ParsingService service = new ParsingServiceImpl();
        Map<String, List<PriceRecord>> cryptos = service.parseCSVFiles("prices_2");
        assertEquals("Count of the parsed cryptos", 1, cryptos.size());
        assertTrue("Crypto symbol", cryptos.keySet().contains("BTC"));
        assertEquals("Number of records parsed", 6, cryptos.get("BTC").size());
    }

    @Test
    void parseCSVFiles_InvalidCSV() {
        ParsingService service = new ParsingServiceImpl();
        Map<String, List<PriceRecord>> cryptos = service.parseCSVFiles("prices_3");
        assertEquals("Count of the parsed cryptos", 1, cryptos.size());
        assertTrue("Crypto symbol", cryptos.keySet().contains("BTC"));
        assertEquals("Number of records parsed", 3, cryptos.get("BTC").size());
    }
}