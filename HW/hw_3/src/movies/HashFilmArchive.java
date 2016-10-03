package movies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

@SuppressWarnings("serial")
public class HashFilmArchive extends HashSet<Movie> implements FilmArchive {

  public ArrayList<Movie> getSorted() {
    TreeSet<Movie> moviesTreeSet = new TreeSet<Movie>(this);
    ArrayList<Movie> moviesList = new ArrayList<Movie>(moviesTreeSet);
    return moviesList;
  }

  public static void main(String[] args) {
    HashFilmArchive archive = new HashFilmArchive();

    for (Movie m: Movie.getTestMovies()) {
      archive.add(m);
    }
    for (Movie m: archive) {
      System.out.println(m);
    }

    System.out.println(archive.size());
  }
}
