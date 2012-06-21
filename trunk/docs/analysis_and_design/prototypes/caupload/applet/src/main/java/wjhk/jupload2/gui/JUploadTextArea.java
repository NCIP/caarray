//
// $Id: JUploadTextArea.java 95 2007-05-02 03:27:05Z
// /C=DE/ST=Baden-Wuerttemberg/O=ISDN4Linux/OU=Fritz
// Elfert/CN=svn-felfert@isdn4linux.de/emailAddress=fritz@fritz-elfert.de $
//
// jupload - A file upload applet.
// Copyright 2007 The JUpload Team
//
// Created: ?
// Creator: William JinHua Kwong
// Last modified: $Date: 2011-01-19 15:52:15 +0100 (mer., 19 janv. 2011) $
//
// This program is free software; you can redistribute it and/or modify it under
// the terms of the GNU General Public License as published by the Free Software
// Foundation; either version 2 of the License, or (at your option) any later
// version. This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
// details. You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software Foundation, Inc.,
// 675 Mass Ave, Cambridge, MA 02139, USA.

package wjhk.jupload2.gui;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JTextArea;

import wjhk.jupload2.policies.UploadPolicy;

/**
 * This class represents the text area for debug output.
 */
@SuppressWarnings("serial")
public class JUploadTextArea extends JTextArea {

    /**
     * Maximum number of characters in the logWindow.
     */
    public final static int MAX_LOG_WINDOW_LENGTH = 800000;

    /**
     * The size we truncate the output to, when the maximum size of debug output
     * is reach. We remove 20%.
     */
    public final static int SIZE_TO_TRUNCATE_TO = (int) (MAX_LOG_WINDOW_LENGTH * 0.8);

    /**
     * The current upload policy
     */
    UploadPolicy uploadPolicy;

    /**
     * Indicates whether the logging in the LogMessageThread is active or not.
     * It's marked as active before starting this thread. It's marked as
     * non-active, when this thread is interrupted, in {@link #unload()}
     */
    boolean loggingActive = false;

    /**
     * The ConcurrentLinkedQueue that'll contain the messages.
     */
    private BlockingQueue<String> messages;

    /**
     * This value is logged in the debug file, and in the debug output, for each
     * line. This allows to sort the outputed line correctly.
     * 
     * @see #displayMsg(String, String)
     */
    private int nextMessageId = 1;

    /**
     * A thread, that will be called in the EventDispatcherThread, to have a
     * tread-safe update of the GUI. This thread is responsible to display one
     * String.
     */
    static class LogMessageThread extends Thread {

        /**
         * The text area that'll contain the messages.
         */
        private JUploadTextArea textArea;

        /**
         * @param textArea
         */
        LogMessageThread(JUploadTextArea textArea) {
            this.textArea = textArea;
            setDaemon(true);
        }

        /** The run method of the Runnable Interface */
        @Override
        public void run() {
            String nextMessage = null;

            if (this.textArea.uploadPolicy.getDebugLevel() >= 30) {
                int nextMessageIdBackup = this.textArea.nextMessageId;
                this.textArea.nextMessageId = 0;
                this.textArea.setText(this.textArea.formatMessageOutput(
                        "[DEBUG]", "Logging system is initialized") + "\n");
                this.textArea.nextMessageId = nextMessageIdBackup;
            }

            while (this.textArea.loggingActive) {
                try {
                    nextMessage = this.textArea.messages.take() + "\n";

                    // Ah, a new message has been delivered...

                    synchronized (this.textArea) {
                        String content = this.textArea.getText();
                        int contentLength = content.length();
                        // If the current content is too long, we truncate it.
                        if (contentLength > JUploadTextArea.MAX_LOG_WINDOW_LENGTH) {
                            content += nextMessage;
                            String newContent = content.substring(content
                                    .length()
                                    - SIZE_TO_TRUNCATE_TO, content.length());
                            this.textArea.setText(newContent);
                            contentLength = SIZE_TO_TRUNCATE_TO;
                        } else {
                            // The result is not too long
                            this.textArea.append(nextMessage);
                            contentLength += nextMessage.length();
                        }
                        this.textArea.setCaretPosition(contentLength - 1);
                    } // synchronized
                } catch (InterruptedException e) {
                    // If we're not running any more, then this 'stop' is
                    // not a
                    // problem any more. We're then just notified we must
                    // stop
                    // the thread.
                    if (this.textArea.loggingActive) {
                        // This should not happen, and we can not put in the
                        // standard JUpload output, as this thread is
                        // responsible for it.
                        e.printStackTrace();
                    }
                }// try
            }// while
        }
    }

    /**
     * The thread, that will put messages in the debug log.
     */
    LogMessageThread logMessageThread = null;

