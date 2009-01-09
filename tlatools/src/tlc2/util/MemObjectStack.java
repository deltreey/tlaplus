// Copyright (c) 2003 Compaq Corporation.  All rights reserved.
// Portions Copyright (c) 2003 Microsoft Corporation.  All rights reserved.
// Last modified on Mon 30 Apr 2007 at 13:26:34 PST by lamport
//      modified on Wed Oct 24 11:56:57 PDT 2001 by yuanyu

package tlc2.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import util.Assert;

public final class MemObjectStack extends ObjectStack {
  private final static int InitialSize = 4096;
  private final static int GrowthFactor = 2;

  /* Fields  */
  private Object[] states;
  private String filename;
    
  public MemObjectStack(String metadir, String name) {
    this.states = new Object[InitialSize];
    this.filename = metadir +  File.separator + name;
  }
    
  final void enqueueInner(Object state) {
    if (this.len == this.states.length) {
      // grow the array
      int newLen = Math.max(1, this.len * GrowthFactor);
      Object[] newStates = new Object[newLen];
      System.arraycopy(this.states, 0, newStates, 0, this.len);
      this.states = newStates;
    }
    this.states[this.len] = state;
  }
    
  final Object dequeueInner() {
    int head = this.len - 1;
    Object res = this.states[head];
    this.states[head] = null;
    return res;
  }

  // Checkpoint.
  public final void beginChkpt() throws IOException {
    String tmpfile = this.filename + ".tmp";
    FileOutputStream fos = new FileOutputStream(tmpfile);
    ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos));
    oos.writeInt(this.len);
    for (int i = 0; i < this.len; i++) {
      oos.writeObject(this.states[i++]);
    }
    oos.close();
    fos.close();
  }

  public final void commitChkpt() throws IOException {
    String oldName = this.filename + ".chkpt";
    File oldChkpt = new File(oldName);
    String newName = this.filename + ".tmp";
    File newChkpt = new File(newName);
    if (!newChkpt.renameTo(oldChkpt)) {
      throw new IOException("MemObjectStack.commitChkpt: cannot delete " + oldChkpt);
    }
  }
  
  public final void recover() throws IOException {
    String chkptfile = this.filename + ".chkpt";
    FileInputStream fis = new FileInputStream(chkptfile);
    ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(fis));
    this.len = ois.readInt();
    try {
      for (int i = 0; i < this.len; i++) {
	this.states[i] = ois.readObject();
      }
    }
    catch (ClassNotFoundException e) {
      Assert.fail("TLC encountered the following error while restarting from " +
		  "a checkpoint;\n the checkpoint file is probably corrupted.\n" +
		  e.getMessage());
    }
    finally {
      ois.close();
      fis.close();
    }
  }

}
