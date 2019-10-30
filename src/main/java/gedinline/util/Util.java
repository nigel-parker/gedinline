package gedinline.util;

public class Util {
    public static void signal(Throwable t) throws Throwable {
        StackTraceElement[] stackTrace = t.getStackTrace();

        for (StackTraceElement stackTraceElement : stackTrace) {
            if (!stackTraceElement.getClassName().startsWith("org.testng")) {
                System.out.println("");
                System.out.println("     *** " + t + " at " + stackTraceElement);
                System.out.println("");
                break;
            }
        }

        throw t;
    }

    private static final long MEMORY_THRESHOLD = 10;
    private static long lastUsage = 0;

    /**
     * returns a String describing current memory utilization. In addition unusually large
     * changes in memory usage will be logged.
     */
    public static String getMemoryUsage() {

        Runtime runtime = Runtime.getRuntime();
        long freeMemory = runtime.freeMemory();
        long totalMemory = runtime.totalMemory();
        long usedMemory = totalMemory - freeMemory;
        long usedInMegabytes = usedMemory / 1000000;
        long totalInMegabytes = totalMemory / 1000000;
        String memoryStatus = usedInMegabytes + "M / " + totalInMegabytes + "M / " + (runtime.maxMemory() / 1000000) + "M";

        if (usedInMegabytes <= lastUsage - MEMORY_THRESHOLD ||
                usedInMegabytes >= lastUsage + MEMORY_THRESHOLD) {
            //System.out.println("Memory usage: " + memoryStatus);
            lastUsage = usedInMegabytes;
        }

        return memoryStatus;
    }
}
