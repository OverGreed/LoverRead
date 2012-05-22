package ua.zp.center.book.activity;

import ua.zp.center.book.Parser;
import ua.zp.center.book.R;
import ua.zp.center.book.dao.BooksDataSource;
import ua.zp.center.book.dao.SavesDataSource;
import ua.zp.center.book.data.Book;
import ua.zp.center.book.data.Save;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class ReadActivity extends Activity {
	public final static String BOOK = "book";
	private Book book;
	private BooksDataSource booksSource;
	private SavesDataSource savesSource;
	private TextView page;
	private TextView bookPage;
	private Button back;
	private Button next;
	private int currentPage;
	private ProgressDialog pd;
	private ScrollView scroll;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read);
 
    	booksSource = new BooksDataSource(this);
    	booksSource.open();
        
        savesSource = new SavesDataSource(this);
        savesSource.open();
        
        Intent intent = getIntent();
        long bookId = intent.getLongExtra(BOOK, 0);
        book = booksSource.getBookById(bookId);
        
        currentPage = book.getReadPage();
        
        page = (TextView)findViewById(R.id.bookPageText);
        bookPage = (TextView)findViewById(R.id.readBookPages);
        
        scroll = (ScrollView)findViewById(R.id.bookScroll);
        
        back = (Button)findViewById(R.id.readBack);
        back.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				if(currentPage>1){
					currentPage--;
					setPage(currentPage);
				}
			}
        	
        });
        next = (Button)findViewById(R.id.readNext);
        next.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				if(book.getPages()>currentPage){
					currentPage++;
					setPage(currentPage);
				}
			}
        	
        });
        setPage(currentPage);
    }
    private void updateBookPage(int page){
    	book.setReadPage(page);
    	booksSource.updateBook(book);
    }
    private void setPage(int bookPage){
        Save save = savesSource.getSave(book, bookPage);
        if(save==null){
        	pd = ProgressDialog.show(
        		this,
        		getText(R.string.updating),
        		getText(R.string.updating_book_page),
        		true,
        		false
        	);
        	new ParsePage().execute(bookPage);
        } else {

        	page.setText(Html.fromHtml(save.getText()));
        	this.bookPage.setText(bookPage+" / "+book.getPages());
			updateBookPage(currentPage);
        }
        scroll.fullScroll(ScrollView.FOCUS_UP);
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
    private class ParsePage extends AsyncTask<Integer,Void,Save>{

		@Override
		protected Save doInBackground(Integer... params) {
			long id = book.getRemoteId();
			String page = Parser.getBookPage(id, params[0]);
			if(page.length()==0){
				return null;
			}
			
			Save saveText = new Save();
			
			saveText.setBookId(book.getId());
			saveText.setPage(params[0]);
			saveText.setText(page);
			
			savesSource.addSave(saveText);
			return saveText;
		}
		@Override
    	protected void onPostExecute(Save save){
			pd.dismiss();
			page.setText(Html.fromHtml(save.getText()));
			bookPage.setText(currentPage+" / "+book.getPages());
			updateBookPage(currentPage);
		}
    }
}
