// Example taken from:
// http://javasourcecode.org/html/open-source/jdk/jdk-6u23/java/net/Socket.java.html
package issta2013.net;

import gov.nasa.jpf.jdart.Symbolic;

/**
 * This class implements client sockets (also called just "sockets"). A socket
 * is an endpoint for communication between two machines.
 * <p>
 * The actual work of the socket is performed by an instance of the
 * <code>SocketImpl</code> class. An application, by changing the socket factory
 * that creates the socket implementation, can configure itself to create
 * sockets appropriate to the local firewall.
 * 
 * @author unascribed
 * @version 1.115, 09/05/07
 * @see java.net.Socket#setSocketImplFactory(java.net.SocketImplFactory)
 * @see java.net.SocketImpl
 * @see java.nio.channels.SocketChannel
 * @since JDK1.0
 */
public class Socket {
  /**
   * Various states of this socket.
   */
  @Symbolic("true")
  private boolean created = false;

  @Symbolic("true")
  private boolean bound = false;

  @Symbolic("true")
  private boolean connected = false;

  @Symbolic("true")
  private boolean closed = false;

  @Symbolic("true")
  private boolean shutIn = false;

  @Symbolic("true")
  private boolean shutOut = false;

  @Symbolic("true")
  int impl = 0;
  
  @Symbolic("true")
  private boolean oldImpl = false;

  public Socket() {
    setImpl();
  }

  public void Socket_PSYCO(int proxy, int p, int ptype) {
    // Create a copy of Proxy as a security measure
    if (proxy == 0) {
      assert false;
    }
//    Proxy p = proxy == Proxy.NO_PROXY ? proxy : sun.net.ApplicationProxy
//        .create(proxy);

    if (ptype == 1) {
      impl = 1;
      // impl.setSocket(this);
    } else {
      if (p == 0) {
        if (factory == 0) {
          impl = 1;
          //impl.setSocket(this);
        } else
          setImpl();
      } else
        assert false;
    }
  }


  public void Socket_PSYCO(int impl) {
    this.impl = impl;
    if (impl != 0) {
      checkOldImpl();
      //this.impl.setSocket(this);
    }
  }

  public void createImpl(boolean stream) {
    if (impl == 0)
      setImpl();
    // impl.create(stream);
    created = true;
  }

  private void checkOldImpl() {
    if (impl == 0)
      return;
    // SocketImpl.connect() is a protected method, therefore we need to use
    // getDeclaredMethod, therefore we need permission to access the member

//    oldImpl = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
//      public Boolean run() {
//        Class[] cl = new Class[2];
//        cl[0] = SocketAddress.class;
//        cl[1] = Integer.TYPE;
//        Class clazz = impl.getClass();
//        while (true) {
//          try {
//            clazz.getDeclaredMethod("connect", cl);
//            // return  Boolean.FALSE;
//          } catch (NoSuchMethodException e) {
//            clazz = clazz.getSuperclass();
//            // java.net.SocketImpl class will always have this abstract method.
//            // If we have not found it by now in the hierarchy then it does not
//            // exist, we are an old style impl.
//            if (clazz.equals(java.net.SocketImpl.class)) {
//              // return  Boolean.TRUE;
//            }
//          }
//        }
//      }
//    });
  }

  /**
   * Sets impl to the system-default type of SocketImpl.
   * 
   * @since 1.4
   */
  public void setImpl() {
    if (factory != 0) {
//      impl = factory.createSocketImpl();
      impl = 1;
      checkOldImpl();
    } else {
      // No need to do a checkOldImpl() here, we know it's an up to date
      // SocketImpl!
      impl = 1;
    }
//    if (impl != null)
//      impl.setSocket(this);
  }

  /**
   * Get the <code>SocketImpl</code> attached to this socket, creating it if
   * necessary.
   * 
   * @// return  the <code>SocketImpl</code> attached to that ServerSocket.
   * @throws SocketException
   *           if creation fails
   * @since 1.4
   */
  public void getImpl() {
    if (!created)
      createImpl(true);
    //// return  impl;
  }

  /**
   * Connects this socket to the server.
   * 
   * @param endpoint
   *          the <code>SocketAddress</code>
   * @throws IOException
   *           if an error occurs during the connection
   * @throws java.nio.channels.IllegalBlockingModeException
   *           if this socket has an associated channel, and the channel is in
   *           non-blocking mode
   * @throws IllegalArgumentException
   *           if endpoint is null or is a SocketAddress subclass not supported
   *           by this socket
   * @since 1.4
   * @spec JSR-51
   */
  public void connect(int endpoint) {
    connect(endpoint, 0);
  }

