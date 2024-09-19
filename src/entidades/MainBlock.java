package entidades;

import estruturas.MainStatements;
import util.CompilerUtil;

import java.util.List;

public class MainBlock {
    private final List<MainStatements> statements;
    private List<String> definicoesVariaveis;

    public MainBlock(List<MainStatements> statements) {
        this.statements = statements;
    }

    public MainBlock(List<MainStatements> statements, List<String> definicoesVariaveis) {
        this.statements = statements;
        this.definicoesVariaveis = definicoesVariaveis;
    }

    public String compileCode() {
        StringBuilder code = new StringBuilder();
        code.append("main()\n");
        code.append(CompilerUtil.compileVariableDefinitions(definicoesVariaveis));
        code.append("begin\n");

        for (MainStatements statement : statements) {
            code.append(statement.compileCode());
        }
        code.append("end");

        return code.toString();
    }
}