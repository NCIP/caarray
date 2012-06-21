package wjhk.jupload2.upload.helper;

import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wjhk.jupload2.exception.JUploadEOFException;
import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.exception.JUploadIOException;
import wjhk.jupload2.policies.UploadPolicy;

/**
 * A helper, to read the response coming from the server.
 * 
 * @author etienne_sf
 */
public class HTTPInputStreamReader {
    // //////////////////////////////////////////////////////////////////////////////
    // //////////////////// Main attributes
    // //////////////////////////////////////////////////////////////////////////////

    /**
     * The current upload policy, always useful.
     */
    private UploadPolicy uploadPolicy = null;

    private HTTPConnectionHelper httpConnectionHelper = null;

    /**
     * Contains the HTTP response bytearrayResponseBody, that is: the server
     * response, without the headers.
     */
    String stringResponseBody = null;

    private byte[] bytearrayResponseBody = new byte[0];

    /**
     * The headers of the HTTP response.
     */
    String responseHeaders = null;

    /**
     * The status message from the first line of the response (e.g. "200 OK").
     */
    String responseMsg = null;

    // ////////////////////////////////////////////////////////////////////////////////////
    // /////////////////// ATTRIBUTE CONTAINING DATA COMING FROM THE RESPONSE
    // ////////////////////////////////////////////////////////////////////////////////////

    private CookieJar cookies = null;

    boolean gotClose = false;

    private boolean gotChunked = false;

    private boolean gotContentLength = false;

    private int clen = 0;

    /**
     * The server HTTP response. Should be 200, in case of success.
     */
    private int httpStatusCode = 0;

    private String line = "";

    private String charset = "ISO-8859-1";

    // ////////////////////////////////////////////////////////////////////////////////////
    // /////////////////// CONSTANTS USED TO CONTROL THE HTTP INTPUT
    // ////////////////////////////////////////////////////////////////////////////////////
    private final static int CHUNKBUF_SIZE = 4096;

    private final byte chunkbuf[] = new byte[CHUNKBUF_SIZE];

    private final static Pattern pChunked = Pattern.compile(
            "^Transfer-Encoding:\\s+chunked", Pattern.CASE_INSENSITIVE);

    private final static Pattern pClose = Pattern.compile(
            "^Connection:\\s+close", Pattern.CASE_INSENSITIVE);

    private final static Pattern pProxyClose = Pattern.compile(
            "^Proxy-Connection:\\s+close", Pattern.CASE_INSENSITIVE);

    private final static Pattern pHttpStatus = Pattern
            .compile("^HTTP/\\d\\.\\d\\s+((\\d+)\\s+.*)$");

    private final static Pattern pContentLen = Pattern.compile(
            "^Content-Length:\\s+(\\d+)$", Pattern.CASE_INSENSITIVE);

    private final static Pattern pContentTypeCs = Pattern.compile(
            "^Content-Type:\\s+.*;\\s*charset=([^;\\s]+).*$",
            Pattern.CASE_INSENSITIVE);

    private final static Pattern pSetCookie = Pattern.compile(
            "^Set-Cookie:\\s+(.*)$", Pattern.CASE_INSENSITIVE);

    /**
     * The standard constructor: does nothing ! Oh yes, it initialize some
     * attribute from the given parameter... :-)
     * 
     * @param httpConnectionHelper The connection helper, associated with this
     *            instance.
     * @param uploadPolicy The current upload policy.
     */
    public HTTPInputStreamReader(HTTPConnectionHelper httpConnectionHelper,
            UploadPolicy uploadPolicy) {
        this.httpConnectionHelper = httpConnectionHelper;
        this.uploadPolicy = uploadPolicy;
        this.cookies = new CookieJar(uploadPolicy);
    }

    /**
     * Return the last read http response (200, in case of success).
     * 
     * @return The last read http response
     */
    public int gethttpStatusCode() {
        return this.httpStatusCode;
    }

