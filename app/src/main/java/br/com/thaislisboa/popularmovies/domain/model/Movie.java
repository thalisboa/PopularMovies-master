package br.com.thaislisboa.popularmovies.domain.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Movie implements Serializable {
    private long id;

    private double voteAverage;
    private String title;
    private String poster;
    private String backdrop;
    private String overview;
    private String date;
    private ArrayList<Trailer> trailers = new ArrayList<>();
    public ArrayList<Review> reviews = new ArrayList<>();

    public Movie() {

    }

    public Movie(long id, double voteAverage,
                 String title, String poster, String backdrop, String overview, String date) {
        this.id = id;
        this.voteAverage = voteAverage;
        this.title = title;
        this.poster = poster;
        this.backdrop = backdrop;
        this.overview = overview;
        this.date = date;
        this.trailers = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }

    public long getMovieId() {
        return id;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getGrade() {
        return String.format("%s/10", getVoteAverage());
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return poster;
    }

    public static String getPosterURL(String posterPath) {
        return "http://image.tmdb.org/t/p/w780/".concat(posterPath);
    }

    public String getOverview() {
        return overview;
    }

    public String getDate() {
        return date;
    }

    public String getYear() {
        String[] date = getDate().split("-");
        return date[0];
    }

    public void addTrailer(String name, String keyYouTube) {
        getTrailers().add(new Trailer(name, keyYouTube));
    }


    public String getTrailerYoutube(int position) {
        Trailer t = getTrailers().get(position);


        if (t == null) {
            return "";

        } else {

            return t.getYouTube();
        }

    }

    public ArrayList<Trailer> getTrailers() {
        return trailers;
    }

    public String getTrailerName(int position) {
        Trailer t = getTrailers().get(position);

        if (t == null) {
            return "";
        } else {
            return t.getName();
        }
    }

    public int getTrailersSize() {
        if (getTrailers() == null) return 0;
        return getTrailers().size();
    }


    @Override
    public String toString() {
        return "Movie{" + "id=" + id +
                ", voteAverage=" + voteAverage +
                ", title='" + title + '\'' +
                ", poster='" + poster + '\'' +
                ", backdrop='" + backdrop + '\'' +
                ", overview='" + overview + '\'' +
                ", date='" + date + '\'' +
                '}';
    }


//review

    public ArrayList<Review> getReviews() {
        return reviews;

    }

    public void addReview(String name, String comment) {
        getReviews().add(new Review(name, comment));
    }

    public String getReviewName(int position) {
        Review r = getReviews().get(position);

        if (r == null) {
            return "";
        } else {
            return r.getName();
        }
    }

    public String getReviewComment(int position) {
        Review r = getReviews().get(position);

        if (r == null) {
            return "";
        } else {
            return r.getComment();
        }
    }


    public int getReviewsSize() {
        if (getReviews() == null) return 0;
        return getReviews().size();
    }

    public void setMovieId(long id) {
        this.id = id;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
