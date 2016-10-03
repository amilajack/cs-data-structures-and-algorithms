package movies;

import java.util.ArrayList;
import java.util.TreeSet;

@SuppressWarnings("serial")
public class TreeFilmArchive extends TreeSet<Movie> implements FilmArchive {

  public ArrayList<Movie> getSorted() {
    ArrayList<Movie> moviesList = new ArrayList<Movie>(this);
    return moviesList;
  }

  public static void main(String[] args) {
    TreeFilmArchive archive = new TreeFilmArchive();

    for (Movie m: Movie.getTestMovies()) {
      archive.add(m);
    }

    for (Movie m: archive.getSorted()) {
      System.out.println(m);
    }
  }
}
