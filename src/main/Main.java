package main;

import entidades.*;
import estruturas.MainStatements;
import util.RegexUtil;
import util.StatementsParser;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) {

        String code = """
                class Animal
                 vars name, age
                
                 method speak()
                 vars n
                 begin
                 n = self.name
                 io.print(n)
                 return 0
                 end-method
                
                 method grow()
                 vars a, one
                 begin
                 a = self.age
                 one = 1
                 a = a + one
                 self.age = a
                 return self.age
                 end-method
                end-class
                
                class Dog
                 vars breed
                
                 method bark()
                 vars n
                 begin
                 n = 100
                 io.print(n)
                 return 0
                 end-method
                end-class
                
                class Cat
                 vars color
                
                 method meow()
                 vars n
                 begin
                 n = 200
                 io.print(n)
                 return 0
                 end-method
                end-class
                
                main()
                vars a, d, c, num, res, temp
                begin
                 a = new Animal
                 d = new Dog
                 c = new Cat
                
                 a.name = 10
                 a.age = 5
                
                 d._prototype = a
                 c._prototype = a
                
                 num = 1
                
                 if num eq 1 then
                  d.breed = 20
                  d.name = 30
                  d.age = 3
                  res = d.grow()
                  io.print(res)
                 else
                  c.color = 40
                  c.name = 50
                  c.age = 2
                  res = c.grow()
                  io.print(res)
                 end-if
                
                 a = new Animal
                
                 num = num + 2
                 num = num * 3
                 num = num - 4
                 num = num / 5
                
                 res = d.grow()
                 io.print(res)
                
                 d.speak()
                end
            """;



        String main = RegexUtil.extractMain(code).getFirst();

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

        LOGGER.log(Level.INFO,finalCode);

    }
}