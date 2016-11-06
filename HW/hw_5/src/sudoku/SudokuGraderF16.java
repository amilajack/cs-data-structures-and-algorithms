package sudoku;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;

public class SudokuGraderF16 {
	private final static Class<?>[]					EMPTY_ARGSLIST 		= { };
	private final static Class<?>[]					OBJECT_ARGSLIST 	= { Object.class };
	private final static Class<?>[]					Grid_ARGSLIST 		= { Grid.class };
	//private final static String						PACKAGE				= "sudokusolution";
	private final static String						PACKAGE				= "sudoku";
	
	private String									gradersNotes;
	private int										commentDeduction;
	private int										styleDeduction;
	private Map<Category, ArrayList<Deduction>> 	catToDeductions 	= new LinkedHashMap<>();
	
	private enum Category
	{
		// For each category, student can't lose > maxDeductions.
		// Grid evaluation
		Grid(45),
		next9Grids(15), 
		isLegal(10), 
		isFull(10), 
		equals(10), 
		// Solver evaluation
		evaluate(10), 
		Solver(35),
		// Comments and style
		Style(5),
		Comments(5);
		
		private int 	maxDeductions;
		
		Category(int maxDeductions)
		{
			this.maxDeductions = maxDeductions;
		}
		
		int getMaxDeductions()
		{
			return maxDeductions;
		}
	}
	
	
	private void grade()
	{
		gradeGrid();
		gradeSolver();
		testSubjective();
		int score = 100;
		for (Category cat: catToDeductions.keySet())
		{
			if (cat == Category.Style  ||  cat == Category.Comments)
				continue;
			ArrayList<Deduction> dedns = catToDeductions.get(cat);
			if (dedns.isEmpty())
				continue;
			sop("--------");
			sop(cat + ":");
			int totalDeductionsThisCategory = 0;
			for (Deduction dedn: dedns)
			{
				sop(dedn);
				totalDeductionsThisCategory += dedn.pointsOff;
			}
			totalDeductionsThisCategory = Math.min(totalDeductionsThisCategory, cat.maxDeductions);
			sop("TOTAL DEDUCTIONS THIS CATEGORY (max=-" + cat.maxDeductions + "): -" + totalDeductionsThisCategory);
			score -= totalDeductionsThisCategory;
 		}
		if (styleDeduction > 0)
			sop("Style: -" + styleDeduction);
		score -= styleDeduction;
		if (commentDeduction > 0)
			sop("Comments: -" + commentDeduction);
		score -= commentDeduction;
		sop("---------------------------\n");
		sop("SCORE: " + score);
		sop("\n" + gradersNotes);
	}
	
	
	public void gradeGrid()
	{
		String err;
		Class<?> className = getClass(PACKAGE + ".Grid");
		if (className == null)
		{
			sop("No Grid Class");
		}
		//check the return type of "isLegal","isFull","next9Grids" methods
		String[] methodNames = {"isLegal","isFull","next9Grids"};
		for (String name: methodNames)
		{
			try
			{
				Method m = className.getDeclaredMethod(name, EMPTY_ARGSLIST);
				if (name.equals("next9Grids"))
				{
					if(!ArrayList.class.isAssignableFrom(m.getReturnType()))
							deduct(Category.Grid, name + "() does not return Arraylist", 10);					
				}					
				else if(name.equals("isLegal"))
				{
					if(m.getReturnType()!= boolean.class)
					{
						sop(m.getReturnType());
						deduct(Category.Grid, name + "() does not return Boolean", 2);
					}
				}
				else if(name.equals("isFull"))
				{
					if(m.getReturnType()!= boolean.class)
						deduct(Category.Grid, name + "() does not return Boolean", 2);
				}

			}
			catch (NoSuchMethodException x)
			{
				deduct(Category.Grid, "No " + name + "() method", 10);
			}
			// check if equal is present
			try
			{
				Method m = className.getDeclaredMethod("equals", OBJECT_ARGSLIST);
				if(m.getReturnType()!= boolean.class)
					deduct(Category.Grid,  "equals() does not return Boolean", 2);
			}
			catch (NoSuchMethodException x)
			{
				deduct(Category.Grid, "No " + name + "() method", 10);
			}
		}
		
		//check if the equals returns correct result
		Grid g1 = TestGridSupplier.getPuzzle1();
		Grid g2 = TestGridSupplier.getPuzzle1();
		// a grid which is having one 
		String[] puzzleTestEqual =
		{
				"..3.1.5..",
				"8..395..1",
				"14.....27",
				".8..7..5.",
				"62.9.4.13",
				".9..2..7.",
				"91.....34",
				"2..748..9",
				"..6.3.2.."		
		};    
		Grid g3 = new Grid(puzzleTestEqual);
		if(!g1.equals(g2))
		{
			err = "equals returned false for the same Grid";
			deduct(Category.Grid, err, 5);
		}
		if(g1.equals(g3))
		{
			err = "equals returned true for the different Grid";
			deduct(Category.Grid, err, 5);
		}
		// check if isLegal correct
		String[] puzzleTestisLegalRow =
		{
					"..3.3.5..",
					"8..395..1",
					"14.....27",
					".8..7..5.",
					"62.9.4.13",
					".9..2..7.",
					"91.....34",
					"2..748..9",
					"..6.3.2.."		
		};  
		String[] puzzleTestisLegalCol =
		{
						"..3.1.5..",
						"8..395..1",
						"14.....27",
						".8..7..5.",
						"62.9.4.13",
						".9..2..7.",
						"91.....34",
						"2..748..9",
						"8.6.3.2.."		
		}; 
		if(!g1.isLegal())
		{
			deduct(Category.Grid, "isLegal() does not return False for a correct Grid", 2);
		}
		if(new Grid(puzzleTestisLegalRow).isLegal())
		{
			deduct(Category.Grid, "isLegal() returns True for an incorrect Grid", 2);
		}
		if(new Grid(puzzleTestisLegalCol).isLegal())
		{
			deduct(Category.Grid, "isLegal() returns True for an incorrect Grid", 2);
		}
		//check for isFull
		String[]  testisFull =
		    {
		        "463217598",
		        "872395641",
		        "159486327",
		        "384671952",
		        "627954813",
		        "591823476",
		        "918562734",
		        "235748169",
		        "746139285"
		    };
		if(g1.isFull())
		{
			deduct(Category.Grid, "isFull() return True for an incomplete Grid", 3);
		}
		if(!new Grid(testisFull).isFull())
		{
			deduct(Category.Grid, "isFull() return False for a complete Grid", 3);
		}
		//check size of the arrayList returned by next9Grids
		if(g1.next9Grids().size()!=9)
		{
			deduct(Category.Grid, "next9Grids() does not return Arraylist of size 9", 5);
		}
	}
	public void gradeSolver()
	{
		Class<?> className = getClass(PACKAGE + ".Solver");
		if (className == null)
		{
			sop("No Solver Class");
		}
		//check  evaluate() 
		Grid g1 = TestGridSupplier.getPuzzle1();
		String[] puzzleTestisLegalRow =
		{
					"..3.3.5..",
					"8..395..1",
					"14.....27",
					".8..7..5.",
					"62.9.4.13",
					".9..2..7.",
					"91.....34",
					"2..748..9",
					"..6.3.2.."		
		}; 
		String[] puzzleTestisLegalCol =
		{
					"..3.1.5..",
					"8..395..1",
					"14.....27",
					".8..7..5.",
					"62.9.4.13",
					".9..2..7.",
					"91.....34",
					"8..748..9",
					"..6.3.2.."		
		}; 
		String[] puzzleTestisLegalGrid =
		{
					"3.3.1.5..",
					"8..395..1",
					"14.....27",
					".8..7..5.",
					"62.9.4.13",
					".9..2..7.",
					"91.....34",
					"8..748..9",
					"..6.3.2.."		
		};
		//String[] methodNames =  {"evalute","solve"};
		Grid g2 = new Grid(puzzleTestisLegalRow);
		Grid g3 =new Grid(puzzleTestisLegalCol);
		Grid g4 = new Grid(puzzleTestisLegalGrid);
		Grid g5 = TestGridSupplier.getSolution1();
		try
		{
			Method m = className.getDeclaredMethod("evaluate", Grid_ARGSLIST);
			if (m.getReturnType() != Evaluation.class)
			{
				deduct(Category.Solver, "evaluate() does not return Evaluation", 5);
			}
			Solver s = new Solver(g1);
			if (s.evaluate(g1)==Evaluation.ABANDON)
			{
				deduct(Category.Solver, "evaluate() returns ABANDON for a legal Grid", 5);
			}
			if (s.evaluate(g2)!=Evaluation.ABANDON ||s.evaluate(g3)!=Evaluation.ABANDON||s.evaluate(g4)!=Evaluation.ABANDON )
			{
				deduct(Category.Solver, "evaluate() does not return ABANDON for an illegal Grid", 5);
			}
			if(s.evaluate(g5)!=Evaluation.ACCEPT )
			{
				deduct(Category.Solver, "evaluate() does not return ACCEPT for a full Grid", 5);
			}
			if(s.evaluate(g1)==Evaluation.ACCEPT )
			{
				deduct(Category.Solver, "evaluate() return ACCEPT for an incomplete Grid", 5);
			}
		}
		catch (NoSuchMethodException x)
		{
			deduct(Category.Solver, "No evaluate() method", 10);
		}
		// check for solver class
		Grid g = TestGridSupplier.getPuzzle1();
		Solver solver = new Solver(g);
		solver.solve();
		Grid soln = solver.getSolutions().get(0);
		if(!soln.equals(TestGridSupplier.getSolution1()))
		{
			deduct(Category.Solver, "Solve does not give correct solution", 35);
		}
	}
	private class Deduction
	{
		private String		reason;
		private int			pointsOff;
		
