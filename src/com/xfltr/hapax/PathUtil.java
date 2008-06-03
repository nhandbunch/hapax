package com.xfltr.hapax;

/**
 * Utility methods for manipulation of UNIX-like paths. See PathUtilTest for
 * examples.
 *
 * This class does probably not create path names that are compatible with
 * Windows.
 *
 * @author dcoker
 */
public final class PathUtil {

  private PathUtil() {
  }

  public static boolean isAbsolute(String path) {
    return path.startsWith("/");
  }


  /**
   * Joins a set of path components into a single path.
   *
   * @param components the components of the path
   * @return the components joined into a string, delimited by slashes.  Runs of
   *         slashes are reduced to a single slash.  If present, leading slash
   *         on the initial component and trailing slash on the final component
   *         are preserved.
   */
  public static String join(String... components) {
    StringBuilder path = new StringBuilder();

    for (int i = 0; i < components.length; i++) {
      path.append(components[i]);
      if (i < components.length - 1
          && components[i + 1].length() > 0
          && !(i == 0 && components[0].length() == 0)) {
        path.append("/");
      }
    }

    return path.toString().replaceAll("[/]+", "/");
  }

  /**
   * Returns a path that is relative to 'dir' for the given 'fullPath'.  Never
   * returns a string with a trailing slash.
   *
   * Occurences of ".." are silently ignored.  It is the responsibility of the
   * caller to ensure that the paths given to this method do not contain a
   * "..".
   *
   * @param dir The path that you wish to make fullPath relative to
   * @return a path relative to dir.  The returned value will never start with a
   *         slash.
   */
  public static String makeRelative(String dir, String fullPath) {
    dir = removeExtraneousSlashes(dir);
    fullPath = removeExtraneousSlashes(fullPath);

    /**
     * If fullpath is indeed underneath dir, then we strip dir from fullPath to
     * get the relative relativePath.  If not, we just use fullPath and assume
     * that it is already a relative relativePath.
     */
    String relativePath;
    if (fullPath.startsWith(dir)) {
      relativePath = fullPath.substring(dir.length());
    } else {
      relativePath = fullPath;
    }

    // Intentional denial: all components with ".." are removed.
    String clean = relativePath
        .replace("/../", "/")
        .replace("/..", "/")
        .replace("../", "/");
    relativePath = join(clean.split("/"));
    relativePath = removeLeadingSlashes(relativePath);

    return relativePath;
  }

  /**
   * Removes leading slashes from a string.
   */
  public static String removeLeadingSlashes(String path) {
    return path.replaceFirst("[/]*", "");
  }

  /**
   * Removes extra slashes from a path.  Leading slash is preserved, trailing
   * slash is stripped, and any runs of more than one slash in the middle is
   * replaced by a single slash.
   */
  public static String removeExtraneousSlashes(String s) {
    s = s.replaceAll("(.)[/]+$", "$1");
    return s.replaceAll("[/]+", "/");
  }


}
