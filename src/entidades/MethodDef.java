package entidades;

import util.CompilerUtil;

import java.util.List;

public class MethodDef {
    private final MethodBody body;
    private final MethodHeader header;
    private List<String> definicoesVariaveis;

    public MethodDef(MethodBody body, MethodHeader header, List<String> definicoesVariaveis) {
        this.body = body;
        this.header = header;
        this.definicoesVariaveis = definicoesVariaveis;
    }

    public MethodDef(MethodBody body, MethodHeader header) {
        this.body = body;
        this.header = header;
    }

    public String compileCode() {
        StringBuilder code = new StringBuilder();
        code.append(header.compileCode());
        if(definicoesVariaveis != null){
            code.append(CompilerUtil.compileVariableDefinitions(definicoesVariaveis));
        }
        code.append(body.compileCode());

        return code.toString();
    }
}
