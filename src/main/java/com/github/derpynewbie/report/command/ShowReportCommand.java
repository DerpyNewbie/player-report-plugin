package com.github.derpynewbie.report.command;

import com.github.derpynewbie.report.Report;
import com.github.derpynewbie.report.util.Messages;
import com.github.derpynewbie.report.util.PluginConfig;
import com.github.derpynewbie.report.util.ReportData;
import com.github.derpynewbie.report.util.comparator.ReportDataDateComparator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;


public class ShowReportCommand implements TabExecutor {

    private static final ArrayList<String> FIRST_ARGUMENT = new ArrayList<>(Arrays.asList("view", "remove"));

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Map<String, String> placeholderMap = getUsagePlaceholder(command);
        ArrayList<ReportData> reportDataList;

        placeholderMap.putAll(Messages.getCommandSenderPlaceholder(commandSender));

        if (strings.length == 0) {
            int i = PluginConfig.LATEST_REPORT_SIZE.getInt();
            reportDataList = getLatestReports(commandSender, null, Math.max(i, 1));
            placeholderMap.put("\\{REPORT_SIZE\\}", String.valueOf(reportDataList.size()));
            if (reportDataList.size() != 0)
                Messages.SHOW_REPORT_LATEST.sendMessageIfExists(commandSender, placeholderMap);
            sendReportDataWithFormat(commandSender, reportDataList, placeholderMap, false);
            return true;
        }

        placeholderMap.put("\\{FIRST_ARGUMENT\\}", strings[0]);
        OfflinePlayer target;

        if (strings.length == 1) {
            if (strings[0].toLowerCase().matches("view|remove")) {
                Messages.SHOW_REPORT_NOT_ENOUGH_ARGUMENT.sendMessageIfExists(commandSender, placeholderMap);
            } else {
                target = Bukkit.getOfflinePlayer(strings[0]);
                placeholderMap.putAll(Messages.getPlayerPlaceholder(target, "TARGET"));
                // Duplicated code
                reportDataList = getLatestReports(commandSender, target, PluginConfig.LATEST_REPORT_SIZE.getInt());
                placeholderMap.put("\\{REPORT_SIZE\\}", String.valueOf(reportDataList.size()));
                if (reportDataList.size() != 0)
                    Messages.SHOW_REPORT_LATEST.sendMessageIfExists(commandSender, placeholderMap);
                sendReportDataWithFormat(commandSender, reportDataList, placeholderMap, true);
            }

            return true;
        }

        if (strings.length == 2) {
            if (strings[0].toLowerCase().matches("view|remove")) {
                target = Bukkit.getOfflinePlayer(strings[1]);
                placeholderMap.putAll(Messages.getPlayerPlaceholder(target, "TARGET"));

                if (strings[0].toLowerCase().matches("view")) {
                    // Duplicated code
                    reportDataList = getLatestReports(commandSender, target, PluginConfig.LATEST_REPORT_SIZE.getInt());
                    sendReportDataWithFormat(commandSender, reportDataList, placeholderMap, true);
                } else if (strings[0].toLowerCase().matches("remove")) {
                    Messages.SHOW_REPORT_NOT_ENOUGH_ARGUMENT.sendMessageIfExists(commandSender, placeholderMap);
                }
            } else {
                try {
                    target = Bukkit.getOfflinePlayer(strings[0]);
                    int size = Integer.parseInt(strings[1]);
                    // Duplicated code
                    reportDataList = getLatestReports(commandSender, target, size);
                    Messages.SHOW_REPORT_LATEST.sendMessageIfExists(commandSender, placeholderMap);
                    sendReportDataWithFormat(commandSender, reportDataList, placeholderMap, true);
                } catch (NumberFormatException ex1) {
                    Messages.SHOW_REPORT_NO_INTEGER_PROVIDED.sendMessageIfExists(commandSender, placeholderMap);
                }
            }

            return true;
        }