  /**
   * Connects this socket to the server with a specified timeout value. A
   * timeout of zero is interpreted as an infinite timeout. The connection will
   * then block until established or an error occurs.
   * 
   * @param endpoint
   *          the <code>SocketAddress</code>
   * @param timeout
   *          the timeout value to be used in milliseconds.
   * @throws IOException
   *           if an error occurs during the connection
   * @throws SocketTimeoutException
   *           if timeout expires before connecting
   * @throws java.nio.channels.IllegalBlockingModeException
   *           if this socket has an associated channel, and the channel is in
   *           non-blocking mode
   * @throws IllegalArgumentException
   *           if endpoint is null or is a SocketAddress subclass not supported
   *           by this socket
   * @since 1.4
   * @spec JSR-51
   */
  public void connect(int endpoint, int timeout) {
    if (endpoint == 0)
      assert false;

    if (timeout < 0)
      assert false;

    if (_isClosed())
      assert false;

    if (!oldImpl && _isConnected())
      assert false;

//    if (!(endpoint instanceof InetSocketAddress))
//      throw new IllegalArgumentException("Unsupported address type");

    if (!created)
      createImpl(true);
    if (!oldImpl) {
//      impl.connect(epoint, timeout);
    } else if (timeout == 0) {
//      if (epoint.isUnresolved())
//        impl.connect(addr.getHostName(), port);
//      else
//        impl.connect(addr, port);
    } else
      assert false;
    connected = true;
    /*
     * If the socket was not bound before the connect, it is now because the
     * kernel will have picked an ephemeral port & a local address
     */
    bound = true;
  }

  /**
   * Binds the socket to a local address.
   * <P>
   * If the address is <code>null</code>, then the system will pick up an
   * ephemeral port and a valid local address to bind the socket.
   * 
   * @param bindpoint
   *          the <code>SocketAddress</code> to bind to
   * @throws IOException
   *           if the bind operation fails, or if the socket is already bound.
   * @throws IllegalArgumentException
   *           if bindpoint is a SocketAddress subclass not supported by this
   *           socket
   * 
   * @since 1.4
   * @see #isBound
   */
  public void bind(int bindpoint) {
    if (_isClosed())
      assert false;
    if (!oldImpl && _isBound())
      assert false;

//    if (bindpoint != 0 && (!(bindpoint instanceof InetSocketAddress)))
//      assert false;
//    int epoint = bindpoint;
//    if (epoint != 0 && epoint.isUnresolved())
//      assert false;
//    if (epoint == null) {
//      epoint = new InetSocketAddress(0);
//    }
//    InetAddress addr = epoint.getAddress();
//    int port = epoint.getPort();
//    checkAddress(addr, "bind");
//    getImpl().bind(addr, port);
    bound = true;
  }

  private void checkAddress(int addr, int op) {
    if (addr == 0) {
      return;
    }
//    if (!(addr instanceof Inet4Address || addr instanceof Inet6Address)) {
//      throw new IllegalArgumentException(op + ": invalid address type");
//    }
  }

  /**
   * set the flags after an accept() call.
   */
  public void postAccept() {
    connected = true;
    created = true;
    bound = true;
  }

  public void setCreated() {
    created = true;
  }

  public void setBound() {
    bound = true;
  }

  public void setConnected() {
    connected = true;
  }

  /**
   * Returns the address to which the socket is connected.
   * 
   * @// return  the remote IP address to which this socket is connected, or
   *         <code>null</code> if the socket is not connected.
   */
  public void getInetAddress() {
    if (!_isConnected());
      // return  0;
//    try {
//      // return  getImpl().getInetAddress();
//    } catch (SocketException e) {
//    }
//    // return  null;
    // return  0;
  }

  /**
   * Gets the local address to which the socket is bound.
   * 
   * @// return  the local address to which the socket is bound or
   *         <code>InetAddress.anyLocalAddress()</code> if the socket is not
   *         bound yet.
   * @since JDK1.1
   */
  public void getLocalAddress() {
    // This is for backward compatibility
    if (!_isBound());
//      // return  InetAddress.anyLocalAddress();
      // return  0;
    int in = 0;
//    try {
//      in = (InetAddress) getImpl().getOption(SocketOptions.SO_BINDADDR);
//      if (in.isAnyLocalAddress()) {
//        in = InetAddress.anyLocalAddress();
//      }
//    } catch (Exception e) {
//      in = InetAddress.anyLocalAddress(); // "0.0.0.0"
//    }
    // return  in;
  }

