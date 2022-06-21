package org.dtrade.trade;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor @Data
public class TradeCouple {

    private final Trader trader1;
    private final Trader trader2;

    public static TradeCouple of(Trader trader1, Trader trader2) {
        return new TradeCouple(trader1, trader2);
    }

}