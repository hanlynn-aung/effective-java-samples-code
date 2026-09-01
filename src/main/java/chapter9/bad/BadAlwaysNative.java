package chapter9.bad;

public final class BadAlwaysNative {

    // Pretend this calls a JNI bridge to uppercase a string.
    public String uppercaseNative(String input) {
        // In a real bad example this would System.loadLibrary(...) and call native code.
        throw new UnsupportedOperationException("native uppercase (no portability, crashes JVM)");
    }

    public long currentTimeNative() {
        // JNI gettimeofday() when System.currentTimeMillis() exists.
        throw new UnsupportedOperationException("native time (System.currentTimeMillis does it)");
    }
}