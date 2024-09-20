package main;

import entidades.Name;
import entidades.Return;

public class Teste {
    public static void main(String[] args) {

        Return retorno = new Return("x");

        System.out.print(retorno.compileCode());

    }
}
