

package ssh.s3_clnt_1_false;

/**
 *
 * @author falk
 */
public class Client {
  
//  private int initial_state;
//  
// private int ssl3_connect(int p1, int p2, int p3, int p4, int p5, int p6, int p7, int p8, int p9, int p10, 
//         int p11, int p12, int p13, int p14, int p15, int p16, int p17, int p18, int p19, int p20,
//         int p21, int p22, int p23, int p24, int p25, int p26, int p27, int p28, int p29, int p30) {
//{ int s__info_callback = p1 ;
//  int s__in_handshake = p2 ;
//  int s__state ;
//  int s__new_session ;
//  int s__server ;
//  int s__version = p3 ;
//  int s__type ;
//  int s__init_num ;
//  int s__bbio = p4 ;
//  int s__wbio = p5 ;
//  int s__hit = p6 ;
//  int s__rwstate ;
//  int s__init_buf___0 ;
//  int s__debug = p7 ;
//  int s__shutdown ;
//  int s__ctx__info_callback = p8 ;
//  int s__ctx__stats__sess_connect_renegotiate = p9 ;
//  int s__ctx__stats__sess_connect = p10 ;
//  int s__ctx__stats__sess_hit = p11 ;
//  int s__ctx__stats__sess_connect_good = p12 ;
//  int s__s3__change_cipher_spec ;
//  int s__s3__flags ;
//  int s__s3__delay_buf_pop_ret ;
//  int s__s3__tmp__cert_req = p13 ;
//  int s__s3__tmp__new_compression = p14 ;
//  int s__s3__tmp__reuse_message = p15 ;
//  int s__s3__tmp__new_cipher = p16 ;
//  int s__s3__tmp__new_cipher__algorithms = p17 ;
//  int s__s3__tmp__next_state___0 ;
//  int s__s3__tmp__new_compression__id = p18 ;
//  int s__session__cipher ;
//  int s__session__compress_meth ;
//  int buf ;
//   long tmp ; // FIXME: this was unsigned in c!
//   long l ;   // FIXME: this was unsigned in c!
//  int num1 ;
//  int cb ;
//  int ret ;
//  int new_state ;
//  int state ;
//  int skip ;
//  int tmp___0 ;
//  int tmp___1 = p19 ;
//  int tmp___2 = p20 ;
//  int tmp___3 = p21 ;
//  int tmp___4 = p22 ;
//  int tmp___5 = p23 ;
//  int tmp___6 = p24 ;
//  int tmp___7 = p25 ;
//  int tmp___8 = p26 ;
//  int tmp___9 = p27 ;
//  int blastFlag ;
//  int __cil_tmp55 ;
//  long __cil_tmp56 ;
//  long __cil_tmp57 ;
//  long __cil_tmp58 ;
//  long __cil_tmp59 ;
//  long __cil_tmp60 ;
//  long __cil_tmp61 ;
//  long __cil_tmp62 ;
//  long __cil_tmp63 ;
//  long __cil_tmp64 ;
//
//  {
//;
//  s__state = initial_state;
//  blastFlag = 0;
//  tmp = p28;
//  cb = 0;
//  ret = -1;
//  skip = 0;
//  tmp___0 = 0;
//  if (s__info_callback != 0) {
//    cb = s__info_callback;
//  } else {
//    if (s__ctx__info_callback != 0) {
//      cb = s__ctx__info_callback;
//    }
//  }
//  s__in_handshake ++;
//  if (tmp___1 - 12288) {
//    if (tmp___2 - 16384) {
//
//    }
//  }
//  {
//  while (true) {
//    while_0_continue: /* CIL Label */ ;
//    state = s__state;
//    if (s__state == 12292) {
//      goto switch_1_12292;
//    } else {
//      if (s__state == 16384) {
//        goto switch_1_16384;
//      } else {
//        if (s__state == 4096) {
//          goto switch_1_4096;
//        } else {
//          if (s__state == 20480) {
//            goto switch_1_20480;
//          } else {
//            if (s__state == 4099) {
//              goto switch_1_4099;
//            } else {
//              if (s__state == 4368) {
//                goto switch_1_4368;
//              } else {
//                if (s__state == 4369) {
//                  goto switch_1_4369;
//                } else {
//                  if (s__state == 4384) {
//                    goto switch_1_4384;
//                  } else {
//                    if (s__state == 4385) {
//                      goto switch_1_4385;
//                    } else {
//                      if (s__state == 4400) {
//                        goto switch_1_4400;
//                      } else {
//                        if (s__state == 4401) {
//                          goto switch_1_4401;
//                        } else {
//                          if (s__state == 4416) {
//                            goto switch_1_4416;
//                          } else {
//                            if (s__state == 4417) {
//                              goto switch_1_4417;
//                            } else {
//                              if (s__state == 4432) {
//                                goto switch_1_4432;
//                              } else {
//                                if (s__state == 4433) {
//                                  goto switch_1_4433;
//                                } else {
//                                  if (s__state == 4448) {
//                                    goto switch_1_4448;
//                                  } else {
//                                    if (s__state == 4449) {
//                                      goto switch_1_4449;
//                                    } else {
//                                      if (s__state == 4464) {
//                                        goto switch_1_4464;
//                                      } else {
//                                        if (s__state == 4465) {
//                                          goto switch_1_4465;
//                                        } else {
//                                          if (s__state == 4466) {
//                                            goto switch_1_4466;
//                                          } else {
//                                            if (s__state == 4467) {
//                                              goto switch_1_4467;
//                                            } else {
//                                              if (s__state == 4480) {
//                                                goto switch_1_4480;
//                                              } else {
//                                                if (s__state == 4481) {
//                                                  goto switch_1_4481;
//                                                } else {
//                                                  if (s__state == 4496) {
//                                                    goto switch_1_4496;
//                                                  } else {
//                                                    if (s__state == 4497) {
//                                                      goto switch_1_4497;
//                                                    } else {
//                                                      if (s__state == 4512) {
//                                                        goto switch_1_4512;
//                                                      } else {
//                                                        if (s__state == 4513) {
//                                                          goto switch_1_4513;
//                                                        } else {
//                                                          if (s__state == 4528) {
//                                                            goto switch_1_4528;
//                                                          } else {
//                                                            if (s__state == 4529) {
//                                                              goto switch_1_4529;
//                                                            } else {
//                                                              if (s__state == 4560) {
//                                                                goto switch_1_4560;
//                                                              } else {
//                                                                if (s__state == 4561) {
//                                                                  goto switch_1_4561;
//                                                                } else {
//                                                                  if (s__state == 4352) {
//                                                                    goto switch_1_4352;
//                                                                  } else {
//                                                                    if (s__state == 3) {
//                                                                      goto switch_1_3;
//                                                                    } else {
//                                                                      goto switch_1_default;
//                                                                      if (0) {
//                                                                        switch_1_12292: 
//                                                                        s__new_session = 1;
//                                                                        s__state = 4096;
//                                                                        s__ctx__stats__sess_connect_renegotiate ++;
//                                                                        switch_1_16384: ;
//                                                                        switch_1_4096: ;
//                                                                        switch_1_20480: ;
//                                                                        switch_1_4099: 
//                                                                        s__server = 0;
//                                                                        if (cb != 0) {
//
//                                                                        }
//                                                                        {
//                                                                        __cil_tmp55 = s__version - 65280;
//                                                                        if (__cil_tmp55 != 768) {
//                                                                          ret = -1;
//                                                                          goto end;
//                                                                        }
//                                                                        }
//                                                                        s__type = 4096;
//                                                                        if (s__init_buf___0 == 0) {
//                                                                          buf = pX;
//                                                                          if (buf == 0) {
//                                                                            ret = -1;
//                                                                            goto end;
//                                                                          }
//                                                                          if (! tmp___3) {
//                                                                            ret = -1;
//                                                                            goto end;
//                                                                          }
//                                                                          s__init_buf___0 = buf;
//                                                                        }
//                                                                        if (! tmp___4) {
//                                                                          ret = -1;
//                                                                          goto end;
//                                                                        }
//                                                                        if (! tmp___5) {
//                                                                          ret = -1;
//                                                                          goto end;
//                                                                        }
//                                                                        s__state = 4368;
//                                                                        s__ctx__stats__sess_connect ++;
//                                                                        s__init_num = 0;
//                                                                        goto switch_1_break;
//                                                                        switch_1_4368: ;
//                                                                        switch_1_4369: 
//                                                                        s__shutdown = 0;
//                                                                        ret = pX;
//                                                                        if (blastFlag == 0) {
//                                                                          blastFlag = 1;
//                                                                        }
//                                                                        if (ret <= 0) {
//                                                                          goto end;
//                                                                        }
//                                                                        s__state = 4384;
//                                                                        s__init_num = 0;
//                                                                        if (s__bbio != s__wbio) {
//
//                                                                        }
//                                                                        goto switch_1_break;
//                                                                        switch_1_4384: ;
//                                                                        switch_1_4385: 
//                                                                        ret = pX;
//                                                                        if (blastFlag == 1) {
//                                                                          blastFlag = 2;
//                                                                        }
//                                                                        if (ret <= 0) {
//                                                                          goto end;
//                                                                        }
//                                                                        if (s__hit) {
//                                                                          s__state = 4560;
//                                                                        } else {
//                                                                          s__state = 4400;
//                                                                        }
//                                                                        s__init_num = 0;
//                                                                        goto switch_1_break;
//                                                                        switch_1_4400: ;
//                                                                        switch_1_4401: ;
//                                                                        if (s__s3__tmp__new_cipher__algorithms - 256) {
//                                                                          skip = 1;
//                                                                        } else {
//                                                                          ret = pX;
//                                                                          if (blastFlag == 2) {
//                                                                            blastFlag = 3;
//                                                                          }
//                                                                          if (ret <= 0) {
//                                                                            goto end;
//                                                                          }
//                                                                        }
//                                                                        s__state = 4416;
//                                                                        s__init_num = 0;
//                                                                        goto switch_1_break;
//                                                                        switch_1_4416: ;
//                                                                        switch_1_4417: 
//                                                                        ret = pX;
//                                                                        if (blastFlag == 3) {
//                                                                          blastFlag = 4;
//                                                                        }
//                                                                        if (ret <= 0) {
//                                                                          goto end;
//                                                                        }
//                                                                        s__state = 4432;
//                                                                        s__init_num = 0;
//                                                                        if (! tmp___6) {
//                                                                          ret = -1;
//                                                                          goto end;
//                                                                        }
//                                                                        goto switch_1_break;
//                                                                        switch_1_4432: ;
//                                                                        switch_1_4433: 
//                                                                        ret = pX;
//                                                                        if (blastFlag == 4) {
//                                                                          goto ERROR;
//                                                                        }
//                                                                        if (ret <= 0) {
//                                                                          goto end;
//                                                                        }
//                                                                        s__state = 4448;
//                                                                        s__init_num = 0;
//                                                                        goto switch_1_break;
//                                                                        switch_1_4448: ;
//                                                                        switch_1_4449: 
//                                                                        ret = pX;
//                                                                        if (blastFlag == 4) {
//                                                                          blastFlag = 5;
//                                                                        }
//                                                                        if (ret <= 0) {
//                                                                          goto end;
//                                                                        }
//                                                                        if (s__s3__tmp__cert_req) {
//                                                                          s__state = 4464;
//                                                                        } else {
//                                                                          s__state = 4480;
//                                                                        }
//                                                                        s__init_num = 0;
//                                                                        goto switch_1_break;
//                                                                        switch_1_4464: ;
//                                                                        switch_1_4465: ;
//                                                                        switch_1_4466: ;
//                                                                        switch_1_4467: 
//                                                                        ret = pX;
//                                                                        if (ret <= 0) {
//                                                                          goto end;
//                                                                        }
//                                                                        s__state = 4480;
//                                                                        s__init_num = 0;
//                                                                        goto switch_1_break;
//                                                                        switch_1_4480: ;
//                                                                        switch_1_4481: 
//                                                                        ret = pX;
//                                                                        if (ret <= 0) {
//                                                                          goto end;
//                                                                        }
//                                                                        l = (unsigned long )s__s3__tmp__new_cipher__algorithms;
//                                                                        if (s__s3__tmp__cert_req == 1) {
//                                                                          s__state = 4496;
//                                                                        } else {
//                                                                          s__state = 4512;
//                                                                          s__s3__change_cipher_spec = 0;
//                                                                        }
//                                                                        s__init_num = 0;
//                                                                        goto switch_1_break;
//                                                                        switch_1_4496: ;
//                                                                        switch_1_4497: 
//                                                                        ret = pX;
//                                                                        if (ret <= 0) {
//                                                                          goto end;
//                                                                        }
//                                                                        s__state = 4512;
//                                                                        s__init_num = 0;
//                                                                        s__s3__change_cipher_spec = 0;
//                                                                        goto switch_1_break;
//                                                                        switch_1_4512: ;
//                                                                        switch_1_4513: 
//                                                                        ret = pX;
//                                                                        if (ret <= 0) {
//                                                                          goto end;
//                                                                        }
//                                                                        s__state = 4528;
//                                                                        s__init_num = 0;
//                                                                        s__session__cipher = s__s3__tmp__new_cipher;
//                                                                        if (s__s3__tmp__new_compression == 0) {
//                                                                          s__session__compress_meth = 0;
//                                                                        } else {
//                                                                          s__session__compress_meth = s__s3__tmp__new_compression__id;
//                                                                        }
//                                                                        if (! tmp___7) {
//                                                                          ret = -1;
//                                                                          goto end;
//                                                                        }
//                                                                        if (! tmp___8) {
//                                                                          ret = -1;
//                                                                          goto end;
//                                                                        }
//                                                                        goto switch_1_break;
//                                                                        switch_1_4528: ;
//                                                                        switch_1_4529: 
//                                                                        ret = pX;
//                                                                        if (ret <= 0) {
//                                                                          goto end;
//                                                                        }
//                                                                        s__state = 4352;
//                                                                        __cil_tmp56 = (long )s__s3__flags;
//                                                                        __cil_tmp57 = __cil_tmp56 + 5;
//                                                                        s__s3__flags = (int )__cil_tmp57;
//                                                                        if (s__hit) {
//                                                                          s__s3__tmp__next_state___0 = 3;
//                                                                          {
//                                                                          __cil_tmp58 = (long )s__s3__flags;
//                                                                          if (__cil_tmp58 - 2L) {
//                                                                            s__state = 3;
//                                                                            __cil_tmp59 = (long )s__s3__flags;
//                                                                            __cil_tmp60 = __cil_tmp59 + 4L;
//                                                                            s__s3__flags = (int )__cil_tmp60;
//                                                                            s__s3__delay_buf_pop_ret = 0;
//                                                                          }
//                                                                          }
//                                                                        } else {
//                                                                          s__s3__tmp__next_state___0 = 4560;
//                                                                        }
//                                                                        s__init_num = 0;
//                                                                        goto switch_1_break;
//                                                                        switch_1_4560: ;
//                                                                        switch_1_4561: 
//                                                                        ret = pX;
//                                                                        if (ret <= 0) {
//                                                                          goto end;
//                                                                        }
//                                                                        if (s__hit) {
//                                                                          s__state = 4512;
//                                                                        } else {
//                                                                          s__state = 3;
//                                                                        }
//                                                                        s__init_num = 0;
//                                                                        goto switch_1_break;
//                                                                        switch_1_4352: 
//                                                                        {
//                                                                        __cil_tmp61 = (long )num1;
//                                                                        if (__cil_tmp61 > 0L) {
//                                                                          s__rwstate = 2;
//                                                                          num1 = tmp___9;
//                                                                          {
//                                                                          __cil_tmp62 = (long )num1;
//                                                                          if (__cil_tmp62 <= 0L) {
//                                                                            ret = -1;
//                                                                            goto end;
//                                                                          }
//                                                                          }
//                                                                          s__rwstate = 1;
//                                                                        }
//                                                                        }
//                                                                        s__state = s__s3__tmp__next_state___0;
//                                                                        goto switch_1_break;
//                                                                        switch_1_3: 
//                                                                        if (s__init_buf___0 != 0) {
//                                                                          s__init_buf___0 = 0;
//                                                                        }
//                                                                        {
//                                                                        __cil_tmp63 = (long )s__s3__flags;
//                                                                        __cil_tmp64 = __cil_tmp63 - 4L;
//                                                                        if (! __cil_tmp64) {
//
//                                                                        }
//                                                                        }
//                                                                        s__init_num = 0;
//                                                                        s__new_session = 0;
//                                                                        if (s__hit) {
//                                                                          s__ctx__stats__sess_hit ++;
//                                                                        }
//                                                                        ret = 1;
//                                                                        s__ctx__stats__sess_connect_good ++;
//                                                                        if (cb != 0) {
//
//                                                                        }
//                                                                        goto end;
//                                                                        switch_1_default: 
//                                                                        ret = -1;
//                                                                        goto end;
//                                                                      } else {
//                                                                        switch_1_break: ;
//                                                                      }
//                                                                    }
//                                                                  }
//                                                                }
//                                                              }
//                                                            }
//                                                          }
//                                                        }
//                                                      }
//                                                    }
//                                                  }
//                                                }
//                                              }
//                                            }
//                                          }
//                                        }
//                                      }
//                                    }
//                                  }
//                                }
//                              }
//                            }
//                          }
//                        }
//                      }
//                    }
//                  }
//                }
//              }
//            }
//          }
//        }
//      }
//    }
//    if (! s__s3__tmp__reuse_message) {
//      if (! skip) {
//        if (s__debug) {
//          ret = pX;
//          if (ret <= 0) {
//            goto end;
//          }
//        }
//        if (cb != 0) {
//          if (s__state != state) {
//            new_state = s__state;
//            s__state = state;
//            s__state = new_state;
//          }
//        }
//      }
//    }
//    skip = 0;
//  }
//  while_0_break: /* CIL Label */ ;
//  }
//
//  end: 
//  s__in_handshake --;
//  if (cb != 0) {
//
//  }
//  return (ret);
//  
//  ERROR: 
//  return (-1);
//}
//}
//
// public void main() 
//{ int s ;
//
//  {
//  {
//  s = 12292;
//  ssl3_connect(12292);
//  }
//  return (0);
//}
//}
//  
}
