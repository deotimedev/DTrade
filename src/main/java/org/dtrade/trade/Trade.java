package org.dtrade.trade;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.text.translate.UnicodeUnescaper;
import org.bukkit.Bukkit;
import org.dtrade.gui.guis.TradeGui;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor @Data
public class Trade {

    private static final Set<Trade> trades = new HashSet<>();

    private final UUID tradeID = UUID.randomUUID();
    private final TradeCouple couple;

    private boolean cancelled;

    public void cancel(Trader canceller) {
        cancelled = true;
        Bukkit.broadcastMessage("BEFORE LENGTH: " + trades.size());
        trades.removeIf(t -> t.getTradeID().equals(tradeID));
        Bukkit.broadcastMessage("AFTER LENGTH: " + trades.size());
        Bukkit.broadcastMessage(String.format("Trade %s cancelled by %s", tradeID, canceller.getPlayer().getName()));
        couple.both(t -> {
            t.getPlayer().sendMessage(t.equals(canceller) ? "\u00a7cYou cancelled the trade." : "\u00a7c" + canceller.getPlayer().getName() + " cancelled the trade.");
            if (!t.equals(canceller)) t.getPlayer().closeInventory();
            t.remove();
        });
    }

    public void initializeTrade() {
        couple.both((t) -> {
            t.getPlayer().openInventory(new TradeGui(t));
            t.getPlayer().sendMessage("\u00a7aYou are now trading with " + couple.other(t).getPlayer().getName() + ".");
        });
    }

    public static Trade createTrade(TradeCouple couple) {
        Bukkit.broadcastMessage(String.format("Trade created with %s and %s", couple.getTrader1().getPlayer().getName(), couple.getTrader2().getPlayer().getName()));
        Trade trade = new Trade(couple);
        trades.add(trade);
        return trade;
    }


    @SneakyThrows
    public static Trade getTradeOf(Trader trader) {
        Trade[] possibleTrades =  trades.stream()
                .filter(t -> t.getCouple().has(trader))
                .toArray(Trade[]::new);
        if(possibleTrades.length > 1) throw new MultiTradeException(trader, possibleTrades);
        return possibleTrades.length == 0 ? null : possibleTrades[0];
    }

}
