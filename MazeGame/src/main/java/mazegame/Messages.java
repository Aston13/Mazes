package mazegame;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Provides localised UI strings backed by {@link ResourceBundle}. Falls back to English when a key
 * is missing in the active locale.
 *
 * <p>Usage: {@code Messages.get("button.play")} or {@code Messages.fmt("message.level_failed", 5)}.
 */
public final class Messages {

  private static final String BUNDLE_NAME = "mazegame.Messages";
  private static volatile Locale activeLocale = Locale.ENGLISH;
  private static volatile ResourceBundle bundle = loadBundle(activeLocale);

  private Messages() {}

  /** Sets the active locale and reloads the resource bundle. */
  public static void setLocale(Locale locale) {
    activeLocale = locale;
    bundle = loadBundle(locale);
  }

  /** Returns the current active locale. */
  public static Locale getLocale() {
    return activeLocale;
  }

  /**
   * Returns the localised string for the given key, or the key itself if not found.
   *
   * @param key the resource bundle key
   * @return the localised string
   */
  public static String get(String key) {
    try {
      return bundle.getString(key);
    } catch (MissingResourceException e) {
      return key;
    }
  }

  /**
   * Returns a formatted localised string, substituting arguments with {@link MessageFormat}.
   *
   * @param key the resource bundle key
   * @param args the format arguments
   * @return the formatted localised string
   */
  public static String fmt(String key, Object... args) {
    String pattern = get(key);
    try {
      return MessageFormat.format(pattern, args);
    } catch (IllegalArgumentException e) {
      return pattern;
    }
  }

  private static ResourceBundle loadBundle(Locale locale) {
    try {
      return ResourceBundle.getBundle(BUNDLE_NAME, locale);
    } catch (MissingResourceException e) {
      return ResourceBundle.getBundle(BUNDLE_NAME, Locale.ENGLISH);
    }
  }
}
