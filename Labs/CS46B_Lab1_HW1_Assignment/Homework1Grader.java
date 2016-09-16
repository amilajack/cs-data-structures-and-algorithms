package planets;

import java.util.*;
import java.lang.reflect.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class Homework1Grader 
{
	private final static float			EXPECTED_MEAN	= 3.3332647e26f;
	private final static Class<?>[]		EMPTY_ARGSLIST 	= { };
	private final static boolean		STUDENT_VERSION	= true;
	
	private Map<Category, ArrayList<Deduction>> 	catToDeductions = new LinkedHashMap<>();
	private int										commentDeduction;
	private int										styleDeduction;
	private String									gradersNotes;
	
	
	private enum Category
	{
		// For each category, student can't lose > maxDeductions.
		Planet(40), 
		Averager(40), 
		Style(10),
		Comments(10);
		
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
	
	
	static
	{
		int maxPoints = 0;
		for (Category cat: Category.values())
			maxPoints += cat.getMaxDeductions();
		assert maxPoints == 100  : maxPoints;
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
	
	
	private void deductMax(Category cat, String reason)
	{
		deduct(cat, reason, cat.getMaxDeductions());
	}
	
	
	private void grade()
	{
		gradePlanet();
		gradeAverager();
		if (!STUDENT_VERSION)
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
		if (score < 100)
			sop("---------------------------\n");
		if (styleDeduction > 0)
			sop("Style: -" + styleDeduction + " of " + Category.Style.getMaxDeductions());
		score -= styleDeduction;
		if (commentDeduction > 0)
			sop("Comments: -" + commentDeduction + " of " + Category.Comments.getMaxDeductions());
		score -= commentDeduction;
		sop("---------------------------\n");
		sop("SCORE: " + score);
		if (gradersNotes != null  &&  !gradersNotes.trim().isEmpty())
			sop("\nNOTES:\n" + gradersNotes);
		
	}
	
	
	private void gradePlanet()
	{
		// Does class exist?
		Class<?> clazz = getClass("planets.Planet");
		if (clazz == null)
		{
			deductMax(Category.Planet, "No planets.Planet class");
		}

		// Does static method getAll() exist?
		try
		{
			Method getAllMeth = clazz.getDeclaredMethod("getAll", EMPTY_ARGSLIST);
			int methMods = getAllMeth.getModifiers();
			if (!Modifier.isStatic(methMods))
			{
				deduct(Category.Planet, "getAll() is not static", 15);
				return;
			}
			if (!Modifier.isPublic(methMods))
			{
				deduct(Category.Planet, "getAll() is not public", 15);
				return;
			}
		}
		catch (NoSuchMethodException x)
		{
			deduct(Category.Planet, "No getAll() method", 20);
		}
	}
	
	
	private void gradeAverager()
	{
		// Does class exist?
		Class<?> clazz = getClass("planets.MassAverager");
		if (clazz == null)
		{
			deductMax(Category.Averager, "No planets.MassAverager class");
			return;
		}
		
		// Does getMeanPlanetaryMass() exist?
		Method getAllMeth = null;
		try
		{
			getAllMeth = clazz.getDeclaredMethod("getMeanPlanetaryMass", EMPTY_ARGSLIST);
			int methMods = getAllMeth.getModifiers();
			if (!Modifier.isPublic(methMods))
			{
				deduct(Category.Averager, "getMeanPlanetaryMass() is not public", 15);
				return;
			}
			if (getAllMeth.getReturnType() != float.class)
			{
				deduct(Category.Averager, "getMeanPlanetaryMass() return type is not float", 25);
				return;
			}
		}
		catch (NoSuchMethodException x)
		{
			deduct(Category.Averager, "No getMeanPlanetaryMass() method", 38);
			return;
		}
		
		// getMeanPlanetaryMass() functionality.
		try
		{
			Constructor<?> ctor = clazz.getConstructor(EMPTY_ARGSLIST);
			MassAverager uut = (MassAverager)ctor.newInstance();
			float avg = (Float)getAllMeth.invoke(uut);
			float tolerance = 0.0001f * EXPECTED_MEAN;
			if (Math.abs(avg - EXPECTED_MEAN) > tolerance)
			{
				deduct(Category.Averager, "Wrong value from getMeanPlanetaryMass(): " + EXPECTED_MEAN, 18);
				return;
			}
		}
		catch (NoSuchMethodException x)
		{
			deduct(Category.Averager, "No no-args ctor", 15);
			return;
		}
		catch (InstantiationException | InvocationTargetException | IllegalAccessException x) 
		{
			assert false;
		}
	}	
	
	
	private Class<?> getClass(String name)
	{
		try 
		{
			return Class.forName(name);
		}
		catch (ClassNotFoundException x)
		{
			return null;
		}
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
			setLayout(new GridLayout(1,  3));
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
	
	
	
	private static void sop(Object x)
	{
		System.out.println(x);
	}
	
	
	public static void main(String[] args)
	{
		new Homework1Grader().grade();
	}
}
