package com.github.derpynewbie.report.command;

import com.github.derpynewbie.report.Report;
import com.github.derpynewbie.report.util.CommandCoolDown;
import com.github.derpynewbie.report.util.Messages;
import com.github.derpynewbie.report.util.PluginConfig;
import com.github.derpynewbie.report.util.ReportData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Map<String, String> placeholder = new HashMap<>(getUsagePlaceholder(command));

        // Player / Console check
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Messages.REPORT_NO_CONSOLE.getMessage());
            return true;
        }

        Player sender = (Player) commandSender;
        Player target;
        placeholder.putAll(Messages.getPlayerPlaceholder(sender));
        placeholder.put("\\{COOL_DOWN_TIME\\}", String.format(PluginConfig.COOL_DOWN_TIME_FORMAT.getString(), CommandCoolDown.getSecondsLeft(sender)));

        if (CommandCoolDown.isCoolingDown(sender)) {
            Messages.REPORT_NO_COMMAND_WHILE_IN_COOL_DOWN.sendMessageIfExists(sender, placeholder);
            return true;
        }

        if (strings.length <= 0) {
            Messages.REPORT_NO_PLAYER_NAME_PROVIDED.sendMessageIfExists(sender, placeholder);
            return true;
        }

        target = Bukkit.getPlayer(strings[0]);
        placeholder.put("\\{FIRST_ARGUMENT\\}", strings[0]);

        if (target == null) {
            Messages.REPORT_PLAYER_NOT_ONLINE.sendMessageIfExists(sender, placeholder);
            return true;
        }

        if (target.getUniqueId() == sender.getUniqueId()) {
            Messages.REPORT_NO_YOURSELF.sendMessageIfExists(sender, placeholder);
            return true;
        }

        if (strings.length <= 1) {
            Messages.REPORT_NO_REASON_PROVIDED.sendMessageIfExists(sender, placeholder);
            return true;
        }

        // Begin actual command logic
        StringBuilder stringBuffer = new StringBuilder();

        for (int i = 1; i < strings.length; i++)
            stringBuffer.append(strings[i]).append(" ");

        String reason = stringBuffer.toString().trim();
        placeholder.putAll(Messages.getPlayerPlaceholder(target, "TARGET"));
        placeholder.put("\\{REASON\\}", reason);

        ReportData reportData = new ReportData(sender, target, reason);
        Report.getInstance().getHelper().createReportData(reportData);

        Messages.REPORT_SUCCESS.sendMessageIfExists(sender, placeholder);
        Report.getInstance().broadcastReport(reportData);
        Double coolDownTime = PluginConfig.COOL_DOWN_TIME.getDouble();
        new CommandCoolDown(sender, coolDownTime).start();

        return true;
    }

    private Map<String, String> getUsagePlaceholder(Command command) {
        Map<String, String> placeholder = new HashMap<>();
        placeholder.put("\\{USAGE\\}", Messages.REPORT_USAGE.getMessage().replaceAll("<command>", command.getLabel()));
        return placeholder;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length <= 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(plName -> plName.toLowerCase().startsWith(strings[0].toLowerCase()) || strings[0].isEmpty()).collect(Collectors.toCollection(ArrayList::new));
        }
        return null;
    }
}