  /**
   * Returns the remote port to which this socket is connected.
   * 
   * @// return  the remote port number to which this socket is connected, or 0 if
   *         the socket is not connected yet.
   */
  public void getPort() {
    if (!_isConnected());
      // return  0;
//    try {
//      // return  getImpl().getPort();
//    } catch (SocketException e) {
//      // Shouldn't happen as we're connected
//    }
    // return  -1;
  }

  /**
   * Returns the local port to which this socket is bound.
   * 
   * @// return  the local port number to which this socket is bound or -1 if the
   *         socket is not bound yet.
   */
  public void getLocalPort() {
    if (!_isBound());
      // return  -1;
//    try {
//      // return  getImpl().getLocalPort();
//    } catch (SocketException e) {
//      // shouldn't happen as we're bound
//    }
    // return  -1;
  }

  /**
   * Returns the address of the endpoint this socket is connected to, or
   * <code>null</code> if it is unconnected.
   * 
   * @// return  a <code>SocketAddress</code> reprensenting the remote endpoint of
   *         this socket, or <code>null</code> if it is not connected yet.
   * @see #getInetAddress()
   * @see #getPort()
   * @see #connect(SocketAddress, int)
   * @see #connect(SocketAddress)
   * @since 1.4
   */
  public void getRemoteSocketAddress() {
    if (!_isConnected());
      // return  0;
//    // return  new InetSocketAddress(getInetAddress(), getPort());
    // return  1;
  }

  /**
   * Returns the address of the endpoint this socket is bound to, or
   * <code>null</code> if it is not bound yet.
   * 
   * @// return  a <code>SocketAddress</code> representing the local endpoint of
   *         this socket, or <code>null</code> if it is not bound yet.
   * @see #getLocalAddress()
   * @see #getLocalPort()
   * @see #bind(SocketAddress)
   * @since 1.4
   */

  public void getLocalSocketAddress() {
    if (!_isBound());
      // return  0;
//    // return  new InetSocketAddress(getLocalAddress(), getLocalPort());
    // return  1;
  }

  /**
   * Returns the unique {@link java.nio.channels.SocketChannel SocketChannel}
   * object associated with this socket, if any.
   * 
   * <p>
   * A socket will have a channel if, and only if, the channel itself was
   * created via the {@link java.nio.channels.SocketChannel#open
   * SocketChannel.open} or {@link java.nio.channels.ServerSocketChannel#accept
   * ServerSocketChannel.accept} methods.
   * 
   * @// return  the socket channel associated with this socket, or <tt>null</tt> if
   *         this socket was not created for a channel
   * 
   * @since 1.4
   * @spec JSR-51
   */
  public void getChannel() {
    // return  0;
  }

  /**
   * Returns an input stream for this socket.
   * 
   * <p>
   * If this socket has an associated channel then the resulting input stream
   * delegates all of its operations to the channel. If the channel is in
   * non-blocking mode then the input stream's <tt>read</tt> operations will
   * throw an {@link java.nio.channels.IllegalBlockingModeException}.
   * 
   * <p>
   * Under abnormal conditions the underlying connection may be broken by the
   * remote host or the network software (for example a connection reset in the
   * case of TCP connections). When a broken connection is detected by the
   * network software the following applies to the returned input stream :-
   * 
   * <ul>
   * 
   * <li>
   * <p>
   * The network software may discard bytes that are buffered by the socket.
   * Bytes that aren't discarded by the network software can be read using
   * {@link java.io.InputStream#read read}.
   * 
   * <li>
   * <p>
   * If there are no bytes buffered on the socket, or all buffered bytes have
   * been consumed by {@link java.io.InputStream#read read}, then all subsequent
   * calls to {@link java.io.InputStream#read read} will throw an
   * {@link java.io.IOException IOException}.
   * 
   * <li>
   * <p>
   * If there are no bytes buffered on the socket, and the socket has not been
   * closed using {@link #close close}, then
   * {@link java.io.InputStream#available available} will // return  <code>0</code>.
   * 
   * </ul>
   * 
   * <p>
   * Closing the returned {@link java.io.InputStream InputStream} will close the
   * associated socket.
   * 
   * @// return  an input stream for reading bytes from this socket.
   * @exception IOException
   *              if an I/O error occurs when creating the input stream, the
   *              socket is closed, the socket is not connected, or the socket
   *              input has been shutdown using {@link #shutdownInput()}
   * 
   * @revised 1.4
   * @spec JSR-51
   */
  public void getInputStream() {
    if (_isClosed())
      assert false;
    if (!_isConnected())
      assert false;
    if (_isInputShutdown())
      assert false;
    int is = 0;
    is = 1;
    // return  is;
  }

