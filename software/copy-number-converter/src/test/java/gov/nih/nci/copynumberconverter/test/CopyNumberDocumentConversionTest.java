package gov.nih.nci.copynumberconverter.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.copynumberconverter.ColumnInfo;
import gov.nih.nci.copynumberconverter.ConversionException;
import gov.nih.nci.copynumberconverter.ConversionModule;
import gov.nih.nci.copynumberconverter.CopyNumberDocumentConversionProcess;
import gov.nih.nci.copynumberconverter.CopyNumberToMageTabDataMatrixConverter;
import gov.nih.nci.copynumberconverter.InvalidOptionsException;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.util.Modules;

/**
 * Tests CopyNumberDocumentConversionProcess class.
 * @author dharley
 *
 */
public class CopyNumberDocumentConversionTest {
    
    private static final String SINGLE_HYBRIDIZATION_INPUT = 
        "ID\tChromosome\tPhysical.Position\tlogratio\n" +
        "A_16_P37638626\t6\t62964904\t-0.447480618012438\n" +
        "A_18_P10656728\t10\t19811167\t-0.258419751156074\n";
    
    private static final String SINGLE_HYBRIDIZATION_EXPECTED_OUTPUT = 
        "Hybridization REF\t\t\ttestsingle\n" +
        "Reporter REF\tChromosome\tPosition\tLog2Ratio\n" +
        "A_16_P37638626\t6\t62964904\t-0.447480618012438\n" +
        "A_18_P10656728\t10\t19811167\t-0.258419751156074\n";
    
    private static final String MULTI_HYBRIDIZATION_USING_PREFIX_INPUT = 
        "ID\tChromosome\tPhysical.Position\ttest1_logratio\ttest2_logratio\n" +
        "A_16_P37638626\t6\t62964904\t-0.447480618012438\t0.447480618012438\n" +
        "A_18_P10656728\t10\t19811167\t-0.258419751156074\t0.258419751156074\n";
    
    private static final String MULTI_HYBRIDIZATION_EXPECTED_OUTPUT = 
        "Hybridization REF\t\t\ttest1\ttest2\n" +
        "Reporter REF\tChromosome\tPosition\tLog2Ratio\tLog2Ratio\n" +
        "A_16_P37638626\t6\t62964904\t-0.447480618012438\t0.447480618012438\n" +
        "A_18_P10656728\t10\t19811167\t-0.258419751156074\t0.258419751156074\n";
    
    private static final String MULTI_HYBRIDIZATION_USING_SUFFIX_INPUT = 
        "ID\tChromosome\tPhysical.Position\tlogratio_test1\tlogratio_test2\n" +
        "A_16_P37638626\t6\t62964904\t-0.447480618012438\t0.447480618012438\n" +
        "A_18_P10656728\t10\t19811167\t-0.258419751156074\t0.258419751156074\n";
    
    private static final String[] GOOD_SINGLE_HYB_OPTIONS = new String[] {
            "-input_file", "foo.txt", "-output_file", "bar.txt", "-probe_names_header", "longcat",
            "-chromosome_id_header", "blah", "-chromosome_position_header", "erm", "-log2ratio_values_header", 
            "ummm", "-hybridization_name", "yo"
    };
    
    private static final String[] BAD_SINGLE_HYB_OPTIONS_NO_CHROMOSOME = new String[] {
            "-input_file", "foo.txt", "-output_file", "bar.txt","-log2ratio_values_header", "ummm", 
            "-probe_names_header", "longcat", "-hybridization_name", "yo", "-chromosome_position_header", "erm"
    };
    
    private static final String[] BAD_SINGLE_HYB_OPTIONS_NO_CHROMOSOME_POSITION = new String[] {
            "-input_file", "foo.txt", "-output_file", "bar.txt","-log2ratio_values_header", "ummm", 
            "-probe_names_header", "longcat", "-hybridization_name", "yo", "-chromosome_id_header", "blah"
    };
    
    private static final String[] BAD_SINGLE_HYB_OPTIONS_HAS_PREFIX = new String[] {
            "-input_file", "foo.txt", "-output_file", "bar.txt", "-chromosome_id_header", "blah", 
            "-chromosome_position_header", "erm", "-log2ratio_values_header", "ummm", "-hybridization_name", "yo",
            "-probe_names_header", "longcat", "-hybridization_prefix"
    };
    
    private static final String[] BAD_SINGLE_HYB_OPTIONS_HAS_SUFFIX = new String[] {
            "-input_file", "foo.txt", "-output_file", "bar.txt", "-chromosome_id_header", "blah", 
            "-chromosome_position_header", "erm", "-log2ratio_values_header", "ummm", "-hybridization_name", "yo",
            "-probe_names_header", "longcat", "-hybridization_suffix"
    };
    
