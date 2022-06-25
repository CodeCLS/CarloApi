package carlo.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.smartcar.sdk.data.Auth;

import java.util.ArrayList;

public class Converter implements BatchPaths {

    public static String doTask(Object value) {
        Gson gson = new Gson();
        return gson.toJson(value);


    }


    public static Object convertJson(String body, Class<?> userClass) {
        Gson gson = new Gson();
        return gson.fromJson(body,userClass);
    }

    @Override
    public String[] getListFromJson(String body) {
        JsonArray array = new JsonObject().getAsJsonArray(body);
        ArrayList<String> paths = new ArrayList<>();
        for (JsonElement jsonObject: array ){
            paths.add(jsonObject.getAsString());
        }
        return paths.toArray(new String[0]);
    }
}
