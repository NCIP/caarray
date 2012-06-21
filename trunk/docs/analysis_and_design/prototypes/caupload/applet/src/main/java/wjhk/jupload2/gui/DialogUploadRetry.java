//
// $Id: DialogPicturePanel.java 95 2007-05-02 03:27:05 +0000 (mer., 02 mai 2007)
// /C=DE/ST=Baden-Wuerttemberg/O=ISDN4Linux/OU=Fritz
// Elfert/CN=svn-felfert@isdn4linux.de/emailAddress=fritz@fritz-elfert.de $
//
// jupload - A file upload applet.
// Copyright 2007 The JUpload Team
//
// Created: 2006-07-11
// Creator: etienne_sf
// Last modified: $Date: 2009-02-16 12:42:50 +0100 (lun., 16 févr. 2009) $
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

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;

import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.policies.UploadPolicy;

/**
 * This JDialog displays a message to the user, to allow him to accept or refuse
 * a retry of an upload, when a 'resumable upload error' occurs. The message is
 * displayed, followed by a timer countdown. When the timer value falls to 0,
 * the retry is automatically executed. This allows the user to quit is screen,
 * and have automatic retries, even for long upload.
 * 
 * @author etienne_sf
 */
@SuppressWarnings("serial")
public class DialogUploadRetry extends JDialog implements ActionListener,
        ComponentListener {

    JButton buttonYes;

    JButton buttonNo;

    JButton buttonDetails;

    JTextArea jtextArea;

    JTextArea detailTestArea;

    JUploadException juploadException = null;

    UploadPolicy uploadPolicy = null;

    /**
     * This timer allows the display of seconds countdown before automatic
     * retry, to the user
     */
    Timer countdownTimer = null;

    /**
     * How many seconds, before automatic validation (response=yes) of this
     * dialog box
     */
    int countdownValue;

    /**
     * Indicates whether the user choosed to accept or refuse the retry. If the
     * countdown falls to 0, this boolean is switched to true, for an automatic
     * retry.
     */
    boolean retryValidated = false;

    /**
     * Creates a new instance.
     * 
     * @param owner The parent frame. Mandatory, as this is a modal dialog.
     * @param juploadException The exception, which occurs. Used to present an
     *            error message.
     * @param numRetry number of the current retry
     * @param uploadPolicy The upload policy which applies.
     * @throws JUploadException
     */
    public DialogUploadRetry(Frame owner, JUploadException juploadException,
            int numRetry, UploadPolicy uploadPolicy) throws JUploadException {
        super(owner, uploadPolicy.getLocalizedString("dialogUploadRetryTitle"),
                true);

        this.uploadPolicy = uploadPolicy;
        this.juploadException = juploadException;

        // Creation of the countdown timer.
        // The number of seconds between two retries is exponentiel: as
        // parameterized at the beginning, then more and more time between two
        // uploads.
        // The first retry should use the applet parameter value, so, we should
        // have pow(1.5, 0) for this one. Actually, the first call to this
        // dialog occurs after the first try, so numRetry is 0 then.
        double retryDelayFactor = Math.pow(1.5, numRetry);
        this.countdownValue = (int) (uploadPolicy.getRetryNbSecondsBetween() * retryDelayFactor);
        this.countdownTimer = new Timer(1000, this);
        this.countdownTimer.start();

        // Creates the components. This must be done in the AWT event
        // dispatching thread.
        try {
            javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    createGUI();
                }
            });
        } catch (InterruptedException e) {
            throw new JUploadException("Error while creating the "
                    + this.getClass().getName(), e.getCause());
        } catch (InvocationTargetException e) {
            throw new JUploadException("Error while creating the "
                    + this.getClass().getName(), e.getCause());
        }

        // The dialog is modal: the next line will return when the DialogPicture
        // is hidden (to be closed, in our case)
        // But we want to know when it will becom visible, to clear the wait
        // cursor.
        setVisible(true);

        // We arrive here, when the dialog is closed.
    }

    /**
     * This method allows the caller, to know if the retry of the last upload
     * should be executed.
     * 
     * @return the retryValidated
     */
    public boolean isRetryValidated() {
        return this.retryValidated;
    }

    /**
     * Creation of the GUI, based on the current parameters
     */
    private void createGUI() {
        // //////////////////////////////////////////////////////////
        // //////////// 1) ERROR TEXT AREA (with countdown value)
        // //////////////////////////////////////////////////////////
        JPanel jPanelText = new JPanel();
        this.jtextArea = new JTextArea(this.uploadPolicy.getLocalizedString(
                "dialogUploadRetryText", this.countdownValue));
        this.jtextArea.setEditable(false);
        this.jtextArea.setMinimumSize(new Dimension(400, 100));
        this.jtextArea.setBackground(jPanelText.getBackground());
        jPanelText.add(this.jtextArea);
        getContentPane().add(jPanelText, BorderLayout.CENTER);

        // //////////////////////////////////////////////////////////
        // //////////// 2) BUTTONS
        // //////////////////////////////////////////////////////////
        // Creation of the yes button
        this.buttonYes = new JButton(this.uploadPolicy
                .getLocalizedString("buttonYes"));
        this.buttonYes.setMinimumSize(new Dimension(80, 30));
        this.buttonYes.setMaximumSize(new Dimension(100, 100));
        this.buttonYes.addActionListener(this);
        // Creation of the nobutton
        this.buttonNo = new JButton(this.uploadPolicy
                .getLocalizedString("buttonNo"));
        this.buttonNo.setMinimumSize(new Dimension(80, 30));
        this.buttonNo.setMaximumSize(new Dimension(100, 100));
        this.buttonNo.addActionListener(this);
        // Creation of the nobutton
        this.buttonDetails = new JButton(this.uploadPolicy
                .getLocalizedString("buttonDetails"));
        this.buttonDetails.setMinimumSize(new Dimension(80, 30));
        this.buttonDetails.setMaximumSize(new Dimension(100, 100));
        this.buttonDetails.addActionListener(this);

        JPanel jPanelButton = new JPanel();
        jPanelButton
                .setLayout(new BoxLayout(jPanelButton, BoxLayout.LINE_AXIS));
        jPanelButton.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        jPanelButton.add(Box.createHorizontalGlue());
        jPanelButton.add(this.buttonYes);
        jPanelButton.add(Box.createRigidArea(new Dimension(10, 0)));
        jPanelButton.add(this.buttonNo);
        jPanelButton.add(Box.createRigidArea(new Dimension(10, 0)));
        jPanelButton.add(this.buttonDetails);

        // //////////////////////////////////////////////////////////
        // //////////// 3) DETAIL TEXT AREA
        // //////////////////////////////////////////////////////////
        JPanel jPanelDetail = new JPanel();
        this.detailTestArea = new JTextArea(this.juploadException.getMessage());
        this.detailTestArea.setEditable(false);
        this.detailTestArea.setMinimumSize(new Dimension(400, 500));
        this.detailTestArea.setBackground(jPanelDetail.getBackground());
        jPanelDetail.add(this.detailTestArea);
        getContentPane().add(jPanelDetail, BorderLayout.CENTER);

        // //////////////////////////////////////////////////////////
        // //////////// 4) Agregation in the root pane
        // //////////////////////////////////////////////////////////
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.add(this.jtextArea);
        mainPanel.add(jPanelButton);
        mainPanel.add(jPanelDetail);

        getContentPane().add(mainPanel);
        getRootPane().setDefaultButton(this.buttonYes);

        // Let's put all this together.
        pack();

        // Details are hidden, at the beginning
        this.detailTestArea.setVisible(false);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.countdownTimer) {
            // One timer event every second.
            this.countdownValue -= 1;
            if (this.countdownValue <= 0) {
                disposeDialog(true);
            } else {
                this.jtextArea.setText(this.uploadPolicy.getLocalizedString(
                        "dialogUploadRetryText", this.countdownValue));
            }
        } else if (event.getActionCommand() == this.buttonDetails
                .getActionCommand()) {
            this.uploadPolicy
                    .displayDebug(
                            "[DialogUploadRetry] User choose to display or hide details",
                            10);
            this.detailTestArea.setVisible(!this.detailTestArea.isVisible());
            // Resizing of the components
            if (this.detailTestArea.isVisible()) {
                this.detailTestArea.setMinimumSize(new Dimension(400, 500));
                this.detailTestArea.setMaximumSize(new Dimension(400, 500));
            } else {
                this.detailTestArea.setMinimumSize(new Dimension(0, 0));
                this.detailTestArea.setMaximumSize(new Dimension(0, 0));
            }
        } else if (event.getActionCommand() == this.buttonNo.getActionCommand()) {
            this.uploadPolicy.displayDebug(
                    "[DialogUploadRetry] User choose buttonNo", 10);
            disposeDialog(false);
        } else if (event.getActionCommand() == this.buttonYes
                .getActionCommand()) {
            this.uploadPolicy.displayDebug(
                    "[DialogUploadRetry] User choose buttonYes", 10);
            disposeDialog(true);
        }
    }

    /**
     * Actually closes the dialog box, and set the retryValidated status
     * 
     * @param retryValidated
     */
    private void disposeDialog(boolean retryValidated) {
        this.retryValidated = retryValidated;
        this.countdownTimer.stop();
        this.dispose();
    }

    /** {@inheritDoc} */
    public void componentHidden(ComponentEvent arg0) {
        // No action
    }

    /** {@inheritDoc} */
    public void componentMoved(ComponentEvent arg0) {
        // No action
    }

    /** {@inheritDoc} */
    public void componentResized(ComponentEvent arg0) {
        // No action
    }

    /** {@inheritDoc} */
    public void componentShown(ComponentEvent arg0) {
        // We set the cursor back to normal
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
