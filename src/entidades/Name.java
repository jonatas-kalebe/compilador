package entidades;

import estruturas.Arguments;

public class Name implements Arguments {
    private final String nome;
    private final String acao;
    private String nomeObjeto;

    public Name(String nome, String acao) {
        this.nome = nome.trim();
        this.acao = acao;
    }

    public Name(String nome, String acao, String nomeObjeto) {
        this.nome = nome.trim();
        this.nomeObjeto = nomeObjeto.trim();
        this.acao = acao;
    }

    @Override
    public String compileCode() {
        return switch (acao) {
            case "load" -> load();
            case "set" -> set();
            case "get" -> get();
            case "store" -> store();
            case "call" -> call();
            case "const" -> constante();
            case "new" -> novo();
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
        return String.format("load %s%nset %s%n", nomeObjeto, nome);
    }

    private String get(){
        return String.format("load %s%nget %s%n", nomeObjeto, nome);
    }

    private String store(){
        return String.format("store %s%n", nome);
    }

    private String call(){
        return String.format("call %s%n", nome);
    }

    private String novo(){
        return String.format("new %s%n", nome);
    }

    private String constante(){
        return String.format("const %s%n", nome);
    }
}

