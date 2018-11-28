package com.aero.andromeda.popup;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.aero.andromeda.popup.AbstractMenuBuilder;
import com.skydoves.powermenu.IMenuItem;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.MenuBaseAdapter;
import com.skydoves.powermenu.OnDismissedListener;
import com.skydoves.powermenu.OnMenuItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class ErtPowerMenu<T, E extends MenuBaseAdapter<T>> extends PowerMenuBase<T, E> implements IMenuItem<T>
{
	protected ErtPowerMenu(Context context, AbstractMenuBuilder abstractMenuBuilder)
	{
		super(context, abstractMenuBuilder);
		
		ErtPowerMenu.Builder<T, E> builder = (ErtPowerMenu.Builder) abstractMenuBuilder;
		
		if(builder.menuItemClickListener != null)
			setOnMenuItemClickListener(builder.menuItemClickListener);
		if(builder.selected != -1)
			setSelectedPosition(builder.selected);
		
		this.adapter = builder.adapter;
		this.adapter.setListView(getMenuListView());
		this.menuListView.setAdapter(adapter);
		addItemList(builder.Ts);
	}
	
	@Override
	protected void initialize(Context context) {
		super.initialize(context);
		this.adapter = (E)(new MenuBaseAdapter<>(menuListView));
	}
	
	@Override
	public void setListView(ListView listView) {
		getAdapter().setListView(getMenuListView());
	}
	
	@Override
	public ListView getListView() {
		return getAdapter().getListView();
	}
	
	@Override
	public void setSelectedPosition(int position) {
		getAdapter().setSelectedPosition(position);
	}
	
	@Override
	public int getSelectedPosition() {
		return getAdapter().getSelectedPosition();
	}
	
	@Override
	public void addItem(Object item) {
		getAdapter().addItem((T)item);
	}
	
	@Override
	public void addItem(int position, T item) {
		getAdapter().addItem(position, item);
	}
	
	@Override
	public void addItemList(List<T> itemList) {
		getAdapter().addItemList(itemList);
	}
	
	@Override
	public void removeItem(T item) {
		getAdapter().removeItem(item);
	}
	
	@Override
	public void removeItem(int position) {
		getAdapter().removeItem(position);
	}
	
	@Override
	public void clearItems() {
		getAdapter().clearItems();
	}
	
	@Override
	public List<T> getItemList() {
		return getAdapter().getItemList();
	}
	
	@OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
	public void onDestroy() {
		dismiss();
	}
	
	@SuppressWarnings("unchecked")
	public static class Builder<T, E extends MenuBaseAdapter<T>> extends AbstractMenuBuilder
	{
		
		private OnMenuItemClickListener<T> menuItemClickListener = null;
		
		private E adapter;
		private List<T> Ts;
		
		public Builder(Context context, E adapter)
		{
			this.context = context;
			this.Ts = new ArrayList<>();
			this.adapter = adapter;
			this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public ErtPowerMenu.Builder setLifecycleOwner(LifecycleOwner lifecycleOwner)
		{
			this.lifecycleOwner = lifecycleOwner;
			return this;
		}
		
		public ErtPowerMenu.Builder setShowBackground(boolean show)
		{
			this.showBackground = show;
			return this;
		}
		
		public ErtPowerMenu.Builder setOnMenuItemClickListener(Object menuItemClickListener)
		{
			this.menuItemClickListener = (OnMenuItemClickListener<T>) menuItemClickListener;
			return this;
		}
		
		public ErtPowerMenu.Builder setOnBackgroundClickListener(View.OnClickListener onBackgroundClickListener)
		{
			this.backgroundClickListener = onBackgroundClickListener;
			return this;
		}
		
		public ErtPowerMenu.Builder setOnDismissListener(OnDismissedListener onDismissListener)
		{
			this.onDismissedListener = onDismissListener;
			return this;
		}
		
		public ErtPowerMenu.Builder setHeaderView(int headerView)
		{
			this.headerView = layoutInflater.inflate(headerView, null);
			return this;
		}
		
		public ErtPowerMenu.Builder setHeaderView(View headerView)
		{
			this.headerView = headerView;
			return this;
		}
		
		public ErtPowerMenu.Builder setFooterView(int footerView)
		{
			this.footerView = layoutInflater.inflate(footerView, null);
			return this;
		}
		
		public ErtPowerMenu.Builder setFooterView(View footerView)
		{
			this.footerView = footerView;
			return this;
		}
		
		public ErtPowerMenu.Builder setAnimation(MenuAnimation menuAnimation)
		{
			this.menuAnimation = menuAnimation;
			return this;
		}
		
		public ErtPowerMenu.Builder setAnimationStyle(int style)
		{
			this.animationStyle = style;
			return this;
		}
		
		public ErtPowerMenu.Builder setMenuRadius(float radius)
		{
			this.menuRadius = radius;
			return this;
		}
		
		public ErtPowerMenu.Builder setMenuShadow(float shadow)
		{
			this.menuShadow = shadow;
			return this;
		}
		
		public ErtPowerMenu.Builder setWidth(int width)
		{
			this.width = width;
			return this;
		}
		
		public ErtPowerMenu.Builder setHeight(int height)
		{
			this.height = height;
			return this;
		}
		
		public ErtPowerMenu.Builder setDividerHeight(int height)
		{
			this.dividerHeight = height;
			return this;
		}
		
		public ErtPowerMenu.Builder setDivider(Drawable divider)
		{
			this.divider = divider;
			return this;
		}
		
		public ErtPowerMenu.Builder setBackgroundColor(int color)
		{
			this.backgroundColor = color;
			return this;
		}
		
		public ErtPowerMenu.Builder setBackgroundAlpha(float alpha)
		{
			this.backgroundAlpha = alpha;
			return this;
		}
		
		public ErtPowerMenu.Builder setFocusable(boolean focusable)
		{
			this.focusable = focusable;
			return this;
		}
		
		public ErtPowerMenu.Builder setSelected(int position)
		{
			this.selected = position;
			return this;
		}
		
		public ErtPowerMenu.Builder setSelectedEffect(boolean effect) {
			this.selectedEffect = effect;
			return this;
		}
		
		public ErtPowerMenu.Builder setTextColor(int color) {
			this.textColor = color;
			return this;
		}
		
		public ErtPowerMenu.Builder setMenuColor(int color) {
			this.menuColor = color;
			return this;
		}
		
		public ErtPowerMenu.Builder setSelectedTextColor(int color) {
			this.selectedTextColor = color;
			return this;
		}
		
		public ErtPowerMenu.Builder setSelectedMenuColor(int color) {
			this.selectedMenuColor = color;
			return this;
		}
		
		public ErtPowerMenu.Builder setIsClipping(boolean isClipping)
		{
			this.isClipping = isClipping;
			return this;
		}
		
		public ErtPowerMenu.Builder addItem(Object item)
		{
			this.Ts.add((T) item);
			return this;
		}
		
		public ErtPowerMenu.Builder addItem(int position, Object item)
		{
			this.Ts.add(position, (T) item);
			return this;
		}
		
		public ErtPowerMenu.Builder addItemList(List<T> itemList)
		{
			this.Ts.addAll(itemList);
			return this;
		}
		
		public ErtPowerMenu build()
		{
			return new ErtPowerMenu<>(context, this);
		}
	}
}
