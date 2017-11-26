package br.com.thaislisboa.popularmovies.domain.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Movie implements Serializable {
    private long id;
    private boolean video;
    private double voteAverage;
    private String title;
    private String poster;
    private String backdrop;
    private String overview;
    private String date;
    private ArrayList<Trailer> trailers;

    public Movie(long id, boolean video, double voteAverage,
                 String title, String poster, String backdrop, String overview, String date) {
        this.id = id;
        this.video = video;
        this.voteAverage = voteAverage;
        this.title = title;
        this.poster = poster;
        this.backdrop = backdrop;
        this.overview = overview;
        this.date = date;
        this.trailers = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public boolean isVideo() {
        return video;
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

    public String getPoster() {
        return "http://image.tmdb.org/t/p/w780/".concat(poster);
    }

    public String getBackdrop() {
        return "http://image.tmdb.org/t/p/w342/".concat(backdrop);
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

    public void addTrailer(String name,String keyYouTube){
        getTrailers().add(new Trailer(name,keyYouTube));
    }

    public ArrayList<Trailer> getTrailers() {
        return trailers;
    }

    @Override
    public String toString() {
        return "Movie{" + "id=" + id +
                ", video=" + video +
                ", voteAverage=" + voteAverage +
                ", title='" + title + '\'' +
                ", poster='" + poster + '\'' +
                ", backdrop='" + backdrop + '\'' +
                ", overview='" + overview + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
