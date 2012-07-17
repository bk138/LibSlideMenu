/**
 * A sliding menu for Android, very much like the Google+ and Facebook apps have.
 * 
 * Based upon the great work done by stackoverflow user Scirocco (http://stackoverflow.com/a/11367825/361413)
 * Some code also taken from https://github.com/darvds/RibbonMenu, thanks!
 */
package com.coboltforge.slidemenu;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.http.util.ExceptionUtils;
import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SlideMenu {
	
	// a simple adapter
	public static class SlideMenuAdapter extends ArrayAdapter<SlideMenu.SlideMenuAdapter.MenuDesc> {
		Activity act;
		SlideMenu.SlideMenuAdapter.MenuDesc[] items;
		class MenuItem {
			public TextView label;
			public ImageView icon;
		}
		static class MenuDesc {
			public int id;
			public int icon;
			public String label;
		}
		public SlideMenuAdapter(Activity act, SlideMenu.SlideMenuAdapter.MenuDesc[] items) {
			super(act, R.id.menu_label, items);
			this.act = act;
			this.items = items;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null) {
				LayoutInflater inflater = act.getLayoutInflater();
				rowView = inflater.inflate(R.layout.slidemenu_listitem, null);
				MenuItem viewHolder = new MenuItem();
				viewHolder.label = (TextView) rowView.findViewById(R.id.menu_label);
				viewHolder.icon = (ImageView) rowView.findViewById(R.id.menu_icon);
				rowView.setTag(viewHolder);
			}

			MenuItem holder = (MenuItem) rowView.getTag();
			String s = items[position].label;
			holder.label.setText(s);
			holder.icon.setImageResource(items[position].icon);

			return rowView;
		}
	}

	private static boolean menuShown = false;
	private int statusHeight;
	private static View menu;
	private static LinearLayout content;
	private static FrameLayout parent;
	private static int menuSize;
	private Activity act;
	private int headerImageRes;
	private TranslateAnimation slideRightAnim;
	private TranslateAnimation slideMenuLeftAnim;
	private TranslateAnimation slideContentLeftAnim;


	
	private ArrayList<SlideMenuAdapter.MenuDesc> menuItems;
	private SlideMenuInterface.OnSlideMenuItemClickListener callback;
	
	
	/**
	 * 
	 * @param act The calling activity.
	 * @param menuResource Menu resource identifier.
	 * @param cb Callback to be invoked on menu item click.
	 * @param slideDuration Slide in/out duration in milliseconds.
	 */
	public SlideMenu(Activity act, int menuResource, SlideMenuInterface.OnSlideMenuItemClickListener cb, int slideDuration) {
		this.act = act;
		this.callback = cb;
	
		// set size
		menuSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, act.getResources().getDisplayMetrics());
		
		// create animations accordingly
		slideRightAnim = new TranslateAnimation(-menuSize, 0, 0, 0);
		slideRightAnim.setDuration(slideDuration);
		slideMenuLeftAnim = new TranslateAnimation(0, -menuSize, 0, 0);
		slideMenuLeftAnim.setDuration(slideDuration);
		slideContentLeftAnim = new TranslateAnimation(menuSize, 0, 0, 0);
		slideContentLeftAnim.setDuration(slideDuration);

		// and get our menu
		parseXml(menuResource);
	}
	
	/**
	 * Sets an image to displayed on top of the menu.
	 * @param imageResource
	 */
	public void setHeaderImage(int imageResource) {
		headerImageRes = imageResource;
	}
	
	
	
	//call this in your onCreate() for screen rotation
	public void checkEnabled() {
		//TODO crashes on restart, should be done internally anyway
//		if(menuShown)
//			this.show(false);
	}
	
	/**
	 * Slide the menu in.
	 */
	public void show() {
		/*
		 *  only have to adopt to status height if there is no action bar,
		 *  neither native nor from support library!
		 */
		try {
			Method getActionBar = act.getClass().getMethod("getActionBar", (Class[])null);
			Object ab = getActionBar.invoke(act, (Object[])null);
			ab.toString(); // check for null
		} catch (Exception e) {
			try {
			// there is no native actionbar, try the support one
			Method getActionBar = act.getClass().getMethod("getSupportActionBar", (Class[])null);
			Object sab = getActionBar.invoke(act, (Object[])null);
			sab.toString(); // check for null
			}
			catch(Exception es) {
				// there also is no support action bar!
				Rect r = new Rect();
				Window window = act.getWindow();
				window.getDecorView().getWindowVisibleDisplayFrame(r);
				statusHeight = r.top;
			}
		}

		/*
		 * phew, finally!
		 */
		this.show(true);
	}
	
	
	
	private void show(boolean animate) {
		
		// modify content layout params
		content = ((LinearLayout) act.findViewById(android.R.id.content).getParent());
		FrameLayout.LayoutParams parm = new FrameLayout.LayoutParams(-1, -1, 3);
		parm.setMargins(menuSize, 0, -menuSize, 0);
		content.setLayoutParams(parm);
		
		// animation for smooth slide-out
		if(animate)
			content.startAnimation(slideRightAnim);
		
		// add the slide menu to parent
		parent = (FrameLayout) content.getParent();
		LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		menu = inflater.inflate(R.layout.slidemenu, null);
		FrameLayout.LayoutParams lays = new FrameLayout.LayoutParams(-1, -1, 3);
		lays.setMargins(0, statusHeight, 0, 0);
		menu.setLayoutParams(lays);
		parent.addView(menu);
		
		// set header
		try {
			ImageView header = (ImageView) act.findViewById(R.id.menu_header); 
			header.setImageDrawable(act.getResources().getDrawable(headerImageRes));
		}
		catch(Exception e) {
			// not found
		}
		
		// connect the menu's listview
		ListView list = (ListView) act.findViewById(R.id.menu_listview);
		SlideMenuAdapter.MenuDesc[] items = menuItems.toArray(new SlideMenuAdapter.MenuDesc[menuItems.size()]);
		SlideMenuAdapter adap = new SlideMenuAdapter(act, items);
		list.setAdapter(adap);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if(callback != null)					
					callback.onSlideMenuItemClick(menuItems.get(position).id);
				
				hide();
			}
		});
		
		// slide menu in
		if(animate)
			menu.startAnimation(slideRightAnim);
		
		
		menu.findViewById(R.id.overlay).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SlideMenu.this.hide();
			}
		});
		enableDisableViewGroup((LinearLayout) parent.findViewById(android.R.id.content).getParent(), false);

		menuShown = true;
	}
	
	
	
	/**
	 * Slide the menu out.
	 */
	public void hide() {
		menu.startAnimation(slideMenuLeftAnim);
		parent.removeView(menu);

		content.startAnimation(slideContentLeftAnim);

		FrameLayout.LayoutParams parm = (FrameLayout.LayoutParams) content.getLayoutParams();
		parm.setMargins(0, 0, 0, 0);
		content.setLayoutParams(parm);
		enableDisableViewGroup((LinearLayout) parent.findViewById(android.R.id.content).getParent(), true);

		menuShown = false;
	}

	//originally: http://stackoverflow.com/questions/5418510/disable-the-touch-events-for-all-the-views
	//modified for the needs here
	private void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
		int childCount = viewGroup.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View view = viewGroup.getChildAt(i);
			if(view.isFocusable())
				view.setEnabled(enabled);
			if (view instanceof ViewGroup) {
				enableDisableViewGroup((ViewGroup) view, enabled);
			} else if (view instanceof ListView) {
				if(view.isFocusable())
					view.setEnabled(enabled);
				ListView listView = (ListView) view;
				int listChildCount = listView.getChildCount();
				for (int j = 0; j < listChildCount; j++) {
					if(view.isFocusable())
						listView.getChildAt(j).setEnabled(false);
				}
			}
		}
	}
	
	// originally: https://github.com/darvds/RibbonMenu
	// credit where credits due!
	private void parseXml(int menu){
		
		menuItems = new ArrayList<SlideMenuAdapter.MenuDesc>();
		
		
		try{
			XmlResourceParser xpp = act.getResources().getXml(menu);
			
			xpp.next();
			int eventType = xpp.getEventType();
			
			
			while(eventType != XmlPullParser.END_DOCUMENT){
				
				if(eventType == XmlPullParser.START_TAG){
					
					String elemName = xpp.getName();
					
					if(elemName.equals("item")){
											
						
						String textId = xpp.getAttributeValue("http://schemas.android.com/apk/res/android", "title");
						String iconId = xpp.getAttributeValue("http://schemas.android.com/apk/res/android", "icon");
						String resId = xpp.getAttributeValue("http://schemas.android.com/apk/res/android", "id");
						
						SlideMenuAdapter.MenuDesc item = new SlideMenuAdapter.MenuDesc();
						item.id = Integer.valueOf(resId.replace("@", ""));
						item.icon = Integer.valueOf(iconId.replace("@", ""));
						item.label = resourceIdToString(textId);
						
						menuItems.add(item);
					}
					
				}
				
				eventType = xpp.next();
				
			}
			
			
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	
	private String resourceIdToString(String text){
		
		if(!text.contains("@")){
			return text;
		} else {
									
			String id = text.replace("@", "");
									
			return act.getResources().getString(Integer.valueOf(id));
			
		}
		
	}
	
}