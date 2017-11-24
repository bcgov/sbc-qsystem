/*
 *  Copyright (C) 1998-2000 COMITA Ltd. All Rights Reserved.
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.apertum.qsystem.common;

/**
 * CodepagePrintStream.java
 *
 * Copyright (C) 1998-2000 COMITA Ltd. All Rights Reserved.
 */

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Класс реализует методы PrintStream с возможностью работы в нужной кодировке.
 *
 * Обычно используется для замены стандартных System.out и System.err:
 * <pre>
 *  public static void main(String[] args)
 *  {
 *   // Установка вывода консольных сообщений в нужной кодировке
 *   try
 *     {
 *      System.setOut(new CodepagePrintStream(System.out,System.getProperty("console.encoding","Cp866"))
 * );
 *     }
 *   catch(UnsupportedEncodingException e)
 *     {
 *      Msg.message(Msg.ALERT,"Unable to setup console codepage: " + e);
 *      Msg.printStackTrace(Msg.ALERT,e);
 *     }
 * </pre>
 *
 * @author Sergey Astakhov
 * @version 1.01, 24.07.2000
 */
public class CodepagePrintStream extends PrintStream {

    private boolean autoFlush = false;
    private boolean trouble = false;
    /**
     * Track both the text- and character-output streams, so that their buffers can be flushed
     * without flushing the entire stream.
     */
    private BufferedWriter textOut;
    private OutputStreamWriter charOut;
    private boolean closing = false; /* To avoid recursive closing */

    public CodepagePrintStream(OutputStream os, String cp) throws UnsupportedEncodingException {
        super(os);

        this.autoFlush = false;
        this.charOut = new OutputStreamWriter(this, cp);
        this.textOut = new BufferedWriter(this.charOut);
    }

    /**
     * Check to make sure that the stream has not been closed
     */
    private void ensureOpen() throws IOException {
        if (out == null) {
            throw new IOException("Stream closed");
        }
    }

    /**
     * Flush the stream.  This is done by writing any buffered output bytes to the underlying output
     * stream and then flushing that stream.
     *
     * @see java.io.OutputStream#flush()
     */
    @Override
    public void flush() {
        synchronized (this) {
            try {
                ensureOpen();
                out.flush();
            } catch (IOException x) {
                trouble = true;
            }
        }
    }

    /**
     * Close the stream.  This is done by flushing the stream and then closing the underlying output
     * stream.
     *
     * @see java.io.OutputStream#close()
     */
    @Override
    public void close() {
        synchronized (this) {
            if (!closing) {
                closing = true;
                try {
                    textOut.close();
                    out.close();
                } catch (IOException x) {
                    trouble = true;
                }
                textOut = null;
                charOut = null;
                out = null;
            }
        }
    }

    /**
     * Flush the stream and check its error state.  The internal error state is set to
     * <code>true</code> when the underlying output stream throws an <code>IOException</code> other
     * than <code>InterruptedIOException</code>, and when the <code>setError</code> method is
     * invoked. If an operation on the underlying output stream throws an
     * <code>InterruptedIOException</code>, then the <code>PrintStream</code> converts the exception
     * back into an interrupt by doing:
     * <pre>
     *     Thread.currentThread().interrupt();
     * </pre>
     * or the equivalent.
     *
     * @return True if and only if this stream has encountered an <code>IOException</code> other
     * than <code>InterruptedIOException</code>, or the <code>setError</code> method has been
     * invoked
     */
    @Override
    public boolean checkError() {
        if (out != null) {
            flush();
        }
        return trouble;
    }

    /**
     * Set the error state of the stream to <code>true</code>.
     *
     * @since JDK1.1
     */
    @Override
    protected void setError() {
        trouble = true;
    }

    /*
     * Exception-catching, synchronized output operations,
     * which also implement the write() methods of OutputStream
     */

    /**
     * Write the specified byte to this stream.  If the byte is a newline and automatic flushing is
     * enabled then the <code>flush</code> method will be invoked.
     *
     * <p> Note that the byte is written as given; to write a character that will be translated
     * according to the platform's default character encoding, use the <code>print(char)</code> or
     * <code>println(char)</code> methods.
     *
     * @param b The byte to be written
     * @see #print(char)
     * @see #println(char)
     */
    @Override
    public void write(int b) {
        try {
            synchronized (this) {
                ensureOpen();
                out.write(b);
                if ((b == '\n') && autoFlush) {
                    out.flush();
                }
            }
        } catch (InterruptedIOException x) {
            Thread.currentThread().interrupt();
        } catch (IOException x) {
            trouble = true;
        }
    }

    /**
     * Write <code>len</code> bytes from the specified byte array starting at offset
     * <code>off</code> to this stream.  If automatic flushing is enabled then the
     * <code>flush</code> method will be invoked.
     *
     * <p> Note that the bytes will be written as given; to write characters that will be translated
     * according to the platform's default character encoding, use the <code>print(char)</code> or
     * <code>println(char)</code> methods.
     *
     * @param buf A byte array
     * @param off Offset from which to start taking bytes
     * @param len Number of bytes to write
     */
    @Override
    public void write(byte buf[], int off, int len) {
        try {
            synchronized (this) {
                ensureOpen();
                out.write(buf, off, len);
                if (autoFlush) {
                    out.flush();
                }
            }
        } catch (InterruptedIOException x) {
            Thread.currentThread().interrupt();
        } catch (IOException x) {
            trouble = true;
        }
    }

    /*
     * The following private methods on the text- and character-output streams
     * always flush the stream buffers, so that writes to the underlying byte
     * stream occur as promptly as with the original PrintStream.
     */
    private void write(char buf[]) {
        try {
            synchronized (this) {
                ensureOpen();
                textOut.write(buf);
                textOut.flush();
                charOut.flush();
                if (autoFlush) {
                    for (int i = 0; i < buf.length; i++) {
                        if (buf[i] == '\n') {
                            out.flush();
                        }
                    }
                }
            }
        } catch (InterruptedIOException x) {
            Thread.currentThread().interrupt();
        } catch (IOException x) {
            trouble = true;
        }
    }

    private void write(String s) {
        try {
            synchronized (this) {
                ensureOpen();
                textOut.write(s);
                textOut.flush();
                charOut.flush();
                if (autoFlush && (s.indexOf('\n') >= 0)) {
                    out.flush();
                }
            }
        } catch (InterruptedIOException x) {
            Thread.currentThread().interrupt();
        } catch (IOException x) {
            trouble = true;
        }
    }

    private void newLine() {
        try {
            synchronized (this) {
                ensureOpen();
                textOut.newLine();
                textOut.flush();
                charOut.flush();
                if (autoFlush) {
                    out.flush();
                }
            }
        } catch (InterruptedIOException x) {
            Thread.currentThread().interrupt();
        } catch (IOException x) {
            trouble = true;
        }
    }

    /* Methods that do not terminate lines */

    /**
     * Print a boolean value.  The string produced by <code>{@link java.lang.String#valueOf(boolean)}
     * is translated into bytes according to the platform's default character encoding, and these
     * bytes are written in exactly the manner of the </code>{@link #write(int)} method.
     *
     * @param b The <code>boolean</code> to be printed
     */
    @Override
    public void print(boolean b) {
        write(b ? "true" : "false");
    }

    /**
     * Print a character.  The character is translated into one or more bytes according to the
     * platform's default character encoding, and these bytes are written in exactly the manner of
     * the {@link #write(int)} method.
     *
     * @param c The <code>char</code> to be printed
     */
    @Override
    public void print(char c) {
        write(String.valueOf(c));
    }

    /**
     * Print an integer.  The string produced by <code>{@link java.lang.String#valueOf(int)} is
     * translated into bytes according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the </code>{@link #write(int)} method.
     *
     * @param i The <code>int</code> to be printed
     */
    @Override
    public void print(int i) {
        write(String.valueOf(i));
    }

    /**
     * Print a long integer.  The string produced by <code>{@link java.lang.String#valueOf(long)} is
     * translated into bytes according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the </code>{@link #write(int)} method.
     *
     * @param l The <code>long</code> to be printed
     */
    @Override
    public void print(long l) {
        write(String.valueOf(l));
    }

    /**
     * Print a floating-point number.  The string produced by <code>{@link
     * java.lang.String#valueOf(float)} is translated into bytes according to the platform's default
     * character encoding, and these bytes are written in exactly the manner of the </code>{@link
     * #write(int)} method.
     *
     * @param f The <code>float</code> to be printed
     */
    @Override
    public void print(float f) {
        write(String.valueOf(f));
    }

    /**
     * Print a double-precision floating-point number.  The string produced by <code>{@link
     * java.lang.String#valueOf(double)} is translated into bytes according to the platform's
     * default character encoding, and these bytes are written in exactly the manner of the
     * </code>{@link #write(int)} method.
     *
     * @param d The <code>double</code> to be printed
     */
    @Override
    public void print(double d) {
        write(String.valueOf(d));
    }

    /**
     * Print an array of characters.  The characters are converted into bytes according to the
     * platform's default character encoding, and these bytes are written in exactly the manner of
     * the {@link #write(int)} method.
     *
     * @param s The array of chars to be printed
     * @throws NullPointerException If <code>s</code> is <code>null</code>
     */
    @Override
    public void print(char s[]) {
        write(s);
    }

    /**
     * Print a string.  If the argument is <code>null</code> then the string <code>"null"</code> is
     * printed.  Otherwise, the string's characters are converted into bytes according to the
     * platform's default character encoding, and these bytes are written in exactly the manner of
     * the {@link #write(int)} method.
     *
     * @param s The <code>String</code> to be printed
     */
    @Override
    public void print(String s) {
        if (s == null) {
            s = "null";
        }
        write(s);
    }

    /**
     * Print an object.  The string produced by the <code>{@link java.lang.String#valueOf(Object)}
     * method is translated into bytes according to the platform's default character encoding, and
     * these bytes are written in exactly the manner of the </code>{@link #write(int)} method.
     *
     * @param obj The <code>Object</code> to be printed
     */
    @Override
    public void print(Object obj) {
        write(String.valueOf(obj));
    }

    /* Methods that do terminate lines */

    /**
     * Terminate the current line by writing the line separator string.  The line separator string
     * is defined by the system property <code>line.separator</code>, and is not necessarily a
     * single newline character (<code>'\n'</code>).
     */
    @Override
    public void println() {
        newLine();
    }

    /**
     * Print a boolean and then terminate the line.  This method behaves as though it invokes
     * <code>{@link #print(boolean)} and then {@link #println()}</code>.
     */
    @Override
    public void println(boolean x) {
        synchronized (this) {
            print(x);
            newLine();
        }
    }

    /**
     * Print a character and then terminate the line.  This method behaves as though it invokes
     * <code>{@link #print(char)} and then {@link #println()}</code>.
     */
    @Override
    public void println(char x) {
        synchronized (this) {
            print(x);
            newLine();
        }
    }

    /**
     * Print an integer and then terminate the line.  This method behaves as though it invokes
     * <code>{@link #print(int)} and then {@link #println()}</code>.
     */
    @Override
    public void println(int x) {
        synchronized (this) {
            print(x);
            newLine();
        }
    }

    /**
     * Print a long and then terminate the line.  This method behaves as though it invokes {@link
     * #print(long)} and then <code>{@link #println()}</code>.
     */
    @Override
    public void println(long x) {
        synchronized (this) {
            print(x);
            newLine();
        }
    }

    /**
     * Print a float and then terminate the line.  This method behaves as though it invokes {@link
     * #print(float)} and then <code>{@link #println()}</code>.
     */
    @Override
    public void println(float x) {
        synchronized (this) {
            print(x);
            newLine();
        }
    }

    /**
     * Print a double and then terminate the line.  This method behaves as though it invokes {@link
     * #print(double)} and then <code>{@link #println()}</code>.
     */
    @Override
    public void println(double x) {
        synchronized (this) {
            print(x);
            newLine();
        }
    }

    /**
     * Print an array of characters and then terminate the line.  This method behaves as though it
     * invokes {@link #print(char[])} and then <code>{@link #println()}</code>.
     */
    @Override
    public void println(char x[]) {
        synchronized (this) {
            print(Arrays.toString(x));
            newLine();
        }
    }

    /**
     * Print a String and then terminate the line.  This method behaves as though it invokes {@link
     * #print(String)} and then <code>{@link #println()}</code>.
     */
    @Override
    public void println(String x) {
        synchronized (this) {
            print(x);
            newLine();
        }
    }

    /**
     * Print an Object and then terminate the line.  This method behaves as though it invokes {@link
     * #print(Object)} and then <code>{@link #println()}</code>.
     */
    @Override
    public void println(Object x) {
        synchronized (this) {
            print(x);
            newLine();
        }
    }
}
