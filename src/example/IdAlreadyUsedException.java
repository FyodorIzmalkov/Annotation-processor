package example;

public class IdAlreadyUsedException extends RuntimeException {
    private FactoryAnnotatedClass existing;

    public IdAlreadyUsedException(FactoryAnnotatedClass existing){
        this.existing = existing;
    }

    public FactoryAnnotatedClass getExisting() {
        return existing;
    }
}
