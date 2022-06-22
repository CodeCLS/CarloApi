package carlo.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.*;
import java.util.HashMap;

public class ResponseBuilder {
    @Key("is_successful_action")
    private Boolean isSuccessfulAction = false;
    @Key("error_code")
    private Integer errorCode;
    @Key("error_msg")
    private String errorMsg;
    private HashMap<String,Object> values = new HashMap<>();

    public ResponseBuilder( boolean isSuccessfulAction, int errorCode, String errorMsg) {
        this.isSuccessfulAction = isSuccessfulAction;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public ResponseBuilder() {
    }


    public boolean isSuccessfulAction() {
        return isSuccessfulAction;
    }

    public ResponseBuilder setSuccessfulAction(boolean successfulAction) {
        isSuccessfulAction = successfulAction;
        return this;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public ResponseBuilder setErrorCode(int errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public ResponseBuilder setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }
    public ResponseBuilder add(String key , String value){
        values.put(key,value);
        return this;

    }
    public ResponseBuilder add(String key , Integer value){
        values.put(key,value);
        return this;

    }
    public ResponseBuilder add(String key , Double value){
        values.put(key,value);
        return this;

    }
    public ResponseBuilder add(String key , String[] value){
        JsonArray jsonArray = new JsonArray();
        for (String i:value){
            jsonArray.add(i);

        }
        add(key,jsonArray);
        return this;

    }
    public ResponseBuilder add(String key , Boolean value){
        values.put(key,value);
        return this;

    }
    public ResponseBuilder add(String key , JsonArray value){
        values.put(key,value);
        return this;

    }





    public String create(){
        JsonObject jsonObject = new JsonObject();
        for (String key : values.keySet()){
            if (values.get(key) == null || values.get(key).equals("")){
                isSuccessfulAction = false;
                errorCode = ErrorManager.INVALID_API_KEY_CODE;
                errorMsg = ErrorManager.INVALID_API_KEY_MSG;
                break;
            }
            Object val = values.get(key);
            if (val instanceof Boolean){
                jsonObject.addProperty(key,(Boolean)val);
            }
            else if (val instanceof Double){
                jsonObject.addProperty(key,(Double)val);
            }
            else if(val instanceof Integer){
                jsonObject.addProperty(key,(Integer)val);
            }
            else if (val instanceof String){
                jsonObject.addProperty(key,(String)val);
            }
            else if (val instanceof JsonArray){
                jsonObject.add(key,(JsonArray) val);
            }
        }
        String annotation = isSuccessfulAction.getClass().getAnnotation(Key.class).value();
        jsonObject.addProperty(annotation,isSuccessfulAction);

        if (!isSuccessfulAction) {
            jsonObject.addProperty(errorCode.getClass().getAnnotation(Key.class).value(), errorCode);
            jsonObject.addProperty(errorMsg.getClass().getAnnotation(Key.class).value(), errorMsg);
        }


        return jsonObject.toString();
    }
}
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.FIELD)
@interface Key {
    String value() default "key";

}