    /**
     * Get the last response bytearrayResponseBody.
     * 
     * @return The last read response bytearrayResponseBody.
     */
    public String getResponseBody() {
        return this.stringResponseBody;
    }

    /**
     * Get the last response bytearrayResponseBody. This byte array should be
     * decoded by using the {@link #getResponseCharset()}.
     * 
     * @return The last read response bytearrayResponseBody.
     * 
     */
    public byte[] getResponseBodyAsByteArray() {
        return this.bytearrayResponseBody;
    }

    /**
     * Get the charset that should be used to decode the last response, when
     * using the {@link #getResponseBodyAsByteArray()} method.
     * 
     * @return The last read response bytearrayResponseBody.
     */
    public String getResponseCharset() {
        return this.charset;
    }

    /**
     * Get the headers of the HTTP response.
     * 
     * @return The HTTP headers.
     */
    public String getResponseHeaders() {
        return this.responseHeaders;
    }

    /**
     * Get the last response message.
     * 
     * @return The response message from the first line of the response (e.g.
     *         "200 OK").
     */
    public String getResponseMsg() {
        return this.responseMsg;
    }

    /**
     * The main method: reads the response in the input stream.
     * 
     * @return The response status (e.g.: 200 if everything was ok)
     * @throws JUploadException
     */
    public int readHttpResponse() throws JUploadException {
        PushbackInputStream httpDataIn = this.httpConnectionHelper
                .getInputStream();

        try {
            // We first read the headers,
            readHeaders(httpDataIn);

            // then the bytearrayResponseBody.
            // If we're in a HEAD request ... we're not interested in the
            // bytearrayResponseBody!
            if (this.httpConnectionHelper.getMethod().equals("HEAD")) {
                this.uploadPolicy
                        .displayDebug(
                                "This is a HEAD request: we don't care about the bytearrayResponseBody",
                                70);
                this.stringResponseBody = "";
            } else {
                readBody(httpDataIn);
            }
        } catch (JUploadException e) {
            throw e;
        } catch (Exception e) {
            throw new JUploadException(e);
        }

        return this.httpStatusCode;
    }

    // //////////////////////////////////////////////////////////////////////////////////////
    // //////////////////// Various utilities
    // //////////////////////////////////////////////////////////////////////////////////////

    /**
     * Concatenates two byte arrays.
     * 
     * @param buf1 The first array
     * @param buf2 The second array
     * @return A byte array, containing buf2 appended to buf2
     */
    static byte[] byteAppend(byte[] buf1, byte[] buf2) {
        byte[] ret = new byte[buf1.length + buf2.length];
        System.arraycopy(buf1, 0, ret, 0, buf1.length);
        System.arraycopy(buf2, 0, ret, buf1.length, buf2.length);
        return ret;
    }

    /**
     * Concatenates two byte arrays.
     * 
     * @param buf1 The first array
     * @param buf2 The second array
     * @param len Number of bytes to copy from buf2
     * @return A byte array, containing buf2 appended to buf2
     */
    static byte[] byteAppend(byte[] buf1, byte[] buf2, int len) {
        if (len > buf2.length)
            len = buf2.length;
        byte[] ret = new byte[buf1.length + len];
        System.arraycopy(buf1, 0, ret, 0, buf1.length);
        System.arraycopy(buf2, 0, ret, buf1.length, len);
        return ret;
    }

    /**
     * Similar like BufferedInputStream#readLine() but operates on raw bytes.
     * Line-Ending is <b>always</b> "\r\n".
     * 
     * @param inputStream
     * 
     * @param charset The input charset of the stream.
     * @param includeCR Set to true, if the terminating CR/LF should be included
     *            in the returned byte array.
     * @return The line, encoded from the input stream with the given charset
     * @throws IOException
     * @throws JUploadException
     */
    public static String readLine(PushbackInputStream inputStream,
            String charset, boolean includeCR) throws IOException,
            JUploadException {
        byte[] line = readLine(inputStream, includeCR);
        return (null == line) ? null : new String(line, charset);
    }

