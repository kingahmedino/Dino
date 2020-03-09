package com.example.dino;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class ApiUtil {

    private ApiUtil(){}
    public static final String QUERY_PARAMETER_KEY = "q";
    public static final String BASE_API_URL =
            "https://www.googleapis.com/books/v1/volumes";
    public static final String KEY = "key";
    public static final String API_KEY = "AIzaSyC-dDv6Mdu6E1i5vvWwWPFEUMYUvvMZYVk";

    public static URL buildUrl(String title){
        URL url = null;
        Uri uri = Uri.parse(BASE_API_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAMETER_KEY, title)
                .appendQueryParameter(KEY, API_KEY)
                .build();
        try{
            url = new URL(uri.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getJSON(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            InputStream stream = connection.getInputStream();
            Scanner scanner = new Scanner(stream);
            scanner.useDelimiter("\\A");
            boolean hasData = scanner.hasNext();
            if (hasData) {
                return scanner.next();
            } else {
                return null;
            }
        }
        catch (Exception e) {
            Log.d("Error", e.toString());
            return null;
        }
        finally {
            connection.disconnect();
        }
    }

    public static ArrayList<Book> getBookFromJSON(String json){
        final String ID = "id";
        final String TITLE = "title";
        final String SUBTITLE = "subTitle";
        final String AUTHORS = "authors";
        final String PUBLISHER = "publisher";
        final String PUBLISHED_DATE = "publishedDate";
        final String ITEMS = "items";
        final String VOLUME_INFO = "volumeInfo";
        final String DESCRIPTION = "description";
        final String IMAGELINKS = "imageLinks";
        final String THUMBNAIL = "thumbnail";

        ArrayList<Book> books = new ArrayList<>();
        try{
            JSONObject jsonBooks = new JSONObject(json);
            JSONArray arrayBooks = jsonBooks.getJSONArray(ITEMS);
            int noOfBooks = arrayBooks.length();
            for (int i = 0; i<noOfBooks; i++){
                JSONObject bookJSON = arrayBooks.getJSONObject(i);
                JSONObject volumeInfoJSON = bookJSON.getJSONObject(VOLUME_INFO);

                int authorNum;
                try{
                    authorNum = volumeInfoJSON.getJSONArray(AUTHORS).length();
                }
                catch (Exception e){
                    authorNum = 0;
                }

                JSONObject imageLinksJSON = null;
                if (volumeInfoJSON.has(IMAGELINKS)){
                    imageLinksJSON = volumeInfoJSON.getJSONObject(IMAGELINKS);
                }

                String[] authors = new String[authorNum];
                    for(int j = 0; j<authors.length; j++){
                        authors[j] = volumeInfoJSON.getJSONArray(AUTHORS).get(j).toString();
                    }
                    Book book = new Book(
                            bookJSON.getString(ID),
                            volumeInfoJSON.getString(TITLE),
                            (volumeInfoJSON.isNull(SUBTITLE) ? "" : volumeInfoJSON.getString(SUBTITLE)),
                            authors,
                            (volumeInfoJSON.isNull(PUBLISHER) ? "" : volumeInfoJSON.getString(PUBLISHER)),
                            (volumeInfoJSON.isNull(PUBLISHED_DATE) ? "" : volumeInfoJSON.getString(PUBLISHED_DATE)),
                            (volumeInfoJSON.isNull(DESCRIPTION)?"":volumeInfoJSON.getString(DESCRIPTION)),
                            (imageLinksJSON==null)? "" : imageLinksJSON.getString(THUMBNAIL)
                    );
                    books.add(book);
            }

        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return books;
    }
}
