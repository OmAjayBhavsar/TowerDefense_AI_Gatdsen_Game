package com.gatdsen.animation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.gatdsen.animation.entity.AnimatedEntity;
import com.gatdsen.animation.entity.Healthbar;
import com.gatdsen.ui.assets.AssetContainer;
import com.gatdsen.ui.assets.AssetContainer.IngameAssets.GameEnemyAnimationType;
import com.gatdsen.ui.assets.AssetContainer.IngameAssets.Direction;

import static com.gatdsen.ui.assets.AssetContainer.IngameAssets.gameEnemyAnimations;

public class GameEnemy extends AnimatedEntity {
    private Integer level;
    private int moving = 0;
    private float moveDuration;
    private int cur = 1;
    static private BitmapFont fonte = new BitmapFont();
    public Healthbar healthbar;

    public GameEnemy(int level, int maxHealth) {
        super(gameEnemyAnimations[Direction.RIGHT.ordinal()][GameEnemyAnimationType.ANIMATION_TYPE_IDLE.ordinal()]);
        this.level = level;
        healthbar = new Healthbar(maxHealth);
        this.add(healthbar);

        fonte.setColor(Color.WHITE);
        fonte.getData().setScale(5);
    }

    /*
    * 0 = up
    * 1 = right
    * 2 = down
    * 3 = down
     */
    public void switchAnimation(int direction) {
        cur = direction;
        setAnimation(gameEnemyAnimations[cur][moving]);
    }

    public void toggleMove(float duration) {
        moving = 1;
        moveDuration = duration;
        setAnimation(gameEnemyAnimations[cur][moving]);
        resetAccTime();
    }

    @Override
    public void draw(Batch batch, float deltaTime, float parentAlpha) {
        fonte.draw(batch, level.toString(), this.getPos().x, this.getPos().y + 90);
        super.draw(batch, deltaTime, parentAlpha);

        //moving zurücksetzen, wenn Path zu Ende
        if (moving == 1 && getAccTime() >= moveDuration) {
            moving = 0;
            setAnimation(gameEnemyAnimations[cur][moving]);
            resetAccTime();
        }
    }
}
