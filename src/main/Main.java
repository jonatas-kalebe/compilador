package main;

import entidades.Program;
import util.Parser;

public class Main {
    public static void main(String[] args) {
        String code = "class Base\n" +
                " vars id\n" +
                " method showid()\n" +
                " vars x\n" +
                " begin\n" +
                " self.id = 10\n" +
                " x = self.id\n" +
                " io.print(x)\n" +
                " x = 0\n" +
                " return x\n" +
                " end-method\n" +
                "end-class\n" +
                "class Pessoa\n" +
                " vars num\n" +
                " method calc(x)\n" +
                " vars y, z\n" +
                " begin\n" +
                " z = self.num\n" +
                " y = x + z\n" +
                " io.print(y)\n" +
                " y = new Base\n" +
                " return y\n" +
                " end-method\n" +
                "end-class\n" +
                "main()\n" +
                "vars p, b, x\n" +
                "begin\n" +
                " b = new Base\n" +
                " p = new Pessoa\n" +
                " p._prototype = b\n" +
                " b.id = 111\n" +
                " p.num = 123\n" +
                " p.id = 321\n" +
                " x = 1024\n" +
                " p.showid()\n" +
                " p.calc(x)\n" +
                "end";

        Parser parser = new Parser(code);
        Program program = parser.parse();

        String compiledCode = program.compileCode();
        System.out.println(compiledCode);
    }
}
