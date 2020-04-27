package org.hcm.libjenkins

// Class declaration
abstract class AbstractLibClass implements Serializable {
  public Script script

  AbstractLibClass ( script ) {
    this.script = script
  }

  AbstractLibClass () {
    this.script = null
  }
}