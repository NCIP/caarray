package gov.nih.nci.caarray.domain.file;

import java.util.Set;

/**
 * A registry of file types. Types are added to the registry by plugins which contain handlers that know how to parse
 * and extract data from the files.
 * 
 * @author dkokotov
 */
public interface FileTypeRegistry {
    /**
     * The FileType for MAGE-TAB IDF files. These types can be parsed by the core system.
     */
    FileType MAGE_TAB_IDF = new FileType("MAGE_TAB_IDF", FileCategory.MAGE_TAB, true, "IDF", "IDF.TXT");

    /**
     * The FileType for MAGE-TAB SDRF files. These types can be parsed by the core system.
     */
    FileType MAGE_TAB_SDRF = new FileType("MAGE_TAB_SDRF", FileCategory.MAGE_TAB, true, "SDRF", "SDRF.TXT");

    /**
     * Determine a file's type based on its extension.
     * 
     * @param filename name of file
     * @return the FileType corresponding to the file extension or null if no matching file type
     */
    FileType getTypeFromExtension(String filename);

    /**
     * Look up a type based on its name.
     * 
     * @param name the type name to find
     * @return the FileType instance with given name in the registry, or null if none exists
     */
    FileType getTypeByName(final String name);

    /**
     * @return all FileTypes in the registry
     */
    Set<FileType> getAllTypes();

    /**
     * @return all FileTypes with category FileCategory.ARRAY_DESIGN in the registry
     */
    Set<FileType> getArrayDesignTypes();

    /**
     * @return all FileTypes with category FileCategory.ARRAY_DESIGN that are parseable in the registry
     */
    Set<FileType> getParseableArrayDesignTypes();

    /**
     * @return all FileTypes with category FileCategory.RAW_DATA in the registry
     */
    Set<FileType> getRawArrayDataTypes();

    /**
     * @return all FileTypes with category FileCategory.DERIVED_DATA in the registry
     */
    Set<FileType> getDerivedArrayDataTypes();

    /**
     * @return all FileTypes with category FileCategory.RAW_DATA or FileCategory.DERIVED_DATA that are parsable in the
     *         registry
     */
    Set<FileType> getParseableArrayDataTypes();

    /**
     * @return all FileTypes with category FileCategory.MAGE_TAB in the registry
     */
    Set<FileType> getMageTabTypes();
}