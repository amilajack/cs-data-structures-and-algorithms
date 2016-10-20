package dna;


public class FastaRecord implements Comparable<FastaRecord>, DNARecord {
  private String			defline;
  private String			sequence;

  //
  // Add a precondition check: throw FastqException if the 1st char of the defline is
  // not '>'. You will have to change the ctor declaration to say that it throws
  // the exception. The exception should contain a useful informative message.
  //
  public FastaRecord(String defline, String sequence) throws FastqException {
    this.defline = defline;
    this.sequence = sequence;

    if (this.defline.charAt(0) != '>') {
      throw new FastqException(
        "Bad 1st char in defline in fastq record: saw X, expected @"
      );
    }
  }


  // Initialize defline and sequence from the input record. For the defline, convert the
  // 1st char ('@') to '>'.
  public FastaRecord(FastqRecord fastqRec) {
    this.defline = ">" + fastqRec.getDefline().substring(1);
    this.sequence = fastqRec.getSequence();
  }

  // Convert defline to string
  public String toString() {
    return defline + "\n" + sequence + "\n";
  }

  @Override
  public String getDefline() {
    return this.defline;
  }

  @Override
  public String getSequence() {
    return this.sequence;
  }

  @Override
  public int compareTo(FastaRecord o) {
    int deflineComparison = this.defline.compareTo(o.getDefline());

    switch(deflineComparison) {
      case 0: {
        return this.sequence.compareTo(o.getSequence());
      }
      default: {
        return deflineComparison;
      }
    }
  }

  //
  // Provide a hashCode() method that returns the sum of the hashcodes of
  // defline and sequence.
  //
  public int hashCode() {
    return (
    this.defline.hashCode() +
    this.sequence.hashCode()
    );
  }
}
