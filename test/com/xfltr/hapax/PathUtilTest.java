/**
 * Copyright 2005 Google Inc.
 * All rights reserved.
 */

package com.xfltr.hapax;

import junit.framework.TestCase;

/**
 * Unit tests for the {@link PathUtil} class.
 *
 * @author dcoker
 */
public class PathUtilTest extends TestCase {

  public void testJoin() throws Exception {
    assertEquals("/a", PathUtil.join("/a"));
    assertEquals("a", PathUtil.join("a"));
    assertEquals("a/b/c", PathUtil.join("a", "b", "c"));
    assertEquals("a/c", PathUtil.join("a", "", "c"));
    assertEquals("a", PathUtil.join("a", "", ""));
    assertEquals("a/ ", PathUtil.join("a", "", " "));
    assertEquals("a/ / ", PathUtil.join("a", " ", " "));
    assertEquals(" /a/b/c/ /e/", PathUtil.join(" ", "a", "b", "c", " ", "e/"));
    assertEquals("/foo/bar", PathUtil.join("/", "foo", "bar"));
    assertEquals("/foo/bar", PathUtil.join("//", "foo", "bar"));
    assertEquals("/foo/bar", PathUtil.join("//", "/foo/", "/bar"));
    assertEquals("/foo/bar/", PathUtil.join("//", "/foo/", "/bar/"));
    assertEquals("/foo/bar/", PathUtil.join("//", "foo", "bar/"));
    assertEquals("/bar", PathUtil.join("//", "//", "bar"));
    assertEquals("/foo/bar", PathUtil.join("/foo/", "bar"));
    assertEquals("/alpha/beta/gamma",
                 PathUtil.join("/alpha", "/beta", "gamma"));
    assertEquals("/alpha/beta/gamma",
                 PathUtil.join("/alpha", "/beta/", "gamma"));
    assertEquals("/alpha/beta/gamma",
                 PathUtil.join("/alpha/", "/beta/", "gamma"));
    assertEquals("", PathUtil.join("", ""));
    assertEquals(" ", PathUtil.join(" "));
    assertEquals(" / ", PathUtil.join(" ", " "));
    assertEquals(" / / / ", PathUtil.join(" ", " ", " ", " "));
    assertEquals(" / / / /a", PathUtil.join(" ", " ", " ", " ", "a"));
    assertEquals(" / / / /a/", PathUtil.join(" ", " ", " ", " ", "a/"));
    assertEquals("foo/", PathUtil.join("", "foo/"));
    assertEquals("foo", PathUtil.join("", "foo"));
    assertEquals("/foo", PathUtil.join("", "/foo"));
    assertEquals("/foo/bar", PathUtil.join("", "/foo/bar"));
    assertEquals("/foo/bar", PathUtil.join("", "", "/foo/bar"));
    assertEquals("/foo/bar", PathUtil.join("", "//foo/bar"));
    assertEquals("foo/bar", PathUtil.join("", "foo/bar"));
    assertEquals(" foo/bar", PathUtil.join("", " foo/bar"));
    assertEquals(" foo/ bar", PathUtil.join("", " foo/ bar"));
    assertEquals(" foo/ bar", PathUtil.join("", " foo/ bar", ""));

  }

  public void testMakeRelative() throws Exception {
    assertEquals("sample",
                 PathUtil.makeRelative("/gfs/bp/pso", "/gfs/bp/pso/sample"));
    assertEquals("sample",
                 PathUtil.makeRelative("/gfs/bp/pso", "/gfs/bp/pso/sample/"));
    assertEquals("sample",
                 PathUtil.makeRelative("/gfs/bp/pso/", "/gfs/bp/pso/sample"));
    assertEquals("sample",
                 PathUtil.makeRelative("/gfs/bp/pso/", "/gfs/bp/pso/sample/"));
    assertEquals("sample/foo",
                 PathUtil.makeRelative("/gfs/bp/pso/",
                                       "/gfs/bp/pso/sample/foo"));
    assertEquals("sample/foo",
                 PathUtil.makeRelative("/gfs/bp/pso/",
                                       "/gfs/bp/pso/sample/foo/"));
    assertEquals("", PathUtil.makeRelative("/gfs/bp/pso/", "/gfs/bp/pso"));
    assertEquals("", PathUtil.makeRelative("/gfs/bp/pso", "/gfs/bp/pso/"));
    assertEquals("", PathUtil.makeRelative("/gfs/bp/pso", "/gfs/bp/pso///"));
    assertEquals("", PathUtil.makeRelative("/gfs/bp/pso", "//gfs//bp//pso///"));
    assertEquals("already_relative",
                 PathUtil.makeRelative("/gfs/bp/pso", "already_relative"));
    assertEquals("already/relative",
                 PathUtil.makeRelative("/gfs/bp/pso", "already/relative"));
    assertEquals("already/relative",
                 PathUtil.makeRelative("/gfs/bp/pso", "already/relative/"));
    assertEquals("already/relative",
                 PathUtil.makeRelative("/gfs/bp/pso", "/already/relative/"));
    assertEquals("already/relative",
                 PathUtil.makeRelative("/gfs/bp/pso", "../already/relative/"));
    assertEquals("already/relative",
                 PathUtil.makeRelative("/gfs/bp/pso",
                                       "../already/../relative/"));
    assertEquals("already/relative",
                 PathUtil.makeRelative("/gfs/bp/pso",
                                       "../already//relative/.."));
    assertEquals("already/relative",
                 PathUtil.makeRelative("/gfs/bp/pso",
                                       "../already//relative/../"));
    assertEquals("already/relative",
                 PathUtil.makeRelative("/gfs/bp/pso",
                                       "../already//relative/../////"));
    assertEquals("already/relative",
                 PathUtil.makeRelative("/gfs/bp/pso",
                                       "///../already//relative/../////"));
  }

  public void testRemoveExtraneousSlashes() throws Exception {
    assertEquals("", PathUtil.removeExtraneousSlashes(""));
    assertEquals("/", PathUtil.removeExtraneousSlashes("/"));
    assertEquals("/", PathUtil.removeExtraneousSlashes("///"));
    assertEquals(".", PathUtil.removeExtraneousSlashes(".///"));
    assertEquals("/.", PathUtil.removeExtraneousSlashes("/./"));
    assertEquals("/usr/local",
                 PathUtil.removeExtraneousSlashes("/usr/local///"));
    assertEquals("/usr/local",
                 PathUtil.removeExtraneousSlashes("/usr//local///"));
    assertEquals("/usr/local",
                 PathUtil.removeExtraneousSlashes("//usr//local///"));
    assertEquals("usr/local",
                 PathUtil.removeExtraneousSlashes("usr//local///"));
  }

  public void testRemoveLeadingSlashes() throws Exception {
    assertEquals("", PathUtil.removeLeadingSlashes("/"));
    assertEquals("foo", PathUtil.removeLeadingSlashes("foo"));
    assertEquals("foo", PathUtil.removeLeadingSlashes("/foo"));
    assertEquals("foo", PathUtil.removeLeadingSlashes("//foo"));
    assertEquals("foo//", PathUtil.removeLeadingSlashes("foo//"));
  }
}
