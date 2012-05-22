package ua.zp.center.book.activity;

import ua.zp.center.book.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class AboutActivity extends Activity{
	
	public void onCreate(Bundle savedInstanceState){
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.about);
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.main_menu, menu);
        MenuItem item = (MenuItem)menu.findItem(R.id.menu_about);
        item.setVisible(false);
    	return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	Intent intent;
    	switch(item.getItemId()){
    		case R.id.menu_search:
    			intent = new Intent(this,SearchActivity.class);
    			startActivity(intent);
    		break;
    		case R.id.menu_saved:
    			intent = new Intent(this,SaveActivity.class);
    			startActivity(intent);
    		break;
    		case R.id.menu_about:
    			intent = new Intent(this,AboutActivity.class);
    			startActivity(intent);
    		break;
    		case R.id.menu_list:
    			intent = new Intent(this,CategoriesActivity.class);
    			startActivity(intent);
    		break;
    	}
    	return true;
    }
}
