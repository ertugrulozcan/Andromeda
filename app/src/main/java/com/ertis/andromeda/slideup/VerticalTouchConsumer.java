package com.ertis.andromeda.slideup;

import android.view.MotionEvent;
import android.view.View;

/**
 * @author pa.gulko zTrap (05.07.2017)
 */
class VerticalTouchConsumer extends TouchConsumer
{
	private boolean mGoingUp = false;
	private boolean mGoingDown = false;
	
	VerticalTouchConsumer(SlideUpBuilder builder, LoggerNotifier notifier, AnimationProcessor animationProcessor)
	{
		super(builder, notifier, animationProcessor);
	}
	
	boolean consumeBottomToTop(View touchedView, MotionEvent event)
	{
		float touchedArea = event.getY();
		mViewHeight = mBuilder.mSliderView.getHeight();
		
		switch (event.getActionMasked())
		{
			case MotionEvent.ACTION_DOWN:
				mStartPositionY = event.getRawY();
				mViewStartPositionY = mBuilder.mSliderView.getTranslationY();
				mCanSlide = touchFromAlsoSlide(touchedView, event);
				mCanSlide |= mBuilder.mTouchableArea >= touchedArea;
				break;
			case MotionEvent.ACTION_MOVE:
				float difference = event.getRawY() - mStartPositionY;
				float moveTo = mViewStartPositionY + difference;
				float percents = moveTo * 100 / mViewHeight;
				calculateDirection(event);
				
				if (moveTo > 0 && mCanSlide)
				{
					if (Math.abs(moveTo) > mViewHeight / 10)
					{
						mNotifier.notifyPercentChanged(percents);
					}
					
					mBuilder.mSliderView.setTranslationY(moveTo);
				}
				break;
			case MotionEvent.ACTION_UP:
				float slideAnimationFrom = mBuilder.mSliderView.getTranslationY();
				if (slideAnimationFrom == mViewStartPositionY)
				{
					return !Internal.isUpEventInView(mBuilder.mSliderView, event);
				}
				
				boolean scrollableAreaConsumed = mBuilder.mSliderView.getTranslationY() > mViewHeight / 5;
				
				if (scrollableAreaConsumed && mGoingDown)
				{
					mAnimationProcessor.setValuesAndStart(slideAnimationFrom, mViewHeight);
				}
				else
				{
					mAnimationProcessor.setValuesAndStart(slideAnimationFrom, 0);
				}
				
				mCanSlide = true;
				mGoingUp = false;
				mGoingDown = false;
				break;
		}
		
		mPrevPositionY = event.getRawY();
		mPrevPositionX = event.getRawX();
		return true;
	}
	
	boolean consumeTopToBottom(View touchedView, MotionEvent event)
	{
		float touchedArea = event.getY();
		mViewHeight = mBuilder.mSliderView.getHeight();
		
		switch (event.getActionMasked())
		{
			case MotionEvent.ACTION_DOWN:
				mStartPositionY = event.getRawY();
				mViewStartPositionY = mBuilder.mSliderView.getTranslationY();
				mCanSlide = touchFromAlsoSlide(touchedView, event);
				mCanSlide |= getBottom() - mBuilder.mTouchableArea <= touchedArea;
				break;
			case MotionEvent.ACTION_MOVE:
				float difference = event.getRawY() - mStartPositionY;
				float moveTo = mViewStartPositionY + difference;
				float percents = moveTo * 100 / -mViewHeight;
				calculateDirection(event);
				
				if (moveTo < 0 && mCanSlide)
				{
					if (Math.abs(moveTo) > mViewHeight / 10)
					{
						mNotifier.notifyPercentChanged(percents);
					}
					
					mBuilder.mSliderView.setTranslationY(moveTo);
				}
				break;
			case MotionEvent.ACTION_UP:
				float slideAnimationFrom = -mBuilder.mSliderView.getTranslationY();
				if (slideAnimationFrom == mViewStartPositionY)
				{
					return !Internal.isUpEventInView(mBuilder.mSliderView, event);
				}
				
				boolean scrollableAreaConsumed = mBuilder.mSliderView.getTranslationY() < -mViewHeight / 5;
				
				if (scrollableAreaConsumed && mGoingUp)
				{
					mAnimationProcessor.setValuesAndStart(slideAnimationFrom, mViewHeight + mBuilder.mSliderView.getTop());
				}
				else
				{
					mAnimationProcessor.setValuesAndStart(slideAnimationFrom, 0);
				}
				
				mCanSlide = true;
				break;
		}
		
		mPrevPositionY = event.getRawY();
		mPrevPositionX = event.getRawX();
		return true;
	}
	
	private void calculateDirection(MotionEvent event)
	{
		mGoingUp = mPrevPositionY - event.getRawY() > 0;
		mGoingDown = mPrevPositionY - event.getRawY() < 0;
	}
}
