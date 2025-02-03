package hexlet.code.utils;

public final class NamedRoutes {
    /**
     * @return main page url such as: "/"
     */
    public static String root() {
        return "/";
    }

    /**
     * @return index page of urls such as: "/urls"
     */
    public static String urlsIndex() {
        return "/urls";
    }

    /**
     * @param id an url id
     * @return url show page such as: "/urls/{id}"
     */
    public static String urlShow(Long id) {
        return urlShow(String.valueOf(id));
    }

    /**
     * @param id an url id
     * @return url show page such as: "/urls/{id}"
     */
    public static String urlShow(String id) {
        return "/urls/" + id;
    }

    /**
     * @param id an url id
     * @return url for check request such as: "/urls/{id}/checks"
     */
    public static String urlCheck(Long id) {
        return urlCheck(String.valueOf(id));
    }

    /**
     * @param id an url id
     * @return url for check request such as: "/urls/{id}/checks"
     */
    public static String urlCheck(String id) {
        return "/urls/" + id + "/checks";
    }
}
