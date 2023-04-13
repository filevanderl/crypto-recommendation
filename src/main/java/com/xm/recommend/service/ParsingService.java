package com.xm.recommend.service;

import com.xm.recommend.model.PriceRecord;

import java.util.List;
import java.util.Map;

public interface ParsingService {
    Map<String, List<PriceRecord>> parseCSVFiles(String inputFolder);

}
