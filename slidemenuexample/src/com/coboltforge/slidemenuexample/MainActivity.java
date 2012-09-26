
package com.coboltforge.slidemenuexample;

import com.coboltforge.slidemenu.SlideMenu;
import com.coboltforge.slidemenu.SlideMenu.SlideMenuItem;
import com.coboltforge.slidemenu.SlideMenuInterface.OnSlideMenuItemClickListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnSlideMenuItemClickListener {

	private SlideMenu slidemenu;
	private final static int MYITEMID = 42;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		/*
		 * There are two ways to add the slide menu: 
		 * From code or to inflate it from XML (then you have to declare it in the activities layout XML)
		 */
		// this is from code. no XML declaration necessary, but you won't get state restored after rotation.
//		slidemenu = new SlideMenu(this, R.menu.slide, this, 333);
		// this inflates the menu from XML. open/closed state will be restored after rotation, but you'll have to call init.
		slidemenu = (SlideMenu) findViewById(R.id.slideMenu);
		slidemenu.init(this, R.menu.slide, this, 333);
		
		// this can set the menu to initially shown instead of hidden
//		slidemenu.setAsShown(); 
		
		// set optional header image
		slidemenu.setHeaderImage(R.drawable.ic_launcher);
		
		// this demonstrates how to dynamically add menu items
		SlideMenuItem item = new SlideMenuItem();
		item.id = MYITEMID;
		item.icon = getResources().getDrawable(R.drawable.ic_launcher);
		item.label = "Dynamically added item";
		slidemenu.addMenuItem(item);
		
		// connect the fallback button in case there is no ActionBar
		Button b = (Button) findViewById(R.id.buttonMenu);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				slidemenu.show();
			}
		});
		
	}


	@Override
	public void onSlideMenuItemClick(int itemId) {

		switch(itemId) {
		case R.id.item_one:
			Toast.makeText(this, "Item one selected", Toast.LENGTH_SHORT).show();
			break;
		case R.id.item_two:
			Toast.makeText(this, "Item two selected", Toast.LENGTH_SHORT).show();
			break;
		case R.id.item_three:
			Toast.makeText(this, "Item three selected", Toast.LENGTH_SHORT).show();
			break;
		case R.id.item_four:
			Toast.makeText(this, "Item four selected", Toast.LENGTH_SHORT).show();
			break;
		case MYITEMID:
			Toast.makeText(this, "Dynamically added item selected", Toast.LENGTH_SHORT).show();
			break;
		}
		
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home: // this is the app icon of the actionbar
			slidemenu.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
}