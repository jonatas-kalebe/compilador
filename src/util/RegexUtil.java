package util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Classe utilitária para expressões regulares
public class RegexUtil {
    private static final String IDENTIFY_CLASS = "class\\s+\\w+[\\s\\S]+?end-class";
    private static final String IDENTIFY_METHOD = "method\\s+\\w+\\s*\\([^)]*\\)[\\s\\S]+?end-method";
    private static final String IDENTIFY_MAIN = "main\\(\\)[\\s\\S]+?end";

    private RegexUtil() {
    }

    public static List<String> extractClasses(String code) {
        return extractMatches(IDENTIFY_CLASS, code);
    }

    public static List<String> extractMethods(String code) {
        return extractMatches(IDENTIFY_METHOD, code);
    }

    public static List<String> extractMain(String code) {
        return extractMatches(IDENTIFY_MAIN, code);
    }

    private static List<String> extractMatches(String regex, String code) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(code);

        List<String> definitions = new ArrayList<>();

        while (matcher.find()) {
            String definition = matcher.group();
            definitions.add(definition);
        }
        return definitions;
    }
}
