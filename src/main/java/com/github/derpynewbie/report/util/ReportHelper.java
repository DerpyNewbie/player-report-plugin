package com.github.derpynewbie.report.util;

import com.github.derpynewbie.report.Report;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ReportHelper {

    private static final String REPORT_DATA_PATH = "data.report";
    private final JavaPlugin plugin;
    private File yamlFile;
    private FileConfiguration configFile;

    public ReportHelper(@NotNull JavaPlugin pl) {
        this.plugin = pl;
        this.yamlFile = new File(pl.getDataFolder(), "report-data.yml");
        this.configFile = YamlConfiguration.loadConfiguration(yamlFile);

        if (!this.configFile.isConfigurationSection("data")) {
            this.configFile.createSection("data");
        }
    }

    public boolean saveDataConfig() {
        try {
            this.configFile.save(yamlFile);
            Report.getInstance().getLogger().info("Saved report data.");
            return true;
        } catch (IOException ex) {
            Report.getInstance().getLogger().severe(String.format("Could not save report data to %s", yamlFile.getPath()));
            ex.printStackTrace();
            return false;
        }
    }

    public FileConfiguration getDataConfig() {
        return configFile;
    }

    @Deprecated
    public boolean createData(@NotNull String dataName, @Nullable Object data) {
        try {
            this.getConfigurationSection("data", false).set(dataName, data);
            return true;
        } catch (IllegalArgumentException | IllegalStateException ex1) {
            ex1.printStackTrace();
            return false;
        }
    }

    public boolean createReportData(@NotNull ReportData data) {
        String path = REPORT_DATA_PATH + "." + data.getReportedPlayerUUID();
        if (!this.configFile.isConfigurationSection(path)) {
            this.configFile.createSection(path);
        }
        ConfigurationSection reporterSection = this.configFile.getConfigurationSection(path);

        if (reporterSection != null) {
            int index = reporterSection.getKeys(false).size();

            reporterSection.set(index + ".reporter", data.getSenderUUID());
            reporterSection.set(index + ".reason", data.getReason());
            reporterSection.set(index + ".time", data.getTime());

            return true;
        } else
            return false;
    }

    public ReportData removeReportData(@NotNull OfflinePlayer reportedPlayer, int uniqueKey) throws NullPointerException, ArrayIndexOutOfBoundsException {
        ConfigurationSection reporterSection = getReporterSection(reportedPlayer.getUniqueId());
        ConfigurationSection removedSection = reporterSection.getConfigurationSection(String.valueOf(uniqueKey));

        if (removedSection == null) {
            Report.getInstance().getLogger().warning("Could not remove report data: report does not exist.");
            throw new NullPointerException(String.format("Could not find report data of player %s", reportedPlayer.getName()));
        }

        ReportData removedReportData = new ReportData(removedSection);
        reporterSection.set(String.valueOf(uniqueKey), null);
        return removedReportData;


//        if (!isReportExist(reportedPlayer)) {

//        } else if (reporterSection.getKeys(false).size() < uniqueKey) {
//            Report.getInstance().getLogger().warning("Could not remove report data: index out of bounds");
//            throw new ArrayIndexOutOfBoundsException("Could not remove report data.");
//        }

//        String[] keys = reporterSection.getKeys(false).toArray(new String[]{});
//        Map<String, Object> objects = new HashMap<>();
//        ReportData removedData = ReportData.getEmpty();
//
//        int newIndex = 0;
//        for (int i = 0; i < keys.length; i++) {
//            if (i != index) {
//                objects.put(String.valueOf(newIndex), reporterSection.get(keys[i]));
//                newIndex++;
//            } else {
//                removedData = new ReportData(getConfigurationSection(reporterSection.getCurrentPath() + "." + String.valueOf(i), false));
//            }
//        }
//
//        if (!objects.isEmpty())
//            this.getReportRootSection().createSection(reportedPlayer.getUniqueId().toString(), objects);
//        else
//            this.getReportRootSection().set(reportedPlayer.getUniqueId().toString(), null);
//        return removedData;
    }

    public boolean isReportExist(@NotNull OfflinePlayer reportedPlayer) {
        ConfigurationSection reporterSection = getReporterSection(reportedPlayer.getUniqueId());
        return reporterSection.getKeys(false).size() != 0;
    }
    // Naming rule   : data.<rootSection>.<reporterSection>.<dataSection>.<data>
    //               : data.<REPORT_DATA_PATH>.<uniqueId>.<uniqueKey>.<data>
    // Data Structure: data.report.<reportedPlayer's Unique ID>.<Unique Keys>.<Data Name>

    @Nullable
    public ReportData getReportDataOf(@NotNull UUID uniqueId, int uniqueKey) {
        ConfigurationSection reporterSection = getReporterSection(uniqueId);
        ConfigurationSection dataSection = reporterSection.getConfigurationSection(String.valueOf(uniqueKey));

        if (dataSection != null)
            return new ReportData(dataSection);
        else
            return null;
    }

    @NotNull
    public ArrayList<ReportData> getReportData(UUID uniqueId) {
        if (!isReportExist(Bukkit.getOfflinePlayer(uniqueId))) {
            plugin.getLogger().fine("User " + uniqueId.toString() + " does not have reports. please try /removeGarbageData");
            return new ArrayList<>();
        }

        ConfigurationSection reporterSection = getReporterSection(uniqueId);
        ArrayList<ReportData> reportDataList = new ArrayList<>();

        for (String uniqueKey :
                reporterSection.getKeys(false)) {
            ConfigurationSection dataSection = reporterSection.getConfigurationSection(uniqueKey);

            if (dataSection != null) {
                try {
                    reportDataList.add(new ReportData(dataSection));
                } catch (IllegalArgumentException ex1) {
                    plugin.getLogger().warning("Could not load report data from " + dataSection.getCurrentPath() + ". Did you edited the data file?");
                }
            } else {
                plugin.getLogger().warning("Could not load data section from " + reporterSection.getCurrentPath() + ", Unique key: " + uniqueKey + ". Did you edited the data file?");
            }
        }

        return reportDataList;
    }

    @NotNull
    public ArrayList<ReportData> getReportData() {
        ConfigurationSection rootSection = getReportRootSection();
        ArrayList<ReportData> reportDataList = new ArrayList<>();

        for (String reportedPlayerUUID :
                rootSection.getKeys(false)) {
            reportDataList.addAll(getReportData(UUID.fromString(reportedPlayerUUID)));
        }

        return reportDataList;
    }

    public List<String> getReportedPlayerNameList() {
        ConfigurationSection rootSection = getReportRootSection();
        return rootSection.getKeys(false).parallelStream().map(Bukkit::getOfflinePlayer).map(OfflinePlayer::getName).collect(Collectors.toCollection(ArrayList::new));
    }

    public void removeGarbageData() {
        ConfigurationSection rootSection = getReportRootSection();
        for (String reportedPlayerUUID :
                rootSection.getKeys(false)) {
            ConfigurationSection reporterSection = rootSection.getConfigurationSection(reportedPlayerUUID);
            if (reporterSection != null && reporterSection.getKeys(false).size() == 0)
                rootSection.set(reportedPlayerUUID, null);
        }
    }

    @NotNull
    private ConfigurationSection getReporterSection(@NotNull UUID uniqueId) {
        return getConfigurationSection(REPORT_DATA_PATH + "." + uniqueId.toString(), false);
    }

    @NotNull
    private ConfigurationSection getReportRootSection() {
        return getConfigurationSection(REPORT_DATA_PATH, false);
    }

    @NotNull
    private ConfigurationSection getConfigurationSection(@NotNull String path, boolean forceGenerate) {
        ConfigurationSection reporterSection = this.configFile.getConfigurationSection(path);
        return forceGenerate || reporterSection == null ? this.configFile.createSection(path) : reporterSection;
    }

}
