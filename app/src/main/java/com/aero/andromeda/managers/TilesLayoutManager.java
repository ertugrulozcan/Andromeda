package com.aero.andromeda.managers;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Parcelable;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.aero.andromeda.helpers.BoundsHelper;

import java.util.HashMap;

public class TilesLayoutManager extends RecyclerView.LayoutManager
{
	public enum Orientation
	{
		VERTICAL, HORIZONTAL
	}
	
	public enum Direction
	{
		START, END
	}
	
	private int spanCount;
	private BoundsHelper rectsHelper;
	private boolean itemOrderIsStable;
	
	private Orientation orientation;
	private int layoutStart = 0;
	private int layoutEnd = 0;
	private int scroll = 0;
	
	private HashMap<Integer, Rect> childFrames = new HashMap<>();
	
	private Integer pendingScrollToPosition;
	
	public TilesLayoutManager(Orientation orientation, int spanSize)
	{
		this.orientation = orientation;
		this.spanCount = spanSize;
	}
	
	@Override
	public RecyclerView.LayoutParams generateDefaultLayoutParams()
	{
		return new RecyclerView.LayoutParams(
				RecyclerView.LayoutParams.MATCH_PARENT,
				RecyclerView.LayoutParams.WRAP_CONTENT);
	}
	
	private int getSize()
	{
		if (orientation == Orientation.VERTICAL)
			return this.getHeight();
		else
			return this.getWidth();
	}
	
	public boolean isItemOrderIsStable()
	{
		return itemOrderIsStable;
	}
	
	public void setItemOrderIsStable(boolean itemOrderIsStable)
	{
		this.itemOrderIsStable = itemOrderIsStable;
	}
	
