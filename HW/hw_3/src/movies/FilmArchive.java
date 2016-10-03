package movies;

import java.util.ArrayList;

public interface FilmArchive {

  public ArrayList<Movie> getSorted();

  public boolean add(Movie movie);
}
