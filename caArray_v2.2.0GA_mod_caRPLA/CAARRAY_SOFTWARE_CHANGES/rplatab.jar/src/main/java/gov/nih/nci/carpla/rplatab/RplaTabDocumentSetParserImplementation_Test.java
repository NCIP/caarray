//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab;

import gov.nih.nci.caarray.validation.InvalidDataException;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;
import gov.nih.nci.carpla.rplatab.RplaConstants.SradfSectionType;
import gov.nih.nci.carpla.rplatab.files.RplaIdfFile;
import gov.nih.nci.carpla.rplatab.files.RplaTabDatasetFileException;

import gov.nih.nci.carpla.rplatab.model.SectionPrincipal;
import gov.nih.nci.carpla.rplatab.sradf.SradfHeader;
import gov.nih.nci.carpla.rplatab.sradf.SradfHeaders;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.SortedMap;
import java.util.Vector;
import java.util.Map.Entry;

public class RplaTabDocumentSetParserImplementation_Test {

	// ##############################################################
	static public void main ( String[] args) {

		ArrayList<String> list = new ArrayList<String>();
		// list.add("../rpla-test-data/data/valid/mda_rpladaset");
		// list.add("../rpla-test-data/data/valid/valid_with_expfactors");
		// list.add("../rpla-test-data/data/valid/multi_samples_per_row");
		// list.add("../rpla-test-data/data/invalid/invalid_headers/bad_sradf_samples_section_headers/bad_1");
		// list.add("../rpla-test-data/data/invalid/invalid_headers/bad_sradf_samples_section_headers/bad_2");
		// list.add("../rpla-test-data/data/invalid/invalid_data/nonemptyattributesforemptyfield");
		// list.add("../rpla-test-data/data/invalid/invalid_headers/incorrectly_referenced_factor_values");
		// list.add("../rpla-test-data/data/invalid/missing_files/missing_referenced_files");
		list.add("../caRPLA_caArrayV2.2.0/RPLATAB_TEST_DATA/invalid/invalid_headers/bad_sradf_samples_section_headers/bad_1");
		for (String dirpath : list) {
			new RplaTabDocumentSetParserImplementation_Test().parse(dirpath,
																	System.out);
		}

	}

	// ##############################################################
	public void parse ( String dirpath, PrintStream out) {
		RplaTabInputFileSet rfset = new RplaTabInputFileSet();
		RplaTabDocumentSet rdataset = null;

		try {

			rfset.addAllFilesInDir(dirpath);

		} catch (RplaTabDatasetFileException re) {
			re.printStackTrace();

		}
		try {
			rdataset = RplaTabDocumentSetParser.INSTANCE.parse(rfset);
		} catch (InvalidDataException re) {
			re.printStackTrace();

		}
		if (rdataset.getValidationResult().getMessages(Type.ERROR).size() != 0) {
			printValidationMessages(rdataset, out);
			return;
		}

		out.println("\n###################START#################################################");
		out.println(dirpath);
		out.println("\n");
		out.println("");
		printHeaders(rdataset, out);
		out.println("\n**********************************************************************");
		out.println("\nSAMPLES SECTION ROWS");
		printRows(rdataset, SradfSectionType.Samples, out);
		out.println("\nARRAY SECTION ROWS");
		printRows(rdataset, SradfSectionType.Array, out);
		out.println("\n");
		out.println("\nARRAY DATA SECTION ROWS");
		printRows(rdataset, SradfSectionType.ArrayData, out);
		out.println("\n**********************************************************************");
		printValidationMessages(rdataset, out);
		out.println("\n#####################END###############################################");
	}

	// ##############################################################
	public static void printHeaders ( RplaTabDocumentSet rset, PrintStream out)
	{

		List<SradfHeader> sh = rset	.getSradfHeaders()
									.getSamplesSectionHeaders()
									.getHeaders();
		out.println("SAMPLES SECTION HEADERS\n");
		printHeaderDetails(sh, out);
		out.println("\n");
		sh = rset.getSradfHeaders().getArraySectionHeaders().getHeaders();
		out.println("ARRAY SECTION HEADERS\n");
		printHeaderDetails(sh, out);
		out.println("\n");

		sh = rset.getSradfHeaders().getArrayDataSectionHeaders().getHeaders();
		out.println("ARRAY DATA SECTION HEADERS\n");
		printHeaderDetails(sh, out);
		out.println("\n");

	}

	// ##############################################################
	static void printHeaderDetails (	List<gov.nih.nci.carpla.rplatab.sradf.SradfHeader> headers,
										PrintStream out)
	{

		for (int ii = 0; ii < headers.size(); ii++) {
			printHeaderDetail(headers.get(ii), true, 0, out);
		}

	}

	// ##############################################################
	static void printHeaderDetail ( SradfHeader header,
									boolean top,
									int level,
									PrintStream out)
	{

		if (top == false) {
			level++;
			for (int tt = 0; tt < level; tt++) {
				System.out.print("\t");
			}

		}

		out.print(header.getValue() + ":");
		if (header.getTerm() != null) {
			out.print("(" + header.getTerm() + ")");
		}
		out.println(" " + header.getCol());

		for (int ii = 0; ii < header.getSubHeaders().size(); ii++) {

			printHeaderDetail(header.getSubHeaders().get(ii), false, level, out);
		}

	}

	// ##############################################################
	private static void printValidationMessages (	RplaTabDocumentSet rdataset,
													PrintStream out)
	{
		out.println("\n\nVALIDATION MESSAGES:");
		if (rdataset == null) {
			out.println("VALIDATION MESSAGES END");
			return;
		}
		List<ValidationMessage> lm = rdataset	.getValidationResult()
												.getMessages();

		ListIterator<ValidationMessage> li = rdataset	.getValidationResult()
														.getMessages()
														.listIterator();

		boolean hasError = false;

		out.println("Number of validation messages:" + lm.size());
		while (li.hasNext()) {
			ValidationMessage mess = li.next();
			out.println(mess.toString());
			if (mess.getType() == Type.ERROR) {
				hasError = true;
			}
		}
		out.println("Set has ERROR messages:" + hasError);
		out.println("VALIDATION MESSAGES END");

		out.println("\n");

	}

	// ##############################################################
	public void parse ( String dirpath) {
		parse(dirpath, System.out);

	}

	// ##############################################################
	private void printRows (	RplaTabDocumentSet rdataset,
								SradfSectionType sectionType,
								PrintStream out)
	{

		SortedMap<Integer, List<SectionPrincipal>> rows = rdataset.getSectionRowsPrincipalObjects(sectionType);

		Iterator<Entry<Integer, List<SectionPrincipal>>> itie = rows.entrySet()
																	.iterator();

		while (itie.hasNext()) {

			Entry<Integer, List<SectionPrincipal>> entry = itie.next();
			List<SectionPrincipal> principals = entry.getValue();
			out.print("row " + entry.getKey() + ":");
			for (SectionPrincipal sp : principals) {
				out.print(sp.toString() + "\t");
			}

			out.println();
		}

	}
}
