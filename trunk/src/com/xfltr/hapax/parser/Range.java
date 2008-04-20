package com.xfltr.hapax.parser;

/**
 * Tuple for start, stop, and skipTo.  Used by nodes to tell {@link
 * com.xfltr.hapax.Template} which sublists of the Template should be
 * processed.
 *
 * @author dcoker
 */
public class Range {
  private final int start;
  private final int stop;
  private final int skipTo;

  public Range(int start, int stop, int skipTo) {
    this.start = start;
    this.stop = stop;
    this.skipTo = skipTo;
  }

  public int getStart() {
    return start;
  }

  public int getStop() {
    return stop;
  }

  public int getSkipTo() {
    return skipTo;
  }
}
