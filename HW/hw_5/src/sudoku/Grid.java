package sudoku;

import java.util.*;


public class Grid
{
  public int[][]						values;


  /**
   * See TestGridSupplier for examples of input.
   * Dots in input strings represent 0s in values[][].
   */
  public Grid(String[] rows)
  {
    values = new int[9][9];

    for (int j=0; j<9; j++)
    {
      String row = rows[j];
      char[] charray = row.toCharArray();

      for (int i=0; i<9; i++)
      {
        char ch = charray[i];
        if (ch != '.') {
          values[j][i] = ch - '0';
        }
      }
    }
  }

  /**
   * Convert the grid to a string
   */
  public String toString()
  {
    String s = "";
    for (int j=0; j<9; j++)
    {
      for (int i=0; i<9; i++)
      {
        int n = values[j][i];
        if (n == 0) s += '.';
        else s += (char)('0' + n);
      }
      s += "\n";
    }
    return s;
  }


  /**
   * Return an array of all the possible grids
   */
  public ArrayList<Grid> next9Grids()
  {
    if (this.isFull()) return null;

    ArrayList<Grid> next9Grids = new ArrayList();

    try {
      int[] firstEmpty = this.findFirstEmpty();
      String[] splitString = this.toString().split("\n");

      for (int i = 1; i < 10; i++)
      {
        StringBuilder charBuildString = new StringBuilder(splitString[firstEmpty[0]]);
        charBuildString.setCharAt(firstEmpty[1], (char)(i + '0'));
        splitString[firstEmpty[0]] = charBuildString.toString();
        next9Grids.add(new Grid(splitString));
      }

      return next9Grids;
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    return next9Grids;
  }

  /**
   * Find the first element that is equal to 0. This will be the element that
   * will be guessed
   */
  private int[] findFirstEmpty() throws Exception {
    for (int j=0; j<9; j++)
    {
      for (int i=0; i<9; i++)
      {
        int current = this.values[j][i];

        if (current == 0) {
          int[] myIntArray = {j,i};
          return myIntArray;
        }
      }
    }

    throw new Exception("Cannot find empty slot. Array is full");
  }

  /**
   * Returns true if this grid is legal. A grid is legal if no row, column,
   * diagonal, or 3x3 block contains a repeated 1, 2, 3, 4, 5, 6, 7, 8, or 9.
   */
  public boolean isLegal()
  {
    return (
      this.checkRow(this.values) &&
      this.checkColumn(this.values)
      // this.checkGrid(this.values)
    );
  }

  /**
   * Check if the row is valid. Return false if incorrect and true if correct
   */
  private boolean checkRow(int[][] x) {
    for (int j=0; j<9; j++)
    {
      HashSet m = new HashSet();

      for (int i=0; i<9; i++)
      {
        int current = x[j][i];

        if (m.contains(current)) {
          return false;
        }

        if (current != 0) {
          m.add(current);
        }
      }
    }

    return true;
  }

  /**
   * Check if the column is valid. Return false if incorrect and true if correct
   */
  private boolean checkColumn(int[][] x) {
    for (int j=0; j<9; j++)
    {
      HashSet m = new HashSet();

      for (int i=0; i<9; i++)
      {
        int current = x[i][j];
        if (m.contains(current)) return false;

        if (current != 0) {
          m.add(current);
        }
      }
    }

    return true;
  }

  /**
   * Check if the grid is valid. Return false if incorrect and true if correct
   */
  private boolean checkGrid(int[][] x) {
    for (int j=0; j<9; j++)
    {
      for (int i=0; i<9; i++)
      {
        if (x[j][i] < 1 && x[j][i] > 9) {
          return false;
        }
      }
    }

    return true;
  }

  /**
   * Returns true if every cell member of values[][] is a digit from 1-9.
   */
  public boolean isFull()
  {
    for (int j=0; j<9; j++)
    {
      for (int i=0; i<9; i++)
      {
        if (values[j][i] < 1 || values[j][i] > 9) {
          return false;
        }
      }
    }

    return true;
  }

  /**
   * Returns true if x is a Grid and, for every (i,j),
   * x.values[i][j] == this.values[i][j].
   */
  public boolean equals(Object x)
  {
    if (!(x instanceof Grid)) return false;

    Grid x1 = (Grid)x;

    for (int j=0; j<9; j++)
    {
      for (int i=0; i<9; i++)
      {
        if (x1.values[j][i] != this.values[j][i]) {
          return false;
        }
      }
    }

    return true;
  }
}
