package com.xm.recommend.service;

import com.xm.recommend.model.Crypto;
import com.xm.recommend.model.DailyNormalizedRange;

import java.util.List;

public interface RecommendationService {
    Crypto getBySymbol(String symbol);

    DailyNormalizedRange getHighestNormalizedRange(String day);

    List<Crypto> getAllSortedByNormalizedRange();

}
