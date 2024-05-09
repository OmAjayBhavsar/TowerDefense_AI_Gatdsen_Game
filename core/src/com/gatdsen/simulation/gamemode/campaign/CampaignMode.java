package com.gatdsen.simulation.gamemode.campaign;

import com.gatdsen.simulation.GameMode;

public abstract class CampaignMode extends GameMode {

    @Override
    public void setMap(String map) {
        throw new UnsupportedOperationException("The map of a campaign mode can not be changed!");
    }

    @Override
    public boolean isCampaignMode() {
        return true;
    }
}
