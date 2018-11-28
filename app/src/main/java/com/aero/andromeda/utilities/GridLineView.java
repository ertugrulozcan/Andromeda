package com.aero.andromeda.utilities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GridLineView extends View
{
	private static final int DEFAULT_PAINT_COLOR = 0xFFEDEDED;
	private static final int DEFAULT_NUMBER_OF_ROWS = 2;
	private static final int DEFAULT_NUMBER_OF_COLUMNS = 3;
	private final Paint paint = new Paint();
	private boolean showGrid = true;
	private int numRows = DEFAULT_NUMBER_OF_ROWS, numColumns = DEFAULT_NUMBER_OF_COLUMNS;
	
	public GridLineView(Context context)
	{
		super(context);
		init();
	}
	
	public GridLineView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}
	
	public GridLineView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}
	
	private void init()
	{
		paint.setColor(DEFAULT_PAINT_COLOR);
	}
	
	public void setLineColor(int color)
	{
		paint.setColor(color);
	}
	
	public void setStrokeWidth(int pixels)
	{
		paint.setStrokeWidth(pixels);
	}
	
	public int getNumberOfRows()
	{
		return numRows;
	}
	
	public void setNumberOfRows(int numRows)
	{
		if (numRows > 0)
		{
			this.numRows = numRows;
		}
		else
		{
			throw new RuntimeException("Cannot have a negative number of rows");
		}
	}
	
	public int getNumberOfColumns()
	{
		return numColumns;
	}
	
	public void setNumberOfColumns(int numColumns)
	{
		if (numColumns > 0)
		{
			this.numColumns = numColumns;
		}
		else
		{
			throw new RuntimeException("Cannot have a negative number of columns");
		}
	}
	
	public boolean isGridShown()
	{
		return showGrid;
	}
	
	public void toggleGrid()
	{
		showGrid = !showGrid;
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		if (showGrid)
		{
			int width = getMeasuredWidth();
			int height = getMeasuredHeight();
			
			// Vertical lines
			for (int i = 1; i < numColumns; i++)
			{
				canvas.drawLine(width * i / numColumns, 0, width * i / numColumns, height, paint);
			}
			
			// Horizontal lines
			for (int i = 1; i < numRows + 1; i++)
			{
				canvas.drawLine(0, (height - 1) * i / numRows, width, (height - 1) * i / numRows, paint);
			}
		}
	}
}