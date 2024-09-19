package util;

import entidades.*;
import entidades.Number;
import estruturas.Arguments;
import estruturas.BodyStatements;
import estruturas.MainStatements;
import util.RegexUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Classe responsável por analisar o código-fonte e construir a AST
public class Parser {

    private final String code;

    public Parser(String code) {
        this.code = code;
    }

    public Program parse() {
        List<String> classCodes = RegexUtil.extractClasses(code);
        List<ClassesBlock> classesBlocks = new ArrayList<>();

        for (String classCode : classCodes) {
            ClassesBlock classesBlock = parseClass(classCode);
            classesBlocks.add(classesBlock);
        }

        List<String> mainCodes = RegexUtil.extractMain(code);
        MainBlock mainBlock = null;
        if (!mainCodes.isEmpty()) {
            mainBlock = parseMain(mainCodes.get(0));
        }

        return new Program(mainBlock, classesBlocks);
    }

    private ClassesBlock parseClass(String classCode) {
        // Extrair o nome da classe
        Pattern classNamePattern = Pattern.compile("class\\s+(\\w+)");
        Matcher classNameMatcher = classNamePattern.matcher(classCode);
        String className = "";
        if (classNameMatcher.find()) {
            className = classNameMatcher.group(1);
        }

        // Extrair definições de variáveis
        Pattern varsPattern = Pattern.compile("vars\\s+([\\w,\\s]+)");
        Matcher varsMatcher = varsPattern.matcher(classCode);
        List<String> variables = new ArrayList<>();
        if (varsMatcher.find()) {
            String varsString = varsMatcher.group(1);
            String[] varsArray = varsString.split(",");
            for (String var : varsArray) {
                variables.add(var.trim());
            }
        }

        // Extrair métodos
        List<String> methodCodes = RegexUtil.extractMethods(classCode);
        List<MethodDef> methods = new ArrayList<>();
        for (String methodCode : methodCodes) {
            MethodDef methodDef = parseMethod(methodCode);
            methods.add(methodDef);
        }

        Name classNameObj = new Name(className, "class");
        return new ClassesBlock(classNameObj, variables, methods);
    }

    private MethodDef parseMethod(String methodCode) {
        // Extrair nome do método e parâmetros
        Pattern methodHeaderPattern = Pattern.compile("method\\s+(\\w+)\\s*\\(([^)]*)\\)");
        Matcher methodHeaderMatcher = methodHeaderPattern.matcher(methodCode);
        String methodName = "";
        List<Name> parameters = new ArrayList<>();
        if (methodHeaderMatcher.find()) {
            methodName = methodHeaderMatcher.group(1);
            String paramsString = methodHeaderMatcher.group(2);
            if (!paramsString.trim().isEmpty()) {
                String[] paramsArray = paramsString.split(",");
                for (String param : paramsArray) {
                    parameters.add(new Name(param.trim(), "param"));
                }
            }
        }

        MethodHeader methodHeader = new MethodHeader(methodName, parameters);

        // Extrair definições de variáveis dentro do método
        Pattern varsPattern = Pattern.compile("vars\\s+([\\w,\\s]+)");
        Matcher varsMatcher = varsPattern.matcher(methodCode);
        List<String> variables = new ArrayList<>();
        if (varsMatcher.find()) {
            String varsString = varsMatcher.group(1);
            String[] varsArray = varsString.split(",");
            for (String var : varsArray) {
                variables.add(var.trim());
            }
        }

        // Extrair corpo do método
        Pattern methodBodyPattern = Pattern.compile("begin\\s*([\\s\\S]*?)\\s*end-method");
        Matcher methodBodyMatcher = methodBodyPattern.matcher(methodCode);
        String methodBodyCode = "";
        if (methodBodyMatcher.find()) {
            methodBodyCode = methodBodyMatcher.group(1).trim();
        }

        MethodBody methodBody = parseMethodBody(methodBodyCode);

        return new MethodDef(methodBody, methodHeader, variables);
    }

    private MethodBody parseMethodBody(String methodBodyCode) {
        // Dividir o corpo do método em declarações
        String[] statements = methodBodyCode.split("\n");
        List<BodyStatements> bodyStatements = new ArrayList<>();
        for (String statement : statements) {
            statement = statement.trim();
            if (!statement.isEmpty()) {
                BodyStatements bodyStatement = parseBodyStatement(statement);
                bodyStatements.add(bodyStatement);
            }
        }

        return new MethodBody(bodyStatements);
    }

