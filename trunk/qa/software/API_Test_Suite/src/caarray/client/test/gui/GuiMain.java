//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.client.test.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestMain;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResultReport;
import caarray.client.test.full.FullApiFacade;
import caarray.client.test.suite.ConfigurableTestSuite;

/**
 * GUI from which the API test suites can configured and run. The interface allows the
 * user to modify parameters such as urls, ports, number of concurrent test threads, and specific
 * test cases to be included/excluded, as well as constrain the tests to specific categories
 * (file download, ArrayDesign search-by-example, etc).
 * 
 * @author vaughng Jul 18, 2009
 */
public class GuiMain
{
    private static final Log log = LogFactory.getLog(GuiMain.class);
    /*  Corresponds to specific test categories and indicates whether a category should be run */
    private boolean[] runTests;
    /* Prevents tests from being initiated when tests are currently running */
    private Boolean isRunning = false;
    
    /* List of available test suite objects */
    private List<List<ConfigurableTestSuite>> testSuiteCollection = new ArrayList<List<ConfigurableTestSuite>>();
    /* Checkboxes corresponding to test suite objects (test categories)  */
    private List<JCheckBox> testCheckBoxes = new ArrayList<JCheckBox>();
    /* List of ApiFacade objects used for test execution, one per test thread */
    private List<ApiFacade> apiFacades = new ArrayList<ApiFacade>();
    /* Primary test execution thread, prevents gui from locking while tests are running */
    private Thread executionThread;
    
    private JPanel selectionPanel = new JPanel();
    private JPanel centerPanel = new JPanel();
    private JTextArea textDisplay = new JTextArea();
    
