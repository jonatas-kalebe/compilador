package entidades;

import util.CompilerUtil;

import java.util.Collections;
import java.util.List;

public class ClassesBlock {
    private final String nome;
    private final List<String> definicoesVariaveis;
    private final List<MethodDef> metodo;
    private final int tipo;

    public ClassesBlock(String nome, List<String> definicoesVariaveis, List<MethodDef> metodo) {
        this.nome = nome;
        this.definicoesVariaveis = definicoesVariaveis != null ? definicoesVariaveis : Collections.emptyList();
        this.metodo = metodo != null ? metodo : Collections.emptyList();

        if (!this.definicoesVariaveis.isEmpty() && !this.metodo.isEmpty()) {
            this.tipo = 3;
        } else if (!this.definicoesVariaveis.isEmpty()) {
            this.tipo = 1;
        } else if (!this.metodo.isEmpty()) {
            this.tipo = 2;
        } else {
            this.tipo = 0;
        }
    }

    public String compileCode() {
        StringBuilder code = new StringBuilder();
        code.append("class ").append(nome).append("\n");

        if (tipo == 1 || tipo == 3) {
            code.append(CompilerUtil.compileVariableDefinitions(definicoesVariaveis));
        }

        if (tipo == 2 || tipo == 3) {
            metodo.forEach(method -> code.append(method.compileCode()).append("\n"));
        }
        code.append("end-class\n");
        return code.toString();
    }
}
