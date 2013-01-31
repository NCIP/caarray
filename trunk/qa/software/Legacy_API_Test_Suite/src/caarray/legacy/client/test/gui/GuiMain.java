//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.legacy.client.test.gui;

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

import caarray.legacy.client.test.ApiFacade;
import caarray.legacy.client.test.TestMain;
import caarray.legacy.client.test.TestProperties;
import caarray.legacy.client.test.TestResultReport;
import caarray.legacy.client.test.full.FullApiFacade;
import caarray.legacy.client.test.suite.ConfigurableTestSuite;

/**
 * @author vaughng
 * Jul 31, 2009
 */
public class GuiMain
{

    private boolean[] runTests;
    private Boolean isRunning = false;
    private List<ConfigurableTestSuite> testSuites = new ArrayList<ConfigurableTestSuite>();
    private List<JCheckBox> testCheckBoxes = new ArrayList<JCheckBox>();
    private ApiFacade apiFacade = new FullApiFacade();
    Thread executionThread;
    
    private JPanel selectionPanel = new JPanel();
    private JPanel centerPanel = new JPanel();
    private JTextArea textDisplay = new JTextArea();
    
    public GuiMain() throws Exception
    {
        JFrame frame = new JFrame("Legacy API Test Suite");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().setLayout(new BorderLayout(6,6));

        JPanel topPanel = new JPanel();

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

        
        GridBagLayout topLayout = new GridBagLayout();
        topPanel.setLayout(topLayout);
        
        JLabel[] labels = new JLabel[]{javaHostLabel,javaPortLabel,gridHostLabel,gridPortLabel, excludeLabel, includeLabel};
        JTextField[] textFields = new JTextField[]{javaHostText,javaPortText,gridHostText,gridPortText,excludeText, includeText};
        JButton[] buttons = new JButton[]{save,save2,save3,save4,save5, save6};
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
        initializeTests();
        
        centerPanel.setLayout(new GridLayout(0,1));
        centerPanel.add(selectionPanel);
        
        

        
        JScrollPane textScroll = new JScrollPane();
        //textDisplay.setSize(500, 700);
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
                        System.out.println("An error occured executing the tests: " + e.getClass());
                        e.printStackTrace();
                    }
                }              
           
        });
        
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(runButton);
        frame.getContentPane().add(bottomPanel, BorderLayout.PAGE_END);
        
        frame.pack();
        frame.setVisible(true);
    }

    private void initializeTests()
    {
        int index = 0;
        List<ConfigurableTestSuite> shortTests = TestMain.getShortTestSuites(apiFacade);
        List<ConfigurableTestSuite> longTests = TestMain.getLongTestSuites(apiFacade);
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
    }
    
    private void resetTests()
    {
        List<ConfigurableTestSuite> shortTests = TestMain.getShortTestSuites(apiFacade);
        List<ConfigurableTestSuite> longTests = TestMain.getLongTestSuites(apiFacade);
        testSuites.clear();
        testSuites.addAll(shortTests);
        testSuites.addAll(longTests);
    }
    
    private void selectAll(boolean select)
    {
        for (JCheckBox box : testCheckBoxes)
        {
            box.setSelected(select);
        }
    }
    private void runTests() throws Exception
    {
        synchronized (isRunning)
        {
           if (!isRunning)
           {
               isRunning = true;
               final TestResultReport resultReport = new TestResultReport();
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
                           System.out.println("Test suite aborted.");
                           t.printStackTrace();
                       }
                       
                   }
                   
               };
               executionThread = new Thread(runner);
               executionThread.start();
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
                    }
                    
                }
            });
        
    }

}
