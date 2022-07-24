package carlo.api;

public interface Callback<T> {
    void value(T value);
    void exception(Exception e);
}
