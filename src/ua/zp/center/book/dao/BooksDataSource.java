package ua.zp.center.book.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.zp.center.book.DbHelper;
import ua.zp.center.book.data.Book;
import ua.zp.center.book.data.Category;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BooksDataSource {
	private SQLiteDatabase database;
	private DbHelper helper;
	private String[] columns = {
		DbHelper.BOOKS_ID,
		DbHelper.BOOKS_CATEGORY_ID,
		DbHelper.BOOKS_REMOTE_ID,
		DbHelper.BOOKS_TITLE,
		DbHelper.BOOKS_AUTOR,
		DbHelper.BOOKS_DESCRIPTION,
		DbHelper.BOOKS_SERIES,
		DbHelper.BOOKS_PAGES,
		DbHelper.BOOKS_READ_PAGE,
		DbHelper.BOOKS_SAVED,
		DbHelper.BOOKS_LANGUAGE,
		DbHelper.BOOKS_ADDED
	};
	public BooksDataSource(Context context){
		helper = new DbHelper(context);
	}
	public void open(){
		database = helper.getWritableDatabase();
	}
	public void close(){
		helper.close();
	}
	public List<Book> getSavedBooks(){
		List<Book> list = new ArrayList<Book>();
		Cursor cursor = database.query(
			DbHelper.TABLE_BOOKS,
			columns,
			DbHelper.BOOKS_SAVED+" = 1",
			null,
			null,
			null,
			DbHelper.BOOKS_TITLE
		);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			Book book = toBook(cursor);
			list.add(book);
			cursor.moveToNext();
		}
		return list;
	}
	public List<Book> getCategoryBooks(Category category){
		List<Book> list = new ArrayList<Book>();
		Cursor cursor = database.query(
			DbHelper.TABLE_BOOKS,
			columns,
			DbHelper.BOOKS_CATEGORY_ID + " = " + category.getId(),
			null,
			null,
			null,
			DbHelper.BOOKS_ADDED
		);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			Book book = toBook(cursor);
			list.add(book);
			cursor.moveToNext();
		}
		cursor.close();
		return list;
	}
	public void addBook(Book book){
		ContentValues values = new ContentValues();
		
		values.put(DbHelper.BOOKS_CATEGORY_ID, book.getCategoryId());
		values.put(DbHelper.BOOKS_REMOTE_ID,   book.getRemoteId());
		values.put(DbHelper.BOOKS_TITLE,       book.getTitle());
		values.put(DbHelper.BOOKS_AUTOR,       book.getAutor());
		values.put(DbHelper.BOOKS_DESCRIPTION, book.getDescription());
		values.put(DbHelper.BOOKS_SERIES, 	   book.getSeries());
		values.put(DbHelper.BOOKS_PAGES,       book.getPages());
		values.put(DbHelper.BOOKS_READ_PAGE,   book.getReadPage());
		values.put(DbHelper.BOOKS_SAVED,       book.isSaved());
		values.put(DbHelper.BOOKS_LANGUAGE,    book.getLanguage());
		Date date = book.getAdded();
		if(date!=null){
			values.put(DbHelper.BOOKS_ADDED,   date.getTime());
		} else {
			values.put(DbHelper.BOOKS_ADDED,   0);
		}
		
		long id = database.insert(DbHelper.TABLE_BOOKS, null, values);
		book.setId(id);
	}
	public Book getBookById(long id){
		Cursor cursor = database.query(
				DbHelper.TABLE_BOOKS,
				columns,
				DbHelper.BOOKS_ID + " = " + id,
				null,
				null,
				null,
				DbHelper.CATEGORIES_TITLE
			);
			Book book = null;
			cursor.moveToFirst();
			while(!cursor.isAfterLast()){
				book = toBook(cursor);
				cursor.moveToNext();
			}
			cursor.close();
			return book;
	}
	public Book getBookByRemoteId(Book book){
		Cursor cursor = database.query(
				DbHelper.TABLE_BOOKS,
				columns,
				DbHelper.BOOKS_REMOTE_ID + " = " + book.getRemoteId(),
				null,
				null,
				null,
				DbHelper.CATEGORIES_TITLE
			);
			cursor.moveToFirst();
			while(!cursor.isAfterLast()){
				book = toBook(cursor);
				cursor.moveToNext();
			}
			cursor.close();
			return book;
	}
	public void updateBook(Book book){
		ContentValues values = new ContentValues();
		values.put(DbHelper.BOOKS_CATEGORY_ID, book.getCategoryId());
		values.put(DbHelper.BOOKS_REMOTE_ID,   book.getRemoteId());
		values.put(DbHelper.BOOKS_TITLE,       book.getTitle());
		values.put(DbHelper.BOOKS_AUTOR,       book.getAutor());
		values.put(DbHelper.BOOKS_DESCRIPTION, book.getDescription());
		values.put(DbHelper.BOOKS_SERIES, 	   book.getSeries());
		values.put(DbHelper.BOOKS_PAGES,       book.getPages());
		values.put(DbHelper.BOOKS_READ_PAGE,   book.getReadPage());
		values.put(DbHelper.BOOKS_SAVED,       book.isSaved());
		values.put(DbHelper.BOOKS_LANGUAGE,    book.getLanguage());
		values.put(DbHelper.BOOKS_ADDED,       book.getAdded().getTime());
		
		database.update(DbHelper.TABLE_BOOKS,values, DbHelper.BOOKS_ID + " = " + book.getId(), null);
	}
	public boolean isExists(Book book){
		boolean exists = false;
		Cursor cursor = database.rawQuery("SELECT COUNT(*), id FROM "+DbHelper.TABLE_BOOKS + " WHERE " + 
			DbHelper.BOOKS_REMOTE_ID+ " = " + book.getRemoteId(),
			null
		);
		cursor.moveToFirst();
		if(!cursor.isAfterLast()){
			exists = cursor.getInt(0)==1;
		} else {
			System.out.println("NO CURSOR");
		}
		cursor.close();
		return exists;
	}
	public void deleteBook(Book book){
		long id = book.getId();
		database.delete(DbHelper.TABLE_BOOKS, DbHelper.BOOKS_ID + " = " + id, null);
	}
	public Map<String,String> toMap(Cursor cursor){
		Map<String,String> item = new HashMap<String,String>();
		return item;
	}
	public Book toBook(Cursor cursor){
		Book book = new Book();
		
		book.setId(cursor.getLong(0));
		book.setCategoryId(cursor.getLong(1));
		book.setRemoteId(cursor.getLong(2));
		book.setTitle(cursor.getString(3));
		book.setAutor(cursor.getString(4));
		book.setDescription(cursor.getString(5));
		book.setSeries(cursor.getString(6));
		book.setPages(cursor.getInt(7));
		book.setReadPage(cursor.getInt(8));
		book.setSaved(cursor.getInt(9)==1);
		book.setLanguage(cursor.getString(10));
		book.setAdded(new Date(cursor.getLong(10)));
		
		return book;
	}
}
