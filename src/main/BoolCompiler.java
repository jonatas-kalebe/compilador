package main;

import entidades.*;
import estruturas.MainStatements;
import util.RegexUtil;
import util.StatementsParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BoolCompiler {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Uso: java BoolCompiler <arquivo_entrada>.bool <arquivo_saida>.boolc");
            System.exit(1);
        }

        String inputFileName = args[0];
        String outputFileName = args[1];

        String code;
        try {
            code = Files.readString(Path.of(inputFileName));
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo de entrada: " + e.getMessage());
            System.exit(1);
            return;
        }

        String finalCode = compileBoolCode(code);

        try {
            Files.writeString(Path.of(outputFileName), finalCode);
        } catch (IOException e) {
            System.err.println("Erro ao escrever o arquivo de saída: " + e.getMessage());
            System.exit(1);
        }
    }

    private static String compileBoolCode(String code) {
        String mainBlockString = RegexUtil.extractMain(code).getFirst();

        List<String> mainVars = RegexUtil.extractVars(mainBlockString);
        List<MainStatements> mainStatements = StatementsParser.processEachLineMainStatement(mainBlockString);

        MainBlock mainBlock = new MainBlock(mainStatements, mainVars);

        List<ClassesBlock> classesBlockList = new ArrayList<>();
        List<String> classes = RegexUtil.extractClasses(code);

        for (String classe : classes) {
            String className = RegexUtil.findName(classe);
            List<String> vars = RegexUtil.extractVars(classe);

            List<String> methods = RegexUtil.extractMethods(classe);
            List<MethodDef> methodsDef = new ArrayList<>();

            for (String method : methods) {
                String methodName = RegexUtil.findMethodName(method);
                List<String> methodVars = RegexUtil.extractVars(method);
                List<String> methodParams = RegexUtil.extractMethodParams(method);
                String methodBodyString = RegexUtil.extractMethodBody(method);

                MethodBody methodBody = StatementsParser.processEachLineMethodStatement(methodBodyString);
                MethodHeader methodHeader = new MethodHeader(methodName, methodParams);
                methodsDef.add(new MethodDef(methodBody, methodHeader, methodVars));
            }
            ClassesBlock classesBlock = new ClassesBlock(className, vars, methodsDef);
            classesBlockList.add(classesBlock);
        }

        Program program = new Program(mainBlock, classesBlockList);

        return program.compileCode();
    }
}
