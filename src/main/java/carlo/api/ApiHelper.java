package carlo.api;

public class ApiHelper {
    private String[] apiCodes = {"oaiu3490S=?n´´03´´ß094ßn´´0"};
    public boolean isValid(String apiCode) {
        return apiCodes.contains(apiCode);
    }
}
