package carlo.api;

public class ErrorManager {
    public static final int INVALID_API_KEY_CODE = 420;
    public static final String INVALID_API_KEY_MSG = "The api key is invalid";
    public static final int INTERNAL_ERROR_KEY_CODE = 430;
    public static final String INTERNAL_ERROR_KEY_MSG = "Internal Error. Try again later";

    public static String createErrorResponse(int invalidApiKeyCode, String invalidApiKeyMsg) {
        ResponseBuilder responseBuilder = new ResponseBuilder();
        responseBuilder.setErrorMsg(invalidApiKeyMsg);
        responseBuilder.setErrorCode(invalidApiKeyCode);
        responseBuilder.setSuccessfulAction(false);
        return responseBuilder.create();
    }
}
