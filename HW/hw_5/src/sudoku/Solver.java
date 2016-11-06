package sudoku;

import java.util.*;


public class Solver
{
  private Grid						      problem;
  private ArrayList<Grid>				solutions;


  public Solver(Grid problem)
  {
    this.problem = problem;
  }

  public void solve()
  {
    solutions = new ArrayList<>();
    solveRecurse(problem);
  }

  /**
   * Standard backtracking recursive solver.
   */
  private void solveRecurse(Grid grid)
  {
    Evaluation eval = evaluate(grid);

    if (eval == Evaluation.ABANDON)
    {
      // Abandon evaluation of this illegal board.
      return;
    }
    else if (eval == Evaluation.ACCEPT)
    {
      // A complete and legal solution. Add it to solutions.
      this.solutions.add(grid);
    }
    else
    {
      // Here if eval == Evaluation.CONTINUE. Generate all 9 possible next grids.
      // Recursively call solveRecurse() on those grids.
      for (Grid g : grid.next9Grids())
      {
        this.solveRecurse(g);
      }
    }
  }

  /**
   * Returns Evaluation.ABANDON if the grid is illegal.
   * Returns ACCEPT if the grid is legal and complete.
   * Returns CONTINUE if the grid is legal and incomplete.
   */
  public Evaluation evaluate(Grid grid)
  {
    boolean isLegal = grid.isLegal();
    boolean isFull = grid.isFull();

    if (!isLegal) return Evaluation.ABANDON;
    if (!isFull) return Evaluation.CONTINUE;

    return Evaluation.ACCEPT;
  }

  public ArrayList<Grid> getSolutions()
  {
    return solutions;
  }

  public static void main(String[] args)
  {
    Grid g = TestGridSupplier.getPuzzle1();
    Solver solver = new Solver(g);

    solver.solve();

    // Print out your solution, or test if it equals() the solution in
    // TestGridSupplier.
    for (Grid some : solver.solutions)
    {
      System.out.println(some.toString());
    }
  }
}
