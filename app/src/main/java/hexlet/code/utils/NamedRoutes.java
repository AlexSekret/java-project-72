package hexlet.code.utils;

public final class NamedRoutes {
    public static String root() {
        return "/";
    }

    public static String urlsIndex() {
        return "/urls";
    }

    public static String urlShow(Long id) {
        return urlShow(String.valueOf(id));
    }

    public static String urlShow(String id) {
        return "/urls/" + id;
    }

    public static String urlCheck(Long id) {
        return urlCheck(String.valueOf(id));
    }

    public static String urlCheck(String s) {
        return "/urls/" + s + "/check";
    }
}
