package ua.zp.center.book.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ua.zp.center.book.DbHelper;
import ua.zp.center.book.Parser;
import ua.zp.center.book.R;
import ua.zp.center.book.dao.CategoriesDataSource;
import ua.zp.center.book.data.Category;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class CategoriesActivity extends Activity {
	
	private ListView list;
	private List<Category> categories;
	private CategoriesDataSource categorySource;
	private ProgressDialog pd;
	private RelativeLayout connect;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories);
        categorySource = new CategoriesDataSource(this);
        categorySource.open();
        categories = categorySource.getAllCategories();
        
        list = (ListView)findViewById(R.id.categoriesList);

        
        connect = (RelativeLayout)findViewById(R.id.categoriesNoConnect);
        
        Button button = (Button)connect.findViewById(R.id.categoryReconnect);
        
        button.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				parse();
			}
		});
        
        if(categories.isEmpty()){
        	parse();
        } else {
        	initList();
        }
        
        list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent intent = new Intent(CategoriesActivity.this,BooksActivity.class);
				Category category = categories.get(position);
				intent.putExtra(BooksActivity.CATEGORY, category.getId());
				startActivity(intent);
			}
        });
    }
    private void parse(){
    	pd = ProgressDialog.show(
    		this,
    		getText(R.string.updating),
    		getText(R.string.updating_categories),
    		true,
    		false
    	);
    	CategoryParser parser = new CategoryParser();
    	parser.execute();
    }
    @Override
    public void onDestroy(){
    	categorySource.close();
    	super.onDestroy();
    }
    @Override
    public void onResume(){
    	categorySource.open();
    	super.onResume();
    }
    @Override
    public void onPause(){
    	categorySource.close();
    	super.onPause();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.main_menu, menu);
        MenuItem item = (MenuItem)menu.findItem(R.id.menu_list);
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
    	}
    	return true;
    }
    private void initList(){
    	List<Map<String,String>> items = new ArrayList<Map<String,String>>();
    	Iterator<Category> iterator = categories.iterator();
    	while(iterator.hasNext()){
    		Category category = iterator.next();
    		Map<String,String> item = new HashMap<String,String>();
    		item.put(DbHelper.CATEGORIES_TITLE, category.getTitle());
    		items.add(item);
    	}
    	SimpleAdapter adapter = new SimpleAdapter(
        	CategoriesActivity.this,
        	items,
        	R.layout.category_item,
        	new String[]{DbHelper.CATEGORIES_TITLE},
        	new int[]{R.id.categoryTitle}
        );
        list.setAdapter(adapter);
    }
    private class CategoryParser extends AsyncTask<Void,Void,List<Category>>{
		@Override
		protected List<Category> doInBackground(Void... params) {
			List<Category> list = Parser.getCategories();
			Iterator<Category> iterator = list.iterator();
			while(iterator.hasNext()){
				Category category = iterator.next();
				category.setPagesLoaded(0);
				categorySource.addCategory(category);
			}
			return list;
		}
		protected void onPostExecute(List<Category> records){
			pd.dismiss();
			if(records.size()==0){
				connect.setVisibility(View.VISIBLE);
			} else {
				connect.setVisibility(View.GONE);
				categories = records;
				initList();
			}
		}
    }
}