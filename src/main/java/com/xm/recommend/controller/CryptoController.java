package com.xm.recommend.controller;


import com.xm.recommend.model.Crypto;
import com.xm.recommend.model.DailyNormalizedRange;
import com.xm.recommend.service.RecommendationService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class CryptoController {

    private final RecommendationService recommendationService;

    public CryptoController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @QueryMapping
    public Crypto cryptoBySymbol(@Argument String symbol) {
        return recommendationService.getBySymbol(symbol);
    }

    @QueryMapping
    public List<Crypto> getAllSortedByNormalizedRange() {
        return recommendationService.getAllSortedByNormalizedRange();
    }

    @QueryMapping
    public DailyNormalizedRange highestDailyNormalizedRange(@Argument String day) {
        return recommendationService.getHighestNormalizedRange(day);
    }

}