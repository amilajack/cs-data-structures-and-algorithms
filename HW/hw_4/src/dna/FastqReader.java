package dna;

import java.io.*;


//
// Reads lines from a BufferedReader and builds a FastqReader.
//


public class FastqReader
{
  private BufferedReader			theBufferedReader;


  public FastqReader(BufferedReader theBufferedReader) {
    this.theBufferedReader = theBufferedReader;
  }


  // Returns next record in the file, or null if EOF.
  // The first line is a defline.
  // Second line is a sequence
  // Third line is a '+' sign
  // Fourth line is the quality
  public FastqRecord readRecord() throws IOException, FastqException{
    // Read the defline. Return null if you read null, indicating end of file.
    String defline = theBufferedReader.readLine();

    if (defline == null) {
      return null;
    }

    String sequence = theBufferedReader.readLine();
    theBufferedReader.readLine();
    String quality = theBufferedReader.readLine();

    // Read the next 3 lines from the buffered reader. Construct and return
    // a FastqRecord.
    FastqRecord fs = new FastqRecord(
    defline,
    sequence,
    quality
    );

    return fs;
  }
}
