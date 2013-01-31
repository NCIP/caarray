//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;

public class Sha1SumUtility {

	
	public static String getSha1SumFromStreamedFile(File rfile) {
		byte[] mdbytes = null;
		char[] chars = null;
		StringBuffer buffie = null;
		try {

			MessageDigest md = java.security.MessageDigest.getInstance("SHA-1");

			InputStream is = new FileInputStream(rfile);

			int numRead = 0;

			long len = rfile.length();

			byte[] bytes = new byte[new Long(len).intValue()];

			// System.out.println("len ="+ len);
			int total = 0;
			int offset = 0;

			while (total < len && offset < len
					&& (numRead = is.read(bytes, offset, bytes.length)) >= 0) {
				total += numRead;
				offset += numRead;
				md.update(bytes);

			}
			// System.out.println("total ="+ total);

			mdbytes = md.digest();

			buffie = new StringBuffer();
			int jj = 0;
			for (byte b : mdbytes) {
				// System.out.print(Integer.toHexString(b & 0xff) + " ");
				buffie.append(Integer.toHexString(b & 0xff));
			}

			// System.out.println("first method:" + buffie.toString());

			chars = getChars(mdbytes);

			// System.out.println(tc.digest());
		} catch (Exception e) {
			e.printStackTrace();

		}
		return buffie.toString();

	}

	// ###########################################################
	private static char[] getChars(byte[] bytes) {
		Charset cs = Charset.forName("8859_1");
		ByteBuffer bb = ByteBuffer.allocate(bytes.length);
		bb.put(bytes);
		bb.flip();
		CharBuffer cb = cs.decode(bb);

		return cb.array();
	}

	// ###########################################################
	static public void main(String[] args) {
		Sha1SumUtility ut = new Sha1SumUtility();
		String path = "/tmp/yjp200612271845.jar";
		// String sum = ut.getSha1sum(new File(path));

		// System.out.println(sum);

		String sum2 = ut.getSha1SumFromStreamedFile(new File(path));

		System.out.println(sum2);

	}
	
	
	// ==================================
	/*
	 * public static byte[] getBytesFromFile(File file) throws IOException {
	 * InputStream is = new FileInputStream(file);
	 *  // Get the size of the file long length = file.length();
	 *  // You cannot create an array using a long type. // It needs to be an
	 * int type. // Before converting to an int type, check // to ensure that
	 * file is not larger than Integer.MAX_VALUE. if (length >
	 * Integer.MAX_VALUE) { // File is too large }
	 *  // Create the byte array to hold the data byte[] bytes = new byte[(int)
	 * length];
	 *  // Read in the bytes int offset = 0; int numRead = 0; while (offset <
	 * bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >=
	 * 0) { offset += numRead; }
	 *  // Ensure all the bytes have been read in if (offset < bytes.length) {
	 * throw new IOException("Could not completely read file " +
	 * file.getName()); }
	 *  // Close the input stream and rString OMEIS_URLeturn bytes is.close();
	 * return bytes; }
	 */
	// ############################################################
	/*
	 * private String getSha1sum(File rfile) { byte[] mdbytes = null; char[]
	 * chars = null; StringBuffer buffie = null; try {
	 * 
	 * MessageDigest md = java.security.MessageDigest.getInstance("SHA-1"); //
	 * System.out.println("about to update digest");
	 * md.update(getBytesFromFile(rfile)); mdbytes = md.digest(); //
	 * System.out.println("Digest(in hex):: " + mdbytes); //
	 * System.out.println(rfile.getName() + ":" + mdbytes.length);
	 * 
	 * buffie = new StringBuffer(); int jj = 0; for (byte b : mdbytes) { //
	 * System.out.print(Integer.toHexString(b & 0xff) + " ");
	 * buffie.append(Integer.toHexString(b & 0xff)); }
	 *  // System.out.println("first method:" + buffie.toString());
	 * 
	 * chars = getChars(mdbytes);
	 *  // System.out.println(tc.digest()); } catch (Exception e) {
	 * e.printStackTrace();
	 *  } return buffie.toString();
	 *  }
	 */
	// ############################################################
	
	
	
	
	
	
	
	
	
	
}
