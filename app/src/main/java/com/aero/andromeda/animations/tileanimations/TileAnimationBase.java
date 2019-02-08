package com.aero.andromeda.animations.tileanimations;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;

import com.aero.andromeda.animations.TileAnimationManager;
import com.aero.andromeda.models.tiles.TileBase;

public abstract class TileAnimationBase implements ITileAnimation
{
    private Context parentContext;
    private boolean isEnabled;
    private final TileAnimationManager tileAnimationManager;
    private AnimatorSet tileAnimation = null;

    public TileAnimationBase(Context parentContext, TileAnimationManager tileAnimationManager)
    {
        this.parentContext = parentContext;
        this.tileAnimationManager = tileAnimationManager;
    }

    protected abstract void Load(TileBase tile);

    protected abstract void Animate(TileBase tile, int delay);

    protected abstract void Unload();

    @Override
    public void Start(TileBase tile, int delay)
    {
        this.Load(tile);
        this.tileAnimationManager.addTileAnimatorRelation(tile, this.tileAnimation);
        this.tileAnimation.addListener(this.tileAnimationManager);
        this.isEnabled = true;
        this.Animate(tile, delay);
    }

    @Override
    public void Start(TileBase tile)
    {
        this.Start(tile, 0);
    }

    @Override
    public void Stop()
    {
        this.tileAnimationManager.removeTileAnimatorRelation(this.tileAnimation);
        this.Unload();
        this.isEnabled = false;
    }

    @Override
    public boolean IsEnabled()
    {
        return this.isEnabled;
    }

    protected AnimatorSet getAnimator()
    {
        return this.tileAnimation;
    }

    protected void setAnimator(AnimatorSet animator)
    {
        this.tileAnimation = animator;
    }

    public Context getContext()
    {
        return this.parentContext;
    }
}
