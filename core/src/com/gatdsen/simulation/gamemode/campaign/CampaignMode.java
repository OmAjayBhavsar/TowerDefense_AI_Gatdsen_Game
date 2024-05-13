package com.gatdsen.simulation.gamemode.campaign;

import com.gatdsen.simulation.gamemode.PlayableGameMode;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class CampaignMode extends PlayableGameMode {

    @Override
    public final boolean isAvailable() {
        ZoneId timeZone = ZoneId.of("Europe/Berlin");
        LocalDateTime now = LocalDateTime.now(timeZone);
        ZonedDateTime submissionOpen = ZonedDateTime.of(getSubmissionOpenTimestamp(), timeZone);
        return now.isAfter(ChronoLocalDateTime.from(submissionOpen));
    }

    abstract protected LocalDateTime getSubmissionOpenTimestamp();

    @Override
    public final String getDisplayName() {
        return "Kampagne " + getCampaignWeek() + "." + getCampaignTask();
    }

    abstract protected int getCampaignWeek();

    abstract protected int getCampaignTask();

    @Override
    public final Type getType() {
        return Type.CAMPAIGN;
    }

    private static final String[] WEEK_TASK_IDENTIFIERS = new String[]{
        "Campaign_%d_%d", "Campaign %d.%d", "c_%d_%d", "c %d.%d", "%d_%d", "%d.%d"
    };
    private static final String[] WEEK_IDENTIFIERS = new String[]{
        "Campaign %d", "c %d"
    };

    public final String[] getIdentifiers() {
        List<String> identifiers = new ArrayList<>();
        int campaignWeek = getCampaignWeek();
        int campaignTask = getCampaignTask();
        if (campaignTask == 1) {
            if (campaignWeek == 1) {
                identifiers.add(String.valueOf(getType().ordinal()));
                identifiers.add("Campaign");
                identifiers.add("c");
            }
            for (String identifier : WEEK_IDENTIFIERS) {
                identifiers.add(String.format(identifier, campaignWeek));
            }
        }
        for (String identifier : WEEK_TASK_IDENTIFIERS) {
            identifiers.add(String.format(identifier, campaignWeek, campaignTask));
        }
        return identifiers.toArray(new String[0]);
    }
}
