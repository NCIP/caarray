package gov.nih.nci.caarray.domain.file;

import java.util.Set;

public interface FileTypeRegistry {

    public static final FileType MAGE_TAB_IDF = new FileType("MAGE_TAB_IDF", FileCategory.MAGE_TAB, true, "IDF",
            "IDF.TXT");
    public static final FileType MAGE_TAB_SDRF = new FileType("MAGE_TAB_SDRF", FileCategory.MAGE_TAB, true, "SDRF",
            "SDRF.TXT");

    /**
     * Determine a file's type based on its extension.
     * 
     * @param filename name of file
     * @return the FileType corresponding to the file extension or null if no matching file type
     */
    public abstract FileType getTypeFromExtension(String filename);

    /**
     * 
     * @param name
     * @return
     */
    public abstract FileType getTypeByName(final String name);

    public abstract Set<FileType> getAllTypes();

    public abstract Set<FileType> getArrayDesignTypes();

    public abstract Set<FileType> getParseableArrayDesignTypes();

    public abstract Set<FileType> getRawArrayDataTypes();

    public abstract Set<FileType> getDerivedArrayDataTypes();

    public abstract Set<FileType> getParseableArrayDataTypes();

    public abstract Set<FileType> getMageTabTypes();

}