    public GuiMain() throws Exception
    {
        JFrame frame = new JFrame("API Test Suite");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().setLayout(new BorderLayout(6,6));

        JPanel topPanel = new JPanel();

        /* ###### Text fields for modifiable parameters ##### */
        
        final JTextField javaHostText = new JTextField(TestProperties
                .getJavaServerHostname(),20);
        JLabel javaHostLabel = new JLabel("Java Service Host");
        JButton save = new JButton("Save");
        save.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent arg0)
            {
                TestProperties.setJavaServerHostname(javaHostText.getText());
            }
            
        });     

        JLabel javaPortLabel = new JLabel("Java Port");
        final JTextField javaPortText = new JTextField(Integer
                .toString(TestProperties.getJavaServerJndiPort()), 5);
        JButton save2 = new JButton("Save");
        save2.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent arg0)
            {
                try
                {
                    int port = Integer.parseInt(javaPortText.getText());
                    TestProperties.setJavaServerJndiPort(port);
                }
                catch (NumberFormatException e)
                {
                    System.out.println(javaPortText.getText() + " is not a valid port number.");
                }              
            }
            
        });

        JLabel gridHostLabel = new JLabel("Grid Service Host");
        final JTextField gridHostText = new JTextField(TestProperties
                .getGridServerHostname(), 20);
        JButton save3 = new JButton("Save");
        save3.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent arg0)
            {
                TestProperties.setGridServerHostname(gridHostText.getText());
            }
            
        });

        JLabel gridPortLabel = new JLabel("Grid Service Port");
        final JTextField gridPortText = new JTextField(Integer
                .toString(TestProperties.getGridServerPort()),5);
        JButton save4 = new JButton("Save");
        save4.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent arg0)
            {
                try
                {
                    int port = Integer.parseInt(gridPortText.getText());
                    TestProperties.setGridServerPort(port);
                }
                catch (NumberFormatException e)
                {
                    System.out.println(gridPortText.getText() + " is not a valid port number.");
                }
                
            }
            
        });
        
        
        
        JLabel excludeLabel = new JLabel("Exclude test cases (comma-separated list):");
        final JTextField excludeText = new JTextField("",30);
        JButton save5 = new JButton("Save");
        save5.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent arg0)
            {
                String testString = excludeText.getText();
                    if (testString != null)
                    {
                        String[] testCases = testString.split(",");
                        if (testCases != null && testCases.length > 0)
                        {
                            List<Float> tests = new ArrayList<Float>();
                            for (String test : testCases)
                            {
                                try
                                {
                                    tests.add(Float.parseFloat(test));
                                }
                                catch (NumberFormatException e)
                                {
                                    System.out.println(test + " is not a valid test case.");
                                }
                            }
                            TestProperties.setExcludedTests(tests);
                        }
                        
                    }
                    
                
                
            }
            
        });
        
        JLabel includeLabel = new JLabel("Include only (comma-separated list):");
        final JTextField includeText = new JTextField("",30);
        JButton save6 = new JButton("Save");
        save6.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent arg0)
            {
                String testString = includeText.getText();
                    if (testString != null)
                    {
                        String[] testCases = testString.split(",");
                        if (testCases != null && testCases.length > 0)
                        {
                            List<Float> tests = new ArrayList<Float>();
                            for (String test : testCases)
                            {
                                try
                                {
                                    tests.add(Float.parseFloat(test));
                                }
                                catch (NumberFormatException e)
                                {
                                    System.out.println(test + " is not a valid test case.");
                                }
                            }
                            TestProperties.setIncludeOnlyTests(tests);
                        }
                        
                    }
                    
            }
            
        });

        JLabel threadLabel = new JLabel("Number of threads:");
        final JTextField threadText = new JTextField(Integer
                .toString(TestProperties.getNumThreads()),5);
        JButton save7 = new JButton("Save");
        save7.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent arg0)
            {
                try
                {
                    int threads = Integer.parseInt(threadText.getText());
                    TestProperties.setNumThreads(threads);
                }
                catch (NumberFormatException e)
                {
                    System.out.println(threadText.getText() + " is not a valid thread number.");
                }
                
            }
            
        });
        GridBagLayout topLayout = new GridBagLayout();
        topPanel.setLayout(topLayout);
        
        JLabel[] labels = new JLabel[]{javaHostLabel,javaPortLabel,gridHostLabel,gridPortLabel,excludeLabel, includeLabel, threadLabel};
        JTextField[] textFields = new JTextField[]{javaHostText,javaPortText,gridHostText,gridPortText,excludeText, includeText,threadText};
        JButton[] buttons = new JButton[]{save,save2,save3,save4, save5, save6, save7};
        for (int i = 0; i < labels.length; i++)
        {
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.NONE;
            c.gridx = 0;
            c.gridy = i;
            topPanel.add(labels[i],c);
            c.gridx = 1;
            topPanel.add(textFields[i],c);
            c.gridx = 2;         
            topPanel.add(buttons[i],c);
        }
        
        frame.getContentPane().add(topPanel,BorderLayout.PAGE_START);

        GridLayout bottomLayout = new GridLayout(0,4);
        selectionPanel.setLayout(bottomLayout);
        JCheckBox selectAll = new JCheckBox("Select/Deselect All");
        selectAll.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JCheckBox box = (JCheckBox)e.getSource();
                selectAll(box.isSelected());
            }         
        }
        );
        selectionPanel.add(selectAll);
        JCheckBox excludeLongTests = new JCheckBox("Exclude Long Tests");
        excludeLongTests.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JCheckBox box = (JCheckBox)e.getSource();
                if (box.isSelected())
                {
                    TestProperties.excludeLongTests();
                }
                else
                {
                    TestProperties.removeExcludedLongTests();
                }
            } 
        });
        TestProperties.excludeLongTests();
        excludeLongTests.setSelected(true);
        selectionPanel.add(excludeLongTests);
        
        //Initialize check boxes corresponding to test categories
        initializeTests();
        
        centerPanel.setLayout(new GridLayout(0,1));
        centerPanel.add(selectionPanel);
        
        //Redirect System messages to gui     
        JScrollPane textScroll = new JScrollPane();
        textScroll.setViewportView(textDisplay);
        System.setOut(new PrintStream(new JTextAreaOutputStream(textDisplay)));
        System.setErr(new PrintStream(new JTextAreaOutputStream(textDisplay)));
        centerPanel.add(textScroll);
        JScrollPane scroll = new JScrollPane(centerPanel);
        
        frame.getContentPane().add(scroll, BorderLayout.CENTER);
        
        JButton runButton = new JButton("Run Tests");
        runButton.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent arg0)
            {
                try
                    {
                        
                        runTests();
                        
                    }
                    catch (Exception e)
                    {
                        System.out.println("An error occured executing the tests: " + e.getClass() + ". Check error log for details.");
                        log.error("Exception encountered:",e);
                    }
                }              
           
        });
        
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(runButton);
        frame.getContentPane().add(bottomPanel, BorderLayout.PAGE_END);
        
        frame.pack();
        frame.setVisible(true);
    }

    /*
     * Creates check boxes corresponding to each test category (ConfigurableTestSuite).
     */
    private void initializeTests() throws Exception
    {
        int index = 0;
        ApiFacade apiFacade = new FullApiFacade();
        List<ConfigurableTestSuite> shortTests = TestMain.getShortTestSuites(apiFacade);
        List<ConfigurableTestSuite> longTests = TestMain.getLongTestSuites(apiFacade);
        List<ConfigurableTestSuite> testSuites = new ArrayList<ConfigurableTestSuite>();
        runTests = new boolean[shortTests.size() + longTests.size()];
        
        for (ConfigurableTestSuite test : shortTests)
        {
            JCheckBox box = new JCheckBox(test.getDisplayName());
            box.addChangeListener(new SelectTestListener(index));
            box.setSelected(true); 
            testCheckBoxes.add(box);
            selectionPanel.add(box);
            testSuites.add(test);
            index++;
        }
        for (ConfigurableTestSuite test : longTests)
        {
            JCheckBox box = new JCheckBox(test.getDisplayName());
            box.addChangeListener(new SelectTestListener(index));
            box.setSelected(false); 
            testCheckBoxes.add(box);
            selectionPanel.add(box);
            testSuites.add(test);
            index++;
        } 
        testSuiteCollection.add(testSuites);
        apiFacades.add(apiFacade);
    }
    
    /*
     * Resets all test suite objects.
     */
    private void resetTests() throws Exception
    {
        apiFacades.clear();
        testSuiteCollection.clear();
        ApiFacade apiFacade = new FullApiFacade();
        List<ConfigurableTestSuite> shortTests = TestMain.getShortTestSuites(apiFacade);
        List<ConfigurableTestSuite> longTests = TestMain.getLongTestSuites(apiFacade);
        List<ConfigurableTestSuite> testSuites = new ArrayList<ConfigurableTestSuite>();
        testSuites.addAll(shortTests);
        testSuites.addAll(longTests);
        testSuiteCollection.add(testSuites);
        apiFacades.add(apiFacade);
    }
    
    /*
     * Selects all test suites.
     */
    private void selectAll(boolean select)
    {
        for (JCheckBox box : testCheckBoxes)
        {
            box.setSelected(select);
        }
    }
    
    /*
     * Initiates selected test suites.
     */
    private void runTests() throws Exception
    {
        synchronized (isRunning)
        {
           if (!isRunning)
           {
               isRunning = true;
               int numThreads = TestProperties.getNumThreads();
               if (numThreads <= 1)
               {
                   runSingleThreadTest();
               }
               else
               {
                   runLoadTest(numThreads);
               }
           }
        }
        
    }
    
    /*
     * Runs a single instance of each test suite object if load testing has
     * not been specified (default).
     */
    private void runSingleThreadTest()
    {

        final TestResultReport resultReport = new TestResultReport();
        final ApiFacade apiFacade = apiFacades.get(0);
        final List<ConfigurableTestSuite> testSuites = testSuiteCollection.get(0);
        Runnable runner = new Runnable()
        {
            public void run()
            {               
                try
                {
                    apiFacade.connect();
                    System.out.println("Executing test suites ...");
                    long start = System.currentTimeMillis();
                    for (int i = 0; i < runTests.length; i++)
                    {
                        if (runTests[i])
                        {
                            testSuites.get(i).runTests(resultReport);
                        }
                    }
                    long time = System.currentTimeMillis() - start;
                    System.out.println("Tests executed in: " + (double)time/(double)1000 + " seconds.");
                    resultReport.writeReport();
                    executionThread = null;
                    resetTests();
                    isRunning = false;
                }
                catch (Throwable t)
                {
                    System.out.println("An exception occured execuitng the tests: " + t.getClass());
                    System.out.println("Test suite aborted. Check error log for details.");
                    t.printStackTrace();
                    log.error("Exception encountered:",t);
                }
                
            }
            
        };
        executionThread = new Thread(runner);
        executionThread.start();
    }
    
    private void runLoadTest(int numThreads)
    {
        LoadTestExecutor executor = new LoadTestExecutor(numThreads);
        executionThread = new Thread(executor);
        executionThread.start();
    }
    
    /*
     * Executes test suites in multiple threads, with each thread running one instance of each
     * selected test suite.
     */
    class LoadTestExecutor implements Runnable
    {
        private int numThreads;
        
        public LoadTestExecutor(int numThreads)
        {
            this.numThreads = numThreads;
        }
        
        public void run()
        {
            try
            {
                for (int i = apiFacades.size(); i < numThreads; i++)
                {
                    apiFacades.add(new FullApiFacade());
                }
                for (int i = testSuiteCollection.size(); i < numThreads; i++)
                {
                    List<ConfigurableTestSuite> shortTests = TestMain.getShortTestSuites(apiFacades.get(i));
                    List<ConfigurableTestSuite> longTests = TestMain.getLongTestSuites(apiFacades.get(i));
                    List<ConfigurableTestSuite> testSuites = new ArrayList<ConfigurableTestSuite>();
                    testSuites.addAll(shortTests);
                    testSuites.addAll(longTests);
                    testSuiteCollection.add(testSuites);
                }
                TestResultReport[] threadReports = new TestResultReport[numThreads];
                for (int i = 0; i < numThreads; i++)
                {
                    threadReports[i] = new TestResultReport();
                }
                Thread[] loadTestThreads = new Thread[numThreads];
                for (int i = 0; i < numThreads; i++)
                {
                    LoadTestThread thread = new LoadTestThread(apiFacades.get(i),testSuiteCollection.get(i),
                            threadReports[i],i);
                    loadTestThreads[i] = new Thread(thread);
                }
                System.out.println("Executing load tests for " + numThreads + " threads ...");
                long start = System.currentTimeMillis();
                for (int i = 0; i < numThreads; i++)
                {
                    loadTestThreads[i].start();
                }
                for (int i = 0; i < numThreads; i++)
                {
                    loadTestThreads[i].join();
                }
                long time = System.currentTimeMillis() - start;
                System.out.println("Load tests completed in " + (double)time/(double)1000 + " seconds.");
                
                TestResultReport finalReport = new TestResultReport();
                for (TestResultReport report : threadReports)
                {
                    finalReport.merge(report);
                }
                System.out.println("Analyzing load test results ...");
                finalReport.writeLoadTestReports();
                executionThread = null;
                resetTests();
                isRunning = false;
            }
            catch (Throwable t)
            {
                System.out.println("An exception occured execuitng the load tests: " + t.getClass());
                System.out.println("Test suite aborted.");
                t.printStackTrace();
                log.error("Exception encountered:",t);
            }
            
            
        }       
    }
    
    /*
     * A single test thread, used for load testing.
     */
    class LoadTestThread implements Runnable
    {
        private ApiFacade apiFacade;
        private List<ConfigurableTestSuite> testSuites;
        private TestResultReport resultReport;
        private int thread;
        public LoadTestThread(ApiFacade apiFacade, List<ConfigurableTestSuite> testSuites, TestResultReport resultReport, int thread)
        {
            this.apiFacade = apiFacade;
            this.testSuites = testSuites;
            this.resultReport = resultReport;
            this.thread = thread;
        }
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            
            try
            {
                apiFacade.connect();
                for (int i = 0; i < runTests.length; i++)
                {
                    if (runTests[i])
                    {
                        testSuites.get(i).runTests(resultReport);
                    }
                }
                resultReport.setThreadId(thread);
                
            }
            catch (Throwable t)
            {
                System.out.println("An exception occured in thread " + thread + ": " + t.getClass());
                System.out.println("Test suite aborted.");
                t.printStackTrace();
                log.error("Exception encountered:",t);
            }
            
        }
        
        
    }
    
    class SelectTestListener implements ChangeListener
    {
        private int index;
        public SelectTestListener(int index)
        {
            this.index = index;
        }
        
        public void stateChanged(ChangeEvent e)
        {
            JCheckBox box = (JCheckBox)e.getSource();
            runTests[index] = box.isSelected();          
        }  
    }
    
    class JTextAreaOutputStream extends OutputStream {
        private JTextArea tarea;
        
        public JTextAreaOutputStream(JTextArea tarea) {
            this.tarea = tarea;
        }
        
        public void write(int b) throws IOException {
            byte[] bytes = new byte[1];
            bytes[0] = (byte)b;
            String newText = new String(bytes);
            tarea.append(newText);
            if (newText.indexOf('\n') > -1) {
                try {
                    tarea.scrollRectToVisible(tarea.modelToView(
                            tarea.getDocument().getLength()));
                } catch (BadLocationException err) {
                    err.printStackTrace();
                }
            }
        }
        public final void write(byte[] arg0) throws IOException {
            String txt = new String(arg0);
            tarea.append(txt);
            try {
                tarea.scrollRectToVisible(tarea.modelToView(tarea.getDocument().getLength()));
            } catch (BadLocationException err) {
                err.printStackTrace();
            }
        }
    }
    /**
     * @param args
     */
    public static void main(String[] args)
    { 
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run()
                {
                    try
                    {
                        new GuiMain();
                    }
                    catch (Throwable t)
                    {
                        System.out.println("An unexpected error occurred during test execution.");
                        t.printStackTrace();
                        log.error("Exception encountered:",t);
                    }
                    
                }
            });
        
    }

}