  /**
   * Returns an output stream for this socket.
   * 
   * <p>
   * If this socket has an associated channel then the resulting output stream
   * delegates all of its operations to the channel. If the channel is in
   * non-blocking mode then the output stream's <tt>write</tt> operations will
   * throw an {@link java.nio.channels.IllegalBlockingModeException}.
   * 
   * <p>
   * Closing the returned {@link java.io.OutputStream OutputStream} will close
   * the associated socket.
   * 
   * @// return  an output stream for writing bytes to this socket.
   * @exception IOException
   *              if an I/O error occurs when creating the output stream or if
   *              the socket is not connected.
   * @revised 1.4
   * @spec JSR-51
   */
  public void getOutputStream() {
    if (_isClosed())
      assert false;
    if (!_isConnected())
      assert false;
    if (_isOutputShutdown())
      assert false;
    int os = 0;
    os = 1;
    // return  os;
  }

  /**
   * Enable/disable TCP_NODELAY (disable/enable Nagle's algorithm).
   * 
   * @param on
   *          <code>true</code> to enable TCP_NODELAY, <code>false</code> to
   *          disable.
   * 
   * @exception SocketException
   *              if there is an error in the underlying protocol, such as a TCP
   *              error.
   * 
   * @since JDK1.1
   * 
   * @see #getTcpNoDelay()
   */
  public void setTcpNoDelay(boolean on) {
    if (_isClosed())
      assert false;
//    getImpl().setOption(SocketOptions.TCP_NODELAY, Boolean.valueOf(on));
  }

  /**
   * Tests if TCP_NODELAY is enabled.
   * 
   * @// return  a <code>boolean</code> indicating whether or not TCP_NODELAY is
   *         enabled.
   * @exception SocketException
   *              if there is an error in the underlying protocol, such as a TCP
   *              error.
   * @since JDK1.1
   * @see #setTcpNoDelay(boolean)
   */
  public void getTcpNoDelay() {
    if (_isClosed())
      assert false;
//    // return  ((Boolean) getImpl().getOption(SocketOptions.TCP_NODELAY))
//        .booleanValue();
    // return  true;
  }

  /**
   * Enable/disable SO_LINGER with the specified linger time in seconds. The
   * maximum timeout value is platform specific.
   * 
   * The setting only affects socket close.
   * 
   * @param on
   *          whether or not to linger on.
   * @param linger
   *          how long to linger for, if on is true.
   * @exception SocketException
   *              if there is an error in the underlying protocol, such as a TCP
   *              error.
   * @exception IllegalArgumentException
   *              if the linger value is negative.
   * @since JDK1.1
   * @see #getSoLinger()
   */
  public void setSoLinger(boolean on, int linger) {
    if (_isClosed())
      assert false;
    if (!on) {
//      getImpl().setOption(SocketOptions.SO_LINGER, new Boolean(on));
    } else {
      if (linger < 0) {
        assert false;
      }
      if (linger > 65535)
        linger = 65535;
//      getImpl().setOption(SocketOptions.SO_LINGER, new Integer(linger));
    }
  }

  /**
   * Returns setting for SO_LINGER. -1 returns implies that the option is
   * disabled.
   * 
   * The setting only affects socket close.
   * 
   * @// return  the setting for SO_LINGER.
   * @exception SocketException
   *              if there is an error in the underlying protocol, such as a TCP
   *              error.
   * @since JDK1.1
   * @see #setSoLinger(boolean, int)
   */
  public void getSoLinger() {
    if (_isClosed())
      assert false;
//    Object o = getImpl().getOption(SocketOptions.SO_LINGER);
//    if (o instanceof Integer) {
//      // return  ((Integer) o).intValue();
//    } else {
//      // return  -1;
//    }
    // return  0;
  }

