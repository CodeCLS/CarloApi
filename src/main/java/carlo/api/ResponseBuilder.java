package carlo.api;

import com.google.gson.JsonObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;

public class ResponseBuilder {
    @Key("is_successful_action")
    private Boolean isSuccessfulAction;
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
    public ResponseBuilder add(String key , Boolean value){
        values.put(key,value);
        return this;

    }





    public String create(){
        JsonObject jsonObject = new JsonObject();
        for (String key : values.keySet()){
            Object val = values.get(key);
            if (val instanceof Boolean){
                jsonObject.addProperty(key,(Boolean)val);
            }
            else if(val instanceof Integer){
                jsonObject.addProperty(key,(Integer)val);
            }
            else if (val instanceof String){
                jsonObject.addProperty(key,(String)val);
            }
        }
        jsonObject.addProperty(isSuccessfulAction.getClass().getDeclaredAnnotation(Key.class).value(),isSuccessfulAction);

        if (!isSuccessfulAction) {
            jsonObject.addProperty(errorCode.getClass().getDeclaredAnnotation(Key.class).value(), errorCode);
            jsonObject.addProperty(errorCode.getClass().getDeclaredAnnotation(Key.class).value(), errorCode);
        }


        return jsonObject.toString();
    }
}
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface Key {
    String value() default "key";

}

