package ua.zp.center.book.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ua.zp.center.book.BookAdapter;
import ua.zp.center.book.Parser;
import ua.zp.center.book.R;
import ua.zp.center.book.dao.BooksDataSource;
import ua.zp.center.book.dao.CategoriesDataSource;
import ua.zp.center.book.data.Book;
import ua.zp.center.book.data.Category;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class BooksActivity extends Activity {
	
	public final static String CATEGORY = "category";
	
	private Category category;
	private List<Book> books;
	
	private BooksDataSource booksSource;
	private CategoriesDataSource categorySource;
	
	private ListView list;
	private TextView categoryName;
	private BookAdapter adapter;
	
	private boolean loading = false;
	private View footerView;
	private RelativeLayout connect;
	
	private Button recheck;
	
	private Integer pages;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.books);
    	
    	booksSource = new BooksDataSource(this);
    	booksSource.open();
    	
        categorySource = new CategoriesDataSource(this);
        categorySource.open();
    	
    	Intent intent = getIntent();
    	long categoryId =intent.getLongExtra(CATEGORY, 0);
    	category = categorySource.getCategoryById(categoryId);
    	
    	categoryName = (TextView)findViewById(R.id.categoryName);
    	categoryName.setText(category.getTitle());
    	
    	list = (ListView)findViewById(R.id.books);
    	books = booksSource.getCategoryBooks(category);
    	
    	footerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.books_footer, null, false);
    	
    	connect = (RelativeLayout)findViewById(R.id.booksNoConnect);

		recheck = (Button)connect.findViewById(R.id.booksReconnect);
		recheck.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				connect.setVisibility(View.INVISIBLE);
				footerView.setVisibility(View.VISIBLE);
				loading = false;
			}
			
		});
    	
    	list.addFooterView(footerView);
    	
    	initList();
    }
    @Override
    public void onDestroy(){
    	booksSource.close();
    	categorySource.close();
    	super.onDestroy();
    }
    @Override
    public void onResume(){
    	booksSource.open();
    	categorySource.open();
    	super.onResume();
    }
    @Override
    public void onPause(){
    	booksSource.close();
    	categorySource.close();
    	super.onPause();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.main_menu, menu);
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
    private void initList(){
    	adapter = new BookAdapter(BooksActivity.this, books);
    	list.setAdapter(adapter);
      	list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent intent = new Intent(BooksActivity.this,BookActivity.class);
				if(books.size()>position){
					Book book = books.get(position);
					intent.putExtra(BookActivity.BOOK, book.getId());
					startActivity(intent);
				}
			}
    	});
    	list.setOnScrollListener(new OnScrollListener(){
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				int lastInScreen = firstVisibleItem + visibleItemCount;
				if((lastInScreen == totalItemCount) && !(loading)){
					loading = true;
					footerView.setVisibility(View.VISIBLE);
					NextPage parser = new NextPage();
					parser.execute(category.getPagesLoaded(), (int)category.getRemoteId());
				}
			}
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
    	});
    }
    private class NextPage extends AsyncTask<Integer,Integer,List<Book>>{
    	
		@Override
		protected List<Book> doInBackground(Integer... params) {

			int page = params[0];
			int categoryId = params[1];
			if(pages==null || pages == -1){
				pages = Parser.getCategoryPages(category.getRemoteId());
			}
			System.out.println("Pages: "+pages+", page:"+page);
			if(pages==page){
				return Parser.getCategoryBooks(categoryId, page);
			} else if( page+1<=pages) {				
				return Parser.getCategoryBooks(categoryId, page+1);
			} else if(pages != -1 && pages<page+1){
				return new ArrayList<Book>();
			}
			return null;
		}
		protected void onPostExecute(List<Book> list){
			if(list==null){
				connect.setVisibility(View.VISIBLE);
				footerView.setVisibility(View.GONE);
				return;
			}

			Iterator<Book> iterator = list.iterator();
			try{
				while(iterator.hasNext()){
					Book book = iterator.next();
					book.setCategoryId(category.getId());
						if(!booksSource.isExists(book)) {
							booksSource.addBook(book);
							books.add(book);
						} else if(book.getCategoryId()==0){
							book.setCategoryId(category.getId());
							booksSource.updateBook(book);
						}
				}
				if(category.getPagesLoaded()< pages){
				category.setPagesLoaded(category.getPagesLoaded()+1);
				}
				categorySource.updateCategory(category);
				footerView.setVisibility(View.GONE);
				adapter.notifyDataSetChanged();
				loading = false;
			}catch(Exception e){
				System.out.println(e);
			}
		}
    }
}