    private BodyStatements parseBodyStatement(String statement) {
        // Tratar diferentes tipos de declarações: atribuições, chamadas de método, retornos

        if (statement.startsWith("return")) {
            String expr = statement.substring("return".length()).trim();
            Return returnStatement = new Return(expr);
            return returnStatement;
        } else if (statement.contains("=")) {
            // Atribuição
            String[] parts = statement.split("=");
            String left = parts[0].trim();
            String right = parts[1].trim();
            Name variable = new Name(left, "store");
            Arguments value = parseArgument(right);
            Attribution attribution = new Attribution(variable, value);
            return attribution;
        } else {
            // Chamada de método
            // Tratar chamadas como obj.method(args)
            MethodCall methodCall = parseMethodCall(statement);
            return methodCall;
        }
    }

    private Arguments parseArgument(String expr) {
        // Tratar diferentes tipos de argumentos: números, variáveis, criação de objeto, expressões
        if (expr.matches("\\d+")) {
            // Número
            int value = Integer.parseInt(expr);
            return new Number(value);
        } else if (expr.startsWith("new")) {
            // Novo objeto
            String className = expr.substring("new".length()).trim();
            return new New(className);
        } else if (expr.contains("+") || expr.contains("-") || expr.contains("*") || expr.contains("/")) {
            // Expressão
            // Simplificado para dois operandos
            String operator = "";
            if (expr.contains("+")) operator = "+";
            else if (expr.contains("-")) operator = "-";
            else if (expr.contains("*")) operator = "*";
            else if (expr.contains("/")) operator = "/";

            String[] operands = expr.split("[+\\-*/]");
            String operand1 = operands[0].trim();
            String operand2 = operands[1].trim();

            Name op1 = new Name(operand1, "load");
            Name op2 = new Name(operand2, "load");

            return  new Attribution(null, op1, op2, operator);
        } else {
            // Variável
            return new Name(expr, "load");
        }
    }

    private MethodCall parseMethodCall(String statement) {
        // Tratar chamadas de método como obj.method(args)
        Pattern methodCallPattern = Pattern.compile("(\\w+)\\.(\\w+)\\s*\\(([^)]*)\\)");
        Matcher methodCallMatcher = methodCallPattern.matcher(statement);
        if (methodCallMatcher.find()) {
            String objectName = methodCallMatcher.group(1);
            String methodName = methodCallMatcher.group(2);
            String argsString = methodCallMatcher.group(3);
            Name object = new Name(objectName, "load");
            Name method = new Name(methodName, "call");

            List<Name> args = new ArrayList<>();
            if (!argsString.trim().isEmpty()) {
                String[] argsArray = argsString.split(",");
                for (String arg : argsArray) {
                    args.add(new Name(arg.trim(), "load"));
                }
            }

            return new MethodCall(object, method, args);
        } else {
            // Caso não seja uma chamada válida
            return null;
        }
    }

    private MainBlock parseMain(String mainCode) {
        // Extrair definições de variáveis
        Pattern varsPattern = Pattern.compile("vars\\s+([\\w,\\s]+)");
        Matcher varsMatcher = varsPattern.matcher(mainCode);
        List<String> variables = new ArrayList<>();
        if (varsMatcher.find()) {
            String varsString = varsMatcher.group(1);
            String[] varsArray = varsString.split(",");
            for (String var : varsArray) {
                variables.add(var.trim());
            }
        }

        // Extrair corpo do main
        Pattern mainBodyPattern = Pattern.compile("begin\\s*([\\s\\S]*?)\\s*end");
        Matcher mainBodyMatcher = mainBodyPattern.matcher(mainCode);
        String mainBodyCode = "";
        if (mainBodyMatcher.find()) {
            mainBodyCode = mainBodyMatcher.group(1).trim();
        }

        List<MainStatements> mainStatements = new ArrayList<>();
        String[] statements = mainBodyCode.split("\n");
        for (String statement : statements) {
            statement = statement.trim();
            if (!statement.isEmpty()) {
                MainStatements mainStatement = parseMainStatement(statement);
                mainStatements.add(mainStatement);
            }
        }

        return new MainBlock(mainStatements, variables);
    }

    private MainStatements parseMainStatement(String statement) {
        // Semelhante ao parseBodyStatement
        if (statement.contains("=")) {
            // Atribuição
            String[] parts = statement.split("=");
            String left = parts[0].trim();
            String right = parts[1].trim();
            Name variable = new Name(left, "store");
            Arguments value = parseArgument(right);
            Attribution attribution = new Attribution(variable, value);
            return attribution;
        } else {
            // Chamada de método
            MethodCall methodCall = parseMethodCall(statement);
            return methodCall;
        }
    }
}
