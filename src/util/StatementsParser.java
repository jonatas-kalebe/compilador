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
    private static final String REGEX_DIGIT = "-?\\d+";

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
                statements.add(processMethodCall(line,false));
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
                statements.add(processMethodCall(line,false));
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
                    variavel1 = parseName(split[1]);
                    comparador = split[2];
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
        } else if (line.contains("(") && !line.contains("main")) {
            return processMethodCall(line,false);
        } else {
            throw new IllegalArgumentException("Linha irreconhecível: " + line);
        }
    }

    private static Name parseName(String token) {
        token = token.trim();
        if (token.matches(REGEX_DIGIT)) {
            return new Name(token, CONSTANTE);
        } else {
            return new Name(token, CARREGAR_VARIAVEL);
        }
    }

    private static MethodCall processMethodCall(String line, boolean isAtribuicao) {
        String[] split = line.split("\\(");
        String methodCallPart = split[0].trim();
        String[] methodParts = methodCallPart.split("\\.");
        if (methodParts.length != 2) {
            throw new IllegalArgumentException("Chamada de método inválida: " + line);
        }

        Name objectName = new Name(methodParts[0].trim(), CARREGAR_VARIAVEL);
        Name methodName = new Name(methodParts[1].trim(), CHAMAR_METODO);
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
        return new MethodCall(objectName, methodName, parameters,isAtribuicao);
    }

    private static Name parseParameter(String param) {
        param = param.trim();
        if (param.matches(REGEX_DIGIT)) {
            return new Name(param, CONSTANTE);
        } else if (param.contains(".")) {
            String[] parts = param.split("\\.");
            return new Name(parts[1].trim(), OBTER_VALOR, parts[0].trim());
        } else {
            return new Name(param, CARREGAR_VARIAVEL);
        }
    }

    private static Return processLineReturn(String line) {
        String returnValue = line.replace(RETORNAR, "").trim();
        if (returnValue.contains(".")) {
            String[] parts = returnValue.split("\\.");
            String objectName = parts[0].trim();
            String attributeName = parts[1].trim();

            Name attributeAccess = new Name(attributeName, OBTER_VALOR, objectName);
            return new Return(attributeAccess);
        } else {
            return new Return(returnValue);
        }
    }

    private static Attribution processLineAttribution(String line) {
        String[] split = line.split("=");
        if (split.length != 2) {
            throw new IllegalArgumentException("Atribuição inválida: " + line);
        }

        String leftSide = split[0].trim();
        String rightSide = split[1].trim();

        Name variable = parseVariable(leftSide);

        String[] operators = {"\\+", "-", "\\*", "/"};
        for (String operator : operators) {
            if (rightSide.matches(".*" + operator + ".*")) {
                String[] operands = rightSide.split(operator);
                if (operands.length != 2) {
                    throw new IllegalArgumentException("Expressão inválida na atribuição: " + line);
                }
                Name operand1 = parseExpressionOperand(operands[0].trim());
                Name operand2 = parseExpressionOperand(operands[1].trim());
                String opSymbol = operator.replace("\\", "");
                return new Attribution(variable, operand1, operand2, opSymbol);
            }
        }

        if (rightSide.startsWith(NOVO_OBJETO)) {
            String className = rightSide.substring(NOVO_OBJETO.length()).trim();
            Name newObject = new Name(className, NOVO_OBJETO);
            return new Attribution(variable, newObject);
        } else if (rightSide.contains("(")) {
            MethodCall methodCall = processMethodCall(rightSide,true);
            return new Attribution(variable, methodCall);
        } else {
            Name value = parseExpressionOperand(rightSide);
            return new Attribution(variable, value);
        }
    }

    private static Name parseVariable(String token) {
        token = token.trim();
        return getNameObject(token, ATRIBUIR_VALOR, ARMAZENAR_VARIAVEL);
    }

    private static Name parseExpressionOperand(String operand) {
        operand = operand.trim();
        if (operand.matches(REGEX_DIGIT)) {
            return new Name(operand, CONSTANTE);
        } else if (operand.startsWith(NOVO_OBJETO)) {
            String className = operand.substring(NOVO_OBJETO.length()).trim();
            return new Name(className, NOVO_OBJETO);
        } else if (operand.contains("(")) {
            MethodCall methodCall = processMethodCall(operand,true);
            return new Name(methodCall.compileCode(), "callMethod");
        } else {
            return getNameObject(operand, OBTER_VALOR, CARREGAR_VARIAVEL);
        }
    }

    private static Name getNameObject(String operand, String obterValor, String carregarVariavel) {
        if (operand.contains(".")) {
            String[] parts = operand.split("\\.");
            String objectName = parts[0].trim();
            String attributeName = parts[1].trim();
            return new Name(attributeName, obterValor, objectName);
        } else {
            return new Name(operand, carregarVariavel);
        }
    }
}
