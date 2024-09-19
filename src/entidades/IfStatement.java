package entidades;

import estruturas.IfStatements;

public record IfStatement(IfStatements statement) {

    public String compileCode() {
        return statement.compileCode();
    }
}