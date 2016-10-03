package movies;

import java.util.*;
import java.util.stream.*;
import java.io.*;
import java.lang.reflect.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;



public class HW3TesterFall16 
{
	private enum Category
	{
		Movie_implements_comparable(10),
		Movie_getTestMovies(5),
		Movie_Constructor(5),
		Movie_compareTo(5),
		Movie_equals(5),
		Movie_toString(5),
		Movie_hashCode(5),
		FilmArchive_Methods(5),
		ListFilmArchive_implements_FilmArchive(5),
		ListFilmArchive_add(5),
		HashFilmArchive_implements_FilmArchive(5),
		HashFilmArchive_uses_inherited_add(5),
		HashFilmArchive_add(5),
		TreeFilmArchive_implements_FilmArchive(5),
		TreeFilmArchive_uses_inherited_add(5),
		TreeFilmArchive_add(5),
		Analyzers(4),
		Style(6),
		Comments(5);
		
		private int 		nPoints;
		
		Category(int nPoints)
		{
			this.nPoints = nPoints;
		}
		
		int getNPoints()
		{
			return nPoints;
		}
		
		static Stream<Category> stream()
		{
			return Arrays.asList(values()).stream();
		}
	}
	
	
	static
	{
		int totalPoints = 0;
		for (Category cat: Category.values())
			totalPoints += cat.getNPoints();
		assert totalPoints == 100  :  "Total points = " + totalPoints + ", should be 100.";		
	}
	
	
	private Map<Category, Integer>			categoryToScore;
	private String							details;
	private Movie[]							testMovies;
	private String							gradersNotes;
	private final static boolean		STUDENT_VERSION	= true;
	private static final String PKG_NAME = "movies";
	
	private static String prependPkg(String suffix) {
		return PKG_NAME + "." + suffix;
	}
	
