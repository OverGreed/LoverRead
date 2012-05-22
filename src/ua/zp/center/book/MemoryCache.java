package ua.zp.center.book;

import java.util.HashMap;
import java.util.Map;

public class MemoryCache {
	private static MemoryCache instance;
	
	private Map<String,Object> list;
	
	private MemoryCache(){
		list = new HashMap<String,Object>();
	}
	public synchronized void add(String name, Object value){
		list.put(name, value);
	}
	public synchronized Object get(String name){
		return list.get(name);
	}
	public synchronized Object get(Long name){
		return list.get(name.toString());
	}
	public static synchronized MemoryCache getInstance(){
		if(instance==null){
			instance = new MemoryCache();
		}
		return instance;
	}
	public synchronized void clear(){
		list.clear();
	}
}