    /**
     * Similar like BufferedInputStream#readLine() but operates on raw bytes.
     * According to RFC 2616, and of line may be CR (13), LF (10) or CRLF.
     * Line-Ending is <b>always</b> "\r\n" in header, but not in text bodies.
     * Update done by TedA (sourceforge account: tedaaa). Allows to manage
     * response from web server that send LF instead of CRLF ! Here is a part of
     * the RFC: <I>"we recommend that applications, when parsing such headers,
     * recognize a single LF as a line terminator and ignore the leading
     * CR"</I>. <BR>
     * Corrected again to manage line finished by CR only. This is not allowed
     * in headers, but this method is also used to read lines in the
     * bytearrayResponseBody.
     * 
     * @param inputStream
     * 
     * @param includeCR Set to true, if the terminating CR/LF should be included
     *            in the returned byte array. In this case, CR/LF is always
     *            returned to the caller, whether the input stream got CR, LF or
     *            CRLF.
     * @return The byte array from the input stream, with or without a trailing
     *         CRLF
     * @throws IOException
     * @throws JUploadException
     */
    public static byte[] readLine(PushbackInputStream inputStream,
            boolean includeCR) throws IOException, JUploadException {
        final byte EOS = -1;
        final byte CR = 13;
        final byte LF = 10;
        int len = 0;
        int buflen = 128; // average line length
        byte[] buf = new byte[buflen];
        byte[] ret = null;
        int b;
        boolean lineRead = false;

        while (!lineRead) {
            try {
                b = inputStream.read();
            } catch (IOException ioe) {
                throw new JUploadIOException(ioe.getClass().getName() + ": "
                        + ioe.getMessage()
                        + " (while reading server response )", ioe);
            } catch (Exception e) {
                throw new JUploadException(e.getClass().getName() + ": "
                        + e.getMessage() + " (while reading server response )",
                        e);
            }
            switch (b) {
                case EOS:
                    // We've finished reading the stream, and so the line is
                    // finished too.
                    if (len == 0) {
                        return null;
                    }
                    lineRead = true;
                    break;
                /*
                 * if (len > 0) { ret = new byte[len]; System.arraycopy(buf, 0,
                 * ret, 0, len); return ret; } return null;
                 */
                case LF:
                    // We found the end of the current line.
                    lineRead = true;
                    break;
                case CR:
                    // We got a CR. It can be the end of line.
                    // Is it followed by a LF ? (not mandatory in RFC 2616)
                    b = inputStream.read();

                    if (b != LF) {
                        // The end of line was a simple LF: the next one blongs
                        // to the next line.
                        inputStream.unread(b);
                    }
                    lineRead = true;
                    break;
                default:
                    buf[len++] = (byte) b;
                    // If the buffer is too small, we let enough space to add CR
                    // and LF, in case of ...
                    if (len + 2 >= buflen) {
                        buflen *= 2;
                        byte[] tmp = new byte[buflen];
                        System.arraycopy(buf, 0, tmp, 0, len);
                        buf = tmp;
                    }
            }
        } // while

        // Let's go back to before any CR and LF.
        while (len > 0 && (buf[len] == CR || buf[len] == LF)) {
            len -= 1;
        }

        // Ok, now len indicates the end of the actual line.
        // Should we add a proper CRLF, or nothing ?
        if (includeCR) {
            // We have enough space to add these two characters (see the default
            // here above)
            buf[len++] = CR;
            buf[len++] = LF;
        }

        if (len > 0) {
            ret = new byte[len];
            if (len > 0)
                System.arraycopy(buf, 0, ret, 0, len);
        } else {
            // line feed for empty line between headers and
            // bytearrayResponseBody, or within the
            // bytearrayResponseBody.
            ret = new byte[0];
        }
        return ret;
    }

