package com.github.derpynewbie.report.util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.InvalidParameterException;
import java.util.*;

public class ReportData {

    private final OfflinePlayer sender;
    private final OfflinePlayer reportedPlayer;
    private final String reason;
    private final Date date;
    private final Integer uniqueKey;
    private final boolean isEmpty;

    private ReportData() {
        sender = Bukkit.getOfflinePlayer("null");
        reportedPlayer = Bukkit.getOfflinePlayer("null");
        reason = "";
        date = new Date();
        uniqueKey = -1;
        isEmpty = true;
    }

    public ReportData(@NotNull OfflinePlayer sender, @NotNull OfflinePlayer reportedPlayer, @NotNull String reason) {
        this(sender, reportedPlayer, reason, null, -1);
    }

    public ReportData(@NotNull OfflinePlayer sender, @NotNull OfflinePlayer reportedPlayer, @NotNull String reason, @Nullable Long date, @Nullable Integer uniqueKey) {
        if (date == null || date == 0)
            date = System.currentTimeMillis();
        if (uniqueKey == null)
            uniqueKey = -1;

        this.sender = sender;
        this.reportedPlayer = reportedPlayer;
        this.reason = reason;
        this.date = new Date(date);
        this.uniqueKey = uniqueKey;
        this.isEmpty = false;
    }

    ReportData(@NotNull ConfigurationSection dataSection) {
        this(Bukkit.getOfflinePlayer(UUID.fromString(Objects.requireNonNull(dataSection.getString("reporter")))),
                Bukkit.getOfflinePlayer(UUID.fromString(getReportedPlayerSectionFromDataSection(dataSection).getName())),
                Objects.requireNonNull(dataSection.getString("reason")),
                dataSection.getLong("time"),
                Integer.valueOf(dataSection.getName()));
    }

    @NotNull
    private static ConfigurationSection getReportedPlayerSectionFromDataSection(@NotNull ConfigurationSection dataSection) throws InvalidParameterException {
        ConfigurationSection reportedPlayerSection = dataSection.getParent();
        if (reportedPlayerSection == null)
            throw new InvalidParameterException("Parameter dataSection does not contain correct data.");
        return reportedPlayerSection;
    }

    public static ReportData getEmpty() {
        return new ReportData();
    }

    public static boolean isNullOrEmpty(@Nullable ReportData data) {
        return data == null | (data != null && data.isEmpty);
    }

    @NotNull
    public OfflinePlayer getSender() {
        return sender;
    }

    @NotNull
    public String getSenderUUID() {
        return getSender().getUniqueId().toString();
    }

    @NotNull
    public OfflinePlayer getReportedPlayer() {
        return reportedPlayer;
    }

    @NotNull
    public String getReportedPlayerUUID() {
        return getReportedPlayer().getUniqueId().toString();
    }

    @NotNull
    public String getReason() {
        return reason;
    }

    @NotNull
    public Long getTime() {
        return date.getTime();
    }

    @NotNull
    public Integer getUniqueKey() {
        return uniqueKey;
    }

    @NotNull
    public String getUniqueKeyAsString() {
        return String.valueOf(getUniqueKey());
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    @NotNull
    public Map<String, String> getPlaceholder() {
        HashMap<String, String> placeholder = new HashMap<>(Messages.getPlayerPlaceholder(getSender(), "REPORT_SENDER"));
        placeholder.putAll(Messages.getPlayerPlaceholder(getReportedPlayer(), "REPORT_TARGET"));
        placeholder.put("\\{REPORT_REASON\\}", getReason());
        placeholder.put("\\{REPORT_DATE\\}", new Date(getTime()).toString());
        placeholder.put("\\{REPORT_UNIQUE_KEY\\}", String.valueOf(getUniqueKey()));

        return placeholder;
    }

    @Override
    public String toString() {
        return String.format("ReportData: %s, %s, %s, %s", getSenderUUID(), getReportedPlayerUUID(), reason, date.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ReportData)
            return ((ReportData) obj).getReportedPlayerUUID().equals(getReportedPlayerUUID()) && ((ReportData) obj).getUniqueKey().equals(getUniqueKey()) && ((ReportData) obj).getSenderUUID().equals(getSenderUUID());
        else if (obj instanceof Player)
            return ((Player) obj).getUniqueId().toString().equals(getReportedPlayerUUID());
        else
            return obj == this;
    }
}
