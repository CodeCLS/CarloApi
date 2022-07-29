package carlo.api;

public class ContentPackage {
    private Object value;
    private Exception exception =new Exception("not successful");

    public ContentPackage() {
    }

    public ContentPackage(Object value, Exception exception) {
        this.value = value;
        this.exception = exception;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
