package util;

import entidades.*;
import estruturas.BodyStatements;
import estruturas.IfStatements;
import estruturas.MainStatements;

import java.util.ArrayList;
import java.util.List;

public class StatementsParser {
    private static final String CONSTANTE = "const";
    private static final String CARREGAR_VARIAVEL = "load";
    private static final String ARMAZENAR_VARIAVEL = "store";
    private static final String CHAMAR_METODO = "call";
    private static final String ATRIBUIR_VALOR = "set";
    private static final String OBTER_VALOR = "get";
    private static final String NOVO_OBJETO = "new";
    private static final String RETORNAR = "return";
    private static final String MARCAR_IF = "ifHere";

    private StatementsParser() {
    }

    public static List<MainStatements> processEachLineMainStatement(String body) {
        List<MainStatements> statements = new ArrayList<>();
        if (body == null || body.isEmpty()) {
            return statements;
        }

        List<String> blocosIf = RegexUtil.extractIfs(body);
        int ifIndex = 0;
        for (String blocoIf : blocosIf) {
            body = body.replace(blocoIf, MARCAR_IF);
        }

        String[] lines = body.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.contains(MARCAR_IF)) {
                statements.add(processEachLineIfStatement(blocosIf.get(ifIndex++)));
            } else if (line.contains("=")) {
                statements.add(processLineAttribution(line));
            } else if (line.contains("(") && !line.contains("main")) {
                statements.add(processMethodCall(line));
            }
        }
        return statements;
    }

    public static MethodBody processEachLineMethodStatement(String body) {
        List<BodyStatements> statements = new ArrayList<>();
        if (body == null || body.isEmpty()) {
            return new MethodBody(statements);
        }

        List<String> blocosIf = RegexUtil.extractIfs(body);
        int ifIndex = 0;
        for (String blocoIf : blocosIf) {
            body = body.replace(blocoIf, MARCAR_IF);
        }

        String[] lines = body.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.contains(MARCAR_IF)) {
                statements.add(processEachLineIfStatement(blocosIf.get(ifIndex++)));
            } else if (line.startsWith(RETORNAR)) {
                statements.add(processLineReturn(line));
            } else if (line.contains("=")) {
                statements.add(processLineAttribution(line));
            } else if (line.contains("(") && !line.contains("main")) {
                statements.add(processMethodCall(line));
            }
        }
        return new MethodBody(statements);
    }

    private static If processEachLineIfStatement(String body) {
        boolean isElse = false;
        List<IfStatements> statements = new ArrayList<>();
        List<IfStatements> statementsElse = new ArrayList<>();
        String comparador = null;
        Name variavel1 = null;
        Name variavel2 = null;

        if (body == null || body.isEmpty()) {
            return new If(comparador, variavel1, variavel2, statements, isElse, statementsElse);
        }

        String[] lines = body.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("if")) {
                String[] split = line.split("\\s+");
                if (split.length >= 4) {
                    comparador = split[2];
                    variavel1 = parseName(split[1]);
                    variavel2 = parseName(split[3]);
                }
            } else if (line.equals("else")) {
                isElse = true;
            } else if (line.equals("end-if")) {
                break;
            } else if (!line.isEmpty()) {
                IfStatements stmt = processIfLine(line);
                if (isElse) {
                    statementsElse.add(stmt);
                } else {
                    statements.add(stmt);
                }
            }
        }
        return new If(comparador, variavel1, variavel2, statements, isElse, statementsElse);
    }

    private static IfStatements processIfLine(String line) {
        if (line.startsWith(RETORNAR)) {
            return processLineReturn(line);
        } else if (line.contains("=")) {
            return processLineAttribution(line);
        } else if (line.contains("(")) {
            return processMethodCall(line);
        } else {

            throw new IllegalArgumentException("linha irreconhecivel: " + line);
        }
    }

    private static Name parseName(String token) {
        if (token.matches("\\d+")) {
            return new Name(token, CONSTANTE);
        } else {
            return new Name(token, CARREGAR_VARIAVEL);
        }
    }

    private static MethodCall processMethodCall(String line) {
        String[] split = line.split("\\(");
        String[] methodParts = split[0].split("\\.");
        if (methodParts.length != 2) {
            throw new IllegalArgumentException("Invalid method call: " + line);
        }

        Name objectName = new Name(methodParts[0], CARREGAR_VARIAVEL);
        Name methodName = new Name(methodParts[1], CHAMAR_METODO);
        List<Name> parameters = new ArrayList<>();

        if (split.length > 1) {
            String paramsString = split[1].replace(")", "").trim();
            if (!paramsString.isEmpty()) {
                String[] paramTokens = paramsString.split(",");
                for (String param : paramTokens) {
                    parameters.add(parseParameter(param.trim()));
                }
            }
        }
        return new MethodCall(objectName, methodName, parameters);
    }

    private static Name parseParameter(String param) {
        if (param.contains(".")) {
            String[] parts = param.split("\\.");
            return new Name(parts[1], OBTER_VALOR, parts[0]);
        } else {
            return new Name(param, CARREGAR_VARIAVEL);
        }
    }

    private static Return processLineReturn(String line) {
        String returnValue = line.replace(RETORNAR, "").trim();
        return new Return(returnValue);
    }

    private static Attribution processLineAttribution(String line) {
        String[] split = line.split("=");
        if (split.length != 2) {
            throw new IllegalArgumentException("Invalid attribution: " + line);
        }

        Name variable = parseVariable(split[0].trim());
        String expression = split[1].trim();

        String[] operators = {"\\+", "-", "\\*", "/"};
        for (String operator : operators) {
            if (expression.matches(".*" + operator + ".*")) {
                String[] operands = expression.split(operator);
                if (operands.length != 2) {
                    throw new IllegalArgumentException("Invalid expression in attribution: " + line);
                }
                Name operand1 = parseExpressionOperand(operands[0].trim());
                Name operand2 = parseExpressionOperand(operands[1].trim());
                return new Attribution(variable, operand1, operand2, operator.replace("\\", ""));
            }
        }

        Name value = parseExpressionOperand(expression);
        return new Attribution(variable, value);
    }

    private static Name parseVariable(String token) {
        if (token.contains(".")) {
            String[] parts = token.split("\\.");
            return new Name(parts[1], ATRIBUIR_VALOR, parts[0]);
        } else {
            return new Name(token, ARMAZENAR_VARIAVEL);
        }
    }

    private static Name parseExpressionOperand(String operand) {
        if (operand.contains(".")) {
            String[] parts = operand.split("\\.");
            return new Name(parts[1], OBTER_VALOR, parts[0]);
        } else if (operand.matches("\\d+")) {
            return new Name(operand, CONSTANTE);
        } else if (operand.startsWith(NOVO_OBJETO)) {
            String objectType = operand.replace(NOVO_OBJETO, "").trim();
            return new Name(objectType, NOVO_OBJETO);
        } else {
            return new Name(operand, CARREGAR_VARIAVEL);
        }
    }
}