	HW3TesterFall16()
	{
		categoryToScore = new LinkedHashMap<>();
		details = "";
	}
	
	
	private void testAll()
	{
		testMovie();
		testFilmArchive();
		testFilmArchiveSubclasses();
		testAnalyzers();
		if (!STUDENT_VERSION)
			testSubjective();
		printScores(!STUDENT_VERSION);
		if(!STUDENT_VERSION)
			sop("\n" + gradersNotes);
	}
	
	
	private void fullScore(Category cat)
	{
		categoryToScore.put(cat, cat.getNPoints());
	}
	
	
	private void zeroScore(Category cat)
	{
		categoryToScore.put(cat, 0);
	}
	
	
	private void printScores(boolean printScores)
	{
		if (printScores) {
			int totalScore = 0;
			for (Category cat: categoryToScore.keySet())
			{
				totalScore += categoryToScore.get(cat);
				sop(cat + ": " + categoryToScore.get(cat));
			}
			sop("---------------");
			sop("TOTAL: " + totalScore);			
		}

		if (!printScores){
			if(!details.isEmpty()){
				sop("\n\n" + details.trim());
			} else {
				sop("Looks good.");
			}
		}
	}
	
	
	private void testMovie()
	{
		// Does movie class exist?
		Class<?> movieClass = null;
		try
		{
			movieClass = Class.forName(prependPkg("Movie"));
		}
		catch (ClassNotFoundException x)
		{
			// No. 0 score for each movie category.
			Category.stream()
				.filter(cat -> cat.name().startsWith(PKG_NAME))
				.forEach(cat -> zeroScore(cat));
			details += "No class Movie in package " + PKG_NAME + ".\n";
			return;
		}
		
		// Check getTestMovies().
		int points = 0;
		try
		{
			Method getTestMoviesMethod = movieClass.getMethod("getTestMovies", new Class<?>[]{ } );
			points += 2;
			if (!Modifier.isStatic(getTestMoviesMethod.getModifiers()))
				details += "getTestMovies() is not static.\n";
			else
			{
				try
				{
					Movie[] testMovies = (Movie[])getTestMoviesMethod.invoke(null);	// static => null invoking object
					boolean tenMovies = false;
					if (testMovies.length == 10)
					{
						tenMovies = true;
						for (Movie m: testMovies)
						{
							if (m == null)
							{
								tenMovies = false;
								break;
							}
						}
					}
					if (tenMovies)
						points = Category.Movie_getTestMovies.getNPoints();
					else
					{
						details += "getTestMovies() does not return 10 movies.\n";
						points++;
					}
				}
				catch (IllegalAccessException|InvocationTargetException x) { }
			}
			categoryToScore.put(Category.Movie_getTestMovies, points);
		}
		catch (NoSuchMethodException x)
		{
			details += "No getTestMovies() method.\n";
			zeroScore(Category.Movie_getTestMovies);
		}
			
		// Correct ctor? (String, int)
		Constructor<?> movieStringIntCtor = null;
		Class<?>[] expectedArgTypes = { String.class, int.class };
		try
		{
			movieStringIntCtor = Class.forName(prependPkg("Movie")).getDeclaredConstructor(expectedArgTypes);
			fullScore(Category.Movie_Constructor);
		}
		catch (ClassNotFoundException x) { }
		catch (NoSuchMethodException x)
		{
			zeroScore(Category.Movie_Constructor);
			details += "Correct Movie ctor is missing, can't test most features.\n";
		}
		
		// equals()
		Movie may1 = null;			// movie "a", year 2
		Movie mby1 = null;
		Movie mby2 = null;
		Movie mby2Again = null;
		if (movieStringIntCtor == null)
		{
			// No (String, int) ctor for Movie, so can't test equals()
			details += "Correct Movie ctor is missing, can't test equals().\n";
			zeroScore(Category.Movie_equals);
		}
		else
		{
			// Construct some instances. Use reflection so this class will compile even
			// if correct ctor for Movie is missing.
			try
			{
				may1 = (Movie)movieStringIntCtor.newInstance("A", 2001);
				mby1 = (Movie)movieStringIntCtor.newInstance("B", 2001);
				mby2 = (Movie)movieStringIntCtor.newInstance("B", 2002);
				mby2Again = (Movie)movieStringIntCtor.newInstance("B", 2002);
				testMovies = new Movie[] { may1, mby1, mby2, mby2Again };
			}
			catch (InvocationTargetException|InstantiationException|IllegalAccessException x) { }			
			points = Category.Movie_compareTo.getNPoints();
			boolean[] expected = { true, false, false, false, 
								   false, true, false, false, 
								   false, false, true, true,
								   false, false, true, true };
			int n = 0;
			for (Movie thisMovie: testMovies)
			{
				for (Movie thatMovie: testMovies)
				{
					boolean result = thisMovie.equals(thatMovie);
					if (result != expected[n])
					{
						points = 0;
						String deet = "equals() returned " + result + " for " + thisMovie + " vs. " + thatMovie;
						deet += " (expected " + expected[n] + ")\n";
						details += deet;
					}
					n++;
				}
			}
			categoryToScore.put(Category.Movie_equals, points);
		}
		
		// Implements Comparable<Movie>
		points = Category.Movie_implements_comparable.getNPoints();
		boolean implementsComparable = false;
		for (Class<?> ifclass: movieClass.getInterfaces())
			if (ifclass.getName().equals("java.lang.Comparable"))
				implementsComparable = true;
		if (!implementsComparable)
		{
			details += "Movie does not declare implements Comparable.\n";
			points = 0;
		}
		else
		{
			File movieDotJava = new File("src/movies/Movie.java");
			if (!movieDotJava.exists())
			{
				// Not sure how to penalize this, and maybe we couldn't get this far
				// if src/movies/Movie.java didn't exist.
			}
			else
			{
				if (!(sourceContainsIgnoreWhitespace(movieDotJava, "Comparable<Movie>")  ||
					  sourceContainsIgnoreWhitespace(movieDotJava, "Comparable<"+ prependPkg("Movie") + ">")))
				{
					details += "Movie does not declare implements Comparable<Movie>\n";
					points = 0;
				}
			}
		}
		categoryToScore.put(Category.Movie_implements_comparable, points);
		
		// compareTo() 
		if (movieStringIntCtor == null)
		{
			// No (String, int) ctor for Movie, so can't test compareTo()
			details += "Correct Movie ctor is missing, can't test compareTo().\n";
			zeroScore(Category.Movie_compareTo);
		}
		else
		{
			// Compare by reflection, because calling compareTo() directly would result in
			// a compiler error if student didn't implement compareTo().
			expectedArgTypes = new Class<?>[]{ Movie.class };
			Method compareToMeth = null;
			try
			{
				compareToMeth = movieClass.getMethod("compareTo", expectedArgTypes);
				points = Category.Movie_compareTo.getNPoints();
				Movie[] movies = { may1, mby1, mby2 };
				int[] expected = { 0, -1, -1, 1, 0, -1, 1, 1, 0 };
				int n = 0;
				for (Movie thisMovie: movies)
				{
					for (Movie thatMovie: movies)
					{
						int result = (Integer)compareToMeth.invoke(thisMovie, thatMovie);     //thisMovie.compareTo(thatMovie);
						if (Math.signum(result) != expected[n])
						{
							points = 0;
							String deet = "compareTo() returned " + result + " for " + thisMovie + " vs. " + thatMovie;
							deet += " (expected ";
							if (expected[n] < 0)
								deet += "<0";
							else if (expected[n] > 0)
								deet += ">0";
							else
								deet += "0";
							deet += ")\n";
							details += deet;
						}
						n++;
					}
				}
			}
			catch (NoSuchMethodException x)
			{
				// Here if compareToMeth couldn't be found.
				details += "No compareTo() method";
				points = 0;
			}
			catch (IllegalAccessException|InvocationTargetException x) { }
			categoryToScore.put(Category.Movie_compareTo, points);
		}
		
		// toString().
		String[] expected = 
		{
			"Movie A (2001)",
			"Movie B (2001)",
			"Movie B (2002)",
			"Movie B (2002)"
		};
		points = Category.Movie_toString.getNPoints();
		int n = 0;
		for (Movie m: testMovies)
		{
			if (!expected[n].equals(m.toString()))
			{
				points = 0;
				details += "Error in toString(): expected " + expected[n] + "   saw " + m.toString() + "\n";
			}
			n++;
		}
		categoryToScore.put(Category.Movie_toString, points);
		
		// hashCode().
		int[] expectedHashCodes = { 2066, 2067, 2068, 2068 };
		points = Category.Movie_hashCode.getNPoints();
		n = 0;
		for (Movie m: testMovies)
		{
			if (expectedHashCodes[n] != m.hashCode())
			{
				points = 0;
				details += "Error in hashCode() for " + m + ": expected " + expectedHashCodes[n] + "   saw " + m.hashCode() + "\n";
			}
			n++;
		}
		categoryToScore.put(Category.Movie_hashCode, points);
	}
	
	
	private void testFilmArchive()
	{
		// Does FilmArchive exist?
		Class<?> farchClass = null;
		try
		{
			farchClass = Class.forName(prependPkg("FilmArchive"));
		}
		catch (ClassNotFoundException x)
		{
			// No. 0 score for each movie category.
			zeroScore(Category.FilmArchive_Methods);
			details += "No interface FilmArchive in package " + PKG_NAME + ".\n";
			return;
		}
		
		String[] methodNames = { "getSorted", "add" };
		Class<?>[][] argTypes = 
		{
			new Class<?>[] { },
			new Class<?>[] { Movie.class }
		};
		Class<?>[] returnTypes = { java.util.ArrayList.class, boolean.class };

		int points = Category.FilmArchive_Methods.getNPoints();
		for (int i=0; i<2; i++)
		{
			try
			{
				Method m = farchClass.getDeclaredMethod(methodNames[i], argTypes[i]);
				if (m.getReturnType() != returnTypes[i])
				{
					details += "Wrong return type for " + methodNames[i] + "() method in FilmArchive.\n";
					points = 0;
				}				
			}
			catch (NoSuchMethodException x)
			{
				details += "No " + methodNames[i] + "() method in FilmArchive.\n";
				points = 0;
			}
		}
		categoryToScore.put(Category.FilmArchive_Methods, points);
	}
	
	
	private void testFilmArchiveSubclasses()
	{
		String[] subclassNames = { "ListFilmArchive", "HashFilmArchive", "TreeFilmArchive" };
		String[] superclassNames = { "ArrayList", "HashSet", "TreeSet" };
		for (int i=0; i<3; i++)
			testFilmArchiveSubclass(subclassNames[i], superclassNames[i]);
	}
	
	
	private void testFilmArchiveSubclass(String subclassName, String superclassName)
	{
		Category implementsCat = Category.valueOf(subclassName + "_implements_FilmArchive");
		Category usesInheritedAddCat = (subclassName.equals("ListFilmArchive"))  ?
				null  :
				Category.valueOf(subclassName + "_uses_inherited_add");
		Category addCat = Category.valueOf(subclassName + "_add");
		
		// Get class.
		Class<?> clazz = null;
		try
		{
			clazz = Class.forName(prependPkg(subclassName));
		}
		catch (ClassNotFoundException x)
		{
			details += "No " + prependPkg(subclassName) + ".\n";
			Category.stream()
				.filter(cat -> cat.name().startsWith(subclassName))
				.forEach(cat -> zeroScore(cat));
			return;
		}
		
		// Check superclass.
		Class superclass = clazz.getSuperclass();
		if (!superclass.getName().equals("java.util." + superclassName))
		{
			details += "Wrong superclass for " + prependPkg(subclassName) + ".\n";
			Category.stream()
				.filter(cat -> cat.name().startsWith(subclassName))
				.forEach(cat -> zeroScore(cat));
			return;
		}
				
		int points = implementsFilmArchive(clazz)  ?  implementsCat.getNPoints()  :  0;
		categoryToScore.put(implementsCat, points);
		
		// Uses inherited add()? (HashFilmArchive and TreeFilmArchive only). This isn't detectable by
		// reflection. I hoped clazz.getDeclaredMethod("add") would do the right thing, but the
		// inherited add() is considered declared because it's declared in the Film Archive interface.
		// Lame. Have to check the source.
		if (usesInheritedAddCat != null)
		{
			String srcName = subclassName + ".java";
			File srcFile = new File("src/movies", srcName);
			if (sourceContainsIgnoreWhitespace(srcFile, "add(Movie"))
			{
				details += "Class " + subclassName + " doesn't use inherited add().";
				zeroScore(usesInheritedAddCat);
			}
			else
				fullScore(usesInheritedAddCat);
		}
		
		// Adds correctly? Construct using reflection, so that this code will compile even if
		// student hasn't provided all 3 subclasses.
		try
		{
			Constructor<?> ctor = clazz.getConstructor(new Class<?>[0]);
			Collection<Movie> theInstance = (Collection<Movie>)ctor.newInstance(new Object[0]);
			// Populate object under test in reverse alphabetical order.
			for (int i=testMovies.length-1; i>=0; i--)
				theInstance.add(testMovies[i]);
			// Test contents - different expectation for different implementations.
			if (addCat == Category.ListFilmArchive_add)
			{
				// List.
				ArrayList<Movie> golden = new ArrayList<Movie>();
				golden.add(testMovies[2]);
				golden.add(testMovies[1]);
				golden.add(testMovies[0]);  // not [3], which equals([2])
				if(golden.equals(theInstance)) {
					categoryToScore.put(addCat, addCat.getNPoints());					
				} else {
					details += " ListFilmArchive.add(): does not behave as expected.";
					categoryToScore.put(addCat, 0);					
				}
				
			}
			else if (addCat == Category.HashFilmArchive_add)
			{
				// Hash.
				if(theInstance.size() == 3) {
					categoryToScore.put(addCat, addCat.getNPoints());					
				} else {
					details += " HashFilmArchive.add(): does not behave as expected.";
					categoryToScore.put(addCat, 0);					
				}
			}
			else
			{
				// Tree.
				assert addCat == Category.TreeFilmArchive_add;
				// Hash.
				if(theInstance.size() == 3) {
					categoryToScore.put(addCat, addCat.getNPoints());					
				} else {
					details += " TreeFilmArchive.add(): does not behave as expected.";
					categoryToScore.put(addCat, 0);					
				}
			}
		}
		catch (NoSuchMethodException|InstantiationException|InvocationTargetException|IllegalAccessException x) { }
		
	}
	
	
	private boolean implementsFilmArchive(Class<?> clazz)
	{
		for (Class<?> ifclass: clazz.getInterfaces())
			if (ifclass.getName().equals(prependPkg("FilmArchive")))
				return true;
		return false;
	}
	
	
	// A merciful category. Half points if each analyzer exists.
	private void testAnalyzers()
	{
		String[] classNames = { "HashAnalyzer", "ListAnalyzer" };
		int points = 0;
		for (String className: classNames)
		{
			try
			{
				Class.forName(prependPkg(className));
				points += Category.Analyzers.getNPoints() / 2;		// Be sure #points is even				
			}
			catch (ClassNotFoundException x)
			{
				details += "No " + prependPkg(className) + "\n";
			}
		}
		categoryToScore.put(Category.Analyzers, points);
	}
	
	
	private boolean sourceContainsIgnoreWhitespace(File source, String searchTerm)
	{
		try
		(
			FileReader fr = new FileReader(source);
			BufferedReader br = new BufferedReader(fr);
		)
		{
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null)
			{
				line = line.replace(" ", "");
				line = line.replace("\t", "");
				sb.append(line);
			}
			return sb.indexOf(searchTerm) > 0;
		}
		catch (IOException x)
		{
			return false;
		}
	}
	
	private void testSubjective()
	{
		SubjectiveDialog dia = new SubjectiveDialog();
		dia.setModal(true);
		dia.setVisible(true);

		gradersNotes = dia.getSubjectivePanel().getNotes();
		int readabilityScore = dia.getSubjectivePanel().getReadabilityScore();
		categoryToScore.put(Category.Style, readabilityScore);
		int commentsScore = dia.getSubjectivePanel().getCommentsScore();
		categoryToScore.put(Category.Comments, commentsScore);	
		dia.dispose();
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
				JSlider slider = new JSlider(0, cat.getNPoints(), cat.getNPoints());
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
		try
		{
			HW3TesterFall16 that = new HW3TesterFall16();
			that.testAll();
		}
		catch (Exception x)
		{
			sop("STRESS: " + x.getMessage());
			x.printStackTrace();
		}
	}
	
}
