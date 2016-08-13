/*
 * Copyright (C) 2015, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * The PSYCO: A Predicate-based Symbolic Compositional Reasoning environment 
 * platform is licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may obtain a 
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0. 
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 * specific language governing permissions and limitations under the License.
 */

package issta2013.io;

//import java.io.*;

import gov.nasa.jpf.jdart.Symbolic;


/**
 * A piped output stream can be connected to a piped input stream to create a
 * communications pipe. The piped output stream is the sending end of the pipe.
 * Typically, data is written to a <code>PipedOutputStream</code> object by one
 * thread and data is read from the connected <code>PipedInputStream</code> by
 * some other thread. Attempting to use both objects from a single thread is not
 * recommended as it may deadlock the thread. The pipe is said to be <a
 * name=BROKEN> <i>broken</i> </a> if a thread that was reading data bytes from
 * the connected piped input stream is no longer alive.
 * 
 * @version %I%, %G%
 * @see java.io.PipedInputStream
 * @since JDK1.0
 */
//public class PipedOutputStream extends OutputStream {
public class PipedOutputStream {

  /*
   * REMIND: identification of the read and write sides needs to be more
   * sophisticated. Either using thread groups (but what about pipes within a
   * thread?) or using finalization (but it may be a long time until the next
   * GC).
   */
//  private PipedInputStream sink;
  
  @Symbolic("true")
  private  int sink = 0;
  @Symbolic("true")
  private  int sinkConnected = 0;
  
    // only used for concrete execution

  /**
   * Creates a piped output stream connected to the specified piped input
   * stream. Data bytes written to this stream will then be available as input
   * from <code>snk</code>.
   * 
   * @param snk
   *          The piped input stream to connect to.
   * @exception IOException
   *              if an I/O error occurs.
   */
//  public PipedOutputStream(PipedInputStream snk) throws IOException {
//    connect(snk);
//  }

  /**
   * Creates a piped output stream that is not yet connected to a piped input
   * stream. It must be connected to a piped input stream, either by the
   * receiver or the sender, before being used.
   * 
   * @see java.io.PipedInputStream#connect(java.io.PipedOutputStream)
   * @see java.io.PipedOutputStream#connect(java.io.PipedInputStream)
   */
  public PipedOutputStream() {
  }

  /**
   * Connects this piped output stream to a receiver. If this object is already
   * connected to some other piped input stream, an <code>IOException</code> is
   * thrown.
   * <p>
   * If <code>snk</code> is an unconnected piped input stream and
   * <code>src</code> is an unconnected piped output stream, they may be
   * connected by either the call: <blockquote>
   * 
   * <pre>
   * src.connect(snk)
   * </pre>
   * 
   * </blockquote> or the call: <blockquote>
   * 
   * <pre>
   * snk.connect(src)
   * </pre>
   * 
   * </blockquote> The two calls have the same effect.
   * 
   * @param snk
   *          the piped input stream to connect to.
   * @exception IOException
   *              if an I/O error occurs.
   */
//  public synchronized void connect(PipedInputStream snk) throws IOException {
//    if (snk == null) {
//      throw new NullPointerException();
//    } else if (sink != null || snk.connected) {
//      throw new IOException("Already connected");
//    }
//    sink = snk;
//    snk.in = -1;
//    snk.out = 0;
//    snk.connected = true;
//  }
  public  synchronized void connect(int snk, int snkConnected) {
    if (snk == 0) {
      assert false;
    } else if (sink != 0 || snkConnected == 1) {
      assert false;
    }
    sink = snk;
    sinkConnected = 1;
  }

  /**
   * Writes the specified <code>byte</code> to the piped output stream.
   * <p>
   * Implements the <code>write</code> method of <code>OutputStream</code>.
   * 
   * @param b
   *          the <code>byte</code> to be written.
   * @exception IOException
   *              if the pipe is <a href=#BROKEN> broken</a>,
   *              {@link #connect(java.io.PipedInputStream) unconnected},
   *              closed, or if an I/O error occurs.
   */
//  public void write(int b) throws IOException {
//    if (sink == null) {
//      throw new IOException("Pipe not connected");
//    }
//    sink.receive(b);
//  }
  public  void write() {
    if (sink == 0) {
      assert false;
    }
  }

  /**
   * Writes <code>len</code> bytes from the specified byte array starting at
   * offset <code>off</code> to this piped output stream. This method blocks
   * until all the bytes are written to the output stream.
   * 
   * @param b
   *          the data.
   * @param off
   *          the start offset in the data.
   * @param len
   *          the number of bytes to write.
   * @exception IOException
   *              if the pipe is <a href=#BROKEN> broken</a>,
   *              {@link #connect(java.io.PipedInputStream) unconnected},
   *              closed, or if an I/O error occurs.
   */
//  public void write(byte b[], int off, int len) throws IOException {
//    if (sink == null) {
//      throw new IOException("Pipe not connected");
//    } else if (b == null) {
//      throw new NullPointerException();
//    } else if ((off < 0) || (off > b.length) || (len < 0)
//        || ((off + len) > b.length) || ((off + len) < 0)) {
//      throw new IndexOutOfBoundsException();
//    } else if (len == 0) {
//      return;
//    }
//    sink.receive(b, off, len);
//  }
//  public void write() throws IOException {
//    if (sink == 0) {
//      assert false;
//    }
//  }

  /**
   * Flushes this output stream and forces any buffered output bytes to be
   * written out. This will notify any readers that bytes are waiting in the
   * pipe.
   * 
   * @exception IOException
   *              if an I/O error occurs.
   */
//  public synchronized void flush() throws IOException {
//    if (sink != null) {
//      synchronized (sink) {
//        sink.notifyAll();
//      }
//    }
//  }
  public  synchronized void flush() {
    if (sink != 0) {
      ;
    }
  }

  /**
   * Closes this piped output stream and releases any system resources
   * associated with this stream. This stream may no longer be used for writing
   * bytes.
   * 
   * @exception IOException
   *              if an I/O error occurs.
   */
//  public void close() throws IOException {
//    if (sink != null) {
//      sink.receivedLast();
//    }
//  }
  public  void close() {
    if (sink != 0) {
      ;
    }
  }
}