package dna;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class DNAGrader
{
  private final static Class<?>[]		EMPTY_ARGSLIST 		= { };
  private final static Class<?>[]		STRING_ARGSLIST 	= { String.class };
  private final static Class<?>[]		STRING_2_ARGSLIST 	= { String.class, String.class };
  private final static Class<?>[]		STRING_3_ARGSLIST 	= { String.class, String.class, String.class };
  private final static Class<?>[]		BR_ARGSLIST 		= { BufferedReader.class };
  private final static Class<?>[]		PW_ARGSLIST 		= { PrintWriter.class };

  private Class<?>								fastqRecordClass;
  private Map<Category, ArrayList<Deduction>> 	catToDeductions = new LinkedHashMap<>();
  private int										commentDeduction;
  private int										styleDeduction;
  private String									gradersNotes;


  private enum Category
  {
    // For each category, student can't lose > maxDeductions.
    DNARecord(10),
    FastqException(10),
    FastqRecord(18),
    FastaRecord(10),
    FastqReader(10),
    FastaWriter(10),
    FileConverter(24),
    Style(5),
    Comments(3);

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
    gradeFastqException();
    gradeDNARecord();
    gradeFastqRecord();
    gradeFastaRecord();
    gradeFastqReader();
    gradeFastaWriter();
    gradeConverter();
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


  private Class<?> getClass(String name)
  {
    if (!name.startsWith("dna."))
    name = "dna." + name;
    try
    {
      return Class.forName(name);
    }
    catch (ClassNotFoundException x)
    {
      return null;
    }
  }


  private void gradeFastqException()
  {
    // Does class exist?
    Class<?> clazz = getClass("dna.FastqException");
    if (clazz == null)
    {
      deductMax(Category.FastqException, "No dna.FastqException class");
      return;
    }

    // Does FastqException(String) ctor exist?
    try
    {
      clazz.getConstructor(STRING_ARGSLIST);
    }
    catch (NoSuchMethodException x)
    {
      deduct(Category.FastqException, "No FastqException(String) constructor", 8);
    }

    // Does method testMessage() exist?
    try
    {
      clazz.getDeclaredMethod("testMessage", STRING_ARGSLIST);
    }
    catch (NoSuchMethodException x)
    {
      deduct(Category.FastqException, "No testMessage(String) method", 3);
    }
  }


  private void gradeDNARecord()
  {
    // Does class exist?
    Class<?> clazz = getClass("dna.DNARecord");
    if (clazz == null)
    {
      deductMax(Category.DNARecord, "No dna.DNARecord class");
      return;
    }

    // Does class define getDefline() and getSequence()?
    String[] names = { "getDefline", "getSequence" };
    for (String name: names)
    {
      try
      {
        Method m = clazz.getDeclaredMethod(name, EMPTY_ARGSLIST);
        if (m.getReturnType() != String.class)
        deduct(Category.DNARecord, name + "() does not return String", 4);
      }
      catch (NoSuchMethodException x)
      {
        deduct(Category.DNARecord, "No " + name + "() method", 5);
      }
    }
  }


  private void gradeFastqRecord()
  {
    String err;

    // Does class exist?
    fastqRecordClass = getClass("dna.FastqRecord");
    if (fastqRecordClass == null)
    {
      deductMax(Category.FastqRecord, "No dna.FastqRecord interface");
      return;
    }

    // Does class declare it implements DNARecord?
    boolean implementsDNARecord = false;
    for (Class<?> c: fastqRecordClass.getInterfaces())
    {
      if (c.getName().equals("dna.DNARecord"))
      {
        implementsDNARecord = true;
        break;
      }
    }
    if (!implementsDNARecord)
    {
      deduct(Category.FastqRecord, "DNARecord doesn't declare that it implements DNARecord.", 2);
    }

    // Does class define getDefline() and getSequence()?
    String[] names = { "getDefline", "getSequence" };
    for (String name: names)
    {
      try
      {
        Method m = fastqRecordClass.getDeclaredMethod(name, EMPTY_ARGSLIST);
        if (m.getReturnType() != String.class)
        deduct(Category.FastqRecord, name + "() does not return String", 4);
      }
      catch (NoSuchMethodException x)
      {
        deduct(Category.FastqRecord, "No " + name + "() method", 5);
      }
    }

    // Does class have instance vars defline, sequence, and quality?
    String[] expectedFields = { "defline", "sequence", "quality" };
    for (String s: expectedFields)
    {
      try
      {
        fastqRecordClass.getDeclaredField(s);
      }
      catch (NoSuchFieldException x)
      {
        deduct(Category.FastqRecord, "No field " + s, 4);
      }
    }

    // Does class have (String, String, String) ctor that throws FastqException?
    Constructor<?> ctor = null;
    try
    {
      ctor = fastqRecordClass.getConstructor(STRING_3_ARGSLIST);
      Class<?>[] exceptionTypes = ctor.getExceptionTypes();
      switch (exceptionTypes.length)
      {
        case 0:
        deduct(Category.FastqRecord, "FastqRecord ctor should throw checked type FastqException", 5);
        break;
        case 1:
        if (!exceptionTypes[0].getName().endsWith("FastqException"))
        deduct(Category.FastqRecord, "FastqRecord ctor should throw checked type FastqException", 5);
        break;
        default:
        err = "FastqRecord ctor throws multipl exception types, should only throw FastqException";
        deduct(Category.FastqRecord, err, 5);
        break;
      }
    }
    catch (NoSuchMethodException x)
    {
      deduct(Category.FastqRecord, "No (String,String,String) constructor", 5);
    }

    // Check ctor.
    FastqRecord rec1AX = null;
    try
    {
      rec1AX = new FastqRecord("@Rec1", "AAAA", "XXXX");
    }
    catch (FastqException x)
    {
      deduct(Category.FastqRecord, "FastqRecord ctor threw exception on valid input (@Rec1, AAAA, XXXX)", 3);
    }
    try
    {
      new FastqRecord(">Rec1", "AAAA", "XXXX");
      deduct(Category.FastqRecord, "FastqRecord ctor did not throw exception on invalid input (>Rec1, AAAA, XXXX)", 3);
    }
    catch (FastqException x)
    {
      // Should get here.
    }

    // Check equals and compareTo.
    FastqRecord rec2AX = null;
    FastqRecord rec1CX = null;
    FastqRecord rec1AY = null;
    FastqRecord rec2TY = null;
    FastqRecord anotherRec1AX = null;
    FastqRecord hiQualRec = null;
    try
    {
      rec2AX = new FastqRecord("@Rec2", "AAAA", "XXXX");
      rec1CX = new FastqRecord("@Rec1", "CCCC", "XXXX");
      rec1AY = new FastqRecord("@Rec1", "AAAA", "YYYY");
      rec2TY = new FastqRecord("@Rec2", "TTTT", "YYYY");
      anotherRec1AX = new FastqRecord("@Rec1", "AAAA", "XXXX");
      hiQualRec = new FastqRecord("@RecN", "ACGT", "!!!!");
    }
    catch (FastqException x) { }
    if (rec1AX == null  ||  rec2AX == null  ||  rec1CX == null  ||  rec1AY == null  ||  rec2TY == null)
    {
      err = "FastqRecord ctor is incorrect, can't create test instances to test"
      + "equals() or compareTo()";
      deduct(Category.FastqRecord, err, 6);
    }
    else
    {
      if (rec1AX.equals(rec2AX))
      {
        err = "equals returned true for (@Rec1, AAAA, XXXX) : (@Rec2, AAAA, XXXX)";
        deduct(Category.FastqRecord, err, 2);
      }
      if (rec1AX.equals(rec1CX))
      {
        err = "equals returned true for (@Rec1, AAAA, XXXX) : (@Rec1, CCCC, XXXX)";
        deduct(Category.FastqRecord, err, 2);
      }
      if (rec1AX.equals(rec1AY))
      {
        err = "equals returned true for (@Rec1, AAAA, XXXX) : (@Rec1, AAAA, YYYY)";
        deduct(Category.FastqRecord, err, 2);
      }
      if (rec1AX.equals(rec2TY))
      {
        err = "equals returned true for (@Rec1, AAAA, XXXX) : (@Rec2, TTTT, YYYY)";
        deduct(Category.FastqRecord, err, 2);
      }
      if (!rec1AX.equals(anotherRec1AX))
      {
        err = "equals returned false for 2 deeply equal instances (@Rec1, AAAA, XXXX)";
        deduct(Category.FastqRecord, err, 2);
      }
      if (rec1AX.compareTo(rec2AX) >= 0)
      {
        err = "compareTo returned >= for (@Rec1, AAAA, XXXX) : (@Rec2, AAAA, XXXX)";
        deduct(Category.FastqRecord, err, 2);
      }
      if (rec1AX.compareTo(rec1CX) >= 0)
      {
        err = "compareTo returned >= for (@Rec1, AAAA, XXXX) : (@Rec1, CCCC, XXXX)";
        deduct(Category.FastqRecord, err, 2);
      }
    }

    // Check quality is high.
    if (!hiQualRec.qualityIsHigh())
    deduct(Category.FastqRecord, "qualityIsHigh is false for (@RecN, ACGT, !!!!)", 2);
    if (rec1AX.qualityIsHigh())
    deduct(Category.FastqRecord, "qualityIsHigh is false for (@Rec1, AAAA, XXXX)", 2);
    Class<?>[] inners = fastqRecordClass.getDeclaredClasses();
    boolean qualityCheckerIsInner = false;
    for (Class<?> c: inners)
    {
      if (c.getName().endsWith("FastqRecord$QualityChecker"))
      {
        qualityCheckerIsInner = true;
        break;
      }
    }
    if (!qualityCheckerIsInner)
    deduct(Category.FastqRecord, "No QualityChecker inner class", 3);

    // Check hash code.
    int expected = "@Rec1".hashCode() + "AAAA".hashCode() + "XXXX".hashCode();
    if (rec1AX.hashCode() != expected)
    deduct(Category.FastqRecord, "hashCode() is not exactly as specified", 3);
  }


  public void gradeFastaRecord()
  {
    // Does class exist?
    Class<?> clazz = getClass("dna.FastaRecord");
    if (clazz == null)
    {
      deductMax(Category.FastaRecord, "No dna.FastaRecord class");
      return;
    }

    // Does class declare it implements DNARecord?
    boolean implementsDNARecord = false;
    for (Class<?> c: clazz.getInterfaces())
    {
      if (c.getName().equals("dna.DNARecord"))
      {
        implementsDNARecord = true;
        break;
      }
    }
    if (!implementsDNARecord)
    {
      deduct(Category.FastaRecord, "DNARecord doesn't declare that it implements DNARecord.", 2);
    }

    // Does class define getDefline() and getSequence()?
    String[] names = { "getDefline", "getSequence" };
    for (String name: names)
    {
      try
      {
        Method m = clazz.getDeclaredMethod(name, EMPTY_ARGSLIST);
        if (m.getReturnType() != String.class)
        deduct(Category.FastaRecord, name + "() does not return String", 4);
      }
      catch (NoSuchMethodException x)
      {
        deduct(Category.FastaRecord, "No " + name + "() method", 5);
      }
    }

    // Check (String, String) and (FastqRecord) ctors.
    try
    {
      clazz.getConstructor(STRING_2_ARGSLIST);
    }
    catch (NoSuchMethodException x)
    {
      deduct(Category.FastaRecord, "No FastaRecord(String, String) constructor", 5);
    }
    try
    {
      clazz.getConstructor(new Class<?>[] { fastqRecordClass });
    }
    catch (NoSuchMethodException x)
    {
      deduct(Category.FastaRecord, "No FastaRecord(FastqRecord) constructor", 5);
    }
  }


  private void gradeFastqReader()
  {
    // Just make sure the class exists and has the right methods.
    Class<?> clazz = getClass("dna.FastqReader");
    if (clazz == null)
    {
      deductMax(Category.FastqReader, "No dna.FastqReader class");
      return;
    }

    // Check for (BufferedReader) ctor
    try
    {
      clazz.getConstructor(BR_ARGSLIST);
    }
    catch (NoSuchMethodException x)
    {
      deduct(Category.FastqReader, "No FastqReader(BufferedReader) constructor", 5);
    }

    // Check for readRecord() method.
    try
    {
      Method readRecordMethod = clazz.getDeclaredMethod("readRecord",EMPTY_ARGSLIST);
      boolean throwsIOException = false;
      boolean throwsFastqException = false;
      for (Class<?> xtype: readRecordMethod.getExceptionTypes())
      {
        if (xtype == java.io.IOException.class)
        throwsIOException = true;
        else if (xtype == dna.FastqException.class)
        throwsFastqException = true;
      }
      if (!throwsIOException)
      deduct(Category.FastqReader, "readRecord() doesn't throw IOException", 3);
      if (!throwsFastqException)
      deduct(Category.FastqReader, "readRecord() doesn't throw FastqException", 3);

    }
    catch (NoSuchMethodException x)
    {
      deduct(Category.FastqReader, "No readRecord() method", 5);
    }
  }


  private void gradeFastaWriter()
  {
    // Just make sure the class exists and has the right methods.
    Class<?> clazz = getClass("dna.FastaWriter");
    if (clazz == null)
    {
      deductMax(Category.FastaWriter, "No dna.FastaWriter class");
      return;
    }

    // Check for (PrintWriter) ctor
    try
    {
      clazz.getConstructor(PW_ARGSLIST);
    }
    catch (NoSuchMethodException x)
    {
      deduct(Category.FastaWriter, "No FastaWriter(PrintWriter) constructor", 5);
    }

    // Check for writeRecord()
    Class<?>[] argtype = new Class<?>[] { FastaRecord.class };
    try
    {
      clazz.getDeclaredMethod("writeRecord", argtype);
    }
    catch (NoSuchMethodException x)
    {
      deduct(Category.FastaWriter, "No writeRecord(FastaRecord) method", 5);
    }
  }


  private void gradeConverter()
  {
    // Convert.
    File fasta = null;
    try
    {
      File ifile = new File("data/HW3.fastq");
      if (!ifile.exists())
      ifile = new File("HW3.fastq");
      if (!ifile.exists())
      {
        sop("Can't find HW3.fastq. Grader, please put it in . or ./data");
        System.exit(1);
      }
      File parent = ifile.getParentFile();
      fasta = new File(parent, "HW3.fasta");
      FileConverter converter = new FileConverter(ifile, fasta);
      converter.convert();
    }
    catch (Exception x)
    {
      if (x instanceof IOException)
      {
        sop("IO Exception while converting ... shouldn't happen");
        sop(x.getMessage());
        x.printStackTrace();
        System.out.println(x);
      }
      else
      {
        String err = "convert() throws unexpected " + x.getClass().getName() + " exception";
        deduct(Category.FileConverter, err, 10);
      }
    }

    // Check output. All records should be fasta (2 lines), with no duplicates. Don't check
    // anything else.
    Set<String> observedDeflines = new HashSet<>();
    try
    (
    FileReader fr = new FileReader(fasta);
    BufferedReader br = new BufferedReader(fr);
    )
    {
      String defline;
      while ((defline = br.readLine()) != null)
      {
        if (defline == null  ||  defline.trim().isEmpty())
        break;
        br.readLine();
        if (observedDeflines.contains(defline))
        {
          String err = "Duplicate defline in fasta file:\n" + defline;
          deduct(Category.FileConverter, err, 10);
          break;
        }
        observedDeflines.add(defline);
      }
    }
    catch (IOException x)
    {
      sop("Trouble reading fasta file, please run grader again.");
      sop(x.getMessage());
      x.printStackTrace();
      System.exit(2);
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
    new DNAGrader().grade();
  }
}
