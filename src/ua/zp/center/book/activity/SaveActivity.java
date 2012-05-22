package ua.zp.center.book.activity;

import ua.zp.center.book.CacheHelper;
import ua.zp.center.book.DbHelper;
import ua.zp.center.book.MemoryCache;
import ua.zp.center.book.R;
import ua.zp.center.book.dao.BooksDataSource;
import ua.zp.center.book.data.Book;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SaveActivity extends Activity {
	private ListView list;
	private List<Book> books;
	
	private BooksDataSource booksSource;
	private CacheHelper cache;
	
	public void onCreate(Bundle savedInstanceState){
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.saved);
    	list = (ListView)findViewById(R.id.savedBooks);
    	
    	cache = new CacheHelper(this);
    	
    	booksSource = new BooksDataSource(this);
    	booksSource.open();
    	books = booksSource.getSavedBooks();

    	if(books.size()>0){
    		initList();
    	}
	}
	private void initList(){
		final List<Map<String,String>> booksList = new ArrayList<Map<String,String>>();
    	Iterator<Book> iterator = books.iterator();
    	while(iterator.hasNext()){
    		Book book = iterator.next();
    		Map<String,String> item = new HashMap<String,String>();
    		item.put(DbHelper.BOOKS_REMOTE_ID, Integer.toString((int)book.getRemoteId()));
    		item.put(DbHelper.BOOKS_TITLE, book.getTitle());
    		item.put(DbHelper.BOOKS_AUTOR, book.getAutor());
    		booksList.add(item);
    	}
    	SimpleAdapter adapter = new SimpleAdapter(
        		this,
        		booksList,
        		R.layout.book_item,
        		new String[] {DbHelper.BOOKS_TITLE, DbHelper.BOOKS_AUTOR},
        		new int[] {R.id.bookListTitle, R.id.bookListAutor}
    	){
    		private Map<Integer,View> views= new HashMap<Integer,View>();
    		@Override
    		public View getView(int position, View view, ViewGroup parent){
    			View itemView;
    			if((itemView = views.get(position))!=null){
    				return itemView;
    			}
				LayoutInflater layout = (LayoutInflater)SaveActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = layout.inflate(R.layout.book_item, null);
    	
    			Map<String,String> data = booksList.get(position);
    			TextView title = (TextView)view.findViewById(R.id.bookListTitle);
    			TextView autor = (TextView)view.findViewById(R.id.bookListAutor);
    			ImageView image = (ImageView)view.findViewById(R.id.bookListImage);
    			
    			Bitmap bitmap = (Bitmap)MemoryCache.getInstance().get(data.get(DbHelper.BOOKS_REMOTE_ID));
    			if(bitmap==null){
	    			ImageGetter img = new ImageGetter();
	    			img.setImageView(image);
	    			img.execute(data.get(DbHelper.BOOKS_REMOTE_ID));
    			} else {
    				image.setImageBitmap(bitmap);
    			}
    			title.setText(data.get(DbHelper.BOOKS_TITLE));
    			autor.setText(data.get(DbHelper.BOOKS_AUTOR));
    			views.put(position, view);
    			return view;
    		}
    	};
    	System.out.println("Adapter:"+adapter);
    	list.setAdapter(adapter);
      	list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent intent = new Intent(SaveActivity.this,BookActivity.class);
				if(books.size()>position){
					Book book = books.get(position);
					intent.putExtra(BookActivity.BOOK, book.getId());
					startActivity(intent);
				}
			}
    	});    	
	}
    @Override
    public void onDestroy(){
    	booksSource.close();
    	super.onDestroy();
    }
    @Override
    public void onResume(){
    	booksSource.open();
    	super.onResume();
    }
    @Override
    public void onPause(){
    	booksSource.close();
    	super.onPause();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.main_menu, menu);
        MenuItem item = (MenuItem)menu.findItem(R.id.menu_saved);
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
    private class ImageGetter extends AsyncTask<String,Void,Bitmap>{
    	private ImageView image;
    	private void setImageView(ImageView image){
    		this.image = image;
    	}
		@Override
		protected Bitmap doInBackground(String... params) {
			return cache.getBitmap(params[0], "http://www.loveread.ec/img/photo_books/"+params[0]+".jpg");
		}
		@Override
    	protected void onPostExecute(Bitmap bitmap){
    		image.setImageBitmap(bitmap);
    	}
    }
}
