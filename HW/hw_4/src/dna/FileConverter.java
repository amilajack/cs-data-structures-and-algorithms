package dna;

import java.io.*;
import java.util.*;


public class FileConverter
{
	private File			        	fastq;
	private File        				fasta;
	public HashSet<String>      fastaList;


	public FileConverter(File fastq, File fasta) {
		this.fastq = fastq;
		this.fasta = fasta;
		this.fastaList = new HashSet<String>();
	}

	//
	// Writes a fasta file consisting of conversion of all records from the fastq with
	// sufficient quality and unique defline.
	//
	// Use a HashSet<String> to check for unique deflines. When you read a fastq record,
	// check if its defline is in the set. If it's in the set, don't do anything with the
	// record. If the defline isn't in the set, add it to the set, build a fasta record,
	// and write the fasta record using the fasta writer.
	//
	public void convert() throws IOException {
		// Build chain of readers.
		FileReader fr = new FileReader(this.fastq);
		BufferedReader br =  new BufferedReader(fr);
		FastqReader fqr = new FastqReader(br);

		// Build chain of writers.
		FileWriter fw = new FileWriter(this.fasta);
		PrintWriter pw = new PrintWriter(fw);
		FastaWriter faWriter = new FastaWriter(pw);

		boolean isFinished = false;

		while(!isFinished) {
			// Read, translate, write.
			try {
				FastqRecord fqRecord = fqr.readRecord();

				if (fqRecord == null) {
					isFinished = true;
					break;
				}

				FastaRecord faRecord = new FastaRecord(fqRecord);

				System.out.println(fqRecord.getDefline());

				if (fqRecord.qualityIsHigh() == false) {
					continue;
				}

				if (fastaList.contains(fqRecord.getDefline())) {
					continue;
				}

				faWriter.writeRecord(faRecord);
			} catch (FastqException e) {
				// e.printStackTrace();
			}
		}

		// Close fr, br, fw, and pw in reverse order of creation.
		pw.close();
		fw.close();
		br.close();
		fr.close();
	}

	public static void main(String[] args) {
		System.out.println("Starting");

		try {
			File fastq = new File("data/HW4.fastq");
			assert fastq.exists() : "Can't find input file " + fastq.getAbsolutePath();
			File fasta = new File("data/HW4.fasta");
			FileConverter converter = new FileConverter(fastq, fasta);
			converter.convert();
		}
		catch (IOException x) {
			System.out.println(x.getMessage());
		}
		System.out.println("Done");
	}
}
