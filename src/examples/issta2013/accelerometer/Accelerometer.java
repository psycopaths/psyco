// Example taken from:
// http://gamesdev.wordpress.com/2008/11/03/circuit-simple-j2me-game/
package issta2013.accelerometer;

import gov.nasa.jpf.jdart.Symbolic;

public class Accelerometer {
  
  @Symbolic("true")
  private  int m_accX = 0;
  
  @Symbolic("true")
  private  int m_accY = 0;

  @Symbolic("true")
  private  int m_conn = 0;
   
  @Symbolic("true")
  private  int m_conn_data_listener = 0;

  @Symbolic("true")
  public  int m_sensorInfo = 0;

  @Symbolic("true")
  private  int m_sensitivity = 0;

  @Symbolic("true")
  private  int m_samples = 0;

  @Symbolic("true")
  private  int m_delta = 0;
  
  public final  int THRESHOLD = 50;

  public Accelerometer() {
  }
  
  
  public Accelerometer(int samples, int sensitivity) {
    m_sensitivity = sensitivity;
    m_samples = samples;
    assert sensitivity != 0;
    m_delta = 1000 / sensitivity;
  }

  public void init(int nondet) {
  //public boolean init(int nondet) {
    int info = nondet;

    if (info != 0) {
      m_sensorInfo = 1;
      //return true;
    } else {
      //return false;
    }
  }

  public  void connect(boolean useListener) {
    if (m_conn == 0) {
      m_conn = 1;
      if (useListener) {
        m_conn_data_listener = 1;
      }
    }
  }

  public  void disconnect() {
    assert m_conn != 0;
//    m_conn.close();
    m_conn_data_listener = 0;
    m_conn = 0;
  }

//  public int getValueX() {
//    return m_accX / m_delta;
//  }
//
//  public int getValueY() {
//    return m_accY / m_delta;
//  }
  
  public  void getValueX() {
    int x = m_accX / m_delta;
  }

  public  void getValueY() {
    int y = m_accY / m_delta;
  }

  public  void update() {
    assert m_conn != 0;
//    Data[] data = m_conn.getData(1);
//    m_accX = data[0].getIntValues()[0];
//    m_accY = data[1].getIntValues()[0];
  }

//  public void dataReceived(int[][] data, boolean b) {
//    assert m_conn_data_listener != 0;
//
//    int[] x = data[0];
//    int[] y = data[1];
//
//    m_accX = x[0];
//    m_accY = y[0];
  public  void dataReceived(int x, int y, boolean b) {
    assert m_conn_data_listener != 0;

    m_accX = x;
    m_accY = y;

    int tmp = 0;

    m_accX = (m_accX < 0) ? max(-1000, m_accX) : min(1000, m_accX);
    m_accY = (m_accY < 0) ? max(-1000, m_accY) : min(1000, m_accY);

    final int abs_accX = abs(m_accX);
    final int abs_accY = abs(m_accY);

    m_accX = (tmp = abs_accX - THRESHOLD) <= 0 ? 0 : tmp * (m_accX / abs_accX);
    m_accY = (tmp = abs_accY - THRESHOLD) <= 0 ? 0 : tmp * (m_accY / abs_accY);
  }

  private  int max(int i, int j) {
    return (i >= j) ? i : j;
  }

  private  int min(int i, int j) {
    return (i <= j) ? i : j;
  }  
  
  private  int abs(int i) {
    return (i >= 0) ? i : -i;
  }
}