        if (strings[0].toLowerCase().matches("view|remove")) {
            try {
                target = Bukkit.getOfflinePlayer(strings[1]);
                int uniqueKey = Integer.parseInt(strings[2]);

                placeholderMap.put("\\{UNIQUE_KEY\\}", String.valueOf(uniqueKey));
                placeholderMap.putAll(Messages.getPlayerPlaceholder(target, "TARGET"));

                if (strings[0].toLowerCase().matches("view")) {
                    reportDataList = getReports(commandSender, target);

                    for (ReportData data :
                            reportDataList) {
                        if (data.getUniqueKey() != uniqueKey)
                            reportDataList.remove(data);
                    }

                    sendReportDataWithFormat(commandSender, reportDataList, placeholderMap, true);

                } else if (strings[0].toLowerCase().matches("remove")) {
                    try {
                        ReportData removedReportData = Report.getInstance().getHelper().removeReportData(target, uniqueKey);
                        placeholderMap.putAll(removedReportData.getPlaceholder());
                        Messages.SHOW_REPORT_REMOVE_SUCCESS.sendMessageIfExists(commandSender, placeholderMap);

                    } catch (NullPointerException | ArrayIndexOutOfBoundsException ex2) {
                        Messages.SHOW_REPORT_NO_SUCH_DATA_FOUND.sendMessageIfExists(commandSender, placeholderMap);
                    }
                }
            } catch (NumberFormatException ex2) {
                Messages.SHOW_REPORT_NO_INTEGER_PROVIDED.sendMessageIfExists(commandSender, placeholderMap);
            }

            return true;
        }
        Messages.SHOW_REPORT_WRONG_SYNTAX.sendMessageIfExists(commandSender, placeholderMap);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            return FIRST_ARGUMENT.stream().sorted((o1, o2) -> o1.compareTo(strings[0])).collect(Collectors.toCollection(ArrayList::new));
        } else if (strings.length == 2) {
            if (strings[0].toLowerCase().matches("view|remove"))
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).sorted((o1, o2) -> o1.compareTo(strings[0])).collect(Collectors.toCollection(ArrayList::new));
            else
                return getUniqueKeyList(strings[0]);
        } else if (strings.length == 3 && strings[0].toLowerCase().matches("view|remove")) {
            return getUniqueKeyList(strings[1]);
        }

        return null;
    }

    @NotNull
    private List<String> getUniqueKeyList(String name) {
        ArrayList<ReportData> dataList = Report.getInstance().getHelper().getReportData(Bukkit.getOfflinePlayer(name).getUniqueId());
        if (dataList.size() == 0)
            return Collections.singletonList(ChatColor.RED + "No unique keys found.");
        else
            return dataList.stream().map(ReportData::getUniqueKeyAsString).collect(Collectors.toCollection(ArrayList::new));
    }

    private void sendReportDataWithFormat(@NotNull CommandSender sender, @NotNull ArrayList<ReportData> reportDataArrayList, @NotNull Map<String, String> placeholder, boolean hasFirstArgument) {
        if (reportDataArrayList.size() == 0) {
            if (hasFirstArgument)
                Messages.SHOW_REPORT_NO_DATA_FOUND_WITH_FIRST_ARGUMENT.sendMessageIfExists(sender, placeholder);
            else
                Messages.SHOW_REPORT_NO_DATA_FOUND.sendMessageIfExists(sender, placeholder);
            return;
        }

        Messages.SHOW_REPORT_DATA_BEGIN.sendMessageIfExists(sender, placeholder);
        for (ReportData data :
                reportDataArrayList) {
            placeholder.putAll(data.getPlaceholder());
            Messages.SHOW_REPORT_DATA_FORMAT.sendMessageIfExists(sender, placeholder);
        }
        Messages.SHOW_REPORT_DATA_END.sendMessageIfExists(sender, placeholder);
    }

    @NotNull
    private ArrayList<ReportData> getLatestReports(@NotNull CommandSender sender, @Nullable OfflinePlayer player, int size) {
        ArrayList<ReportData> reportDataList = getReports(sender, player);
        reportDataList.sort(new ReportDataDateComparator());
        ArrayList<ReportData> latestReports = new ArrayList<>(reportDataList.subList(0, Math.min(reportDataList.size(), size)));
        latestReports.sort(new ReportDataDateComparator());
        return latestReports;
    }

    @NotNull
    private ArrayList<ReportData> getReports(@NotNull CommandSender sender, @Nullable OfflinePlayer player) {
        Messages.SHOW_REPORT_ON_DATA_LOAD.sendMessageIfExists(sender, null);
        if (player == null)
            return Report.getInstance().getHelper().getReportData();
        else
            return Report.getInstance().getHelper().getReportData(player.getUniqueId());
    }

    private Map<String, String> getUsagePlaceholder(Command command) {
        Map<String, String> placeholder = new HashMap<>();
        placeholder.put("\\{USAGE\\}", Messages.SHOW_REPORT_USAGE.getMessage().replaceAll("<command>", command.getLabel()));
        return placeholder;
    }


}
