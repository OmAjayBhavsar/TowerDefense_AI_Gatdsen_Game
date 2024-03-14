package com.gatdsen.animation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gatdsen.animation.entity.AnimatedEntity;
import com.gatdsen.ui.assets.AssetContainer;
import com.gatdsen.ui.assets.AssetContainer.IngameAssets.GameTowerAnimationType;

import static com.gatdsen.ui.assets.AssetContainer.IngameAssets.gameTowerAnimations;
//import com.gatdsen.ui.assets.AssetContainer;

public class GameTower extends AnimatedEntity {

    private Integer level = 1;
    private int type = 1;
    static private BitmapFont fonte = new BitmapFont();
    private Animation<TextureRegion> idleAnimation;
    public Animation<TextureRegion> attackAnimation;

    private boolean attacking = false;
    private GameTowerAnimationType currentAnimation = GameTowerAnimationType.ANIMATION_TYPE_IDLE;

    public GameTower(int level, int type) {
        super(gameTowerAnimations[type][GameTowerAnimationType.ANIMATION_TYPE_IDLE.ordinal()]);

        idleAnimation = gameTowerAnimations[type][GameTowerAnimationType.ANIMATION_TYPE_IDLE.ordinal()];
        attackAnimation = gameTowerAnimations[type][GameTowerAnimationType.ANIMATION_TYPE_ATTACK.ordinal()];
        this.level = level;
        this.type = type;

        fonte.setColor(Color.WHITE);
        fonte.getData().setScale(5);
    }

    // Animation auf Angriff ändern und Timer für Länge starten
    public void attack() {
        if (attackAnimation != null) {
            attacking = true;
            setAnimation(attackAnimation);
            resetAccTime();
        }
    }

    @Override
    public void draw(Batch batch, float deltaTime, float parentAlpha) {
        fonte.draw(batch, level.toString(), this.getPos().x + 10, this.getPos().y + 60);
        super.draw(batch, deltaTime, parentAlpha);

        // Angriffs-Animation zurücksetzen, wenn sie durchgelaufen ist.
        if (attacking && attackAnimation.isAnimationFinished(getAccTime())) {
            attacking = false;
            setAnimation(idleAnimation);
            resetAccTime();
        }
    }
}
