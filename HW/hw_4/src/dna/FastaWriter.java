package dna;

import java.io.*;


//
// Writes a fasta record to a print writer.
//


public class FastaWriter {
  private PrintWriter			thePrintWriter;


  public FastaWriter(PrintWriter thePrintWriter) {
    this.thePrintWriter = thePrintWriter;
  }


  // Write the rec as 2 separate lines: first the defline, then the sequence.
  // To write something on a separate line, use the println() method of PrintWriter.
  public void writeRecord(FastaRecord rec) throws IOException {
    this.thePrintWriter.println(">" + rec.getDefline().substring(1));
    this.thePrintWriter.println(rec.getSequence());
  }
}
