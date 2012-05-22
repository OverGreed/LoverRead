package ua.zp.center.book;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
	
	public final static String TABLE_CATEGORIES = "categories";
	
	public final static String CATEGORIES_ID = "id";
	public final static String CATEGORIES_TITLE = "title";
	public final static String CATEGORIES_REMOTE_ID = "remote_id";
	public final static String CATEGORIES_PAGES_LOADED = "pages_loaded";
	
	private final static String CREATE_CATEGORIES = "create table " +
			TABLE_CATEGORIES + " ( " +
			CATEGORIES_ID +" integer primary key autoincrement, " +
			CATEGORIES_REMOTE_ID + " integer not null, " +
			CATEGORIES_TITLE + " varchar(64) not null, " +
			CATEGORIES_PAGES_LOADED + " integer not null);";
	
	public final static String TABLE_BOOKS = "books";
	
	public final static String BOOKS_ID = "id";
	public final static String BOOKS_CATEGORY_ID = "category_id";
	public final static String BOOKS_REMOTE_ID = "remote_id";
	public final static String BOOKS_TITLE = "title";
	public final static String BOOKS_AUTOR = "autor";
	public final static String BOOKS_DESCRIPTION = "description";
	public final static String BOOKS_SERIES = "series";
	public final static String BOOKS_PAGES = "pages";
	public final static String BOOKS_SAVED = "saved";
	public final static String BOOKS_READ_PAGE = "read_page";
	public final static String BOOKS_LANGUAGE = "language";
	public final static String BOOKS_ADDED = "added";
	
	private final static String CREATE_BOOKS = "create table " +
			TABLE_BOOKS + "( " + 
			BOOKS_ID + " integer primary key autoincrement, " +
			BOOKS_CATEGORY_ID + " integer, "+
			BOOKS_REMOTE_ID + " integer, " +
			BOOKS_TITLE + " varchar(255) not null, " +
			BOOKS_AUTOR + " varchar(255) not null, " +
			BOOKS_DESCRIPTION + " text not null,"+
			BOOKS_SERIES + " varchar(255), " +
			BOOKS_PAGES + " integer, " +
			BOOKS_READ_PAGE + " integer DEFAULT 1 NOT NULL, " +
			BOOKS_SAVED + " boolean DEFAULT '0' NOT NULL, " +
			BOOKS_LANGUAGE + " varchar(255), "+
			BOOKS_ADDED+" integer)";
	
	public final static String TABLE_SAVES = "saves";
	
	public final static String SAVES_ID = "id";
	public final static String SAVES_BOOK_ID = "book_id";
	public final static String SAVES_BOOK_PAGE = "page";
	public final static String SAVES_BOOK_TEXT = "text";
	
	private final static String CREATE_SAVES = "create table "+
			TABLE_SAVES +"( "+
			SAVES_ID +" integer primary key autoincrement, "+
			SAVES_BOOK_ID+" integer, "+
			SAVES_BOOK_PAGE+" integer, "+
			SAVES_BOOK_TEXT+" text not null);";
	
	private final static String DATABASE_NAME = "books.db";
	private final static int DATABASE_VERSION = 1;
	
	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_CATEGORIES);
		db.execSQL(CREATE_BOOKS);
		db.execSQL(CREATE_SAVES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_CATEGORIES);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_BOOKS);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_SAVES);
		onCreate(db);
	}
}
