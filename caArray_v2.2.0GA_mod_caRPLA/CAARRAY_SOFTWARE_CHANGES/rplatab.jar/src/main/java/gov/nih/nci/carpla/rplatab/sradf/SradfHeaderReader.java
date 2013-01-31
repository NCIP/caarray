//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab.sradf;

import gov.nih.nci.caarray.validation.ValidationMessage.Type;
import gov.nih.nci.carpla.rplatab.RplaConstants;
import gov.nih.nci.carpla.rplatab.RplaTabDocumentSet;
import gov.nih.nci.carpla.rplatab.RplaTabParsingException;

import gov.nih.nci.carpla.rplatab.RplaConstants.SradfSectionType;

import gov.nih.nci.carpla.rplatab.files.SradfFile;

import gov.nih.nci.carpla.rplatab.sradf.javacc.generated.ParseException;
import gov.nih.nci.carpla.rplatab.sradf.javacc.generated.RplaDatasetSradfGrammar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Vector;

public class SradfHeaderReader {

	public static SradfHeaders readSradfHeaders (	RplaTabDocumentSet dataset,
													SradfFile sradfFile)
																		throws RplaTabParsingException
	{

		try {

			// --
			java.io.Reader r = getHeaderReader(	sradfFile,
												RplaConstants.SradfSectionType.Samples);

			RplaDatasetSradfGrammar parser = new RplaDatasetSradfGrammar(r);

			SradfSectionHeaders samplessectionheaders = parser.parseSamplesSectionheaders();

			samplessectionheaders.setSectionType(RplaConstants.SradfSectionType.Samples);
			// checkForMultipleTabs(samplessectionheaders);

			r.close();

			r = getHeaderReader(sradfFile, RplaConstants.SradfSectionType.Array);

			parser.ReInit(r);
			SradfSectionHeaders arraylocationssectionheaders = parser.parseArraySectionHeaders();

			// checkForMultipleTabs(arraylocationssectionheaders);

			arraylocationssectionheaders.setSectionType(RplaConstants.SradfSectionType.Array);

			r.close();

			// --
			r = getHeaderReader(sradfFile,
								RplaConstants.SradfSectionType.ArrayData);

			parser.ReInit(r);
			SradfSectionHeaders arraydatasectionheaders = parser.parseArrayDataSectionHeaders();

			// checkForMultipleTabs(arraydatasectionheaders);
			arraydatasectionheaders.setSectionType(RplaConstants.SradfSectionType.ArrayData);

			r.close();
			r = null;

			// =====================================================================

			// =====================================================================
			SradfHeaders sfi = new SradfHeaders();

			sfi.setSectionHeaders(	samplessectionheaders,
									arraylocationssectionheaders,
									arraydatasectionheaders);

			// sfi.setSradfFileHolder(sradffileholder);

			dataset.setSradfHeaders(sfi);

			return sfi;

		} catch (ParseException pe) {
			// System.out.println(pe.getMessage());
			dataset.getValidationResult().addMessage(	dataset	.getSradfFile()
																.getFile(),
														Type.ERROR,
														pe.getMessage());
			throw new RplaTabParsingException(pe.getMessage());
		} catch (IOException ioe) {
			ioe.printStackTrace();
			// add to ValidationResult
			dataset.getValidationResult().addMessage(	dataset	.getSradfFile()
																.getFile(),
														Type.ERROR,
														ioe.getMessage());
			throw new RplaTabParsingException(ioe.getMessage());

		}

	}

	private static Reader getHeaderReader ( SradfFile sradffile,
											SradfSectionType section)
	{

		try {
			if (section == SradfSectionType.Samples) {
				String str = null;
				BufferedReader in = new BufferedReader(new FileReader(sradffile	.getFile()
																				.getAbsolutePath()));

				while ((str = in.readLine()).startsWith("#"))
					;
				;// + "\n";

				java.io.StringReader sr = new java.io.StringReader(str);
				java.io.Reader r = new java.io.BufferedReader(sr);
				return r;
			}

			else if (section == SradfSectionType.Array) {
				String str = null;
				BufferedReader in = new BufferedReader(new FileReader(sradffile	.getFile()
																				.getAbsolutePath()));
				while (!(str = in.readLine()).startsWith(RplaConstants.SradfSamplesSectionEnd)) {

				}
				while ((str = in.readLine()).startsWith("#"))
					;
				// str = in.readLine();// + "\n";

				java.io.StringReader sr = new java.io.StringReader(str);
				java.io.Reader r = new java.io.BufferedReader(sr);
				return r;
			} else if (section == SradfSectionType.ArrayData) {
				String str = null;
				BufferedReader in = new BufferedReader(new FileReader(sradffile	.getFile()
																				.getAbsolutePath()));
				while (!(str = in.readLine()).startsWith(RplaConstants.SradfArraySectionEnd)) {

				}
				while ((str = in.readLine()).startsWith("#"))
					;
				// str = in.readLine();// + "\n";

				java.io.StringReader sr = new java.io.StringReader(str);
				java.io.Reader r = new java.io.BufferedReader(sr);
				return r;
			}

		} catch (Exception e) {
			e.printStackTrace();

		}

		// TODO proper exception handling
		return null;
	}

}
