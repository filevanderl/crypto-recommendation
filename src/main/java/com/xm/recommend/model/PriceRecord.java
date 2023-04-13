package com.xm.recommend.model;

import com.opencsv.bean.CsvBindByName;

public class PriceRecord {
    @CsvBindByName
    double price;
    @CsvBindByName
    String symbol;
    @CsvBindByName
    long timestamp;

    public PriceRecord(String symbol, double price, long timestamp) {
        this.price = price;
        this.symbol = symbol;
        this.timestamp = timestamp;
    }

    public PriceRecord() {

    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}