package wjhk.jupload2.upload.helper;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import wjhk.jupload2.exception.JUploadIOException;
import wjhk.jupload2.upload.AbstractJUploadTestHelper;

/**
 * The JUnit test for the ByteArrayEncoderHTTP. Allows automation of tests, to
 * avoid regressions.
 * 
 * @author etienne_sf
 */
public class ByteArrayEncoderHTTPTest extends AbstractJUploadTestHelper {

	ByteArrayEncoderHTTP byteArrayEncoderHTTP = null;

	final static String testCase = "A string, with accents: שאיט\u00f8\u00e5\u00d8\u00e6";

	String boundary = "A boundary";

	String encoding = null;

	byte[] target = null;

	/**
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.byteArrayEncoderHTTP = new ByteArrayEncoderHTTP(
				this.juploadContext.getUploadPolicy(), this.boundary,
				ByteArrayEncoderHTTP.DEFAULT_ENCODING);
		this.encoding = this.byteArrayEncoderHTTP.getEncoding();
		this.target = testCase.getBytes(this.encoding);
	}

	/**
	 * @throws Exception
	 *             The test is wrong, if this exception is fired
	 */
	@Test
	public void testByteArrayEncoderHTTPUploadPolicy() throws Exception {
		this.byteArrayEncoderHTTP.close();
		this.byteArrayEncoderHTTP = new ByteArrayEncoderHTTP(
				this.juploadContext.getUploadPolicy());
		// Nothing else to do, we just check the instance creation.
	}

	/**
	 * @throws Exception
	 *             The test is wrong, if this exception is fired
	 */
	@Test
	public void testByteArrayEncoderHTTPUploadPolicyString() throws Exception {
		this.byteArrayEncoderHTTP.close();
		this.byteArrayEncoderHTTP = new ByteArrayEncoderHTTP(
				this.juploadContext.getUploadPolicy(), null);
		this.byteArrayEncoderHTTP.close();
		this.byteArrayEncoderHTTP = new ByteArrayEncoderHTTP(
				this.juploadContext.getUploadPolicy(), "A boundary");
		// Nothing else to do, we just check the instance creation.
	}

	/**
	 * @throws Exception
	 *             The test is wrong, if this exception is fired
	 */
	@Test
	public void testByteArrayEncoderHTTPUploadPolicyStringString()
			throws Exception {
		this.byteArrayEncoderHTTP.close();
		this.byteArrayEncoderHTTP = new ByteArrayEncoderHTTP(
				this.juploadContext.getUploadPolicy(), "A boundary",
				ByteArrayEncoderHTTP.DEFAULT_ENCODING);
		// Nothing else to do, we just check the instance creation.
	}

	private void finishTestAppend() throws Exception {
		this.byteArrayEncoderHTTP.close();
		byte[] result = this.byteArrayEncoderHTTP.getEncodedByteArray();
		Assert.assertTrue(Arrays.equals(result, this.target));
	}

	/**
	 * @throws Exception
	 *             The test is wrong, if this exception is fired
	 */
	@Test
	public void testAppendString() throws Exception {
		this.byteArrayEncoderHTTP.append(testCase);
		finishTestAppend();
	}

	/**
	 * @throws Exception
	 *             The test is wrong, if this exception is fired
	 */
	@Test
	public void testAppendInt() throws Exception {
		this.byteArrayEncoderHTTP.append(65);
		this.byteArrayEncoderHTTP.close();
		byte[] result = this.byteArrayEncoderHTTP.getEncodedByteArray();
		byte[] targetInt = { 65 };
		Assert.assertTrue(Arrays.equals(result, targetInt));
	}

	/**
	 * @throws Exception
	 *             The test is wrong, if this exception is fired
	 */
	@Test
	public void testAppendByteArray() throws Exception {
		this.byteArrayEncoderHTTP.append(this.target);
		finishTestAppend();
	}

