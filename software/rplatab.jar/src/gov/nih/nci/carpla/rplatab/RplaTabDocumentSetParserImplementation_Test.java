package gov.nih.nci.carpla.rplatab;

import gov.nih.nci.caarray.validation.InvalidDataException;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.carpla.rplatab.files.RplaIdfFile;
import gov.nih.nci.carpla.rplatab.files.RplaTabDatasetFileException;

import gov.nih.nci.carpla.rplatab.sradf.SradfHeaders;

import java.io.File;
import java.util.List;
import java.util.ListIterator;

public class RplaTabDocumentSetParserImplementation_Test {
	static public void main ( String[] args) {
		RplaTabInputFileSet rfset = new RplaTabInputFileSet();
		RplaTabDocumentSet rdataset = null;
		try {

			rfset
					.addAllFilesInDir("/home/gvf/workspace/CARPLA-DOCS/comprehensive/REAL_RPLATAB_DATASET/Dataset1/");

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

		List<ValidationMessage> lm = rdataset
				.getValidationResult()
				.getMessages();

		ListIterator<ValidationMessage> li = rdataset
				.getValidationResult()
				.getMessages()
				.listIterator();
		while (li.hasNext()) {
			ValidationMessage mess = li.next();
			System.out.println(mess.toString());
		}

	}
}
