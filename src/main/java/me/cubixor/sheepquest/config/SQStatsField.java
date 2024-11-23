package me.cubixor.sheepquest.config;

import me.cubixor.minigamesapi.spigot.config.stats.BasicStatsField;
import me.cubixor.minigamesapi.spigot.config.stats.StatsField;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum SQStatsField implements StatsField {
    SHEEP, KILLS, DEATHS, BONUS_SHEEP;

    public static List<StatsField> getAllFields() {
        return Stream.concat(
                        Arrays.stream(BasicStatsField.values()),
                        Arrays.stream(SQStatsField.values()))
                .collect(Collectors.toList());
    }

    @Override
    public String getCode() {
        return this.toString().toLowerCase();
    }
}