    /**
     * Read the headers from the given input stream.
     * 
     * @param httpDataIn The http input stream
     * @throws IOException
     * @throws JUploadException
     */
    private void readHeaders(PushbackInputStream httpDataIn)
            throws IOException, JUploadException {
        StringBuffer sbHeaders = new StringBuffer();
        // Headers are US-ASCII (See RFC 2616, Section 2.2)
        String tmp;
        // We must be reading the first line of the HTTP header.
        this.uploadPolicy.displayDebug(
                "-------- Response Headers Start --------", 80);

        do {
            tmp = readLine(httpDataIn, "US-ASCII", false);
            if (null == tmp) {
                throw new JUploadEOFException(this.uploadPolicy,
                        "reading headers");
            }
            if (this.httpStatusCode == 0) {
                Matcher m = pHttpStatus.matcher(tmp);
                if (m.matches()) {
                    this.httpStatusCode = Integer.parseInt(m.group(2));
                    this.responseMsg = m.group(1);
                } else {
                    // The status line must be the first line of the
                    // response. (See RFC 2616, Section 6.1) so this
                    // is an error.

                    // We first display the wrong line.
                    this.uploadPolicy.displayDebug("First line of response: '"
                            + tmp + "'", 80);
                    // Then, we throw the exception.
                    throw new JUploadException(
                            "HTTP response did not begin with status line.");
                }
            }
            // Handle folded headers (RFC 2616, Section 2.2). This is
            // handled after the status line, because that line may
            // not be folded (RFC 2616, Section 6.1).
            if (tmp.startsWith(" ") || tmp.startsWith("\t"))
                this.line += " " + tmp.trim();
            else
                this.line = tmp;

            // The read line is now correctly formatted.
            this.uploadPolicy.displayDebug(this.line, 80);
            sbHeaders.append(tmp).append("\n");

            if (pClose.matcher(this.line).matches())
                this.gotClose = true;
            if (pProxyClose.matcher(this.line).matches())
                this.gotClose = true;
            if (pChunked.matcher(this.line).matches())
                this.gotChunked = true;
            Matcher m = pContentLen.matcher(this.line);
            if (m.matches()) {
                this.gotContentLength = true;
                this.clen = Integer.parseInt(m.group(1));
            }
            m = pContentTypeCs.matcher(this.line);
            if (m.matches())
                this.charset = m.group(1);
            m = pSetCookie.matcher(this.line);
            if (m.matches()) {
                this.uploadPolicy.displayDebug(
                        "Calling this.cookies.parseCookieHeader, with parameter: "
                                + m.group(1), 80);
                this.cookies.parseCookieHeader(m.group(1));
                this.uploadPolicy.displayDebug("Cookie header parsed.", 80);
            }
            // RFC 2616, Section 6. Body is separated by the header with an
            // empty line: so end of headers is an empty line.
        } while (this.line.length() > 0);

        this.responseHeaders = sbHeaders.toString();
        this.uploadPolicy.displayDebug(
                "--------- Response Headers End ---------", 80);
    }// readHeaders()

