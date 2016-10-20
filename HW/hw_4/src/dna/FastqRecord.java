package dna;

//
// FastqRecord contains the defline, sequence, and quality string
// from a record in a fastq file.
//

public class FastqRecord implements Comparable<FastqRecord>, DNARecord
{
	private String			defline;
	private String			sequence;
	private String			quality;


	//
	// Add a precondition check: throw FastqException if the 1st char of the defline is
	// not '@'. You will have to change the ctor declaration to say that it throws
	// the exception. The exception should contain a useful informative message.
	//
	public FastqRecord(String defline, String sequence, String quality) throws FastqException
	{
		this.defline = defline;
		this.sequence = sequence;
		this.quality = quality;

		if (this.defline.charAt(0) != '@') {
			throw new FastqException(
			  "Bad 1st char in defline in fastq record: saw X, expected @"
			);
		}
	}

	//
	// Provide a hashCode() method that returns the sum of the hashcodes of
	// defline, sequence, and quality.
	//
	public int hashCode() {
		return (
  		this.defline.hashCode() +
  		this.quality.hashCode() +
  		this.sequence.hashCode()
		);
	}

	//
	// Complete this. Return true if the first char of quality is '!'.
	//
	public boolean qualityIsHigh()
	{
		return this.quality.charAt(0) == '!';
	}

	public String toString()
	{
		return defline + "\n" + sequence + "\n+\n" + quality + "\n";
	}

	@Override
	public String getDefline() {
		return this.defline;
	}

	@Override
	public boolean equals(Object o) {
		FastqRecord f = (FastqRecord)o;
		return f.compareTo(this) == 0;
	}

	@Override
	public String getSequence() {
		return this.sequence;
	}

	@Override
	public int compareTo(FastqRecord o) {
		int deflineComparison = this.defline.compareTo(o.defline);

		switch(deflineComparison) {
			case 0: {
				int sequenceComparison = this.sequence.compareTo(o.sequence);

				switch(sequenceComparison) {
					case 0: {
						return this.quality.compareTo(o.quality);
					}
					default: {
						return sequenceComparison;
					}
				}
			}
			default: {
				return deflineComparison;
			}
		}
	}
}
