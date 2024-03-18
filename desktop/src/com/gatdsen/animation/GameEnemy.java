package com.gatdsen.animation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
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
        healthbar.setRelPos(new Vector2(100, 70));

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
        super.draw(batch, deltaTime, parentAlpha);
        fonte.draw(batch, level.toString(), this.getPos().x + 100, this.getPos().y + 70 + 90);

        //moving zurücksetzen, wenn Path zu Ende
        if (moving == 1 && getAccTime() >= moveDuration) {
            moving = 0;
            setAnimation(gameEnemyAnimations[cur][moving]);
            resetAccTime();
        }
    }
}
