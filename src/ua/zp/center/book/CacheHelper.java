package ua.zp.center.book;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CacheHelper {
	private File cacheDir;
	public CacheHelper(Context context){
        //if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
            //cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"LoveRead");
        //} else {
            cacheDir=context.getCacheDir();
        //}
        if(!cacheDir.exists()){
            cacheDir.mkdirs();
        }
	}
	public Bitmap getFileBitmap(String fileName){
		Bitmap image = null;
		String name = String.valueOf(fileName.hashCode());
		File file = new File(cacheDir, name);
		if(file.exists()){
			InputStream is;
			try {
				is = (InputStream) new FileInputStream(file);
				image = BitmapFactory.decodeStream(is);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return image;
	}
	public Bitmap getBitmap(String fileName, String url){
		Bitmap image = getFileBitmap(fileName);
		if(image==null){
			String name = String.valueOf(fileName.hashCode());
			File file = new File(cacheDir, name);
	    	try {
	    		
	    		URL uri = new URL(url);
	            HttpURLConnection conn= (HttpURLConnection)uri.openConnection();
	            conn.setDoInput(true);
	            conn.connect();
	            InputStream is = conn.getInputStream();
	            OutputStream os = new FileOutputStream(file);
	            CopyStream(is,os);
	            image = BitmapFactory.decodeStream(new FileInputStream(file));
	    	} catch (Exception e){
    		
	    	}
		}
		return image;
	}
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
	public void putBitmapFile(String url,InputStream is){
		String name = String.valueOf(url.hashCode());
		File file = new File(cacheDir, name);
		OutputStream os;
		try {
			os = new FileOutputStream(file);
			CopyStream(is, os);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void clear(){
		File[] list = cacheDir.listFiles();
		if(list==null){
			return;
		}
		for(File file:list){
			file.delete();
		}
	}
}