    private static final String[] BAD_SINGLE_HYB_OPTIONS_HAS_DELIMITER = new String[] {
            "-input_file", "foo.txt", "-output_file", "bar.txt", "-chromosome_id_header", "blah", 
            "-chromosome_position_header", "erm", "-log2ratio_values_header", "ummm", "-hybridization_name", "yo",
            "-probe_names_header", "longcat", "-hybridization_delimiter", "_"
    };
    
    private static final String[] BAD_SINGLE_HYB_OPTIONS_NO_INTPUT = new String[] {
            "-output_file", "bar.txt", "-chromosome_id_header", "blah", 
            "-chromosome_position_header", "erm", "-log2ratio_values_header", "ummm", "-hybridization_name", "yo",
            "-probe_names_header", "longcat"
    };
    
    private static final String[] BAD_SINGLE_HYB_OPTIONS_NO_OUTPUT = new String[] {
            "-input_file", "foo.txt", "-probe_names_header", "longcat", "-chromosome_id_header", "blah", 
            "-chromosome_position_header", "erm", "-log2ratio_values_header", "ummm", "-hybridization_name", "yo"
    };
    
    private static final String[] BAD_SINGLE_HYB_OPTIONS_NO_LOG2RATIOS = new String[] {
            "-input_file", "foo.txt", "-output_file", "bar.txt", "-chromosome_id_header", "blah", 
            "-chromosome_position_header", "erm", "-hybridization_name", "yo",
            "-probe_names_header", "longcat", "-hybridization_delimiter", "_"
    };
    
    private static final String[] GOOD_MULTI_HYB_PREFIX_OPTIONS = new String[] {
            "-input_file", "foo.txt", "-output_file", "bar.txt", "-chromosome_id_header", "blah", 
            "-chromosome_position_header", "erm", "-log2ratio_values_header", "ummm", "-hybridization_prefix", 
            "-probe_names_header", "longcat", "-hybridization_delimiter", "_"
    };
    
    private static final String[] GOOD_MULTI_HYB_SUFFIX_OPTIONS = new String[] {
            "-input_file", "foo.txt", "-output_file", "bar.txt", "-chromosome_id_header", "blah", 
            "-chromosome_position_header", "erm", "-log2ratio_values_header", "ummm", "-hybridization_suffix", 
            "-probe_names_header", "longcat", "-hybridization_delimiter", "_"
    };
    
    private static final String[] BAD_MULTI_HYB_PREFIX_OPTIONS_NO_CHROMOSOME = new String[] {
            "-input_file", "foo.txt", "-output_file", "bar.txt", "-log2ratio_values_header", "ummm",
            "-probe_names_header", "longcat", "-hybridization_prefix", "-hybridization_delimiter", "_",
            "-chromosome_position_header", "erm"
    };

    private static final String[] BAD_MULTI_HYB_PREFIX_OPTIONS_NO_CHROMOSOME_POSITION = new String[] {
            "-input_file", "foo.txt", "-output_file", "bar.txt", "-log2ratio_values_header", "ummm",
            "-chromosome_id_header", "blah", "-chromosome_id_header", "blah", "-probe_names_header",
            "longcat", "-hybridization_prefix", "-hybridization_delimiter", "_"
    };
    
    private static final String[] BAD_MULTI_HYB_PREFIX_OPTIONS_HAS_HYB_NAME = new String[] {
            "-input_file", "foo.txt", "-output_file", "bar.txt", "-chromosome_id_header", "blah", 
            "-chromosome_position_header", "erm", "-log2ratio_values_header", "ummm", "-hybridization_prefix", 
            "-probe_names_header", "longcat", "-hybridization_delimiter", "_", "-hybridization_name", "yo"
    };
    
    private static final String[] BAD_MULTI_HYB_PREFIX_OPTIONS_NO_DELIMITER = new String[] {
            "-input_file", "foo.txt", "-output_file", "bar.txt", "-chromosome_id_header", "blah", 
            "-probe_names_header", "longcat", "-chromosome_position_header", "erm", "-log2ratio_values_header", "ummm",
            "-hybridization_prefix"
    };
    
    private static final String[] BAD_MULTI_HYB_PREFIX_OPTIONS_NO_SUFFIX_OR_PREFIX = new String[] {
            "-input_file", "foo.txt", "-output_file", "bar.txt", "-chromosome_id_header", "blah", 
            "-chromosome_position_header", "erm", "-log2ratio_values_header", "ummm", 
            "-probe_names_header", "longcat", "-hybridization_delimiter", "_"
    };
    