	/**
	 * @throws Exception
	 *             The test is wrong, if this exception is fired
	 */
	@Test
	public void testAppendByteArrayEncoder() throws Exception {
		ByteArrayEncoderHTTP bae = new ByteArrayEncoderHTTP(this.juploadContext
				.getUploadPolicy());
		bae.append(ByteArrayEncoderHTTPTest.testCase);
		bae.close();
		// append should throw an exception, if executed on a non-closed
		this.byteArrayEncoderHTTP.append(bae);
		finishTestAppend();
	}

	/**
	 * @throws Exception
	 *             The test is wrong, if this exception is fired
	 */
	@Test
	public void testAppendTextProperty() throws Exception {
		String name = "A name";
		String value = "A value";

		// First: calculate the result.
		StringBuffer sb = new StringBuffer();
		sb.append(this.byteArrayEncoderHTTP.getBoundary()).append("\r\n");
		sb.append("Content-Disposition: form-data; name=\"").append(name)
				.append("\"\r\n");
		sb.append("Content-Transfer-Encoding: 8bit\r\n");
		sb.append("Content-Type: text/plain; charset=").append(this.encoding).append(
				"\r\n");
		sb.append("\r\n");
		sb.append(value).append("\r\n");
		this.target = sb.toString().getBytes(this.encoding);

		// Then, do the test.
		this.byteArrayEncoderHTTP.appendTextProperty(name, value, -1);
		finishTestAppend();
	}

	/**
	 * @throws Exception
	 *             The test is wrong, if this exception is fired
	 */
	@Test
	public void testAppendEndPropertyList() throws Exception {
		// First: calculate the result.
		StringBuffer sb = new StringBuffer();
		sb.append(this.byteArrayEncoderHTTP.getBoundary()).append("--\r\n");
		this.target = sb.toString().getBytes(this.encoding);

		// Then, do the test.
		this.byteArrayEncoderHTTP.appendEndPropertyList();
		finishTestAppend();
	}

	/**
	 * @throws Exception
	 *             The test is wrong, if this exception is fired
	 */
	@Test
	public void testGetBoundary() throws Exception {
		Assert.assertTrue("Boundary should be the one given on creation",
				this.boundary.equals(this.byteArrayEncoderHTTP.getBoundary()));
	}

	/**
	 * @throws Exception
	 *             The test is wrong, if this exception is fired
	 */
	@Test
	public void testGetDefaultEncoding() throws Exception {
		Assert.assertTrue("UTF-8".equals(ByteArrayEncoderHTTP
				.getDefaultEncoding()));
	}

	/**
	 * @throws Exception
	 *             The test is wrong, if this exception is fired
	 */
	@Test
	public void testIsClosed() throws Exception {
		Assert
				.assertTrue(this.byteArrayEncoderHTTP.isClosed() == this.byteArrayEncoderHTTP.closed);
	}

	/**
	 * @throws Exception
	 *             The test is wrong, if this exception is fired
	 */
	@Test
	public void testGetEncoding() throws Exception {
		Assert.assertTrue(this.byteArrayEncoderHTTP.getEncoding().equals(
				this.encoding));
	}

	/**
	 * @throws Exception
	 *             The test is wrong, if this exception is fired
	 */
	@Test
	public void testGetEncodedLength() throws Exception {
		testAppendString();
		Assert
				.assertTrue(this.byteArrayEncoderHTTP.getEncodedLength() == this.target.length);
	}

	/**
	 * @throws Exception
	 *             The test is wrong, if this exception is fired
	 */
	@Test
	public void testGetEncodedByteArray() throws Exception {
		this.byteArrayEncoderHTTP.append("Any string");

		boolean aJUploadIOExceptionWasFired = false;
		try {
			this.byteArrayEncoderHTTP.getEncodedByteArray();
		} catch (JUploadIOException e) {
			aJUploadIOExceptionWasFired = true;
		}
		Assert
				.assertTrue(
						"An exception should be fired when getEncodedByteArray is called on a non closed ByteArray",
						aJUploadIOExceptionWasFired);
	}

}
