package gov.nih.nci.carpla.rplatab;

import gov.nih.nci.caarray.validation.InvalidDataException;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;
import gov.nih.nci.carpla.rplatab.files.RplaIdfFile;
import gov.nih.nci.carpla.rplatab.files.RplaTabDatasetFileException;

import gov.nih.nci.carpla.rplatab.sradf.SradfHeader;
import gov.nih.nci.carpla.rplatab.sradf.SradfHeaders;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class RplaTabDocumentSetParserImplementation_Test {

	// ##############################################################
	static public void main ( String[] args) {

		ArrayList<String> list = new ArrayList<String>();
		list.add("../rpla-test-data/data/mda_rpladaset");
		list.add("../rpla-test-data/data/invalid/missing_files/missing_referenced_files");
		list.add("../rpla-test-data/data/invalid/invalid_headers/bad_sradf_samples_section_headers/bad_1");
		list.add("../rpla-test-data/data/invalid/invalid_headers/bad_sradf_samples_section_headers/bad_2");
		list.add("../rpla-test-data/data/invalid/invalid_data/nonemptyattributesforemptyfield");
		for (String dirpath : list) {
			new RplaTabDocumentSetParserImplementation_Test().parse(dirpath,
																	System.out);
		}

	}

	// ##############################################################
	public static void printHeaders ( RplaTabDocumentSet rset, PrintStream out)
	{

		List<SradfHeader> sh = rset.getSradfHeaders()
				.getSamplesSectionHeaders()
				.getHeaders();

		for (int ii = 0; ii < sh.size(); ii++) {

			out.print(sh.get(ii).getValue() + "," + sh.get(ii).getCol() + "\t");
		}
		out.println();
		sh = rset.getSradfHeaders().getArraySectionHeaders().getHeaders();

		for (int ii = 0; ii < sh.size(); ii++) {

			out.print(sh.get(ii).getValue() + "," + sh.get(ii).getCol() + "\t");
		}
		out.println();
		sh = rset.getSradfHeaders().getArrayDataSectionHeaders().getHeaders();

		for (int ii = 0; ii < sh.size(); ii++) {

			out.print(sh.get(ii).getValue() + "," + sh.get(ii).getCol() + "\t");
		}

	}

	// ##############################################################
	private static void printValidationMessages (	RplaTabDocumentSet rdataset,
													PrintStream out)
	{
		// TODO Auto-generated method stub
		List<ValidationMessage> lm = rdataset.getValidationResult()
				.getMessages();

		ListIterator<ValidationMessage> li = rdataset.getValidationResult()
				.getMessages()
				.listIterator();

		boolean hasError = false;
		out.println("\n**********************************************************************");
		out.println("**********************************************************************");
		out.println(rdataset.getRplaIdfFile().getFile().getAbsolutePath());
		out.println(rdataset.getSradfFile().getFile().getAbsolutePath());
		out.println("ValidationMessages BEGIN");
		out.println("Number of validation messages:" + lm.size());
		while (li.hasNext()) {
			ValidationMessage mess = li.next();
			out.println(mess.toString());
			if (mess.getType() == Type.ERROR) {
				hasError = true;
			}
		}
		out.println("Set has ERROR messages:" + hasError);
		out.println("ValidationMessages END");

		if (!hasError) {
			out.println("Headers were:");
			printHeaders(rdataset, out);

		}
		out.println("\n**********************************************************************");
		out.println("**********************************************************************\n");
	}

	// ##############################################################
	public void parse ( String dirpath) {
		parse(dirpath, System.out);

	}

	// ##############################################################
	public void parse ( String dirpath, PrintStream out) {
		RplaTabInputFileSet rfset = new RplaTabInputFileSet();
		RplaTabDocumentSet rdataset = null;

		try {

			// rfset.addAllFilesInDir("../rpla-test-data/data/mda_rpladaset");
			// rfset.addAllFilesInDir("../rpla-test-data/data/invalid/missing_files/missing_referenced_files");
			// rfset
			// .addAllFilesInDir("../rpla-test-data/data/invalid/invalid_headers/bad_sradf_samples_section_headers/bad_1");
			rfset.addAllFilesInDir(dirpath);
			// rfset
			// .addAllFilesInDir("../rpla-test-data/data/invalid/invalid_headers/bad_sradf_samples_section_headers/bad_2");
		} catch (RplaTabDatasetFileException re) {
			re.printStackTrace();

		}

		try {
			rdataset = RplaTabDocumentSetParser.INSTANCE.parse(rfset);
		} catch (RplaTabParsingException pe) {
			pe.printStackTrace();

		} catch (InvalidDataException ide) {
			ide.printStackTrace();

		}

		printValidationMessages(rdataset, out);

	}
}
