package com.gatdsen.animation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.gatdsen.animation.entity.AnimatedEntity;
import com.gatdsen.animation.entity.Healthbar;
import com.gatdsen.ui.assets.AssetContainer;
import com.gatdsen.ui.assets.AssetContainer.IngameAssets.GameEnemyAnimationType;

import static com.gatdsen.ui.assets.AssetContainer.IngameAssets.gameEnemyAnimations;

public class GameEnemy extends AnimatedEntity {
    private Integer level;
    private BitmapFont fonte;
    public Healthbar healthbar;

    public GameEnemy(int level, int maxHealth, BitmapFont fonts) {
        super(gameEnemyAnimations[GameEnemyAnimationType.ANIMATION_TYPE_IDLE.ordinal()]);
        this.level = level;
        fonte = fonts;

        healthbar = new Healthbar(maxHealth);
        this.add(healthbar);

        fonte.setColor(Color.WHITE);
        fonte.getData().setScale(5);
    }

    @Override
    public void draw(Batch batch, float deltaTime, float parentAlpha) {
        fonte.draw(batch, level.toString(), this.getPos().x, this.getPos().y + 90);
        super.draw(batch, deltaTime, parentAlpha);
    }
}
