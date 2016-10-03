package movies;

public class Movie implements Comparable<Movie> {
  private String title;
  private int year;

  Movie(String title, int year) {
    this.title = title;
    this.year = year;
  }

  @Override
  public int compareTo(Movie movie) {
    int titleComparison = this.title.compareTo(movie.title);
    int yearComparison = this.year == movie.year
                           ? 0
                           : this.year > movie.year
                             ? 1
                             : -1;

  return titleComparison == 0 && yearComparison == 0
      ? 0
      : titleComparison == 0
        ? yearComparison
        : titleComparison;
  }

  public String toString() {
    return "Movie " + this.title + " (" + this.year + ")";
  }

  public static Movie[] getTestMovies() {
    Movie[] movies = {
      new Movie("Foo", 1212),
      new Movie("Foo", 1200),
      new Movie("Doo", 1000),
      new Movie("Lee", 1000),
      new Movie("SpaceCow", 1433),
      new Movie("SpaceCow", 1433),
      new Movie("SpaceCow", 1433),
      new Movie("SpaceCow", 1433),
      new Movie("SpaceCow", 1433),
      new Movie("SpaceCow", 1433)
    };

    return movies;
  }

  public int hashCode() {
    return this.title.hashCode() + this.year;
  }

  public boolean equals(Object x) {
    return this.compareTo((Movie)x) == 0;
  }
}
