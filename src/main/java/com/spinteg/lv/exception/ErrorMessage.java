package com.spinteg.lv.exception;

import java.util.Date;

public class ErrorMessage {
  private int status;
  private String message;

  public ErrorMessage(int status, Date timestamp, String message, String details) {
    this.status = status;
    this.message = message;
  }
}