    private static final String[] BAD_MULTI_HYB_PREFIX_OPTIONS_NO_PROBE_NAMES_HEADER = new String[] {
            "-input_file", "foo.txt", "-output_file", "bar.txt", "-log2ratio_values_header", "ummm",
            "-hybridization_prefix", "-hybridization_delimiter", "_", "-chromosome_id_header", "blah",
            "-chromosome_position_header", "erm"
    };
    
    private static final String[] BAD_SINGLE_HYB_OPTIONS_NO_PROBE_NAMES_HEADER = new String[] {
            "-input_file", "foo.txt", "-output_file", "bar.txt","-log2ratio_values_header", "ummm", 
            "-hybridization_name", "yo", "-chromosome_id_header", "blah",
            "-chromosome_position_header", "erm"
    };
    
    private static final String NO_PROBE_NAMES_HEADER_ERROR = "-probe_names_header is a required option";
    private static final String NO_DELIMITER_ERROR = "Must specify -hybridization_delimiter";
    private static final String NO_PREFIX_OR_SUFFIX_ERROR = 
        "neither -hybridization_prefix nor -hybridization_suffix were specified";
    private static final String NO_LOG2RATIO_VALUES_HEADER_ERROR = "-log2ratio_values_header is a required option";
    private static final String NO_CHROMOSOME_ID_HEADER_ERROR = "-chromosome_id_header is a required option";
    private static final String NO_CHROMOSOME_POSITION_HEADER_ERROR = "-chromosome_position_header is a required option";
    private static final String ILLEGAL_MULTI_HYBRIDIZATION_OPTIONS_ERROR =
        "all multi-hybridization options are illegal";
    private static final String NO_OUTPUT_FILE_ERROR = "-output_file is a required option";
    private static final String NO_INPUT_FILE_ERROR = "-input_file is a required option";
    
    private CopyNumberDocumentConversionProcess fakeConversionProcess;
    private CopyNumberDocumentConversionProcess realConversionProcess;
    

