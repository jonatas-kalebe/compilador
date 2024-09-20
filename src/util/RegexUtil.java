package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


// Classe utilitária para expressões regulares
public class RegexUtil {
    private static final String IDENTIFY_CLASS = "class\\s+\\w+[\\s\\S]+?end-class";
    private static final String IDENTIFY_METHOD = "method\\s+\\w+\\s*\\([^)]*\\)[\\s\\S]+?end-method";
    private static final String IDENTIFY_IF = "if[\\s\\S]+?end-if";
    private static final String IDENTIFY_METHOD_BODY = "begin[\\s\\S]+?end-method";
    private static final String IDENTIFY_MAIN = "main\\(\\)[\\s\\S]+?end$";
    private static final String IDENTIFY_METHOD_PARAMS = "method\\s+\\w+\\s*\\((.*)\\)";

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

    public static String extractMethodBody(String code) {
        return extractMatches(IDENTIFY_METHOD_BODY, code).getFirst();
    }
    public static List<String> extractIfs(String code) {
        return extractMatches(IDENTIFY_IF, code);
    }

    private static String findLinesStatingWith(String code, String start) {
        return Arrays.stream(code.split("\n"))
                .map(String::trim)
                .filter(line -> line.startsWith(start)).findFirst().orElse("");
    }

    public static List<String> extractVars(String code) {
        String linesVar=findLinesStatingWith(code, "vars");

        List<String> varsList = new ArrayList<>();
        String[] lines = linesVar.split("\n");
        for (String line : lines) {
            line = line.replace("vars", "").trim();
            String[] vars = line.split(",");
            for (String var : vars) {
                varsList.add(var.trim());
            }
        }
        return varsList;
    }

    public static String findName(String code) {
        String className = findLinesStatingWith(code, "class");
        return className.replace("class", "").trim();
    }

    public static String findMethodName(String code) {
        String methodName = findLinesStatingWith(code, "method");
        return methodName.replaceAll("method\\s+([\\w]+)\\s*\\(.*\\)", "$1").trim();
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


    public static List<String> extractMethodParams(String method) {
        String methodName = findLinesStatingWith(method, "method");
        String params = methodName.replaceAll(IDENTIFY_METHOD_PARAMS, "$1").trim();
        return Arrays.asList(params.split(","));
    }
}
