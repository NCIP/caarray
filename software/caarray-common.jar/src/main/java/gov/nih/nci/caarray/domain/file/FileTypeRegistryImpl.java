//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.file;

import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;
import gov.nih.nci.caarray.util.CaArrayUtils;

import java.util.Locale;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Default implementation of FileTypeRegistry.
 * 
 * @author dkokotov
 */
@Singleton
public class FileTypeRegistryImpl implements FileTypeRegistry {
    private final Set<FileType> types = Sets.newTreeSet();

    /**
     * Create a FileTypeRegistryImpl with the set of types provided by the given array data and array design file
     * handlers.
     * 
     * @param dataHandlers the known array data handlers, each of which provides types to be added to the registry
     * @param designHandlers the known array design handlers, each of which provides types to be added to the registry
     */
    @Inject
    public FileTypeRegistryImpl(Set<DataFileHandler> dataHandlers, Set<DesignFileHandler> designHandlers) {
        for (final DataFileHandler handler : dataHandlers) {
            this.types.addAll(handler.getSupportedTypes());
        }
        for (final DesignFileHandler handler : designHandlers) {
            this.types.addAll(handler.getSupportedTypes());
        }

        // add the mage-tab types which are currently not platform based
        this.types.add(MAGE_TAB_IDF);
        this.types.add(MAGE_TAB_SDRF);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileType getTypeFromExtension(String filename) {
        Preconditions.checkNotNull(filename);
        final String upperFileName = filename.toUpperCase(Locale.getDefault());
        for (final FileType type : this.types) {
            for (final String ext : type.getExtensions()) {
                if (upperFileName.endsWith("." + ext)) {
                    return type;
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileType getTypeByName(final String name) {
        Preconditions.checkNotNull(name);
        return CaArrayUtils.find(this.types, new Predicate<FileType>() {
            @Override
            public boolean apply(FileType ft) {
                return name.equals(ft.getName());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<FileType> getAllTypes() {
        return this.types;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<FileType> getArrayDesignTypes() {
        return Sets.filter(this.types, new Predicate<FileType>() {
            @Override
            public boolean apply(FileType ft) {
                return ft.isArrayDesign();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<FileType> getParseableArrayDesignTypes() {
        return Sets.filter(this.types, new Predicate<FileType>() {
            @Override
            public boolean apply(FileType ft) {
                return ft.isParseableArrayDesign();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<FileType> getRawArrayDataTypes() {
        return Sets.filter(this.types, new Predicate<FileType>() {
            @Override
            public boolean apply(FileType ft) {
                return ft.isRawArrayData();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<FileType> getDerivedArrayDataTypes() {
        return Sets.filter(this.types, new Predicate<FileType>() {
            @Override
            public boolean apply(FileType ft) {
                return ft.isDerivedArrayData();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<FileType> getParseableArrayDataTypes() {
        return Sets.filter(this.types, new Predicate<FileType>() {
            @Override
            public boolean apply(FileType ft) {
                return ft.isParseableData();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<FileType> getMageTabTypes() {
        return Sets.filter(this.types, new Predicate<FileType>() {
            @Override
            public boolean apply(FileType ft) {
                return ft.isMageTab();
            }
        });
    }

    /**
     * Return the names of the given FileType instances.
     * 
     * @param types the type instances whose names to return
     * @return the type names
     */
    public static Iterable<String> namesForTypes(Iterable<FileType> types) {
        return Iterables.transform(types, new Function<FileType, String>() {
            @Override
            public String apply(FileType ft) {
                return ft.getName();
            }
        });
    }

}
