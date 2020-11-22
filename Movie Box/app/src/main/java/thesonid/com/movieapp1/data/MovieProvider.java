package thesonid.com.movieapp1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

public class MovieProvider extends ContentProvider {
    public static final String TAG = MovieProvider.class.getSimpleName();

    private MovieDbHelper mDbHelper;
    static SQLiteDatabase generalDB;

    public static final int MOVIE = 100;
    public static final int MOVIE_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE, MOVIE);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/#", MOVIE_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new MovieDbHelper(getContext());
        generalDB = mDbHelper.getReadableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                cursor = database.query(MovieContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MOVIE_ID:
                selection = MovieContract.MovieEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(MovieContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                Uri newUri = insertMovie(uri, contentValues);
                getContext().getContentResolver().notifyChange(uri, null);
                return newUri;
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertMovie(Uri uri, ContentValues values) {
        String title = values.getAsString(MovieContract.MovieEntry.COLUMN_TITLE);
        Integer ido=values.getAsInteger(MovieContract.MovieEntry.COLUMN_ID);


        if (title == null || ido == null ) {
            throw new IllegalArgumentException("invlaid");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
        if (id == -1) {
            return null;
        }
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case MOVIE:
                rowsUpdated = database.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_ID:
                selection = MovieContract.MovieEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = database.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not successful for " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }


    public static boolean CheckIsDataAlreadyInDBorNot(String fieldValue) {
        String Query = "Select * from " + MovieContract.MovieEntry.TABLE_NAME + " where " + "id" + " = " + fieldValue;
        Cursor cursor = generalDB.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public static int getRowId(String fieldValue) {
        String Query = "Select * from " + MovieContract.MovieEntry.TABLE_NAME + " where " + "id" + " = " + fieldValue;
        Cursor cursor = generalDB.rawQuery(Query, null);
        cursor.moveToFirst();
        int i=cursor.getInt(cursor.getColumnIndex("_id"));
        cursor.close();
        return i;
    }

}