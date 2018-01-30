package br.com.thaislisboa.popularmovies.domain.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class MovieContract {


    public static final String PATH_MOVIES = "movies";
    public static final String[] PROJ_MOVIE_LIST_PROJECTION = {
            MovieEntry._ID,
            MovieEntry.COLUMN_POSTER
    };
    public static final String[] PROJ_MOVIE_LIST_DETAILS = {
            MovieEntry._ID,
            MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_POSTER,
            MovieEntry.COLUMN_VOTEAVERANGE,
            MovieEntry.COLUMN_OVERVIEW,
            MovieEntry.COLUMN_DATE
    };
    public static String CONTENT_AUTHORITY = "br.com.thaislisboa.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class MovieEntry implements BaseColumns {


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES + "#";

        public static final int DATABASE_VERSION = 1;


        //This is the name of the SQL database for movies.
        public static final String COLUMN_ID = "id";
        public static final String MOVIE_TABLE = "movies";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_VOTEAVERANGE = "vote";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_DATE = "date";


        public static Uri buildMovieUriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
}