  /**
   * Send one byte of urgent data on the socket. The byte to be sent is the
   * lowest eight bits of the data parameter. The urgent byte is sent after any
   * preceding writes to the socket OutputStream and before any future writes to
   * the OutputStream.
   * 
   * @param data
   *          The byte of data to send
   * @exception IOException
   *              if there is an error sending the data.
   * @since 1.4
   */
  public void sendUrgentData(int data) {
//    if (!getImpl().supportsUrgentData()) {
//      assert false;
//    }
//    getImpl().sendUrgentData(data);
  }

  /**
   * Enable/disable OOBINLINE (receipt of TCP urgent data)
   * 
   * By default, this option is disabled and TCP urgent data received on a
   * socket is silently discarded. If the user wishes to receive urgent data,
   * then this option must be enabled. When enabled, urgent data is received
   * inline with normal data.
   * <p>
   * Note, only limited support is provided for handling incoming urgent data.
   * In particular, no notification of incoming urgent data is provided and
   * there is no capability to distinguish between normal data and urgent data
   * unless provided by a higher level protocol.
   * 
   * @param on
   *          <code>true</code> to enable OOBINLINE, <code>false</code> to
   *          disable.
   * 
   * @exception SocketException
   *              if there is an error in the underlying protocol, such as a TCP
   *              error.
   * 
   * @since 1.4
   * 
   * @see #getOOBInline()
   */
  public void setOOBInline(boolean on) {
    if (_isClosed())
      assert false;
//    getImpl().setOption(SocketOptions.SO_OOBINLINE, Boolean.valueOf(on));
  }

  /**
   * Tests if OOBINLINE is enabled.
   * 
   * @// return  a <code>boolean</code> indicating whether or not OOBINLINE is
   *         enabled.
   * @exception SocketException
   *              if there is an error in the underlying protocol, such as a TCP
   *              error.
   * @since 1.4
   * @see #setOOBInline(boolean)
   */
  public void getOOBInline() {
    if (_isClosed())
      assert false;
//    // return  ((Boolean) getImpl().getOption(SocketOptions.SO_OOBINLINE))
//        .booleanValue();
    // return  true;
  }

  /**
   * Enable/disable SO_TIMEOUT with the specified timeout, in milliseconds. With
   * this option set to a non-zero timeout, a read() call on the InputStream
   * associated with this Socket will block for only this amount of time. If the
   * timeout expires, a <B>java.net.SocketTimeoutException</B> is raised, though
   * the Socket is still valid. The option <B>must</B> be enabled prior to
   * entering the blocking operation to have effect. The timeout must be > 0. A
   * timeout of zero is interpreted as an infinite timeout.
   * 
   * @param timeout
   *          the specified timeout, in milliseconds.
   * @exception SocketException
   *              if there is an error in the underlying protocol, such as a TCP
   *              error.
   * @since JDK 1.1
   * @see #getSoTimeout()
   */
  public void setSoTimeout(int timeout) {
    if (_isClosed())
      assert false;
    if (timeout < 0)
      assert false;

//    getImpl().setOption(SocketOptions.SO_TIMEOUT, new Integer(timeout));
  }

  /**
   * Returns setting for SO_TIMEOUT. 0 returns implies that the option is
   * disabled (i.e., timeout of infinity).
   * 
   * @// return  the setting for SO_TIMEOUT
   * @exception SocketException
   *              if there is an error in the underlying protocol, such as a TCP
   *              error.
   * @since JDK1.1
   * @see #setSoTimeout(int)
   */
  public void getSoTimeout() {
    if (_isClosed())
      assert false;
//    Object o = getImpl().getOption(SocketOptions.SO_TIMEOUT);
//    /* extra type safety */
//    if (o instanceof Integer) {
//      // return  ((Integer) o).intValue();
//    } else {
//      // return  0;
//    }
    // return  0;
  }

