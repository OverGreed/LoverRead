package ua.zp.center.book.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import ua.zp.center.book.DbHelper;
import ua.zp.center.book.data.Book;
import ua.zp.center.book.data.Save;

public class SavesDataSource {
	private SQLiteDatabase database;
	private DbHelper helper;
	private String[] columns = {
		DbHelper.SAVES_ID,
		DbHelper.SAVES_BOOK_ID,
		DbHelper.SAVES_BOOK_PAGE,
		DbHelper.SAVES_BOOK_TEXT
	};
	public SavesDataSource(Context context){
		helper = new DbHelper(context);
	}
	public void open(){
		database = helper.getWritableDatabase();
	}
	public void close(){
		helper.close();
	}
	public void addSave(Save save){
		ContentValues values = new ContentValues();
		values.put(DbHelper.SAVES_BOOK_ID, save.getBookId());
		values.put(DbHelper.SAVES_BOOK_PAGE, save.getPage());
		values.put(DbHelper.SAVES_BOOK_TEXT, save.getText());
		long id = database.insert(DbHelper.TABLE_SAVES, null, values);
		save.setId(id);
	}
	public void updateSave(Save save){
		ContentValues values = new ContentValues();
		values.put(DbHelper.SAVES_BOOK_ID, save.getBookId());
		values.put(DbHelper.SAVES_BOOK_PAGE, save.getPage());
		values.put(DbHelper.SAVES_BOOK_TEXT, save.getText());
		database.update(DbHelper.TABLE_SAVES, values, DbHelper.SAVES_ID+ " = "+save.getId(), null);
	}
	public Save getSave(Book book, int page){
		Save save = null;
		Cursor cursor = database.query(
			DbHelper.TABLE_SAVES,
			columns,
			DbHelper.SAVES_BOOK_ID+" = "+book.getId()+" AND "+DbHelper.SAVES_BOOK_PAGE+" = "+page,
			null,
			null,
			null,
			null
		);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			save = toSave(cursor);
			cursor.moveToNext();
		}
		cursor.close();
		return save;
	}
	public boolean isExists(Book book, int page){
		boolean exists = false;
		Cursor cursor = database.rawQuery("SELECT COUNT(*), id FROM "+DbHelper.TABLE_SAVES + " WHERE " + 
			DbHelper.BOOKS_ID+ " = " + book.getRemoteId() + " AND "+ DbHelper.SAVES_BOOK_PAGE + " = " + page,
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
	public int getBookSavedCount(Book book){
		int total = 0;
		Cursor cursor = database.rawQuery("SELECT COUNT(*), id FROM "+DbHelper.TABLE_SAVES + " WHERE " + 
			DbHelper.BOOKS_ID+ " = " + book.getId(),
			null
		);
		cursor.moveToFirst();
		if(!cursor.isAfterLast()){
			total = cursor.getInt(0);
		} else {
			System.out.println("NO CURSOR");
		}
		cursor.close();
		return total;
	}
	public void deleteSave(Book book){
		long id = book.getId();
		database.delete(
			DbHelper.TABLE_SAVES,
			DbHelper.SAVES_BOOK_ID + " = ?",
			new String[]{
				Long.toString(id)
			}
		);
	}
	public void deleteSave(Book book, int page){
		long id = book.getId();
		database.delete(
			DbHelper.TABLE_SAVES,
			DbHelper.SAVES_BOOK_ID + " = ? AND "+DbHelper.SAVES_BOOK_PAGE+" = ?",
			new String[]{
				Long.toString(id),
				Integer.toString(page)
			}
		);
	}
	public Save toSave(Cursor cursor){
		Save save = new Save();
		
		save.setId(cursor.getInt(0));
		save.setBookId(cursor.getInt(1));
		save.setPage(cursor.getInt(2));
		save.setText(cursor.getString(3));
		
		return save;
	}
}
