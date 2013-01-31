//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab.sradf;

public class ReplaceME {

	public static HEADERTYPE getType ( String columnHeaderString) {

		if (columnHeaderString.compareTo("Source Name") == 0) {
			return HEADERTYPE.SOURCENAME;
		} else if (columnHeaderString.compareTo("Sample Name") == 0) {
			return HEADERTYPE.SAMPLENAME;
		} else if (columnHeaderString.compareTo("Protocol REF") == 0) {
			return HEADERTYPE.PROTOCOLREF;
		} else if (columnHeaderString.compareTo("Characteristics") == 0) {
			return HEADERTYPE.CHARACTERISTICS;
		} else if (columnHeaderString.compareTo("Term Source REF") == 0) {
			return HEADERTYPE.TERMSOURCEREF;
		} else if (columnHeaderString.compareTo("Parameter Value") == 0) {
			return HEADERTYPE.PARAMETERVALUE;
		}else if (columnHeaderString.compareTo("Factor Value") == 0) {
			return HEADERTYPE.FACTORVALUE;
		}

		else if (columnHeaderString.compareTo("Sample REF") == 0) {
			return HEADERTYPE.SAMPLEREF;
		} else if (columnHeaderString.compareTo("Block Row") == 0) {
			return HEADERTYPE.BLOCKROW;
		} else if (columnHeaderString.compareTo("Block Column") == 0) {
			return HEADERTYPE.BLOCKCOLUMN;
		}

		else if (columnHeaderString.compareTo("Column") == 0) {
			return HEADERTYPE.COLUMN;
		} else if (columnHeaderString.compareTo("Row") == 0) {
			return HEADERTYPE.ROW;
		} else if (columnHeaderString.compareTo("Array REF") == 0) {
			return HEADERTYPE.ARRAYREF;
		}

		else if (columnHeaderString.compareTo("Dilution REF") == 0) {
			return HEADERTYPE.DILUTIONREF;
		}

		else if (columnHeaderString.compareTo("Antibody REF") == 0) {
			return HEADERTYPE.ANTIBODYREF;
		}

		else if (columnHeaderString.compareTo("Assay Name") == 0) {
			return HEADERTYPE.ASSAYNAME;
		}

		else if (columnHeaderString.compareTo("Technology Type") == 0) {
			return HEADERTYPE.TECHNOLOGYTYPE;
		}

		else if (columnHeaderString.compareTo("Image File") == 0) {
			return HEADERTYPE.IMAGEFILE;
		}

		else if (columnHeaderString.compareTo("Array Data File") == 0) {
			return HEADERTYPE.ARRAYDATAFILE;
		} else if (columnHeaderString.compareTo("Derived Array Data File") == 0) {
			return HEADERTYPE.DERIVEDARRAYDATAFILE;
		}
		else if (columnHeaderString.compareTo("Comment") == 0) {
			return HEADERTYPE.COMMENT;
		}else if (columnHeaderString.compareTo("Unit") == 0) {
			return HEADERTYPE.UNIT;
		}

		else {
			System.out.println("ReplaceME doesnt know" + columnHeaderString);
			return HEADERTYPE.NULL;
		}

	}

}
