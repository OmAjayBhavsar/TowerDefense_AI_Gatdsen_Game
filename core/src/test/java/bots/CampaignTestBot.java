package bots;

import com.gatdsen.manager.state;
import com.gatdsen.manager.player.Bot;
import com.gatdsen.manager.Controller;

public class CampaignTestBot extends Bot {
    @Override
    public String getStudentName() {
        return "Cornelius Zenker";
    }

    @Override
    public int getMatrikel() {
        return -1; //Heh, you thought
    }

    @Override
    public String getName() {
        return "Training Bot";
    }

    @Override
    public void init(state state) {

    }

    @Override
    public void executeTurn(state state, Controller controller) {
        controller.getRemainingUses();
    }
}
