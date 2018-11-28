package com.aero.andromeda.helpers;

import android.graphics.Rect;

import com.aero.andromeda.managers.TilesLayoutManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.aero.andromeda.managers.TilesLayoutManager.Orientation.VERTICAL;
import static com.aero.andromeda.managers.TilesLayoutManager.Orientation.HORIZONTAL;

public class BoundsHelper
{
	private final TilesLayoutManager layoutManager;
	private final TilesLayoutManager.Orientation orientation;
	
	private HashMap<Integer, Rect> rectsCache;
	private ArrayList<Rect> freeRects;
	
	final private Comparator<Rect> rectComparator = new Comparator<Rect>()
	{
		@Override
		public int compare(Rect rect1, Rect rect2)
		{
			switch (BoundsHelper.this.orientation)
			{
				case VERTICAL:
				{
					if (rect1.top == rect2.top)
					{
						if (rect1.left < rect2.left)
						{
							return -1;
						}
						else
						{
							return 1;
						}
					}
					else
					{
						if (rect1.top < rect2.top)
						{
							return -1;
						}
						else
						{
							return 1;
						}
					}
				}
				case HORIZONTAL:
				{
					if (rect1.left == rect2.left)
					{
						if (rect1.top < rect2.top)
						{
							return -1;
						}
						else
						{
							return 1;
						}
					}
					else
					{
						if (rect1.left < rect2.left)
						{
							return -1;
						}
						else
						{
							return 1;
						}
					}
				}
			}
			
			return 0;
		}
	};
	
	public BoundsHelper(final TilesLayoutManager layoutManager, final TilesLayoutManager.Orientation orientation)
	{
		this.layoutManager = layoutManager;
		this.orientation = orientation;
		
		this.rectsCache = new HashMap<>();
		this.freeRects = new ArrayList<>();
		
		Rect initialFreeRect;
		if (orientation == VERTICAL)
		{
			initialFreeRect = new Rect(0, 0, this.layoutManager.getSpanCount(), Integer.MAX_VALUE);
		}
		else
		{
			initialFreeRect = new Rect(0, 0, Integer.MAX_VALUE, this.layoutManager.getSpanCount());
		}
		
		this.freeRects.add(initialFreeRect);
	}
	
	public int getSize()
	{
		if (this.orientation == VERTICAL)
		{
			return layoutManager.getWidth() - layoutManager.getPaddingLeft() - layoutManager.getPaddingRight();
		}
		else
		{
			return layoutManager.getHeight() - layoutManager.getPaddingTop() - layoutManager.getPaddingBottom();
		}
	}
	
	public int getItemSize()
	{
		return this.getSize() / this.layoutManager.getSpanCount();
	}
	
	public int getStart()
	{
		if (orientation == VERTICAL)
		{
			return this.freeRects.get(0).top * this.getItemSize();
		}
		else
		{
			return this.freeRects.get(0).left * this.getItemSize();
		}
	}
	
	public int getEnd()
	{
		if (orientation == VERTICAL)
		{
			return (this.freeRects.get(this.freeRects.size() - 1).top + 1) * this.getItemSize();
		}
		else
		{
			return (this.freeRects.get(this.freeRects.size() - 1).left + 1) * this.getItemSize();
		}
	}
	
	public Rect findRect(int position, TilesLayoutManager.SpanSize spanSize)
	{
		if (position < rectsCache.size())
			return rectsCache.get(position);
		else
			return this.findRectForSpanSize(spanSize);
	}
	
	private Rect findRectForSpanSize(TilesLayoutManager.SpanSize spanSize)
	{
		Rect lane = this.getFirstContainsRect(spanSize);
		if (lane != null)
			return new Rect(lane.left, lane.top, lane.left + spanSize.getWidth(), lane.top + spanSize.getHeight());
		else
			return null;
	}
	
	private Rect getFirstContainsRect(TilesLayoutManager.SpanSize spanSize)
	{
		for (Rect rect : this.freeRects)
		{
			Rect itemRect = new Rect(rect.left, rect.top, rect.left + spanSize.getWidth(), rect.top + spanSize.getHeight());
			if (rect.contains(itemRect))
				return rect;
		}
		
		return null;
	}
	
	public void pushRect(int position, Rect rect)
	{
		this.rectsCache.put(position, rect);
		this.subtract(rect);
	}
	
	protected void subtract(Rect subtractedRect)
	{
		List<Rect> interestingRects = this.getInterestingRects(subtractedRect);
		List<Rect> possibleNewRects = new ArrayList<>();
		List<Rect> adjacentRects = new ArrayList<>();
		
		for (Rect free : interestingRects)
		{
			if (this.isAdjacentTo(free, subtractedRect) && !subtractedRect.contains(free))
			{
				adjacentRects.add(free);
			}
			else
			{
				freeRects.remove(free);
				// Left
				if (free.left < subtractedRect.left)
				{
					possibleNewRects.add(new Rect(free.left, free.top, subtractedRect.left, free.bottom));
				}
				
				// Right
				if (free.right > subtractedRect.right)
				{
					possibleNewRects.add(new Rect(subtractedRect.right, free.top, free.right, free.bottom));
				}
				
				// Top
				if (free.top < subtractedRect.top)
				{
					possibleNewRects.add(new Rect(free.left, free.top, free.right, subtractedRect.top));
				}
				
				// Bottom
				if (free.bottom > subtractedRect.bottom)
				{
					possibleNewRects.add(new Rect(free.left, subtractedRect.bottom, free.right, free.bottom));
				}
			}
		}
		
		for (Rect rect : possibleNewRects)
		{
			boolean isAdjacent = this.isContainsRect(adjacentRects, rect);
			if (isAdjacent)
				continue;
			
			boolean isContained = this.isContainsRect(possibleNewRects, rect);
			if (isContained)
				continue;
			
			freeRects.add(rect);
		}
		
		this.freeRects.sort(rectComparator);
	}
	
	private boolean isAdjacentTo(Rect rect1, Rect rect2)
	{
		return (rect1.right == rect2.left
				|| rect1.top == rect2.bottom
				|| rect1.left == rect2.right
				|| rect1.bottom == rect2.top);
	}
	
	private boolean isContainsRect(List<Rect> rectList, Rect rect)
	{
		for (Rect iterator : rectList)
		{
			if (iterator != rect && iterator.contains(rect))
				return true;
		}
		
		return false;
	}
	
	private List<Rect> getInterestingRects(Rect subtractedRect)
	{
		List<Rect> interestingRects = new ArrayList<>();
		
		for (Rect rect : this.freeRects)
		{
			if (this.isAdjacentTo(rect, subtractedRect) || rect.intersects(subtractedRect.left, subtractedRect.top, subtractedRect.right, subtractedRect.bottom))
				interestingRects.add(rect);
		}
		
		return interestingRects;
	}
}
