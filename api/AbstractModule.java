package api;

public abstract class AbstractModule {
    protected abstract String getName();

    public void greet() {
        System.out.println("Hello from " + getName());
    }
}
