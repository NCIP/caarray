//
// $Id: JUploadPanelImpl.java 295 2007-06-27 08:43:25 +0000 (mer., 27 juin 2007)
// etienne_sf $
//
// jupload - A file upload applet.
// Copyright 2007 The JUpload Team
//
// Last modified: $Date: 2007-06-27 08:43:25 +0000 (mer., 27 juin 2007) $
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

package wjhk.jupload2.gui.image;

/**
 * This class contains the accessory that displays the image preview <B> in the
 * file chooser</B>, when in picture mode. 
 * 
 * @see PictureUploadPolicy
 */
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.filedata.PictureFileData;
import wjhk.jupload2.policies.UploadPolicy;

class LoadImageThread extends Thread {

    /**
     * That cursor that will be used each time the user select a new file, when
     * resizing the picture before displaying in the preview accessory.
     */
    final Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);

    /**
     * The file that is to be loaded.
     */
    File file;

    /**
     * The preview, where the resulting picture must be displayed.
     */
    JUploadImagePreview jUploadImagePreview;

    /**
     * Only constructor, with the file to be loaded.
     * 
     * @param file The file to load, once the thread is started.
     */
    LoadImageThread(JUploadImagePreview jUploadImagePreview, File file) {
        this.file = file;
        this.jUploadImagePreview = jUploadImagePreview;
    }

    /**
     * The work itself: it allows the loading and resizing of the picture in a
     * separate thread, to avoid blocking the user interface.
     */
    @Override
    public void run() {

        this.jUploadImagePreview.uploadPolicy.displayDebug(
                "LoadImageThread.start (start)", 50);
        this.jUploadImagePreview.jFileChooser.setCursor(this.waitCursor);
        ImageIcon thumbnail = null;
        try {
            thumbnail = PictureFileData.getImageIcon(this.file,
                    this.jUploadImagePreview.getWidth(),
                    this.jUploadImagePreview.getHeight(), 
                    this.jUploadImagePreview.uploadPolicy);
        } catch (JUploadException e) {
            this.jUploadImagePreview.uploadPolicy.displayErr(e);
        }

        // A try to minimize memory footprint
        PictureFileData.freeMemory("JUploadImagePreview.run()",
                this.jUploadImagePreview.uploadPolicy);

        if (thumbnail != null) {
            this.jUploadImagePreview.setThumbnail(thumbnail);
        }
        this.jUploadImagePreview.jFileChooser.setCursor(null);
        this.jUploadImagePreview.uploadPolicy.displayDebug(
                "LoadImageThread.start (end)", 50);
    }
}

/** ImagePreview.java by FileChooserDemo2.java. */
public class JUploadImagePreview extends JComponent implements
        PropertyChangeListener {

    /** A generated serialVersionUID, to avoid warning during compilation */
    private static final long serialVersionUID = -6882108570945459638L;

    /**
     * The current upload policy.
     */
    UploadPolicy uploadPolicy;

    /**
     * Current file chooser, which owns this file preview.
     */
    JFileChooser jFileChooser = null;

    /**
     * The picture, resized to the preview size.
     */
    ImageIcon thumbnail = null;

    /**
     * The selected picture, which should contain the picture to display.
     * Currently useless, as it is used only in the {@link #setFile(File)}
     * method. It may be useful, in the future..
     */
    File file = null;

    /**
     * The current thread, that is loading the picture. A new thread is created,
     * each time a new picture is to be loaded.
     */
    LoadImageThread loadImageThread = null;

    /**
     * The standard constructor for this class.
     * 
     * @param jFileChooser The current file chooser, which will contain this
     *            acessory.
     * @param uploadPolicy The current upload policy.
     */
    public JUploadImagePreview(JFileChooser jFileChooser,
            UploadPolicy uploadPolicy) {
        this.jFileChooser = jFileChooser;
        this.uploadPolicy = uploadPolicy;

        setPreferredSize(new Dimension(200, 200));
        jFileChooser.addPropertyChangeListener(this);
    }

    /**
     * Changes the current picture to display. This method is called by
     * {@link LoadImageThread#start()} method, when the resized picture has been
     * calculated.
     * 
     * @param thumbnail
     */
    void setThumbnail(ImageIcon thumbnail) {
        this.thumbnail = thumbnail;
        repaint();
    }

    /**
     * Changes the current file: this erases the current displayed picture, then
     * call the {@link LoadImageThread#start()} method. This generate the
     * picture asynchroneously. Directories are ignored.
     */
    void setFile(File fileParam) {
        if (fileParam != null && fileParam.isDirectory()) {
            this.file = null;
        } else {
            this.file = fileParam;
        }

        // First: clear the current picture.
        this.thumbnail = null;
        repaint();

        // If a thread is running, let's stop it.
        if (this.loadImageThread != null && this.loadImageThread.isAlive()) {
            // Let's forget this thread.
            this.loadImageThread.interrupt();
            this.loadImageThread = null;
        }

        // Next: load aysnchronously the picture.
        if (this.file != null) {
            this.loadImageThread = new LoadImageThread(this, this.file);
            // We want this thread to be executed before the icon loading
            // threads.
            this.loadImageThread.setPriority(Thread.MAX_PRIORITY);
            // Let's start the thread, and exit: the applet is not blocked.
            this.loadImageThread.start();
            repaint();
        }
    }

    /**
     * Hum, we're interested in these events: DIRECTORY_CHANGED_PROPERTY and
     * SELECTED_FILE_CHANGED_PROPERTY.
     * 
     * @param e
     */
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
            // The directory changed, don't show an image.
            setFile(null);
        } else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
            // If a file became selected, find out which one.
            setFile((File) e.getNewValue());
        }
    }

    /**
     * Actual display of the picture. We just have to center the thumbnail,
     * here.
     */
    @Override
    protected void paintComponent(Graphics g) {
        // Do we have a picture to display ?
        if (this.thumbnail != null) {
            int x = getWidth() / 2 - this.thumbnail.getIconWidth() / 2;
            int y = getHeight() / 2 - this.thumbnail.getIconHeight() / 2;
            if (y < 0) {
                y = 0;
            }
            if (x < 5) {
                x = 5;
            }
            this.thumbnail.paintIcon(this, g, x, y);
            this.uploadPolicy.displayDebug(
                    "JUploadImagePreview.paintComponent, after paintIcon", 50);
        }
    }
}
