//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab;

import gov.nih.nci.carpla.rplatab.files.ArrayDataFile;
import gov.nih.nci.carpla.rplatab.files.ImageFile;
import gov.nih.nci.carpla.rplatab.files.RplaIdfFile;
import gov.nih.nci.carpla.rplatab.files.RplaTabDatasetFileException;
import gov.nih.nci.carpla.rplatab.files.SradfFile;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

public class RplaTabInputFileSet {

	private RplaIdfFile			_rplaIdfFile;

	private SradfFile			_sradfFile;

	private Set<ImageFile>		_imagefiles		= new HashSet<ImageFile>();
	private Set<ArrayDataFile>	_arraydatafiles	= new HashSet<ArrayDataFile>();

	private Set<File>			_miscfiles		= new HashSet<File>();

	public void addFile ( File file) throws RplaTabDatasetFileException {
		String extension = FilenameUtils.getExtension(file.getName());
		if (extension.toLowerCase().compareTo("rplaidf") == 0) {
			RplaIdfFile ridffile = new RplaIdfFile();
			try {
				ridffile.setFile(file);
				this.setRplaIdf(ridffile);
			} catch (RplaTabDatasetFileException re) {
				throw re;
			}
		}

		else if (extension.toLowerCase().compareTo("sradf") == 0) {

			SradfFile sfile = new SradfFile();
			try {
				sfile.setFile(file);
				this.setSradfFile(sfile);
			} catch (RplaTabDatasetFileException re) {

				throw re;
			}
			this.setSradfFile(sfile);
		}

		else {
			// should perform checks
			_miscfiles.add(file);

		}

	}

	public Set<File> getMiscFiles () {

		return _miscfiles;
	}

	public RplaIdfFile getRplaIdfFile () {
		return _rplaIdfFile;
	}

	public SradfFile getSradfFile () {
		return _sradfFile;
	}

	public void setRplaIdf ( RplaIdfFile file) {

		_rplaIdfFile = file;
	}

	public void setSradfFile ( SradfFile sradffile)
													throws RplaTabDatasetFileException
	{

		_sradfFile = sradffile;
	}

	public void addAllFilesInDir ( String string)
													throws RplaTabDatasetFileException
	{
		File dir = new File(string);
		File[] files = dir.listFiles();
		for (File file : files) {
			try {
				this.addFile(file);
			} catch (RplaTabDatasetFileException re) {
				throw re;
			}
		}

	}

}
