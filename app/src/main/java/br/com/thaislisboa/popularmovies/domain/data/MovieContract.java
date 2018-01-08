package br.com.thaislisboa.popularmovies.domain.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class MovieContract  {

    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    public static final String PATH_MOVIES = "movies";
    public static String CONTENT_AUTHORITY = "br.com.thaislisboa.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class MovieEntry implements BaseColumns {


        //* This is the {@link Uri} used to get a full list of terms and definitions.

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        //This is a String type that denotes a Uri references a list or directory.

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES + "#";

        public static final int DATABASE_VERSION = 1;


        //This is the name of the SQL database for movies.
        public static final String MOVIE_TABLE = "movies";
        public static final String COLUMN_MOVIE_ID = "id_movie";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER = "poster";


        //This is an array containing all the column headers in the terms table.
        public static final String[] COLUMNS =
                {COLUMN_MOVIE_ID, COLUMN_TITLE, COLUMN_POSTER};


        // This method creates a {@link Uri} for a single term, referenced by id.
        //@param id The id of the term.
        //     * @return The Uri with the appended id.

        public static Uri buildTermUriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
}
