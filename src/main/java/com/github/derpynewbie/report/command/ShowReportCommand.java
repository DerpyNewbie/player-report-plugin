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
            handleShowLatest(commandSender, null, PluginConfig.LATEST_REPORT_SIZE.getInt(1), placeholderMap, false);
            return true;
        }

        placeholderMap.put("\\{FIRST_ARGUMENT\\}", strings[0]);
        OfflinePlayer target;

        if (strings.length == 1) {
            if (strings[0].toLowerCase().matches("remove")) {
                Messages.SHOW_REPORT_NOT_ENOUGH_ARGUMENT.sendMessageIfExists(commandSender, placeholderMap);
            } else if (strings[0].toLowerCase().matches("view")) {
                handleShowLatest(commandSender, null, PluginConfig.LATEST_REPORT_SIZE.getInt(1), placeholderMap, false);
            } else {
                target = Bukkit.getOfflinePlayer(strings[0]);
                handleShowLatest(commandSender, target, PluginConfig.LATEST_REPORT_SIZE.getInt(1), placeholderMap, true);
            }
            return true;
        }

        if (strings.length == 2) {
            if (strings[0].toLowerCase().matches("view|remove")) {
                placeholderMap.put("\\{FIRST_ARGUMENT\\}", strings[1]);
                target = Bukkit.getOfflinePlayer(strings[1]);

                if (strings[0].toLowerCase().matches("view")) {
                    handleShowLatest(commandSender, target, PluginConfig.LATEST_REPORT_SIZE.getInt(1), placeholderMap, true);
                } else if (strings[0].toLowerCase().matches("remove")) {
                    placeholderMap.putAll(Messages.getPlayerPlaceholder(target, "TARGET"));
                    Messages.SHOW_REPORT_NOT_ENOUGH_ARGUMENT.sendMessageIfExists(commandSender, placeholderMap);
                }
            } else {
                try {
                    target = Bukkit.getOfflinePlayer(strings[0]);
                    int size = Integer.parseInt(strings[1]);
                    // Duplicated code
                    handleShowLatest(commandSender, target, size, placeholderMap, true);
                } catch (NumberFormatException ex1) {
                    Messages.SHOW_REPORT_NO_INTEGER_PROVIDED.sendMessageIfExists(commandSender, placeholderMap);
                }
            }

            return true;
        }

        if (strings[0].toLowerCase().matches("view|remove")) {
            placeholderMap.put("\\{FIRST_ARGUMENT\\}", strings[1]);
            try {
                target = Bukkit.getOfflinePlayer(strings[1]);
                int uniqueKey = Integer.parseInt(strings[2]);

                placeholderMap.put("\\{UNIQUE_KEY\\}", String.valueOf(uniqueKey));
                placeholderMap.putAll(Messages.getPlayerPlaceholder(target, "TARGET"));

                if (strings[0].toLowerCase().matches("view")) {
                    List<ReportData> singleReport = getSingleReport(commandSender, target, uniqueKey);
                    if (!ReportData.isNullOrEmpty(singleReport.get(0)))
                        sendReportDataWithFormat(commandSender, singleReport, placeholderMap, true);
                    else
                        Messages.SHOW_REPORT_NO_SUCH_DATA_FOUND.sendMessageIfExists(commandSender, placeholderMap);
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

    private void handleShowLatest(@NotNull CommandSender sender, @Nullable OfflinePlayer target, int size, @NotNull Map<String, String> placeholder, boolean hasFirstArgument) {
        ArrayList<ReportData> reportDataList = getLatestReports(sender, target, size);
        placeholder.put("\\{REPORT_SIZE\\}", String.valueOf(reportDataList.size()));

        if (reportDataList.size() != 0) {
            if (target != null) {
                placeholder.putAll(Messages.getPlayerPlaceholder(target, "TARGET"));
                Messages.SHOW_REPORT_LATEST_WITH_NAME.sendMessageIfExists(sender, placeholder);
            } else {
                Messages.SHOW_REPORT_LATEST.sendMessageIfExists(sender, placeholder);
            }
        }

        sendReportDataWithFormat(sender, reportDataList, placeholder, hasFirstArgument);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            return FIRST_ARGUMENT.stream().filter(args -> args.toLowerCase().startsWith(strings[0].toLowerCase()) || strings[0].isEmpty()).collect(Collectors.toCollection(ArrayList::new));
        } else if (strings.length == 2) {
            if (strings[0].toLowerCase().matches("view|remove"))
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(plName -> plName.toLowerCase().startsWith(strings[1].toLowerCase()) || strings[1].isEmpty()).collect(Collectors.toCollection(ArrayList::new));
            else
                return getUniqueKeyList(strings[0]);
        } else if (strings.length == 3 && strings[0].toLowerCase().matches("view|remove")) {
            return getUniqueKeyList(strings[1]);
        }

        return null;
    }

    @NotNull
    private List<String> getUniqueKeyList(@NotNull String name) {
        if (name.isEmpty())
            return Collections.singletonList(ChatColor.RED + "[ERROR: Name is empty.]");
        ArrayList<ReportData> dataList = Report.getInstance().getHelper().getReportData(Bukkit.getOfflinePlayer(name).getUniqueId());
        if (dataList.size() == 0)
            return Collections.singletonList(ChatColor.RED + "[ERROR: No unique keys found.]");
        else
            return dataList.stream().map(ReportData::getUniqueKeyAsString).collect(Collectors.toCollection(ArrayList::new));
    }

    private void sendReportDataWithFormat(@NotNull CommandSender sender, @NotNull List<ReportData> reportDataList, @NotNull Map<String, String> placeholder, boolean hasFirstArgument) {
        if (reportDataList.size() == 0 || reportDataList.size() == 1 && ReportData.isNullOrEmpty(reportDataList.get(0))) {
            if (hasFirstArgument)
                Messages.SHOW_REPORT_NO_DATA_FOUND_WITH_FIRST_ARGUMENT.sendMessageIfExists(sender, placeholder);
            else
                Messages.SHOW_REPORT_NO_DATA_FOUND.sendMessageIfExists(sender, placeholder);
            return;
        }

        Messages.SHOW_REPORT_DATA_BEGIN.sendMessageIfExists(sender, placeholder);
        for (ReportData data :
                reportDataList) {
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

    @NotNull
    private List<ReportData> getSingleReport(@NotNull CommandSender sender, @NotNull OfflinePlayer player, int uniqueKey) {
        Messages.SHOW_REPORT_ON_DATA_LOAD.sendMessageIfExists(sender, null);
        return Collections.singletonList(Report.getInstance().getHelper().getReportDataOf(player.getUniqueId(), uniqueKey));
    }

    private Map<String, String> getUsagePlaceholder(Command command) {
        Map<String, String> placeholder = new HashMap<>();
        placeholder.put("\\{USAGE\\}", Messages.SHOW_REPORT_USAGE.getMessage().replaceAll("<command>", command.getLabel()));
        return placeholder;
    }


}
