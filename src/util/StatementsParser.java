package util;


import entidades.*;
import estruturas.BodyStatements;
import estruturas.IfStatements;
import estruturas.MainStatements;

import java.util.ArrayList;
import java.util.List;

public class StatementsParser {
    private StatementsParser() {
    }

    public static List<MainStatements> processEachLineMainStatement(String body) {
        if (body != null && !body.isEmpty()) {
            List <String> blocosIf = RegexUtil.extractIfs(body);
            for (String blocoIf : blocosIf) {
                body = body.replace(blocoIf, "");
            }
            String[] lines = body.split("\n");
            List<MainStatements> statements = new ArrayList<>();
            for (String line : lines) {
                if (line.contains("=")) {
                    statements.add(processLineAttribution(line));
                }
                else if(line.contains("(")&&!line.contains("main")){
                    statements.add(processMethodCall(line));
                }
            }
            for (String blocoIf : blocosIf) {
                statements.add(processEachLineIfStatement(blocoIf));
            }
            return statements;
        }
        return new ArrayList<>();
    }

    public static MethodBody processEachLineMethodStatement(String body) {
        if (body != null && !body.isEmpty()) {
            boolean isIf=false;
            String[] lines = body.split("\n");
            List<BodyStatements> statements = new ArrayList<>();
            for (String line : lines) {
                if (line.contains("end-if")){
                    isIf=false;
                }else {
                    if(!isIf){
                        if (line.contains("if")) {
                            isIf=true;
                            statements.add(processEachLineIfStatement(line));

                        }
                        else if (line.contains("return")) {
                            statements.add(processLineReturn(line));
                        }
                        else if (line.contains("=")) {
                            statements.add(processLineAttribution(line));
                        }
                        else if(line.contains("(")){
                            statements.add(processMethodCall(line));
                        }
                    }
                }

            }
            return new MethodBody(statements);
        }
        return new MethodBody(null);

    }

    public static If processEachLineIfStatement(String body) {
        boolean isElse=false;
        List<IfStatements> statements = new ArrayList<>();
        List<IfStatements> statementsElse = new ArrayList<>();
        String comparador = null;
        Name variavel1 = null;
        Name variavel2= null;
        if (body != null && !body.isEmpty()) {
            String[] lines = body.split("\n");

            for (String line : lines) {
                if (line.startsWith("if")) {
                    String[] split = line.trim().split(" ");
                    comparador = split[2];
                    variavel1 = new Name(split[1], "load");
                    variavel2 = new Name(split[3], "load");
                }
                else if(line.contains("else")){
                    isElse=true;
                }
                else if (line.contains("return")) {
                    if (isElse) {
                        statementsElse.add(processLineReturn(line));
                    } else {
                        statements.add(processLineReturn(line));
                    }
                }
                else if (line.contains("=")) {
                    if (isElse) {
                        statementsElse.add(processLineAttribution(line));
                    } else {
                        statements.add(processLineAttribution(line));
                    }

                }
                else if(line.contains("(")){
                    if (isElse) {
                        statementsElse.add(processMethodCall(line));
                    } else {
                        statements.add(processMethodCall(line));
                    }
                } else if (line.contains("end-if")) {
                    break;
                }
            }
        }
        return new If(comparador,variavel1,variavel2,statements,isElse,statementsElse);
    }

    private static MethodCall processMethodCall(String line) {
        String[] split = line.split("\\(");
        String[] splitMethod = split[0].split("\\.");
        Name methodName =new Name( splitMethod[1],"call");
        Name objectName =new Name( splitMethod[0],"load");
        List<Name> names = new ArrayList<>();
        if (split.length > 1) {
            String[] splitParameters = split[1].replace(")", "").split(",");
            for (String parameter : splitParameters) {
                if (parameter.contains(".")) {
                    String[] splitObject = parameter.split("\\.");
                    names.add(new Name(splitObject[1], "get", splitObject[0]));
                } else {
                    if (!parameter.isEmpty()){
                        names.add(new Name(parameter, "load"));
                    }

                }
            }
        }
        return new MethodCall(methodName, objectName, names);
    }

    public static Return processLineReturn(String line) {
        String returnName = line.replace("return", "").trim();
        return new Return(returnName);
    }

    public static Attribution processLineAttribution(String line) {
        String[] split = line.split("=");
        Name variavel;
        if (split[0].contains(".")){
            String[] splitObject =split[0].split("\\.");
            variavel= new Name(splitObject[1], "set",splitObject[0]);
        }
        else {
            variavel = new Name(split[0], "store");
        }
        String[] operators = {"\\+", "\\-", "\\*", "\\/"};


        for (String operator : operators) {
            if (split[1].contains(operator.replace("\\", ""))) {
                String[] splitAdd = split[1].split(operator);
                Name[] names = new Name[2];
                for(int i=0;i<2;i++){
                    String splitTemp=splitAdd[i].trim();
                    if (splitTemp.contains(".")) {
                        String[] splitObject =splitTemp.split("\\.");
                        names[i] = new Name(splitObject[1], "get",splitObject[0]);
                    }  else {
                        try {
                            Integer.parseInt(splitTemp);
                            names[i] = new Name(splitTemp, "const");
                        } catch (NumberFormatException e) {
                            names[i] = new Name(splitTemp, "load");
                        }
                    }
                }
                return new Attribution(variavel, names[0], names[1], operator.replace("\\", ""));
            }

        }
        Name valor;
        if (split[1].contains(".")) {
            String[] splitAdd = split[1].split("\\.");
            valor = new Name(splitAdd[1], "get", splitAdd[0]);
            return new Attribution(variavel, valor);
        } else if (split[1].contains("new")) {
            valor = new Name(split[1].replace("new", "").trim(), "new");
            return new Attribution(variavel, valor);

        }

        try {
            Integer.parseInt(split[1].trim());
            valor = new Name(split[1], "const");
        } catch (NumberFormatException e) {
            valor = new Name(split[1], "load");
        }


        return new Attribution(variavel, valor);
    }
}
