package entidades;

import java.util.List;

public class Program {
    private final MainBlock main;
    private List<ClassesBlock> classesBlock;

    public Program(MainBlock main) {
        this.main = main;
    }

    public Program(MainBlock main, List<ClassesBlock> classesBlock) {
        this.main = main;
        this.classesBlock = classesBlock;
    }

    public String compileCode() {
        StringBuilder code = new StringBuilder();
        if(classesBlock != null){
            for (ClassesBlock classBlock : classesBlock) {
                code.append(classBlock.compileCode());
            }
        }
        code.append(main.compileCode());
        return code.toString();
    }

    public void setClassesBlock(List<ClassesBlock> classesBlock) {
        this.classesBlock = classesBlock;
    }
}
