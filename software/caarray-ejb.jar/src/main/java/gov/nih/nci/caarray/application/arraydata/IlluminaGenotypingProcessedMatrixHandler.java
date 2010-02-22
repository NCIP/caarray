package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.application.arraydata.illumina.ValidatingProcessor;
import gov.nih.nci.caarray.application.arraydata.illumina.LoadingProcessor;
import gov.nih.nci.caarray.application.arraydata.illumina.DesignElementBuilderProcessor;
import gov.nih.nci.caarray.application.arraydata.illumina.IlluminaArrayDataTypes;
import gov.nih.nci.caarray.application.arraydata.illumina.IlluminaGenotypingProcessedMatrixQuantitationType;
import gov.nih.nci.caarray.application.arraydata.illumina.DefaultHeaderProcessor;
import gov.nih.nci.caarray.application.arraydata.illumina.ValidatingHeaderProcessor;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.project.Experiment;

import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.util.io.DelimitedFileReader;
import gov.nih.nci.caarray.util.io.DelimitedFileReaderFactory;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Illumina Genotyping Processed Matrix importer.
 * @since 3.4.0
 * @author gax
 */
public class IlluminaGenotypingProcessedMatrixHandler extends AbstractDataFileHandler {

    private static final Logger LOG = Logger.getLogger(IlluminaGenotypingProcessedMatrixHandler.class);

    /**
     * array design cached during validation, for use when the design info is not available during data loading.
     * this is a big problem when getArrayDesign() is called assuming that the design can be determined from the file
     * content.
     * @see gov.nih.nci.caarray.upgrade.FixHybridizationsWithMissingArraysMigrator
     * #getArrayDesignFromFile(java.lang.Long) .
     */

    private static final Map<File, CaArrayFile> CACHE = new WeakHashMap<File, CaArrayFile>();
    
    @Override
    QuantitationTypeDescriptor[] getQuantitationTypeDescriptors(File file) {
        
        DefaultHeaderProcessor proc = new DefaultHeaderProcessor();
        processFile(proc, null, file);
        List<QuantitationTypeDescriptor> desc = new ArrayList<QuantitationTypeDescriptor>();
        for (QuantitationTypeDescriptor d : proc.getHybBlocks()[0].getQTypes()) {
            if (d != null) {
                desc.add(d);
            }
        }
        return desc.toArray(new IlluminaGenotypingProcessedMatrixQuantitationType[desc.size()]);
    }