		Deduction(String reason, int pointsOff)
		{
			this.reason = reason;
			this.pointsOff = pointsOff;
		}
		
		public String toString()
		{
			return reason + ": -" + pointsOff;
		}
	}
	private void deduct(Category cat, String reason, int pointsOff)
	{
		ArrayList<Deduction> dedsForCat = catToDeductions.get(cat);
		if (dedsForCat == null)
			catToDeductions.put(cat, (dedsForCat= new ArrayList<>()));
		dedsForCat.add(new Deduction(reason, pointsOff));
	}
	
	private Class<?> getClass(String name)
	{
		if (!name.startsWith(PACKAGE))
			name = PACKAGE + "." + name;
		try 
		{
			return Class.forName(name);
		}
		catch (ClassNotFoundException x)
		{
			return null;
		}
	}
	private static void sop(Object x)
	{
		System.out.println(x);
	}
	private void testSubjective()
	{
		SubjectiveDialog dia = new SubjectiveDialog();
		dia.setModal(true);
		dia.setVisible(true);

		gradersNotes = dia.getSubjectivePanel().getNotes();
		int readabilityScore = dia.getSubjectivePanel().getReadabilityScore();
		styleDeduction = Category.Style.getMaxDeductions() - readabilityScore;
		int commentsScore = dia.getSubjectivePanel().getCommentsScore();
		commentDeduction = Category.Comments.getMaxDeductions() - commentsScore;
	}
	

