package ua.zp.center.book;

import java.util.List;
//import java.util.Map;

import ua.zp.center.book.data.Book;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BookAdapter extends BaseAdapter{
	
	protected List<Book> items;
	protected Context context;
	protected CacheHelper cache;
	
	public BookAdapter(Context context, List<Book> items){
		cache = new CacheHelper(context);
		this.items = items;
		this.context = context;
	}
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		Book item = items.get(position);
		if(view==null){
			LayoutInflater layout = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layout.inflate(R.layout.book_item, null);
		}
		TextView title = (TextView)view.findViewById(R.id.bookListTitle);
		title.setText(item.getTitle());
		
		TextView autor = (TextView)view.findViewById(R.id.bookListAutor);
		autor.setText(item.getAutor());
		
		ImageView image = (ImageView)view.findViewById(R.id.bookListImage);
		image.setTag(Long.toString(item.getRemoteId()));
		Bitmap bitmap = cache.getFileBitmap(Long.toString(item.getRemoteId()));
		if(bitmap==null){
			ImageGetter img = new ImageGetter(context, image);
			img.execute(Long.toString(item.getRemoteId()));
			image.setImageResource(R.drawable.book);
		} else {
			image.setImageBitmap(bitmap);
		}
		return view;
	}
}
