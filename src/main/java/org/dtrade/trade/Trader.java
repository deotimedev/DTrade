package org.dtrade.trade;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.dtrade.EconomyHandler;

import java.util.*;

@RequiredArgsConstructor @Data
public class Trader {

    private static final Set<Trader> TRADERS = new HashSet<>();

    private transient Trade trade;
    private final UUID traderID = UUID.randomUUID();

    private final Player player;
    private final List<ItemStack> offeredItems = new LinkedList<>();
    private long offeredCoins = 0;
    private boolean acceptedTrade = false;

    public void toggleAccept() {
        acceptedTrade = !acceptedTrade;
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 100f, 1f);
        trade.getCouple().both((t) -> t.getPlayer().updateInventory());
        trade.updateTradeAccepted();
    }

    public void remove() {
        TRADERS.removeIf(t -> t.traderID.equals(traderID));
    }

    public void addTradeItem(ItemStack itemStack) {
        offeredItems.add(itemStack);
    }

    public void removeTradeItem(int index) {
        offeredItems.remove(index);
    }

    public boolean hasCoins(double amount) {
        Economy eco = trade.getPlugin().getEconomyHandler().getEconomy();
        double bal = eco.getBalance(getPlayer());
        return bal >= amount;
    }

    public Trader getPartner() {
        return trade.getCouple().other(this);
    }

    public static Trader createTrader(Player player) {
        Trader trader = new Trader(player);
        TRADERS.add(trader);
        return trader;
    }

    public static Trader getTrader(Player trader) {
        return TRADERS.stream().filter(t -> t.getPlayer().equals(trader)).findFirst().orElse(null);
    }

}
