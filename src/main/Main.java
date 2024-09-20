package main;

import entidades.*;
import estruturas.MainStatements;
import util.RegexUtil;
import util.StatementsParser;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String code = """
                class Base
                 vars id
                 method showid(x, y, rerw)
                 vars x
                 begin
                 self.id = 10
                 x = self.id
                 return x
                 end-method
                end-class
                main()
                vars p, b, x
                begin
                 if a1 eq b2 then
                  c = 123
                 end-if
                 if a2 eq b3 then
                  c = 124
                 end-if
                   if a3 eq b3 then
                  c = 125
                 end-if
                 b = new Base
                 p = new Pessoa
                 p._prototype = b
                 b.id = 111
                 p.num = 123
                 p.id = 321
                 x = 1024
                 if a eq b then
                  c = b + a
                 else
                  c = 123
                 end-if
                 p.showid()
                 p.calc(x)
                end""";



        String main = RegexUtil.extractMain(code).get(0);

        List<String> mainVars = RegexUtil.extractVars(main);

        List<MainStatements> mainStatements = StatementsParser.processEachLineMainStatement(main);

        MainBlock mainBlock = new MainBlock(mainStatements,mainVars);

        List<ClassesBlock> classesBlockList = new ArrayList<>();


        List<String> classes = RegexUtil.extractClasses(code);

        for (String classe : classes) {

            String className = RegexUtil.findName(classe);

            List<String> vars = RegexUtil.extractVars(classe);

            List<String> methods = RegexUtil.extractMethods(classe);
            List<MethodDef> metodos = new ArrayList<>();

            for (String method : methods) {
                String methodName = RegexUtil.findMethodName(method);
                List<String> methodVars = RegexUtil.extractVars(method);
                List<String> methodParams=RegexUtil.extractMethodParams(method);
                String methodBodyString = RegexUtil.extractMethodBody(method);
                MethodBody methodBody = StatementsParser.processEachLineMethodStatement(methodBodyString);
                MethodHeader methodHeader=new MethodHeader(methodName,methodParams);
                metodos.add( new MethodDef(methodBody,methodHeader,methodVars));
            }
            ClassesBlock classesBlock=new ClassesBlock(className,vars,metodos);
            classesBlockList.add(classesBlock);
        }

        Program program = new Program(mainBlock,classesBlockList);

        String finalCode = program.compileCode();

        System.out.println(finalCode);

    }
    //todo falta falta prototype e io.print
}