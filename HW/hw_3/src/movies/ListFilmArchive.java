package movies;

import java.util.ArrayList;
import java.util.TreeSet;

@SuppressWarnings("serial")
public class ListFilmArchive extends ArrayList<Movie> implements FilmArchive {

  @Override
  public ArrayList<Movie> getSorted() {
    TreeSet<Movie> moviesTreeSet = new TreeSet<Movie>(this);
    ArrayList<Movie> moviesList = new ArrayList<Movie>(moviesTreeSet);
    return moviesList;
  }

  @Override
  public boolean add(Movie movie) {
    // TODO Auto-generated method stub
    for (Movie m : this) {
      if (m.equals(movie)) {
        return false;
      }
    }
    
    super.add(movie);

    return true;
  }

  public static void main(String[] args) {
    ListFilmArchive archive = new ListFilmArchive();
    for (Movie m: Movie.getTestMovies()) {
      archive.add(m);
    }
    for (Movie m: archive.getSorted()) {
      System.out.println(m);
    }
  }
}
