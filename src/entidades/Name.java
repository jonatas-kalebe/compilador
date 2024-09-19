package entidades;

import estruturas.Arguments;

public class Name implements Arguments {
    private final String nome;
    private final String acao;
    private String nomeObjeto;

    public Name(String nome, String acao) {
        this.nome = nome;
        this.acao = acao;
    }

    public Name(String nome, String acao, String nomeObjeto) {
        this.nome = nome;
        this.nomeObjeto = nomeObjeto;
        this.acao = acao;
    }

    @Override
    public String compileCode() {
        return switch (acao) {
            case "load" -> load();
            case "set" -> set();
            case "store" -> store();
            case "call" -> call();
            default -> "";
        };
    }

    public String getNome() {
        return nome;
    }

    private String load(){
        return String.format("load %s%n", nome);
    }

    private String set(){
        return String.format("load %s%nset%s%n", nomeObjeto, nome);
    }

    private String store(){
        return String.format("store %s%n", nome);
    }

    private String call(){
        return String.format("call %s%n", nome);
    }
}

