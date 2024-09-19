package entidades;

import estruturas.BodyStatements;

import java.util.List;

public class MethodBody {
    private final List<BodyStatements> statements;

    public MethodBody(List<BodyStatements> statements) {
        this.statements = statements;
    }

    public String compileCode() {
        StringBuilder code = new StringBuilder();
        code.append("begin\n");

        if(statements != null){
            for (BodyStatements statement : statements) {
                code.append(statement.compileCode());
            }
        }

        code.append("end");

        return code.toString();
    }
}
