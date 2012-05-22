package ua.zp.center.book.activity;

import java.util.List;

import ua.zp.center.book.BookAdapter;
import ua.zp.center.book.Parser;
import ua.zp.center.book.R;
import ua.zp.center.book.data.Book;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchActivity extends Activity {
	
	private ImageButton searchBtn;
	private TextView searchText;
	private ProgressDialog pd;
	private ListView searchList;
	
	public void onCreate(Bundle savedInstanceState){
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.search);
    	
    	searchText = (TextView)findViewById(R.id.searchText);
    	
    	searchBtn = (ImageButton)findViewById(R.id.searchBtn);
    	searchBtn.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				String text = searchText.getText().toString();
				
	        	pd = ProgressDialog.show(
            		SearchActivity.this,
            		getText(R.string.searching),
            		getText(R.string.search_result),
            		true,
            		false
            	);
				
				Search search = new Search();
				search.execute(text);
			}
    	});
    	searchList = (ListView)findViewById(R.id.searchList);
      	searchList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent intent = new Intent(SearchActivity.this,BookActivity.class);
				if(searchList.getAdapter().getCount()>position){
					Book book = (Book)searchList.getAdapter().getItem((int)position);
					intent.putExtra(BookActivity.BOOK, book);
					intent.putExtra(BookActivity.SEARCH, true);
					startActivity(intent);
				}
			}
    	});
      	searchList.setSmoothScrollbarEnabled(true);
      	searchList.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_INSET);
	}
	private void updateList(List<Book> list){
		searchList.setAdapter(new BookAdapter(SearchActivity.this, list));
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.main_menu, menu);
        MenuItem item = (MenuItem)menu.findItem(R.id.menu_search);
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
    private class Search extends AsyncTask<String,Void,List<Book>>{

		@Override
		protected List<Book> doInBackground(String... params) {
			String text = params[0];
			List<Book> books = Parser.getSearchBooks(text);
			return books;
		}
		
		@Override
    	protected void onPostExecute(List<Book> list){
			pd.dismiss();
			updateList(list);
		}
    }
}
