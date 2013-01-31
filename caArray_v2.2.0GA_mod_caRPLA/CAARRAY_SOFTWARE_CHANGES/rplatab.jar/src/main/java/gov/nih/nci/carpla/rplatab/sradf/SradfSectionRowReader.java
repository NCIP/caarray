//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab.sradf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Vector;

import gov.nih.nci.carpla.rplatab.RplaConstants;
import gov.nih.nci.carpla.rplatab.RplaConstants.SradfSectionType;

public class SradfSectionRowReader {

	private String[]			values;
	private File				file;
	private char				separator	= '\t';

	BufferedReader				_in;
	private SradfSectionType	_sectionType;
	private String				_endLine;

	// ####################################################################
	public String[] nextRow () {
		String str = null;
		String[] values;
		try {

			if (((str = _in.readLine()) != null) && str.compareTo(_endLine) != 0) {
				//below will take care of extra fluff tabs at end of lines
				return (str.split("\t"));

			} else
				return null;

		} catch (Exception e) {

		}
		return null;
	}

	// ####################################################################
	public SradfSectionRowReader(File file, SradfSectionType sectionType) {
		_sectionType = sectionType;
		try {
			if (sectionType == SradfSectionType.Samples) {

				_endLine = RplaConstants.SradfSamplesSectionEnd;
				String str = null;
				_in = new BufferedReader(new FileReader(file.getAbsolutePath()));

				boolean flag = true;
				while (flag) {
					str = _in.readLine();

					if (str.startsWith("#") == false) {
						flag = false;
					}
				}

			}

			else if (sectionType == SradfSectionType.Array) {
				_endLine = RplaConstants.SradfArraySectionEnd;
				String str = null;
				_in = new BufferedReader(new FileReader(file.getAbsolutePath()));
				while ((str = _in.readLine()).compareTo(RplaConstants.SradfSamplesSectionEnd) != 0) {

				}

				boolean flag = true;
				while (flag) {
					str = _in.readLine();

					if (str.startsWith("#") == false) {
						flag = false;
					}
				}

			} else if (sectionType == SradfSectionType.ArrayData) {

				_endLine = RplaConstants.SradfArrayDataSectionEnd;

				_in = new BufferedReader(new FileReader(file.getAbsolutePath()));
				String str = null;
				while ((str = _in.readLine()).compareTo(RplaConstants.SradfArraySectionEnd) != 0) {

				}
				
				boolean flag = true;
				while (flag) {
					str = _in.readLine();
					
					if (str.startsWith("#") == false) {
						flag = false;
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

}
