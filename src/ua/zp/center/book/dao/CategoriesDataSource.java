package ua.zp.center.book.dao;

import java.util.ArrayList;
import java.util.List;

import ua.zp.center.book.DbHelper;
import ua.zp.center.book.data.Category;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CategoriesDataSource {
	private SQLiteDatabase database;
	private DbHelper helper;
	private String[] columns ={
		DbHelper.CATEGORIES_ID,
		DbHelper.CATEGORIES_REMOTE_ID,
		DbHelper.CATEGORIES_TITLE,
		DbHelper.CATEGORIES_PAGES_LOADED
	};
	public CategoriesDataSource(Context context){
		helper = new DbHelper(context);
	}
	public void open(){
		database = helper.getWritableDatabase();
	}
	public void close(){
		helper.close();
	}
	public Category getCategoryById(long id){
		Cursor cursor = database.query(
			DbHelper.TABLE_CATEGORIES,
			columns,
			DbHelper.CATEGORIES_ID + " = " + id,
			null,
			null,
			null,
			DbHelper.CATEGORIES_TITLE
		);
		Category category = null;
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			category = toCategory(cursor);
			cursor.moveToNext();
		}
		cursor.close();
		return category;
	}
	public List<Category> getAllCategories(){
		List<Category> list = new ArrayList<Category>();
		Cursor cursor = database.query(DbHelper.TABLE_CATEGORIES, columns, null, null, null, null, DbHelper.CATEGORIES_TITLE);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			Category category = toCategory(cursor);
			list.add(category);
			cursor.moveToNext();
		}
		cursor.close();
		return list;
	}
	public void addCategory(Category category){
		ContentValues values = new ContentValues();
		
		values.put(DbHelper.CATEGORIES_REMOTE_ID,   category.getRemoteId());
		values.put(DbHelper.CATEGORIES_TITLE,       category.getTitle());
		values.put(DbHelper.CATEGORIES_PAGES_LOADED, category.getPagesLoaded());
		
		long id = database.insert(DbHelper.TABLE_CATEGORIES, null, values);
		category.setId(id);
		
	}
	public void deleteCategory(Category category){
		long id = category.getId();
		database.delete(DbHelper.TABLE_CATEGORIES, DbHelper.CATEGORIES_ID + " = " + id, null);
	}
	public void updateCategory(Category category){
		long id = category.getId();
		ContentValues values = new ContentValues();	
		values.put(DbHelper.CATEGORIES_REMOTE_ID,   category.getRemoteId());
		values.put(DbHelper.CATEGORIES_TITLE, 		category.getTitle());
		values.put(DbHelper.CATEGORIES_PAGES_LOADED, category.getPagesLoaded());
		
		database.update(DbHelper.TABLE_CATEGORIES, values, DbHelper.CATEGORIES_ID + " = "+id, null);
	}
	private Category toCategory(Cursor cursor){
		Category category = new Category();
		
		category.setId(cursor.getLong(0));
		category.setRemoteId(cursor.getLong(1));
		category.setTitle(cursor.getString(2));
		category.setPagesLoaded(cursor.getInt(3));
		
		return category;
	}
}
