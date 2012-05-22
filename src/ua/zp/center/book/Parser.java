package ua.zp.center.book;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ua.zp.center.book.data.Book;
import ua.zp.center.book.data.Category;

public class Parser {
	
	private final static Pattern books = Pattern.compile("\"td_top_color\".*?\"td_top_text\".*?<strong>([^<>]+)</strong>.*?\"span_str\".*?view_global.php\\?id=([0-9]+).*?(?:Серия:.*?<strong>([^<>]+)</strong>.*?)?Автор:.*?<strong>([^<>]+)</strong>.*?Страниц:[^0-9]*?([0-9]+).*?Язык:[^0-9]*?([^<>]+)<.*?\"td_center_color\".*?<p>(.+?)</p>", Pattern.MULTILINE);
	private final static Pattern categoryPages = Pattern.compile("<span class='current'>([0-9]+)<\\/span>", Pattern.MULTILINE);
	private final static Pattern categories = Pattern.compile("index.php\\?id_genre=([0-9]+)\" title=\"([^\"'<>]+)\"");
	private final static Pattern book = Pattern.compile("=(?:\"|')?(em|MsoNormal|strong|take_h1)(?:\"|')?>((?!<img).*?)</(?:p|div)>", Pattern.MULTILINE|Pattern.DOTALL);
	private final static Pattern search = Pattern.compile("view_global.php\\?id=([0-9]+)\"[^<>]*>.*?>([^<>]*)</a>.*?>([^<>]+)</a>", Pattern.MULTILINE|Pattern.DOTALL);
	
	private final static Pattern bookDetails = Pattern.compile("(?:<span>([^<>]+)</span>([^<>]+)<br>|(td_text_10)\">([0-9 :/]+)</p>)", Pattern.MULTILINE|Pattern.DOTALL);
	private final static Pattern bookMain = Pattern.compile("<p class=\"span_str\">(.+)В нашей библиотеке вы", Pattern.MULTILINE|Pattern.DOTALL);
	private final static Pattern bookPages = Pattern.compile(">([0-9]+)</a><a[^<>]+>Вперед", Pattern.MULTILINE|Pattern.DOTALL);
	
	public static List<Book> getCategoryBooks(long id, int page){
		System.out.println("http://loveread.ec/index_book.php?id_genre="+id+"&p="+page);
		String result = NetHelper.getRemotePage("http://loveread.ec/index_book.php?id_genre="+id+"&p="+page);
		if(result.length()>0){
			List<Book> list = new ArrayList<Book>();
			Matcher match = books.matcher(result);
			while(match.find()){
				Book book = new Book();
				String title = match.group(1);
				Integer bookId = Integer.parseInt(match.group(2));
				String series = match.group(3);
				if(series==null){
					series = "";
				}
				String autor = match.group(4);
				//Integer pages = Integer.parseInt(match.group(5));
				String language = match.group(6);
				String description = match.group(7);
				
				book.setTitle(title.trim());
				book.setRemoteId(bookId);
				book.setSeries(series.trim());
				book.setAutor(autor.trim());
				//book.setPages(pages);
				book.setPages(0);
				book.setLanguage(language.trim());
				book.setDescription("<p>"+description.trim()+"</p>");
				book.setReadPage(1);
				
				list.add(book);
			}
			return list;
		}
		return null;
	}
	public static Book getBook(Book book){
		String result = NetHelper.getRemotePage("http://www.loveread.ec/view_global.php?id="+book.getRemoteId());
		if(result.length()>0){
			Matcher match = bookMain.matcher(result);
			while(match.find()){
				book.setDescription(match.group(1).trim());
			}
			book.setCategoryId(0);
			book.setReadPage(1);
			match = bookDetails.matcher(result);
			while(match.find()){
				String key = match.group(1);
				String value = match.group(2);
				if(key==null){
					key = match.group(3);
					value = match.group(4);
				}
				key = key.trim();
				if(key.equals("Страниц:")){
					//book.setPages(Integer.parseInt(value.trim()));
					book.setPages(0);
				} else if(key.equals("Язык:")){
					book.setLanguage(value);
				} else if(key.equals("td_text_10")){
			        SimpleDateFormat formatter = new SimpleDateFormat("dd/mm/yyyy HH:mm");
			        try {
						Date dateStr = formatter.parse(value);
						book.setAdded(dateStr);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return book;
	}
	public static int getBookPages(Book book){
		int pages = 0;
		try{
			String text = NetHelper.getRemotePage("http://www.loveread.ec/read_book.php?id="+book.getId()+"&p=1");
			Matcher match = bookPages.matcher(text);
			while(match.find()){
				pages = Integer.parseInt(match.group(1));
			}
		} catch(Exception e){
			
		}
		return pages;
	}
	public static List<Book> getSearchBooks(String request) {
		List<Book> list = new ArrayList<Book>();
		try{
			String result = NetHelper.getRemotePage("http://www.loveread.ec/search.php?search="+URLEncoder.encode(request, "cp1251"));
			if(result.length()>0){
				Matcher match = search.matcher(result);
				while(match.find()){
					Book book = new Book();
					
					book.setRemoteId(Long.parseLong(match.group(1)));
					book.setTitle(match.group(2).trim());
					book.setAutor(match.group(3).trim());
					
					list.add(book);
				}
			}
		} catch(UnsupportedEncodingException e){}
		return list;
	}
	public static int getCategoryPages(long id){

		Integer pages = null;
		String result = NetHelper.getRemotePage("http://loveread.ec/index.php?id_genre="+id);
		if(result.length()>0){
			Matcher match = categoryPages.matcher(result);
			while(match.find()){
				pages = new Integer(match.group(1));
			}
		}
		if(pages==null){
			pages = -1;
		}
		return pages;
	}
	public static List<Category> getCategories(){
		List<Category> list = new ArrayList<Category>();
		String result = NetHelper.getRemotePage("http://loveread.ec/");
		if(result.length()>0){
			Matcher match = categories.matcher(result);
			while(match.find()){
				Category category = new Category();
				Integer remoteId = new Integer(match.group(1));
				String title = match.group(2);
				
				category.setRemoteId(remoteId);
				category.setTitle(title.trim());
				
				list.add(category);
			}
		}
		return list;
	}
	public static String getBookPage(long id, int page){
		String text = NetHelper.getRemotePage("http://www.loveread.ec/read_book.php?id="+id+"&p="+page);
		StringBuilder result = new StringBuilder();
		Matcher match = book.matcher(text);
		while(match.find()){
			String type = match.group(1);
			String content = match.group(2).trim();
			if(type.equals("take_h1")) {
				result.append("<h2>").append(content).append("</h2>");
			} else if(type.equals("em")) {
				result.append("<em>").append(content).append("</em>");
			} else if(type.equals("strong")){
				result.append("<b>").append(content).append("</b>");
			} else {
				result.append("<p>").append(content).append("</p>");
			}
		}
		return result.toString();
	}
}