    @Override
    List<String> getHybridizationNames(File dataFile) {
        DefaultHeaderProcessor proc = new DefaultHeaderProcessor();
        processFile(proc, null, dataFile);
        List<String> hybNames = new ArrayList<String>();
        for (DefaultHeaderProcessor.HybBlock block : proc.getHybBlocks()) {
            hybNames.add(block.getQTypeColNames()[0]);
        }
        return hybNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayDesign getArrayDesign(ArrayDesignService arrayDesignService, File file) {
        /* the array design cannot be determind from the data file and is assumed to be the same as the one computed
         * during validation; ie. validate() must be called first.
         */
        ArrayDesign design = findIlluminaArrayDesign(file);
        return design;
    }

    @Override
    void validate(CaArrayFile caArrayFile, File file, MageTabDocumentSet mTabSet, final FileValidationResult result,
            ArrayDesignService arrayDesignService) {

        CACHE.put(file, caArrayFile);
        ArrayDesign design = findIlluminaArrayDesign(caArrayFile, result);
        
        final Set<String> sdrfHybNames;
        if (mTabSet != null && mTabSet.getSdrfDocuments() != null && !mTabSet.getSdrfDocuments().isEmpty()) {
            sdrfHybNames = new HashSet<String>();
            for (List<gov.nih.nci.caarray.magetab.sdrf.Hybridization> l : mTabSet.getSdrfHybridizations().values()) {
                for (gov.nih.nci.caarray.magetab.sdrf.Hybridization h : l) {
                    sdrfHybNames.add(h.getName());
                }
            }
        } else {
            sdrfHybNames = null;
        }
        ValidatingHeaderProcessor headerProc = new ValidatingHeaderProcessor(result, sdrfHybNames);
        ValidatingProcessor proc = new ValidatingProcessor(headerProc, result, design);
        processFile(headerProc, proc, file);
    }

    


    @Override
    void loadData(DataSet dataSet, List<QuantitationType> types, File file, ArrayDesignService arrayDesignService) {

        ArrayDesign design = findIlluminaArrayDesign(file);
        // pass 1: load design element and count row.
        DefaultHeaderProcessor header = new DefaultHeaderProcessor();
        DesignElementBuilderProcessor designElementProc = new DesignElementBuilderProcessor(dataSet, design);
        processFile(header, designElementProc, file);
        super.prepareColumns(dataSet, types, designElementProc.getList().size());
        LOG.info("Pass 1/2 loaded " + designElementProc.getList().size() + " design elements.");
        // pass 2: fill columns.
        LoadingProcessor loader = new LoadingProcessor(header.getHybBlocks(), dataSet, this);
        processFile(header, loader, file);
        LOG.info("Pass 2/2 loaded data.");
    }

    @Override
    Logger getLog() {
        return LOG;
    }

    @Override
    ArrayDataTypeDescriptor getArrayDataTypeDescriptor(File dataFile) {
        return IlluminaArrayDataTypes.ILLUMINA_GENOTYPING_PROCESSED_MATRIX;
    }

    private static DelimitedFileReader openReader(File dataFile) {
        try {
            return DelimitedFileReaderFactory.INSTANCE.getTabDelimitedReader(dataFile);
        } catch (IOException e) {
            throw new IllegalStateException("File " + dataFile.getName() + " could not be read", e);
        }
    }

    private void processFile(HeaderProcessor headerProc, RowProcessor rowProc, File file) {
        DelimitedFileReader r = openReader(file);
        try {
            boolean keepGoing = r.hasNextLine() && headerProc.parseHeader(r.nextLine(), r.getCurrentLineNumber());
            while (rowProc != null && keepGoing && r.hasNextLine()) {
                keepGoing = rowProc.parseRow(r.nextLine(), r.getCurrentLineNumber());
            }
        } catch (IOException e) {
            throw new IllegalStateException(AbstractDataFileHandler.READ_FILE_ERROR_MESSAGE, e);
        } finally {
            r.close();
        }
    }

    private ArrayDesign findIlluminaArrayDesign(CaArrayFile caArrayFile, FileValidationResult result) {
        Experiment exp = caArrayFile.getProject().getExperiment();
        if (exp.getArrayDesigns().size() != 1) {
            result.addMessage(Type.ERROR, "Experiment must have exactly one design");
        }
        ArrayDesign design = AbstractDataSetImporter.findArrayDesignFromExperiment(exp);
        return design;
    }

    private ArrayDesign findIlluminaArrayDesign(CaArrayFile caArrayFile) {
        return findIlluminaArrayDesign(caArrayFile, new FileValidationResult(null) {
            @Override
            public ValidationMessage addMessage(Type type, String message) {
                LOG.log(type == Type.ERROR ?  Level.ERROR : Level.INFO, message);
                return super.addMessage(type, message);
            }
        });
    }

    private ArrayDesign findIlluminaArrayDesign(File file) {
        CaArrayFile f = CACHE.get(file);
        ArrayDesign design = null;
        if (f != null) {
            design = findIlluminaArrayDesign(f);
        }
        return design;
    }

    /**
     * functor to process rows in an tabular file.
     */
    public static interface RowProcessor {
        /**
         * process a row in a tabular file.
         * @param row a row of data from the file.
         * @param lineNum the line number in the file.
         * @return true is we want to keep parsing the file.
         */
        boolean parseRow(List<String> row, int lineNum);
    }

    /**
     * functor to process rows in an tabular file.
     */
    public static interface HeaderProcessor {
        /**
         * process the header row in a tabular file.
         * @param row a row of data from the file.
         * @param lineNum the line number in the file.
         * @return true is we want to keep parsing the file.
         */
        boolean parseHeader(List<String> row, int lineNum);
    }
}