    /**
     * Constructs a new empty TextArea with the specified number of rows and
     * columns.
     * 
     * @param rows The desired number of text rows (lines).
     * @param columns The desired number of columns.
     * @param uploadPolicy The current uploadPolicy
     */
    public JUploadTextArea(int rows, int columns, UploadPolicy uploadPolicy) {
        super(rows, columns);
        this.uploadPolicy = uploadPolicy;
        this.messages = new LinkedBlockingQueue<String>();
        setBackground(new Color(255, 255, 203));
        setEditable(false);
        setLineWrap(true);
        setWrapStyleWord(true);

        // The queue, where messages to display will be posted.
        this.logMessageThread = new LogMessageThread(this);
        this.logMessageThread.setName(this.logMessageThread.getClass()
                .getName());
        // NO START HERE: the logMessageThread needs to know the upload policy,
        // to run properly. The thread is started in the setUploadPolicy method.

        // The unload callback will be registered, once the uploadPolicy has
        // been built, by DefaultJUploadContext.init(JUploadApplet)
    }

    /**
     * Add a string to the queue of string to be added to the logWindow. This is
     * necessary, to manage the non-thread-safe Swing environment.
     * 
     * @param tag The tag (eg: INFO, DEBUG...)
     * @param msg The message to add, at the end of the JUploadTextArea.
     * @return The formatted text that was added to the log window.
     */
    public final String displayMsg(String tag, String msg) {
        String fullMessage = formatMessageOutput(tag, msg);

        try {
            // messages is a BlockingQueue. So the next line may 'block' the
            // applet main thread. But, we're optimistic: this should not happen
            // as we instanciate an unbound LinkedBlockingQueue. We'll be
            // blocked at Integer.MAX_VALUE, that is ... much after an
            // OutOfMemory is thrown !
            this.messages.put(fullMessage);
        } catch (InterruptedException e) {
            System.out.println("WARNING - [" + this.getClass().getName()
                    + "] Message lost due to " + e.getClass().getName() + " ("
                    + fullMessage + ")");
        }
        return fullMessage;
    }

    /**
     * This call must be synchronized, so that there is no interaction with the
     * LogMessageThread thread.
     * 
     * @see JTextArea#append(String)
     */
    synchronized public void append(String t) {
        super.append(t);
    }

    /** @see JUploadPanel#copyLogWindow() */
    public synchronized void copyLogWindow() {
        selectAll();
        copy();
    }

    /**
     * This call must be synchronized, so that there is no interaction with the
     * LogMessageThread thread.
     * 
     * @see JTextArea#insert(String, int)
     */
    synchronized public void insert(String str, int pos) {
        super.insert(str, pos);
    }

    /**
     * This call must be synchronized, so that there is no interaction with the
     * LogMessageThread thread.
     * 
     * @see JTextArea#replaceRange(String, int, int)
     */
    synchronized public void replaceRange(String str, int start, int end) {
        super.replaceRange(str, start, end);
    }

    /**
     * This call must be synchronized, so that there is no interaction with the
     * LogMessageThread thread.
     * 
     * @see JTextArea#setText(String)
     */
    synchronized public void setText(String t) {
        super.setText(t);
    }

    /**
     * @param uploadPolicy the uploadPolicy to set
     */
    public void setUploadPolicy(UploadPolicy uploadPolicy) {
        this.uploadPolicy = uploadPolicy;
        this.uploadPolicy.getContext().registerUnload(this, "unload");
        // We can now start the log thread.
        this.loggingActive = true;
        this.logMessageThread.start();
    }

    /**
     * Free any used ressources. Actually close the LogMessageThread thread.
     */
    public synchronized void unload() {
        this.loggingActive = false;
        this.logMessageThread.interrupt();
    }

    /**
     * Format the message, with the given tag. This method also add the time and
     * the Thread name.<BR>
     * e.g.:<BR>
     * nextMessageId[tab]14:04:30.718[tab]FileUploadManagerThread[tab][DEBUG][tab]
     * Found one reader for jpg extension
     * 
     * @param tag The tag ([WARN], [ERROR]...)
     * @param msg The message to format.
     * @return The formatted message, without trailing EOL character.
     */
    String formatMessageOutput(String tag, String msg) {
        final String stamp = String.format("%1$05d", this.nextMessageId++) + " \t"
                + new SimpleDateFormat("HH:mm:ss.SSS ").format(new Date())
                + "\t" + Thread.currentThread().getName() + "\t" + tag + " \t";
        while (msg.endsWith("\n")) {
            msg = msg.substring(0, msg.length() - 1);
        }
        return (stamp + msg.replaceAll("\n", "\n" + stamp));
    }

}