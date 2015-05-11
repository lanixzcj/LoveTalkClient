package com.example.lovetalk.service;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;

/**
 * Created by lzw on 14-9-27.
 */
@AVClassName("AddRequest")
public class AddRequest extends AVObject {
  public static final int STATUS_WAIT = 0;
  public static final int STATUS_DONE = 1;

  public static final String FROM_USER = "fromUser";
  public static final String TO_USER = "toUser";
  public static final String STATUS = "status";
  //User fromUser;
  //User toUser;
  //int status;

  public AddRequest() {
  }

  public AVUser getFromUser() {
    return getAVUser(FROM_USER, AVUser.class);
  }

  public void setFromUser(AVUser fromUser) {
    put(FROM_USER, fromUser);
  }

  public AVUser getToUser() {
    return getAVUser(TO_USER, AVUser.class);
  }

  public void setToUser(AVUser toUser) {
    put(TO_USER, toUser);
  }

  public int getStatus() {
    return getInt(STATUS);
  }

  public void setStatus(int status) {
    put(STATUS, status);
  }
}
