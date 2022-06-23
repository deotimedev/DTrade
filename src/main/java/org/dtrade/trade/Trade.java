package org.dtrade.trade;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.dtrade.DTrade;
import org.dtrade.gui.guis.TradeGui;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@RequiredArgsConstructor @Data
public class Trade {

    private static final Set<Trade> trades = new HashSet<>();

    private final UUID tradeID = UUID.randomUUID();
    private final TradeCouple couple;

    private boolean cancelled;

    private int secondsUntilAccept = -1;

    public void cancel(@NotNull Trader canceller) {
        cancelled = true;
        trades.removeIf(t -> t.getTradeID().equals(tradeID));
        couple.both(t -> {
            Player player = t.getPlayer();
            List<ItemStack> offeredItems = t.getOfferedItems();

            for(ItemStack item : offeredItems) {
                if(player.getInventory().firstEmpty() != -1) player.getInventory().addItem(item);
                else {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                    player.sendMessage("\u00a7cYour inventory was full so an item was dropped.");
                }
            }

            t.remove();
            t.getPlayer().sendMessage(t.equals(canceller) ? "\u00a7cYou cancelled the trade." : "\u00a7c" + canceller.getPlayer().getName() + " cancelled the trade.");
            if (!t.equals(canceller)) t.getPlayer().closeInventory();
        });
    }

    public void initializeTrade() {
        couple.both((t) -> {
            t.getPlayer().openInventory(new TradeGui(t));
            t.getPlayer().sendMessage("\u00a7aYou are now trading with " + couple.other(t).getPlayer().getName() + ".");
        });
    }

    public static @NotNull Trade createTrade(TradeCouple couple) {
        Bukkit.broadcastMessage(String.format("Trade created with %s and %s", couple.getTrader1().getPlayer().getName(), couple.getTrader2().getPlayer().getName()));
        Trade trade = new Trade(couple);
        trades.add(trade);
        return trade;
    }


    @SneakyThrows
    public static @Nullable Trade getTradeOf(Trader trader) {
        Trade[] possibleTrades =  trades.stream()
                .filter(t -> t.getCouple().has(trader))
                .toArray(Trade[]::new);
        if(possibleTrades.length > 1) throw new MultiTradeException(trader, possibleTrades);
        return possibleTrades.length == 0 ? null : possibleTrades[0];
    }

    public boolean isTradeAccepted() {
        return couple.bothMeet(Trader::isAcceptedTrade);
    }

    public void updateTradeAccepted() {
        if (!isTradeAccepted()) return;

        secondsUntilAccept = 5;

        new BukkitRunnable() {

            @Override
            public void run() {

                if (!isTradeAccepted()) {
                    secondsUntilAccept = -1;
                    couple.both(t -> {
                        t.getPlayer().updateInventory();
                    });
                    this.cancel();
                    return;
                }

                couple.both(t -> {
                    t.getPlayer().updateInventory();
                });

                if (secondsUntilAccept == 0) {
                    confirmTrade();
                    this.cancel();
                    secondsUntilAccept = -1;
                    return;
                }

                secondsUntilAccept--;

            }

        }.runTaskTimer(DTrade.getInstance(), 0L, 20L);

    }

    private void confirmTrade() {
        Bukkit.broadcastMessage("Trade accepted");
    }


}
