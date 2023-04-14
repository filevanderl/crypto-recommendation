package com.xm.recommend.service;

import com.opencsv.bean.CsvToBeanBuilder;
import com.xm.recommend.model.PriceRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.EMPTY_MAP;

/**
 * Service parsing CSV files from classpath resources.
 */
@Service
public class ParsingServiceImpl implements ParsingService {
    private static final Logger log = LoggerFactory.getLogger("com.xm.recommend.service.ParsingService");
    private static final char CSV_COLUMN_SEPARATOR = ',';

    public Map<String, List<PriceRecord>> parseCSVFiles(String inputFolder) {
        Map<String, List<PriceRecord>> cryptos = new HashMap<>();
        Path folderPath;
        try {
            folderPath = ResourceUtils.getFile("classpath:" + inputFolder).toPath();
        } catch (Exception e) {
            log.error("Could not find 'prices' folder with CSV files!");
            return EMPTY_MAP;
        }

        try (Stream<Path> paths = Files.walk(folderPath)) {
            paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".csv"))
                    .forEach((path) -> {
                        log.info("parsing {} to get prize values", path.getFileName());
                        try {
                            List<PriceRecord> records = readFile(path.toFile());
                            if (!records.isEmpty()) {
                                String symbol = records.get(0).getSymbol();
                                // we need to parse at least symbol to consider record valid
                                if (StringUtils.isNoneBlank(symbol)) {
                                    // assuming in one file we can have records for one crypto type and never mixed records for different cryptos
                                    if (cryptos.containsKey(symbol)) {
                                        cryptos.get(symbol).addAll(records);
                                    } else {
                                        cryptos.put(records.get(0).getSymbol(), records);
                                    }
                                }
                            }
                        } catch (FileNotFoundException e) {
                            log.error("Could not read a file '{}' for parsing", path.getFileName());
                        }
                    });

        } catch (IOException e) {
            log.error("Error reading input folder with CSV files!");
        }
        return cryptos;
    }

    private List<PriceRecord> readFile(File csvFile) throws FileNotFoundException {
        return new CsvToBeanBuilder<PriceRecord>(new FileReader(csvFile))
                .withSeparator(CSV_COLUMN_SEPARATOR)
                .withIgnoreQuotations(true)
                .withType(PriceRecord.class)
                .build()
                .parse();
    }
}
