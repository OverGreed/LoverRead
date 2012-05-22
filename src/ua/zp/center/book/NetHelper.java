package ua.zp.center.book;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetHelper {

	public static boolean isConnected(Context context){
		ConnectivityManager cm =
		        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if(ni != null && ni.isConnected()){
			return true;
		}
		return false;
	}
	public static String getRemotePage(String urlPath){
		StringBuilder result = new StringBuilder();
		try {
			String line;
			URL url = new URL(urlPath);
			HttpURLConnection connect = (HttpURLConnection)url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					connect.getInputStream(),
					"cp1251"
				),
				8192
			);
			while((line = br.readLine())!=null){
				result.append(line);
			}
			br.close();
		} catch(Exception e){}
		return result.toString();
	}
}