	@Override
	public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state)
	{
		this.rectsHelper = new BoundsHelper(this, this.orientation);
		
		this.layoutStart = getPaddingStartForOrientation();
		this.layoutEnd = getPaddingEndForOrientation();
		
		// Clear cache, since layout may change
		childFrames.clear();
		
		// If there were any views, detach them so they can be recycled
		this.detachAndScrapAttachedViews(recycler);
		
		Integer pendingScroll = this.pendingScrollToPosition;
		if (pendingScroll != null && pendingScroll >= this.getSpanCount())
		{
			this.scroll = 0;
			View lastAddedView = null;
			int position = 0;
			
			// Keep adding views until reaching the one needed
			for (int i = 0; i < pendingScroll; i++)
			{
				if (lastAddedView != null)
				{
					// Recycle views to reduce RAM usage
					this.updateEdgesWithRemovedChild(lastAddedView, Direction.START);
					this.removeAndRecycleView(lastAddedView, recycler);
				}
				
				lastAddedView = this.makeView(position, Direction.END, recycler);
				this.updateEdgesWithNewChild(lastAddedView);
				
				position++;
			}
			
			View view = makeView(pendingScroll, Direction.END, recycler);
			
			int offset = 0;
			if (orientation == Orientation.VERTICAL)
				offset = view.getTop() - getTopDecorationHeight(view);
			else
				offset = view.getLeft() - getLeftDecorationWidth(view);
			
			this.removeAndRecycleView(view, recycler);
			
			this.layoutStart = offset;
			this.scrollBy(-offset, state);
			this.fillAfter(pendingScroll, recycler, state, this.getSize());
			this.recycleChildrenOutOfBounds(Direction.END, recycler);
			
			this.pendingScrollToPosition = null;
		}
		else
		{
			// Fill from start to visible end
			this.fillGap(Direction.END, recycler, state);
			
			this.recycleChildrenOutOfBounds(Direction.END, recycler);
		}
	}
	
	@Override
	public void onLayoutCompleted(RecyclerView.State state)
	{
		super.onLayoutCompleted(state);
		
		// Check if after changes in layout we aren't out of its bounds
		int overScroll = scroll + this.getSize() - layoutEnd - getPaddingEndForOrientation();
		boolean allItemsInScreen = this.getFirstVisiblePosition() == 0 && this.getLastVisiblePosition() == state.getItemCount() - 1;
		if (!allItemsInScreen && overScroll > 0)
		{
			// If we are, fix it
			scrollBy(overScroll, state);
		}
	}
	
	public int getSpanCount()
	{
		return this.spanCount;
	}
	
	protected void measureChild(int position, View view) throws Exception
	{
		int itemWidth = this.rectsHelper.getItemSize();
		int itemHeight = this.rectsHelper.getItemSize();
		
		if (!(view.getLayoutParams() instanceof TileSpanLayoutParams))
		{
			throw new Exception("View LayoutParams must be of type TileSpanLayoutParams");
		}
		
		TileSpanLayoutParams layoutParams = (TileSpanLayoutParams)view.getLayoutParams();
		SpanSize spanSize = layoutParams.getSpanSize();
		
		int usedSpan = 0;
		if (orientation == Orientation.HORIZONTAL)
			usedSpan = spanSize.getHeight();
		else
			usedSpan = spanSize.getWidth();
		
		if (usedSpan > this.getSpanCount() || usedSpan < 1)
		{
			throw new Exception("SpanSize error!");
		}
		
		// This rect contains just the row and column number - i.e.: [0, 0, 1, 1]
		Rect rect = this.rectsHelper.findRect(position, spanSize);
		
		// Multiply the rect for item width and height to get positions
		int left = rect.left * itemWidth;
		int right = rect.right * itemWidth;
		int top = rect.top * itemHeight;
		int bottom = rect.bottom * itemHeight;
		
		Rect insetsRect = new Rect();
		this.calculateItemDecorationsForChild(view, insetsRect);
		
		// Measure child
		int width = right - left - insetsRect.left - insetsRect.right;
		int height = bottom - top - insetsRect.top - insetsRect.bottom;
		layoutParams.width = width;
		layoutParams.height = height;
		measureChildWithMargins(view, width, height);
		
		// Remove free space from the helper
		this.rectsHelper.pushRect(position, rect);
		
		// Cache rect
		childFrames.put(position, new Rect(left, top, right, bottom));
	}
	
	protected void layoutChild(int position, View view)
	{
		Rect frame = childFrames.get(position);
		
		if (frame != null)
		{
			int scroll = this.scroll;
			
			int startPadding = getPaddingStartForOrientation();
			
			if (orientation == Orientation.VERTICAL)
			{
				layoutDecorated(view,
						frame.left + this.getPaddingLeft(),
						frame.top - scroll + startPadding,
						frame.right + this.getPaddingLeft(),
						frame.bottom - scroll + startPadding);
			}
			else
			{
				layoutDecorated(view,
						frame.left - scroll + startPadding,
						frame.top + this.getPaddingTop(),
						frame.right - scroll + startPadding,
						frame.bottom + this.getPaddingTop());
			}
		}
		
		// A new child was layouted, layout edges change
		updateEdgesWithNewChild(view);
	}
	
	protected void fillGap(Direction direction, RecyclerView.Recycler recycler, RecyclerView.State state)
	{
		int firstPosition = this.getFirstVisiblePosition();
		
		if (direction == Direction.END)
		{
			this.fillAfter(firstPosition + this.getChildCount(), recycler, state, this.getSize());
		}
		else
		{
			this.fillBefore(firstPosition - 1, recycler, 0);
		}
	}
	
	protected void fillAfter(int positionParam, RecyclerView.Recycler recycler, RecyclerView.State state, int extraSpace)
	{
		int position = positionParam;
		View itemAtPosition = findViewByPosition(position - 1);
		
		int decorTop = 0;
		if (itemAtPosition != null)
			getTopDecorationHeight(itemAtPosition);
		
		int limit = getPaddingStartForOrientation() + scroll + this.getSize() + extraSpace + decorTop;
		
		while (this.canAddMoreViews(Direction.END, limit) && position < state.getItemCount())
		{
			View addedView = this.makeAndAddView(position, Direction.END, recycler);
			if (addedView == null)
				break;
			
			position++;
		}
	}
	
	protected void fillBefore(int positionParam, RecyclerView.Recycler recycler, int extraSpace)
	{
		int position = positionParam;
		int limit = getPaddingStartForOrientation() + scroll + extraSpace;
		Integer startOfLine = null;
		
		while ((canAddMoreViews(Direction.START, limit) || this.isInIncompleteLine(position, startOfLine)) && position >= 0)
		{
			this.makeAndAddView(position, Direction.START, recycler);
			
			if (childFrames.containsKey(position))
			{
				if (orientation == Orientation.VERTICAL)
					startOfLine = childFrames.get(position).top;
				else
					startOfLine = childFrames.get(position).left;
			}
			
			position--;
		}
	}
	
	private boolean isInIncompleteLine(int position, Integer startOfLine)
	{
		if (childFrames.containsKey(position))
		{
			Rect frame = childFrames.get(position);
			int start;
			if (orientation == Orientation.VERTICAL)
				start = frame.top;
			else
				start = frame.left;
			
			if (startOfLine != null)
				return start == startOfLine;
		}
		
		return false;
	}
	
	protected boolean canAddMoreViews(Direction direction, int limit)
	{
		if (direction == Direction.START)
			return this.getFirstVisiblePosition() > 0 && limit < layoutStart;
		else
			return limit > layoutEnd;
	}
	
	protected View makeAndAddView(int position, Direction direction, RecyclerView.Recycler recycler)
	{
		try
		{
			View view = recycler.getViewForPosition(position);
			this.measureChild(position, view);
			this.layoutChild(position, view);
			
			if (direction == Direction.END)
			{
				this.addView(view);
			}
			else
			{
				this.addView(view, 0);
			}
			
			return view;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	protected View makeView(int position, Direction direction, RecyclerView.Recycler recycler)
	{
		try
		{
			View view = recycler.getViewForPosition(position);
			this.measureChild(position, view);
			this.layoutChild(position, view);
			
			return view;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	protected int getFirstVisiblePosition()
	{
		if (this.getChildCount() == 0)
		{
			return 0;
		}
		
		return getPosition(getChildAt(0));
	}
	
	protected int getLastVisiblePosition()
	{
		if (this.getChildCount() == 0)
		{
			return 0;
		}
		
		return getPosition(getChildAt(this.getChildCount() - 1));
	}
	
	protected int getPaddingStartForOrientation()
	{
		if (this.orientation == Orientation.VERTICAL)
			return this.getPaddingTop();
		else
			return this.getPaddingLeft();
	}
	
	protected int getPaddingEndForOrientation()
	{
		if (this.orientation == Orientation.VERTICAL)
			return this.getPaddingBottom();
		else
			return this.getPaddingRight();
	}
	
	protected void updateEdgesWithNewChild(View view)
	{
		int childStart = this.getChildStart(view) + scroll + getPaddingStartForOrientation();
		if (childStart < this.layoutStart)
		{
			this.layoutStart = childStart;
		}
		
		int childEnd = this.getChildEnd(view) + scroll + getPaddingStartForOrientation();
		if (childEnd > this.layoutEnd)
		{
			this.layoutEnd = childEnd;
		}
	}
	
	protected void updateEdgesWithRemovedChild(View view, Direction direction)
	{
		int childStart = getChildStart(view) + this.scroll;
		int childEnd = getChildEnd(view) + this.scroll;
		
		if (direction == Direction.END)
		{
			// Removed from start
			this.layoutStart = getPaddingStartForOrientation() + childEnd;
		}
		else if (direction == Direction.START)
		{
			// Removed from end
			this.layoutEnd = getPaddingStartForOrientation() + childStart;
		}
	}
	
	private int getChildStart(View child)
	{
		if (this.orientation == Orientation.VERTICAL)
		{
			return getDecoratedTop(child);
		}
		else
		{
			return getDecoratedLeft(child);
		}
	}
	
	private int getChildEnd(View child)
	{
		if (this.orientation == Orientation.VERTICAL)
		{
			return getDecoratedBottom(child);
		}
		else
		{
			return getDecoratedRight(child);
		}
	}
	
	protected void recycleChildrenOutOfBounds(Direction direction, RecyclerView.Recycler recycler)
	{
		if (direction == Direction.END)
		{
			recycleChildrenFromStart(direction, recycler);
		}
		else
		{
			recycleChildrenFromEnd(direction, recycler);
		}
	}
	
	protected void recycleChildrenFromStart(Direction direction, RecyclerView.Recycler recycler)
	{
		int childCount = this.getChildCount();
		int start = this.getPaddingStartForOrientation();
		
		int detachedCount = 0;
		
		for (int i = 0; i < childCount; i++)
		{
			View child = getChildAt(i);
			int childEnd = getChildEnd(child);
			
			if (childEnd >= start)
			{
				break;
			}
			
			detachedCount++;
		}
		
		while (detachedCount-- > 0)
		{
			View child = getChildAt(0);
			removeAndRecycleView(child, recycler);
			updateEdgesWithRemovedChild(child, direction);
		}
	}
	
	protected void recycleChildrenFromEnd(Direction direction, RecyclerView.Recycler recycler)
	{
		int childCount = this.getChildCount();
		int end = this.getSize() + getPaddingEndForOrientation();
		
		int firstDetachedPos = 0;
		int detachedCount = 0;
		
		for (int i = childCount - 1; i >= 0; i--)
		{
			View child = getChildAt(i);
			int childStart = getChildStart(child);
			
			if (childStart <= end)
			{
				break;
			}
			
			firstDetachedPos = i;
			detachedCount++;
		}
		
		while (detachedCount-- > 0)
		{
			View child = getChildAt(firstDetachedPos);
			removeAndRecycleViewAt(firstDetachedPos, recycler);
			updateEdgesWithRemovedChild(child, direction);
		}
	}
	
	protected void scrollBy(int distanceParam, RecyclerView.State state)
	{
		int distance = distanceParam;
		int paddingEndLayout = getPaddingEndForOrientation();
		
		int start = 0;
		int end = layoutEnd + paddingEndLayout;
		
		scroll -= distance;
		
		// Correct scroll if was out of bounds at start
		if (scroll < start)
		{
			distance += scroll;
			scroll = start;
		}
		
		// Correct scroll if it would make the layout scroll out of bounds at the end
		if (scroll + this.getSize() > end && (this.getFirstVisiblePosition() + this.getChildCount() + this.getSpanCount()) >= state.getItemCount())
		{
			distance -= (end - scroll - this.getSize());
			scroll = end - this.getSize();
		}
		
		if (orientation == Orientation.VERTICAL)
		{
			offsetChildrenVertical(distance);
		}
		else
		{
			offsetChildrenHorizontal(distance);
		}
	}
	
	private int scrollBy(int deltaParam, RecyclerView.Recycler recycler, RecyclerView.State state)
	{
		int delta = deltaParam;
		
		// If there are no view or no movement, return
		if (delta == 0)
		{
			return 0;
		}
		
		boolean canScrollBackwards = this.getFirstVisiblePosition() >= 0 && 0 < scroll && delta < 0;
		
		boolean canScrollForward =
				(this.getFirstVisiblePosition() + this.getChildCount()) <= state.getItemCount() &&
						layoutEnd + getPaddingEndForOrientation() > (this.scroll + this.getSize()) &&
						delta > 0;
		
		// If can't scroll forward or backwards, return
		if (!(canScrollBackwards || canScrollForward))
		{
			return 0;
		}
		
		this.scrollBy(-delta, state);
		
		Direction direction = Direction.START;
		if (delta > 0)
			direction = Direction.END;
		
		this.recycleChildrenOutOfBounds(direction, recycler);
		this.fillGap(direction, recycler, state);
		
		return delta;
	}
	
	@Override
	public int computeVerticalScrollOffset(RecyclerView.State state)
	{
		if (this.getChildCount() == 0)
		{
			return 0;
		}
		
		return this.getFirstVisiblePosition();
	}
	
	@Override
	public int computeVerticalScrollExtent(RecyclerView.State state)
	{
		return this.getChildCount();
	}
	
	@Override
	public int computeVerticalScrollRange(RecyclerView.State state)
	{
		return state.getItemCount();
	}
	
	@Override
	public boolean canScrollVertically()
	{
		return this.orientation == Orientation.VERTICAL;
	}
	
	@Override
	public boolean canScrollHorizontally()
	{
		return this.orientation == Orientation.HORIZONTAL;
	}
	
	@Override
	public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state)
	{
		return this.scrollBy(dx, recycler, state);
	}
	
	@Override
	public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state)
	{
		return this.scrollBy(dy, recycler, state);
	}
	
	@Override
	public void scrollToPosition(int position)
	{
		this.pendingScrollToPosition = position;
		this.requestLayout();
	}
	
	@Override
	public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position)
	{
		LinearSmoothScroller smoothScroller = new LinearSmoothScroller(recyclerView.getContext())
		{
			@Override
			public PointF computeScrollVectorForPosition(int targetPosition)
			{
				if (TilesLayoutManager.this.getChildCount() == 0)
				{
					return null;
				}
				
				int direction = 1;
				if (targetPosition < TilesLayoutManager.this.getFirstVisiblePosition())
					direction = -1;
				
				return new PointF(0f, (float)direction);
			}
			
			@Override
			public int getVerticalSnapPreference()
			{
				return LinearSmoothScroller.SNAP_TO_START;
			}
		};
	}
	
	
	@Override
	public int getDecoratedMeasuredWidth(View child)
	{
		int position = this.getPosition(child);
		if (childFrames.containsKey(position))
			return childFrames.get(position).width();
		else
			return 0;
	}
	
	@Override
	public int getDecoratedMeasuredHeight(View child)
	{
		int position = this.getPosition(child);
		if (childFrames.containsKey(position))
			return childFrames.get(position).height();
		else
			return 0;
	}
	
	@Override
	public int getDecoratedTop(View child)
	{
		int position = getPosition(child);
		int decoration = getTopDecorationHeight(child);
		
		int top = 0;
		if (childFrames.containsKey(position))
			top = childFrames.get(position).top + decoration;
		
		if (orientation == Orientation.VERTICAL)
		{
			top -= scroll;
		}
		
		return top;
	}
	
	@Override
	public int getDecoratedRight(View child)
	{
		int position = getPosition(child);
		int decoration = getLeftDecorationWidth(child) + getRightDecorationWidth(child);
		
		int right = 0;
		if (childFrames.containsKey(position))
			right = childFrames.get(position).right + decoration;
		
		if (orientation == Orientation.HORIZONTAL)
		{
			right -= this.scroll - getPaddingStartForOrientation();
		}
		
		return right;
	}
	
	@Override
	public int getDecoratedLeft(View child)
	{
		int position = getPosition(child);
		int decoration = getLeftDecorationWidth(child);
		
		int left = 0;
		if (childFrames.containsKey(position))
			left = childFrames.get(position).left + decoration;
		
		if (orientation == Orientation.HORIZONTAL)
		{
			left -= this.scroll;
		}
		
		return left;
	}
	
	@Override
	public int getDecoratedBottom(View child)
	{
		int position = getPosition(child);
		int decoration = getTopDecorationHeight(child) + getBottomDecorationHeight(child);
		
		int bottom = 0;
		if (childFrames.containsKey(position))
			bottom = childFrames.get(position).bottom + decoration;
		
		if (orientation == Orientation.VERTICAL)
		{
			bottom -= this.scroll - getPaddingStartForOrientation();
		}
		
		return bottom;
	}
	
	
	public static class SpanSize
	{
		private int width;
		private int height;
		
		public SpanSize(int width, int height)
		{
			this.width = width;
			this.height = height;
		}
		
		public int getWidth()
		{
			return width;
		}
		
		public void setWidth(int width)
		{
			this.width = width;
		}
		
		public int getHeight()
		{
			return height;
		}
		
		public void setHeight(int height)
		{
			this.height = height;
		}
	}
	
	public static class TileSpanLayoutParams extends RecyclerView.LayoutParams
	{
		private SpanSize spanSize = new SpanSize(width = 0, height = 0);
		
		public SpanSize getSpanSize()
		{
			return this.spanSize;
		}
		
		public TileSpanLayoutParams(SpanSize spanSize)
		{
			super(spanSize.width, spanSize.height);
			this.spanSize = spanSize;
		}
		
		public TileSpanLayoutParams(Context c, AttributeSet attrs)
		{
			super(c, attrs);
		}
		
		public TileSpanLayoutParams(int width, int height)
		{
			super(width, height);
		}
		
		public TileSpanLayoutParams(ViewGroup.MarginLayoutParams source)
		{
			super(source);
		}
		
		public TileSpanLayoutParams(ViewGroup.LayoutParams source)
		{
			super(source);
		}
		
		public TileSpanLayoutParams(RecyclerView.LayoutParams source)
		{
			super(source);
		}
	}
}
