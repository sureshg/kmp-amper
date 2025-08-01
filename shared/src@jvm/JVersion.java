public class JVersion {
    public static String get() {
        System.out.println(ScopedValue.newInstance());
        return System.getProperty("java.version");
    }
}