    /**
     * Read the bytearrayResponseBody from the given input stream.
     * 
     * @param httpDataIn The http input stream
     * @throws IOException
     * @throws JUploadException
     * @throws JUploadException
     */
    private void readBody(PushbackInputStream httpDataIn) throws IOException,
            JUploadException {
        // && is evaluated from left to right so !stop must come first!
        while ((!this.gotContentLength) || (this.clen > 0)) {
            if (this.gotChunked) {
                // Read the chunk header.
                // This is US-ASCII! (See RFC 2616, Section 2.2)
                this.line = readLine(httpDataIn, "US-ASCII", false);
                if (null == this.line)
                    throw new JUploadEOFException(this.uploadPolicy,
                            "reading HTTP Body, HTTP chunked mode (1)");
                // Handle a single chunk of the response
                // We cut off possible chunk extensions and ignore them.
                // The length is hex-encoded (RFC 2616, Section 3.6.1)
                int len = Integer.parseInt(this.line.replaceFirst(";.*", "")
                        .trim(), 16);
                this.uploadPolicy.displayDebug("Chunk: " + this.line + " dec: "
                        + len, 70);
                if (len == 0) {
                    // RFC 2616, Section 3.6.1: A length of 0 denotes
                    // the last chunk of the bytearrayResponseBody.

                    // This code wrong if the server sends chunks with trailers!
                    // (trailers are HTTP Headers that are send *after* the
                    // bytearrayResponseBody. These are announced
                    // in the regular HTTP header "Trailer".
                    // Fritz: Never seen them so far ...
                    // TODO: Implement trailer-handling.
                    break;
                }

                // Loop over the chunk (len == length of the chunk)
                while (len > 0) {
                    int rlen = (len > CHUNKBUF_SIZE) ? CHUNKBUF_SIZE : len;
                    int ofs = 0;
                    if (rlen > 0) {
                        while (ofs < rlen) {
                            int res = httpDataIn.read(this.chunkbuf, ofs, rlen
                                    - ofs);
                            if (res < 0)
                                throw new JUploadEOFException(
                                        this.uploadPolicy,
                                        "reading body, HTTP chunk mode (2)");
                            len -= res;
                            ofs += res;
                        }
                        if (ofs < rlen)
                            throw new JUploadException("short read");
                        if (rlen < CHUNKBUF_SIZE)
                            this.bytearrayResponseBody = byteAppend(
                                    this.bytearrayResponseBody, this.chunkbuf,
                                    rlen);
                        else
                            this.bytearrayResponseBody = byteAppend(
                                    this.bytearrayResponseBody, this.chunkbuf);
                    }
                }
                // Got the whole chunk, read the trailing CRLF.
                readLine(httpDataIn, false);
            } else {
                // Not chunked. Use either content-length (if available)
                // or read until EOF.
                if (this.gotContentLength) {
                    // Got a Content-Length. Read exactly that amount of
                    // bytes.
                    while (this.clen > 0) {
                        int rlen = (this.clen > CHUNKBUF_SIZE) ? CHUNKBUF_SIZE
                                : this.clen;
                        int ofs = 0;
                        if (rlen > 0) {
                            while (ofs < rlen) {
                                int res = httpDataIn.read(this.chunkbuf, ofs,
                                        rlen - ofs);
                                if (res < 0)
                                    throw new JUploadEOFException(
                                            this.uploadPolicy,
                                            "reading HTTP bytearrayResponseBody, not chunked mode");
                                this.clen -= res;
                                ofs += res;
                            }
                            if (ofs < rlen)
                                throw new JUploadException("short read");
                            if (rlen < CHUNKBUF_SIZE)
                                this.bytearrayResponseBody = byteAppend(
                                        this.bytearrayResponseBody,
                                        this.chunkbuf, rlen);
                            else
                                this.bytearrayResponseBody = byteAppend(
                                        this.bytearrayResponseBody,
                                        this.chunkbuf);
                        }
                    }
                } else {
                    // No Content-length available, read until EOF
                    //
                    while (true) {
                        byte[] lbuf = readLine(httpDataIn, true);
                        if (null == lbuf)
                            break;
                        this.bytearrayResponseBody = byteAppend(
                                this.bytearrayResponseBody, lbuf);
                    }
                    break;
                }
            }
        } // while

        // Convert the whole bytearrayResponseBody according to the charset.
        // The default for charset ISO-8859-1, but overridden by
        // the charset attribute of the Content-Type header (if any).
        // See RFC 2616, Sections 3.4.1 and 3.7.1.
        this.stringResponseBody = new String(this.bytearrayResponseBody,
                this.charset);

        // At the higher debug level, we display the response.
        this.uploadPolicy.displayDebug("-------- Response Body Start --------",
                99);
        this.uploadPolicy.displayDebug(this.stringResponseBody, 99);
        this.uploadPolicy.displayDebug("-------- Response Body End --------",
                99);
    }// readBody
}
