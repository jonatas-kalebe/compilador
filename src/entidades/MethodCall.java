package entidades;

import estruturas.Arguments;
import estruturas.BodyStatements;
import estruturas.IfStatements;
import estruturas.MainStatements;

import java.util.List;

public class MethodCall implements IfStatements, BodyStatements, MainStatements, Arguments {
    private final Name objeto;
    private final Name metodo;
    private List<Name> parametros;

    public MethodCall(Name objeto, Name metodo) {
        this.objeto = objeto;
        this.metodo = metodo;
    }

    public MethodCall(Name objeto, Name metodo, List<Name> parametros) {
        this.objeto = objeto;
        this.metodo = metodo;
        this.parametros = parametros;
    }

    @Override
    public String compileCode() {
        StringBuilder parametrosLoad= new StringBuilder();
        if(parametros!=null){
            for (Name parametro : parametros) {
                parametrosLoad.append(parametro.compileCode());
            }
        }
        if(objeto.getNome().equals("io")){
            return parametrosLoad +objeto.compileCode()+metodo.compileCode()+"pop\n";
        }
        return parametrosLoad +objeto.compileCode()+metodo.compileCode();
    }
}