    @Before
    public void setUp() throws Exception {
        fakeConversionProcess =
            Guice.createInjector(Modules.override(new ConversionModule()).with(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(CopyNumberDocumentConversionProcess.class).to(CopyNumberDocumentConversionProcessStub.class);
                }
            })).getInstance(CopyNumberDocumentConversionProcess.class);
        realConversionProcess =
            Guice.createInjector(new ConversionModule()).getInstance(CopyNumberDocumentConversionProcess.class);
    }
    
    @Test
    public void testSingleHybridization() throws Exception {
        File inputFile = createFile(SINGLE_HYBRIDIZATION_INPUT, "test_single.input.txt");
        File outputFile = new File("test_single.output.txt");
        ColumnInfo columnInfo = new ColumnInfo(
                "ID",
                "Chromosome",
                "Physical.Position",
                "logratio",
                null,
                false,
                "testsingle");
        realConversionProcess.convert(inputFile.getAbsolutePath(), outputFile.getAbsolutePath(), columnInfo);
        String outFileContents = getFileContents(outputFile);
        assertEquals(SINGLE_HYBRIDIZATION_EXPECTED_OUTPUT, outFileContents);
        FileUtils.deleteQuietly(inputFile);
        FileUtils.deleteQuietly(outputFile);
    }
    
    @Test
    public void testMultiHybridizationWitPrefix() throws Exception {
        File inputFile = createFile(MULTI_HYBRIDIZATION_USING_PREFIX_INPUT, "test_multi-prefix.input.txt");
        File outputFile = new File("test_multi-prefix.output.txt");
        FileUtils.deleteQuietly(outputFile);
        ColumnInfo columnInfo = new ColumnInfo(
                "ID",
                "Chromosome",
                "Physical.Position",
                "logratio",
                "_",
                true,
                null);
        realConversionProcess.convert(inputFile.getAbsolutePath(), outputFile.getAbsolutePath(), columnInfo);
        String outFileContents = getFileContents(outputFile);
        assertEquals(MULTI_HYBRIDIZATION_EXPECTED_OUTPUT, outFileContents);
        FileUtils.deleteQuietly(inputFile);
        FileUtils.deleteQuietly(outputFile);
    }
    
    @Test
    public void testMultiHybridizationWitSuffix() throws Exception {
        File inputFile = createFile(MULTI_HYBRIDIZATION_USING_SUFFIX_INPUT, "test_multi-suffix.input.txt");
        File outputFile = new File("test_multi-suffix.output.txt");
        FileUtils.deleteQuietly(outputFile);
        ColumnInfo columnInfo = new ColumnInfo(
                "ID",
                "Chromosome",
                "Physical.Position",
                "logratio",
                "_",
                false,
                null);
        realConversionProcess.convert(inputFile.getAbsolutePath(), outputFile.getAbsolutePath(), columnInfo);
        String outFileContents = getFileContents(outputFile);
        assertEquals(MULTI_HYBRIDIZATION_EXPECTED_OUTPUT, outFileContents);
        FileUtils.deleteQuietly(inputFile);
        FileUtils.deleteQuietly(outputFile);
    }
    
    @Test
    public void testCommandLineArgs() throws Exception {
        testOptions(null, GOOD_SINGLE_HYB_OPTIONS);
        testOptions(null, GOOD_MULTI_HYB_PREFIX_OPTIONS);
        testOptions(null, GOOD_MULTI_HYB_SUFFIX_OPTIONS);
        testOptions(NO_INPUT_FILE_ERROR, BAD_SINGLE_HYB_OPTIONS_NO_INTPUT);
        testOptions(NO_OUTPUT_FILE_ERROR, BAD_SINGLE_HYB_OPTIONS_NO_OUTPUT);
        testOptions(NO_CHROMOSOME_ID_HEADER_ERROR, BAD_SINGLE_HYB_OPTIONS_NO_CHROMOSOME);
        testOptions(NO_CHROMOSOME_POSITION_HEADER_ERROR, BAD_SINGLE_HYB_OPTIONS_NO_CHROMOSOME_POSITION);
        testOptions(NO_CHROMOSOME_ID_HEADER_ERROR, BAD_MULTI_HYB_PREFIX_OPTIONS_NO_CHROMOSOME);
        testOptions(NO_CHROMOSOME_POSITION_HEADER_ERROR, BAD_MULTI_HYB_PREFIX_OPTIONS_NO_CHROMOSOME_POSITION);
        testOptions(ILLEGAL_MULTI_HYBRIDIZATION_OPTIONS_ERROR, BAD_SINGLE_HYB_OPTIONS_HAS_PREFIX);
        testOptions(ILLEGAL_MULTI_HYBRIDIZATION_OPTIONS_ERROR, BAD_SINGLE_HYB_OPTIONS_HAS_SUFFIX);
        testOptions(ILLEGAL_MULTI_HYBRIDIZATION_OPTIONS_ERROR, BAD_SINGLE_HYB_OPTIONS_HAS_DELIMITER);
        testOptions(NO_LOG2RATIO_VALUES_HEADER_ERROR, BAD_SINGLE_HYB_OPTIONS_NO_LOG2RATIOS);
        testOptions(ILLEGAL_MULTI_HYBRIDIZATION_OPTIONS_ERROR, BAD_MULTI_HYB_PREFIX_OPTIONS_HAS_HYB_NAME);
        testOptions(NO_PREFIX_OR_SUFFIX_ERROR, BAD_MULTI_HYB_PREFIX_OPTIONS_NO_SUFFIX_OR_PREFIX);
        testOptions(NO_DELIMITER_ERROR, BAD_MULTI_HYB_PREFIX_OPTIONS_NO_DELIMITER);
        testOptions(NO_PROBE_NAMES_HEADER_ERROR, BAD_MULTI_HYB_PREFIX_OPTIONS_NO_PROBE_NAMES_HEADER);
        testOptions(NO_PROBE_NAMES_HEADER_ERROR, BAD_SINGLE_HYB_OPTIONS_NO_PROBE_NAMES_HEADER);
    }
    
    private void testOptions(final String expectedErrorFragment, final String[] options) throws ConversionException {
        try {
            new CopyNumberToMageTabDataMatrixConverter(options, fakeConversionProcess);
            if (StringUtils.isEmpty(expectedErrorFragment)) {
                assertTrue(true);
            } else {
                fail();
            }
        } catch (InvalidOptionsException e) {
            if (StringUtils.isEmpty(expectedErrorFragment)) {
                fail();
            } else {
                assertTrue(-1 != expectedErrorFragment.indexOf(expectedErrorFragment));
            }
        }
    }
    
    private File createFile(final String contentsOfFile, final String filePath) throws IOException {
        File file = new File(filePath);
        FileUtils.writeStringToFile(file, contentsOfFile);
        return file;
    }
    
    private String getFileContents(final File file) throws IOException {
        return FileUtils.readFileToString(file);
    }

}