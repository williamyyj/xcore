package org.cc.org.mvel2.templates.util.io;

import org.cc.org.mvel2.templates.util.TemplateOutputStream;

import java.io.IOException;
import java.io.OutputStream;

public class StandardOutputStream implements TemplateOutputStream {
  private OutputStream outputStream;

  public StandardOutputStream(OutputStream outputStream) {
    this.outputStream = outputStream;
  }

  public TemplateOutputStream append(CharSequence c) {
    try {
      for (int i = 0; i < c.length(); i++) {
        outputStream.write(c.charAt(i));
      }

      return this;
    }
    catch (IOException e) {
      throw new RuntimeException("failed to write to stream", e);
    }
  }

  public TemplateOutputStream append(char[] c) {
    try {

      for (char i : c) {
        outputStream.write(i);
      }
      return this;
    }
    catch (IOException e) {
      throw new RuntimeException("failed to write to stream", e);
    }
  }

  @Override
  public String toString() {
    return null;
  }
}
