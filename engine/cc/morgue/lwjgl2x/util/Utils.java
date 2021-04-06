package cc.morgue.lwjgl2x.util;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * A collection of miscellaneous static functions.
 */
public class Utils {

    public enum OperatingSystem {
        WINDOWS, LINUX, MAC;
    }
    
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0");

	/**
	 * Default valid characters.
	 */
	private static final char VALID_CHARS[] = { '!', '@', '#', '$', '%', '^',
			'&', '*', '(', ')', '-', '_', '=', '+', '[', '{', ']', '}', ';',
			':', '\'', '"', ',', '<', '.', '>', '/', '?', ' ' };

    public Utils() {
        // ..
    }

    /**
     * @return The operating system of the local machine.
     */
    public static OperatingSystem getOperatingSystem() {
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().contains("windows")) {
            // Logger.getLogger(Platform.class.getName()).log(Level.INFO, "User is running " + osName);
            return OperatingSystem.WINDOWS;
        }
        if (osName.toLowerCase().contains("mac") || osName.toLowerCase().contains("os x") || (osName.toLowerCase().contains("darwin"))) {
            // Logger.getLogger(Platform.class.getName()).log(Level.INFO, "User is running " + osName);
            return OperatingSystem.MAC;
        }
        try {
            // Logger.getLogger(Platform.class.getName()).log(Level.INFO, "User is running " + execute("lsb_release -si") + "");
            return OperatingSystem.LINUX;
        } catch (Exception e) {
            throw new RuntimeException("Unable to determine operating system.", e);
        }
    }

    /**
     * @return The current calendar date, formatted as <code>Month ##, ####</code>
     */
    public static String getCalendarDate() {
        return Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + ", " + Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * @return The current calendar time, formatted as <code>HH:MM.SS</code>
     */
    public static String getCalendarTime() {
        return Calendar.getInstance().get(Calendar.HOUR) + ":" + Calendar.getInstance().get(Calendar.MINUTE) + "." + (Calendar.getInstance().get(Calendar.SECOND) + 1);
    }

    /**
     * @return The current time in milliseconds (using nanosecond precision).
     */
    public static long getCurrentTimeMillis() {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
    }

    /**
     * The amount of memory being used by the application.
     */
    public static String getMemoryUsage() {
        //return (MemoryUtils.getDirectMemoryUsage() / 1000000) + "MB";
        return formatSize(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
    }
    
    /**
     * Request a garbage collection, to free unallocated memory.
     */
    public static String gc() {
        long usage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
        long cleanup = usage - (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        if (cleanup > 0) {
            return formatSize(cleanup);
        }
        return "NaN";
    }

    /**
     * Returns the distance between this entity and the target Vector3f.
     *
     * NOTE: This function ignores the Y (height) coordinate.
     */
    public static int distanceTo(int x, int z, int xx, int zz) {
        // https://github.com/dylanvicc/Florence/blob/master/src/com/florence/task/impl/EntityCombatFollowingTask.java
        int dx = Math.abs(x - xx);
        int dz = Math.abs(z - zz);
        return dx + dz;
        //return roundToInteger(Math.sqrt(Math.pow(x - xx, 2) + Math.pow(z - zz, 2)));
    }

    /**
     * Updates the given String of text, by enabling a backspace feature, and by
     * clipping the said text at the given maximum length.
     */
    public static String formatInputString(String input, int keyCode, char keyChar, int clipLength, int backspaceKey) {
        if (keyCode == backspaceKey && input.length() > 0) {
            return input.substring(0, input.length() - 1);
		} else if (validChar(keyChar) && input.length() + 1 <= clipLength) {
            return input += keyChar;
        }
        return input;
    }

	/**
	 * aA-zZ, 0-9, and VALID_CHARS[] are THE ONLY usable chat
	 * characters.
	 */
	public static boolean validChar(char letterOrDigit) {
		if (Character.isLetterOrDigit(letterOrDigit)) {
			return true;
		}
		for (int i = 0; i < VALID_CHARS.length; i++) {
			if (letterOrDigit == VALID_CHARS[i]) {
				return true;
			}
		}
		return false;
	}

    /**
     * Replaces the input string with asterisks.
     */
    public static String replaceWithAsterisks(String string) {
        StringBuilder asterisks = new StringBuilder();
        int l = string.length();
        for (int i = 0; i < l; i++) {
            asterisks.append("*");
        }
        return asterisks.toString();
    }

    /**
     * Formats the given number with a more readable output, example: 9001 becomes
     * 9,001.
     */
    public static String formatNumber(int number) {
        return NumberFormat.getInstance().format(number);
    }

    /**
     * @param millis
     *            The millisecond time to be formatted.
     * @return Returns the given millisecond time value as HH:MM:SS
     */
    public static String formatTimeMillis(long millis) {
        if (millis < 1000) {
            return millis + "ms";
        }
        int s = (int) (millis / 1000) % 60;
        int m = (int) ((millis / (1000 * 60)) % 60);
        int h = (int) ((millis / (1000 * 60 * 60)) % 24);
        return h + ":" + m + ":" + s;
    }

    /**
     * Formats the supplied size value from bytes to a more readable value.
     */
    public static String formatSize(long bytes) {
        double kb = bytes / 1024.0;
        double mb = (kb / 1024.0);
        double gb = (mb / 1024.0);
        if (gb > 1) {
            return DECIMAL_FORMAT.format(gb).concat("GB");
        } else if (mb > 1) {
            return DECIMAL_FORMAT.format(mb).concat("MB");
        } else if (kb > 1) {
            return DECIMAL_FORMAT.format(kb).concat("KB");
        } else {
            return DECIMAL_FORMAT.format(bytes).concat("B");
        }
    }

    /**
     * @see #formatSize(long)
     */
    public static String formatSize(File file) {
        return formatSize(file.length());
    }

    /**
     * @return The system archetype; 32 or 64 as an integer.
     */
	public static int getArchetype() {
		String arch = System.getenv("PROCESSOR_ARCHITECTURE");
		String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");
		String realArch = arch.endsWith("64") || wow64Arch != null && wow64Arch.endsWith("64") ? "64" : "32";
		return realArch.equals("64") ? 64 : 32;
	}

	/**
	 * @return The installed java version (as a double).
	 */
	public static double getJavaVersion() {
		String version = System.getProperty("java.version");
		int pos = version.indexOf('.');
		pos = version.indexOf('.', pos + 1);
		return Double.parseDouble(version.substring(0, pos));
	}

	public static String getUserHomeDirectory() {
		return new File(System.getProperty("user.home") + File.separator).getAbsolutePath() + File.separator;
	}

	public static String getDesktopDirectory() {
        return new File(new File(System.getProperty("user.home") + File.separator, "Desktop").getAbsolutePath() + File.separator).getAbsolutePath() + File.separator;
	}

}
