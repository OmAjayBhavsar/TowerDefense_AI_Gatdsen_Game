package com.gatdsen.simulation.gamemode.campaign;

import com.gatdsen.simulation.gamemode.PlayableGameMode;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDateTime;

public abstract class CampaignMode extends PlayableGameMode {

    @Override
    public Type getType() {
        return Type.CAMPAIGN;
    }

    @Override
    public boolean isAvailable() {
        ZoneId timeZone = ZoneId.of("Europe/Berlin");
        LocalDateTime now = LocalDateTime.now(timeZone);
        ZonedDateTime submissionOpen = ZonedDateTime.of(getSubmissionOpenTimestamp(), timeZone);
        return now.isAfter(ChronoLocalDateTime.from(submissionOpen));
    }

    protected abstract LocalDateTime getSubmissionOpenTimestamp();
}
