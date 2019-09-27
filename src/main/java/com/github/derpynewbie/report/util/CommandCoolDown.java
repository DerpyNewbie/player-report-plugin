package com.github.derpynewbie.report.util;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.Consumer;

public class CommandCoolDown extends DerpyScheduler {

    private static HashMap<Player, CommandCoolDown> playerCommandCoolDownHashMap = new HashMap<>();
    private @NotNull Player coolDownPlayer;

    public CommandCoolDown(@NotNull Player p, @NotNull Double inSeconds) {
        super(inSeconds);
        this.coolDownPlayer = p;
    }

    private CommandCoolDown(@NotNull Player p, @NotNull Double inSeconds, Consumer<DerpyScheduler> updateNotifier) {
        super(inSeconds, updateNotifier);
        this.coolDownPlayer = p;
    }

    public static boolean isCoolingDown(Player player) {
        return playerCommandCoolDownHashMap.containsKey(player);
    }

    public static Double getSecondsLeft(Player player) {
        if (isCoolingDown(player))
            return playerCommandCoolDownHashMap.get(player).getSecondsLeft();
        else
            return -1D;
    }

    @Override
    public final void onStart() {
        super.onStart();
        playerCommandCoolDownHashMap.put(coolDownPlayer, this);
    }

    @Override
    public final void onRun() {

    }

    @Override
    public final void onEnd() {
        super.onEnd();
        playerCommandCoolDownHashMap.remove(coolDownPlayer);
    }
}
