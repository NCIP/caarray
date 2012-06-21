//
// $Id: JUploadApplet.java 1376 2010-07-28 21:47:39Z etienne_sf $
//
// jupload - A file upload applet.
// Copyright 2007 The JUpload Team
//
// Created: ?
// Creator: William JinHua Kwong
// Last modified: $Date: 2010-07-28 23:47:39 +0200 (mer., 28 juil. 2010) $
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

package wjhk.jupload2;

import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.swing.JApplet;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import wjhk.jupload2.context.JUploadContext;
import wjhk.jupload2.context.JUploadContextApplet;
import wjhk.jupload2.context.JavascriptHandler;
import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.policies.UploadPolicy;

/**
 * The applet. It contains quite only the call to creation of the
 * {@link JUploadContextApplet}, which contains the technical context. This
 * context is responsible for loading the relevant {@link UploadPolicy}. <BR>
 * <BR>
 * The behavior of the applet can easily be adapted, by : <DIR> <LI>Using an
 * existing {@link wjhk.jupload2.policies.UploadPolicy}, and specifying
 * parameters. <LI>Creating a new upload policy, based on the
 * {@link wjhk.jupload2.policies.DefaultUploadPolicy}, or created from scratch.
 * <BR>
 * For all details on this point, please read the <a
 * href="../../../howto-customization.html">howto-customization.html</a> page.
 * 
 * @author William JinHua Kwong (largely updated by etienne_sf)
 * @version $Revision: 1376 $
 */
public class JUploadApplet extends JApplet {

	/** A generated serialVersionUID, to avoid warning during compilation */
	private static final long serialVersionUID = -3207851532114846776L;

	/**
	 * The current execution context.
	 */
	transient JUploadContext juploadContext = null;

	/**
	 * Called each time the applet is shown on the web page.
	 */
	@Override
	public void init() {
		class JUploadAppletInitializer implements Runnable {
			JUploadApplet applet;

			JUploadAppletInitializer(JUploadApplet applet) {
				this.applet = applet;
			}

			public void run() {
				juploadContext = new JUploadContextApplet(applet);
			}
		}

		try {
			SwingUtilities.invokeAndWait(new JUploadAppletInitializer(this));
		} catch (InterruptedException e) {
			// Hum, if we get here, there may be no logging system built ..
			// Let's output something in the Java consoles
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// Hum, if we get here, there may be no logging system built ..
			// Let's output something in the Java consoles
			e.printStackTrace();
		}

		if (this.juploadContext == null) {
			JOptionPane
					.showMessageDialog(
							null,
							"An error occured during applet initialization. Please check the java console output",
							"Alert", JOptionPane.ERROR_MESSAGE);
		} else {
			// Let's refresh the display, and have the caret well placed.
			try {
				this.juploadContext.getUploadPolicy().displayInfo(
						"JUploadApplet is now initialized.");
			} catch (JUploadException e) {
				// Can't use standard JUpload log mode...
				System.out.println("JUploadApplet is now initialized.");
			}
		}
	}

	/**
	 * Called each time the applet is shown on the web page.
	 */
	@Override
	public void start() {
		if (this.juploadContext == null) {
			String msg = "An error occured during applet initialization. Please check the java console output (juploadContext is null in applet.start())";
			JOptionPane.showMessageDialog(null, msg, "Alert",
					JOptionPane.ERROR_MESSAGE);
			throw new java.lang.IllegalStateException(msg);
		} else {
			try {
				this.juploadContext.getUploadPolicy().start();
				this.juploadContext.getUploadPolicy().displayInfo(
						"JUploadApplet is now started.");
			} catch (JUploadException e) {
				// Can't use standard JUpload log mode...
				System.out.println("JUploadApplet is now started.");
			}
			this.validate();
		}
	}

	/**
	 * @see java.applet.Applet#stop()
	 */
	@Override
	public void stop() {
		try {
			this.juploadContext.getUploadPolicy().displayInfo(
					"JUploadApplet is now stopped.");
		} catch (JUploadException e) {
			// Can't use standard JUpload log mode...
			System.out.println("JUploadApplet is now stopped.");
		}
	}

	/**
	 * @see java.applet.Applet#destroy()
	 */
	@Override
	public void destroy() {
		class JUploadAppletDestroyer implements Runnable {
			JUploadApplet applet;

			JUploadAppletDestroyer(JUploadApplet applet) {
				this.applet = applet;
			}

			public void run() {
				applet.juploadContext.runUnload();
				applet.getContentPane().removeAll();
			}
		}
		try {
			this.juploadContext.getUploadPolicy().displayInfo(
					"JUploadApplet is being destroyed.");
		} catch (JUploadException e1) {
			// Can't use standard JUpload log mode...
			System.out.println("JUploadApplet is now destroyed.");
		}

		// Execute a job on the event-dispatching thread:
		// destroying this applet's GUI.
		try {
			SwingUtilities.invokeAndWait(new JUploadAppletDestroyer(this));
		} catch (Exception e) {
		}
	}

	/**
	 * This allow runtime modifications of properties, from javascript.
	 * Currently, this can only be used after full initialization. This method
	 * only calls the UploadPolicy.setProperty method. <BR>
	 * Ex: document.jupload.setProperty(prop, value);
	 * 
	 * @param prop
	 *            The property name that must be set.
	 * @param value
	 *            The value of this property.
	 * @see JUploadContext#setProperty(String, String)
	 */
	public void setProperty(String prop, String value) {
		this.juploadContext.setProperty(prop, value);
	}

	/**
	 * Javascript can call this method to start the upload.
	 * 
	 * @return Returns the upload result. See the constants defined in the
	 *         {@link JavascriptHandler} javadoc.
	 */
	public String startUpload() {
		return this.juploadContext.startUpload();
	}

	/**
	 * @see java.applet.Applet#getParameterInfo()
	 */
	public String[][] getParameterInfo() {
		// FIXME Implement Applet.getParameterInfo()
		return null;
	}

	/**
	 * @see java.applet.Applet#getAppletInfo()
	 */
	public String getAppletInfo() {
		return "JUpload applet, available at http://jupload.sourceforge.net";
	}
	

	public String selectFiles() {
        System.out.println("******************************************************");
        System.out.println("** JavaScript to Applet bridge created successfully **");
        System.out.println("******************************************************");
	    
        String filesInfoJSON = (String) AccessController.doPrivileged( new PrivilegedAction<Object>(){
            public Object run() {
                juploadContext.selectFiles();
                return juploadContext.getSelectedFilesInfoJSON();
            }
        });
        
        return filesInfoJSON;
	}
	
    public String trackProgress() {
        String progressInfoJSON = (String) AccessController.doPrivileged( new PrivilegedAction<Object>(){
            public Object run() {
                return juploadContext.getProgressInfoJSON();
            }
        });
        
        return progressInfoJSON;
    }
    
    public boolean isUploadFinished() {
        Boolean bUploadFinished = (Boolean) AccessController.doPrivileged( new PrivilegedAction<Object>(){
            public Object run() {
                return juploadContext.isUploadFinished();
            }
        });
        
        return bUploadFinished;
    }

}
