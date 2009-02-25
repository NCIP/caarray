package gov.nih.nci.carpla.rplatab.sradf.test;

import gov.nih.nci.carpla.rplatab.sradf.SradfHeader;
import gov.nih.nci.carpla.rplatab.sradf.javacc.generated.RplaDatasetSradfGrammar;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.Vector;

public class ArrayDataSectionHeaderParserTester {

	static void print ( Vector<gov.nih.nci.carpla.rplatab.sradf.SradfHeader> headers)
	{

		for (int ii = 0; ii < headers.size(); ii++) {

			printHeaders(headers.get(ii), true, 0);
		}

	}

	static void printHeaders ( SradfHeader header, boolean top, int level) {

		if (top == false) {
			level++;
			for (int tt = 0; tt < level; tt++) {
				System.out.print("\t");
			}

		}

		System.out.print(header.getValue() + "\t" );
		if ( header.getTerm() != null){
			System.out.print( header.getTerm() + "\t");
		}
		System.out.println(header.getCol() );
		
		for (int ii = 0; ii < header.getSubHeaders().size(); ii++) {

			printHeaders(header.getSubHeaders().get(ii), false,level);
		}

	}

	static public void main ( String[] args) {

		int filecount = 0;
		int exception_count = 0;
		try {
			String base = "/home/gvf/workspace/reviewjavacc/testfiles/header_testfiles/arraydatasection/" + args[0];
			FilenameFilter filter = new FilenameFilter() {
				public boolean accept ( File dir, String name) {
					return !name.startsWith(".");
				}
			};

			File basedir = new File(base);
			System.out.println("Looking in " + basedir.getAbsolutePath());
			File[] filearray = basedir.listFiles(filter);
			
			
			filecount = filearray.length;
			
			
		
			
			
			for (int ii = 0; ii < filearray.length; ii++) {

				try {

					File file = filearray[ii];

					System.out
							.println("\n\n\n################################################################\n################################################################");

					System.out.println(file.getAbsolutePath());

					BufferedReader in = new BufferedReader(new FileReader(file
							.getAbsolutePath()));
					String str = in.readLine() + "\n";
					System.out.println(str);

					java.io.StringReader sr = new java.io.StringReader(str);
					java.io.Reader r = new java.io.BufferedReader(sr);

					RplaDatasetSradfGrammar parser = new RplaDatasetSradfGrammar(
							r);

					Vector<gov.nih.nci.carpla.rplatab.sradf.SradfHeader> sampleHeaders = parser
							.parseArrayDataSectionHeaders();

					print(sampleHeaders);

				} catch (Exception e) {
					exception_count++;
					System.out.flush();
					e.printStackTrace();
				}

				System.out
						.println("\n################################################################\n################################################################");

			}
			System.out.println("number of files:" + filecount + "\t"
					+ "number of invalid files:" + exception_count);

		} catch (Exception e) {
			System.out.flush();
			e.printStackTrace();
		}

	}

}
