package entidades;

import estruturas.BodyStatements;
import estruturas.IfStatements;
import estruturas.MainStatements;

import java.util.List;

public class If implements BodyStatements, MainStatements {
    private final String comparador;
    private final Name variavel1;
    private final Name variavel2;
    private final List<IfStatements> ifStatements;
    private List<IfStatements> ifStatementselse;
    private final boolean isElse;

    public If(String comparador, Name variavel1, Name variavel2, List<IfStatements> ifStatements, boolean isElse) {
        this.comparador = comparador;
        this.variavel1 = variavel1;
        this.variavel2 = variavel2;
        this.ifStatements = ifStatements;
        this.isElse = isElse;
    }
    public If(String comparador, Name variavel1, Name variavel2, List<IfStatements> ifStatements, boolean isElse, List<IfStatements> ifStatementselse) {
        this.comparador = comparador;
        this.variavel1 = variavel1;
        this.variavel2 = variavel2;
        this.ifStatements = ifStatements;
        this.isElse = isElse;
        this.ifStatementselse = ifStatementselse;
    }

    @Override
    public String compileCode() {
        StringBuilder ifBlockCode = new StringBuilder();
        StringBuilder elseBlockCode = new StringBuilder();

        if(ifStatements!=null){
            for (IfStatements ifStatement : ifStatements) {
                ifBlockCode.append(ifStatement.compileCode());
            }
        }

        if(ifStatementselse!=null){
            for (IfStatements elseStatement : ifStatementselse) {
                elseBlockCode.append(elseStatement.compileCode());
            }
        }
        int lines;

        if(ifBlockCode.isEmpty()){
            lines=0;
        }else {
            lines= ifBlockCode.toString().split("\n").length;
        }
        int linesElse;
        if(elseBlockCode.isEmpty()){
            linesElse=0;
        }else {
            linesElse= elseBlockCode.toString().split("\n").length;
        }
        StringBuilder code = new StringBuilder();
        code.append(variavel1.compileCode() + variavel2.compileCode() + Comparator.getComparator(comparador) + "if " + lines + "\n" + ifBlockCode);
        if (isElse) {
            code.append("else " + linesElse + "\n" + elseBlockCode);
        }
        return code.append("end-if\n").toString();
    }
}
