package layer.assembler;

public interface IAssembler<T, R> {

    T[][] assemble(T[][] base, R[][] layer);

}
