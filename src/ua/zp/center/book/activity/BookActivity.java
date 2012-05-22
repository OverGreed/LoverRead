package ua.zp.center.book.activity;

import ua.zp.center.book.ImageGetter;
import ua.zp.center.book.Parser;
import ua.zp.center.book.R;
import ua.zp.center.book.dao.BooksDataSource;
import ua.zp.center.book.dao.SavesDataSource;
import ua.zp.center.book.data.Book;
import ua.zp.center.book.data.Save;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class BookActivity extends Activity {
	public final static String BOOK = "book";
	public final static String SEARCH = "search";
	private TextView title;
	private TextView autor;
	private TextView series;
	private TextView language;
	private TextView description;
	private ImageView image;
	private Book book;
	private Button save;
	private Button read;
	private ProgressDialog pd;
	
	private BooksDataSource booksSource;
	private SavesDataSource savesSource;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book);
        
    	booksSource = new BooksDataSource(this);
    	booksSource.open();
        
    	savesSource = new SavesDataSource(this);
    	savesSource.open();
    	
        title = (TextView)findViewById(R.id.bookTitle);
        autor = (TextView)findViewById(R.id.bookAutor);
        series = (TextView)findViewById(R.id.bookSerias);

        language = (TextView)findViewById(R.id.bookLanguage);
        description = (TextView)findViewById(R.id.bookDescription);
        image = (ImageView)findViewById(R.id.bookImage);
        image.setTag("SHOW");
        
        Intent intent = getIntent();
        boolean isSearch = intent.getBooleanExtra(SEARCH,false);
        if(isSearch){
        	book = (Book)intent.getSerializableExtra(BOOK);
        	if(!booksSource.isExists(book)) {
	        	ParseBook parse = new ParseBook();
	        	parse.execute(book);
        	} else {
        		book = booksSource.getBookByRemoteId(book);
        		if(book.getAdded()!=null){
	        		booksSource.addBook(book);
	        		initBook();
        		}
        	}
        } else {
        	long bookId = intent.getLongExtra(BOOK, 0);
         	book = booksSource.getBookById(bookId);
        	initBook();
        }
    }
    private void disableLock(){
    	KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE);
    	KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
    	lock.disableKeyguard();
    }
    private void initBook(){
    	if(book.getPages()==0){
        	pd = ProgressDialog.show(
        		this,
        		getText(R.string.updating),
        		getText(R.string.updating_book_page),
        		true,
        		false
        	);
    		new ParseCountPages().execute(book);
    	}
        read = (Button)findViewById(R.id.readBook);
        read.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BookActivity.this,ReadActivity.class);
				intent.putExtra(BOOK, book.getId());
				startActivity(intent);
			}
        });
        save = (Button)findViewById(R.id.saveBook);
        if(book.isSaved()){
        	Drawable rm = getResources().getDrawable(R.drawable.remove);
        	save.setCompoundDrawablesWithIntrinsicBounds(rm, null, null, null);
        	save.setText(R.string.book_remove);
        }
        save.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				if(book.isSaved()){
					savesSource.deleteSave(book);
					book.setSaved(false);
					booksSource.updateBook(book);
					
		        	Drawable add = getResources().getDrawable(R.drawable.add);
		        	save.setCompoundDrawablesWithIntrinsicBounds(add, null, null, null);
		        	save.setText(R.string.book_save);
				} else {
	    			pd  = new ProgressDialog(BookActivity.this);
	    			pd.setMessage(getString(R.string.saving_book));
	    			pd.setTitle(getString(R.string.saving_book));
	    			pd.setCancelable(false);
	    			pd.setMax(book.getPages());
	    			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	    			pd.show();
	    			disableLock();
	    			ParsePages parser = new ParsePages();
	    			parser.execute();
				}
			}
        });

        title.setText(book.getTitle());
        autor.setText(book.getAutor());
        series.setText(book.getSeries());
        language.setText(book.getLanguage());
        description.setText(Html.fromHtml(book.getDescription()));
        new ImageGetter(this,image).execute(Long.toString(book.getRemoteId()));
    }
    @Override
    public void onDestroy(){
    	booksSource.close();
    	savesSource.close();
    	super.onDestroy();
    }
    @Override
    public void onResume(){
    	booksSource.open();
    	savesSource.open();
    	super.onResume();
    }
    @Override
    public void onPause(){
    	booksSource.close();
    	savesSource.close();
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

    private class ParseCountPages extends AsyncTask<Book,Void,Book>{

		@Override
		protected Book doInBackground(Book... params) {
			Book book = Parser.getBook(params[0]);
			book.setPages(Parser.getBookPages(book));
			return book;
		}
		@Override
    	protected void onPostExecute(Book book){
			pd.dismiss();
			booksSource.updateBook(book);
			BookActivity.this.book = book;
			initBook();
		}
    }
    private class ParseBook extends AsyncTask<Book,Void,Book>{

		@Override
		protected Book doInBackground(Book... params) {
			Book book = Parser.getBook(params[0]);
			book.setPages(Parser.getBookPages(book));
			return book;
		}
		@Override
    	protected void onPostExecute(Book book){
			book.setCategoryId(0);
			booksSource.addBook(book);
			BookActivity.this.book = book;
			initBook();
		}
    }
    private class ParsePages extends AsyncTask<Void,Void,Boolean>{

		@Override
    	protected void onPostExecute(Boolean success){
    		pd.dismiss();
    		if(success){
    			book.setSaved(true);
    			booksSource.updateBook(book);
            	Drawable rm = getResources().getDrawable(R.drawable.remove);
            	save.setCompoundDrawablesWithIntrinsicBounds(rm, null, null, null);
            	save.setText(R.string.book_remove);
    		} else {
    			
    		}
    	}
		@Override
		protected Boolean doInBackground(Void... params) {
			long id = book.getRemoteId();
			int pages = book.getPages();
			if(pages==0){
				pages = Parser.getBookPages(book);
			}
			for(int i=1;i<=pages;i++){
				if(!savesSource.isExists(book, i)){
					String page = Parser.getBookPage(id, i);
					if(page.length()==0){
						return false;
					}
					
					Save saveText = new Save();
					
					saveText.setBookId(book.getId());
					saveText.setPage(i);
					saveText.setText(page);
					
					savesSource.addSave(saveText);
					pd.setProgress(i);
				}
			}
			return true;
		}
    }
}
