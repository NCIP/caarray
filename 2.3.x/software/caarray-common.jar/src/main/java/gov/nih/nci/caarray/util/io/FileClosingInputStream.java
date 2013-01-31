//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/**
 * InputStream implementation that automatically deletes a file when
 * the underlying stream is exhaused, or in error cases.
 */
public class FileClosingInputStream extends InputStream {

    private final InputStream is;
    private final File f;

    /**
     * @param is the inputstream to wrap
     * @param f the file to delete when done
     */
    public FileClosingInputStream(InputStream is, File f) {
        this.is = is;
        this.f = f;
        f.deleteOnExit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int available() throws IOException {
        return is.available();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(is);
        f.delete();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void mark(int readlimit) {
        is.mark(readlimit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean markSupported() {
        return is.markSupported();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return is.read(b, off, len);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] b) throws IOException {
        return is.read(b);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void reset() throws IOException {
        is.reset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long skip(long n) throws IOException {
        return is.skip(n);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws IOException {
        return is.read();
    }

    /**
     * {@inheritDoc}
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

}