	private class SubjectivePanel extends JPanel
	{
		private ArrayList<JSlider> sliders;
		private JTextArea notesTA;
		
		SubjectivePanel()
		{
			sliders = new ArrayList<>();
			setLayout(new BorderLayout());
			setLayout(new GridLayout(1, 3));
			Category[] cats = { Category.Style, Category.Comments };
			for (Category cat: cats)
			{
				JPanel pan = new JPanel(new BorderLayout());
				pan.add(new JLabel(cat.name()), BorderLayout.NORTH);
				JSlider slider = new JSlider(0, cat.getMaxDeductions(), cat.getMaxDeductions());
				slider.setMajorTickSpacing(1);
				slider.setPaintTicks(true);
				slider.setPaintLabels(true);
				slider.setSnapToTicks(true);
				pan.add(slider, BorderLayout.SOUTH);
				sliders.add(slider);
				add(pan);
			}
			notesTA = new JTextArea(10, 25);
			JPanel commentsPan = new JPanel(new BorderLayout());
			commentsPan.add(new JLabel("Your notes"), BorderLayout.NORTH);
			commentsPan.add(notesTA, BorderLayout.CENTER);
			add(commentsPan);
		}
		
		int getReadabilityScore()
		{
			return sliders.get(0).getValue();
		}
		
		int getCommentsScore()
		{
			return sliders.get(1).getValue();
		}
		
		String getNotes()
		{
			return notesTA.getText().trim();
		}
	}
	
	
	private class SubjectiveDialog extends JDialog implements ActionListener
	{
		private SubjectivePanel 	subjPan;
		
		SubjectiveDialog()
		{
			subjPan = new SubjectivePanel();
			add(subjPan, BorderLayout.CENTER);
			JPanel okPan = new JPanel();
			JButton okBtn = new JButton("Ok");
			okBtn.addActionListener(this);
			okPan.add(okBtn);
			add(okPan, BorderLayout.SOUTH);
			pack();
		}
		
		public void actionPerformed(ActionEvent e)
		{
			setVisible(false);
		}
		
		SubjectivePanel getSubjectivePanel()
		{
			return subjPan;
		}
	}
	public static void main(String[] args)
	{
		//new Grader().testSubjective();
		new SudokuGraderF16().grade();
	}
}
