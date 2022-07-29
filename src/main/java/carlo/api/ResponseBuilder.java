package carlo.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.smartcar.sdk.data.BatchResponse;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;

public class ResponseBuilder {
    private Boolean isSuccessfulAction = true;
    private Integer errorCode;
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
    public ResponseBuilder add(String key , JSONObject value){
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
    public ResponseBuilder add(String key , JsonObject value){
        values.put(key,value);
        return this;

    }





    public String create(){
        JsonObject jsonObject = new JsonObject();
        for (String key : values.keySet()){
            System.out.println("reponse: " + values);
            if (values.get(key) == null || values.get(key).equals("")){
                System.out.println("Value is null --> Unsuccessful Action");
                isSuccessfulAction = false;
                errorCode = ErrorManager.INVALID_API_KEY_CODE;
                errorMsg = ErrorManager.INVALID_API_KEY_MSG;
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

            else if (val instanceof JsonObject){
                jsonObject.add(key,(JsonObject) val);
            }
        }

        if (!isSuccessfulAction) {
            jsonObject = new JsonObject();
            jsonObject.addProperty(ApiManager.ERROR_CODE, errorCode);
            jsonObject.addProperty(ApiManager.ERROR_MSG, errorMsg);
        }
        jsonObject.addProperty(ApiManager.SUCCESSFUL_ACTION, isSuccessfulAction);



        return jsonObject.toString();
    }

    public String createBatchResponse(BatchResponse response) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ApiManager.BATCH,response.toString());
        return jsonObject.toString();
    }
}

