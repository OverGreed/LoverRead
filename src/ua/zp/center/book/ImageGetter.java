package ua.zp.center.book;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ImageGetter extends AsyncTask<String,Void,Bitmap>{
	
	private ImageView image;
	private CacheHelper cache;
	private String tag;
	public ImageGetter(Context context, ImageView image){
		this.image = image;
		tag = (String)image.getTag();
		cache = new CacheHelper(context);
	}
	@Override
	protected Bitmap doInBackground(String... params) {
		try{
			return cache.getBitmap(params[0], "http://www.loveread.ec/img/photo_books/"+params[0]+".jpg");
		}catch(Exception e){
			
		}
		return null;
	}
	@Override
	protected void onPostExecute(Bitmap bitmap){
		if(bitmap==null){
			image.setImageResource(R.drawable.book);
		} else {
			try{
				if(image.getTag().equals(tag)){
					image.setImageBitmap(bitmap);
				}
			}catch(Exception e){
				
			}
		}
	}
}