  /**
   * Sets the SO_SNDBUF option to the specified value for this <tt>Socket</tt>.
   * The SO_SNDBUF option is used by the platform's networking code as a hint
   * for the size to set the underlying network I/O buffers.
   * 
   * <p>
   * Because SO_SNDBUF is a hint, applications that want to verify what size the
   * buffers were set to should call {@link #getSendBufferSize()}.
   * 
   * @exception SocketException
   *              if there is an error in the underlying protocol, such as a TCP
   *              error.
   * 
   * @param size
   *          the size to which to set the send buffer size. This value must be
   *          greater than 0.
   * 
   * @exception IllegalArgumentException
   *              if the value is 0 or is negative.
   * 
   * @see #getSendBufferSize()
   * @since 1.2
   */
  public void setSendBufferSize(int size) {
    if (!(size > 0)) {
      assert false;
    }
    if (_isClosed())
      assert false;
//    getImpl().setOption(SocketOptions.SO_SNDBUF, new Integer(size));
  }

  public void getSendBufferSize() {
    if (_isClosed())
      assert false;
    int result = 0;
//    Object o = getImpl().getOption(SocketOptions.SO_SNDBUF);
//    if (o instanceof Integer) {
//      result = ((Integer) o).intValue();
//    }
//    // return  result;
  }

  public void setReceiveBufferSize(int size) {
    if (size <= 0) {
      assert false;
    }
    if (_isClosed())
      assert false;
    // getImpl().setOption(SocketOptions.SO_RCVBUF, new Integer(size));
  }

  public void getReceiveBufferSize() {
    if (_isClosed())
      assert false;
    int result = 0;
//    Object o = getImpl().getOption(SocketOptions.SO_RCVBUF);
//    if (o instanceof Integer) {
//      result = ((Integer) o).intValue();
//    }
//    // return  result;
  }

  public void setKeepAlive(boolean on) {
    if (_isClosed())
      assert false;
//    getImpl().setOption(SocketOptions.SO_KEEPALIVE, Boolean.valueOf(on));
  }

  public void getKeepAlive() {
    if (_isClosed())
      assert false;
//    // return  ((Boolean) getImpl().getOption(SocketOptions.SO_KEEPALIVE))
//        .booleanValue();
    // // return  true;
  }

  public void setTrafficClass(int tc) {
    if (tc < 0 || tc > 255)
      assert false;

    if (_isClosed())
      assert false;
//    getImpl().setOption(SocketOptions.IP_TOS, new Integer(tc));
  }

  public void getTrafficClass() {
//    // return  ((Integer) (getImpl().getOption(SocketOptions.IP_TOS))).intValue();
    //// return  0;
  }

  public void setReuseAddress(boolean on) {
    if (_isClosed())
      assert false;
//    getImpl().setOption(SocketOptions.SO_REUSEADDR, Boolean.valueOf(on));
  }

  public void getReuseAddress() {
    if (_isClosed())
      assert false;
//    // return  ((Boolean) (getImpl().getOption(SocketOptions.SO_REUSEADDR)))
//        .booleanValue();
    //// return  true;
  }

  public void close() {
      if (_isClosed())
        return;
//      if (created)
//        impl.close();
      closed = true;
  }

  public void shutdownInput() {
    if (_isClosed())
      assert false;
    if (!_isConnected())
      assert false;
    if (_isInputShutdown())
      assert false;
//    getImpl().shutdownInput();
    shutIn = true;
  }

  public void shutdownOutput() {
    if (_isClosed())
      assert false;
    if (!_isConnected())
      assert false;
    if (_isOutputShutdown())
      assert false;
//    getImpl().shutdownOutput();
    shutOut = true;
  }

  public void isConnected() {
    // Before 1.3 Sockets were always connected during creation
    assert connected || oldImpl;
  }

  public void isBound() {
    // Before 1.3 Sockets were always bound during creation
    assert bound || oldImpl;
  }

  public void isClosed() {
    assert closed;
  }

  public void isInputShutdown() {
    assert shutIn;
  }


  public void isOutputShutdown() {
    assert shutOut;
  }

  private boolean _isConnected() {
    // Before 1.3 Sockets were always connected during creation
    return  connected || oldImpl;
  }

  private boolean _isBound() {
    // Before 1.3 Sockets were always bound during creation
    return  bound || oldImpl;
  }

  private boolean _isClosed() {
    return  closed;
  }

  private boolean _isInputShutdown() {
    return  shutIn;
  }


  private boolean _isOutputShutdown() {
    return  shutOut;
  }
  
  @Symbolic("true")
  private int factory = 0;

  public void setSocketImplFactory(int fac) {
    if (factory != 0) {
      assert false;
    }
    factory = fac;
  }

  public void setPerformancePreferences(int connectionTime, int latency,
      int bandwidth) {
  }
}
