package thesonid.com.movieapp1.data;

import android.net.Uri;
import android.provider.BaseColumns;


public final class MovieContract {

    public static final String CONTENT_AUTHORITY = "thesonid.com.movieapp1";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";

    private MovieContract() {}

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIE);
        public final static String TABLE_NAME = "movie";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_TITLE = "title";
        public final static String COLUMN_ID = "id";
    }
}