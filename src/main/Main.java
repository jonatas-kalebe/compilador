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
             vars id, value
             method calculate(x)
             vars temp
             begin
              self.id = x
              temp = self.value + x
              if x gt 0 then
               temp = temp * x
              else
               temp = temp - x
              end-if
              io.print(temp)
              return temp
             end-method
            end-class
            
            class Derived
             vars data
             method process(y)
             vars result
             begin
              self.data = y
              result = self.data + self.id
              io.print(result)
              return result
             end-method
            end-class
            
            main()
            vars baseObj, derivedObj, num, output
            begin
             num = 10
             baseObj = new Base
             baseObj.value = 5
            
             derivedObj = new Derived
             derivedObj._prototype = baseObj
             derivedObj.id = 20
            
             output = derivedObj.calculate(num)
             io.print(output)
            
             output = derivedObj.process(num)
             io.print(output)
            end
               """;



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