package carlo.api;

import com.google.gson.Gson;
import com.smartcar.sdk.data.Auth;

public class Converter {

    public static String doTask(Object value) {
        Gson gson = new Gson();
        return gson.toJson(value);


    }


    public static Object convertJson(String body, Class<?> userClass) {
        Gson gson = new Gson();
        return gson.fromJson(body,userClass);
    }
}
