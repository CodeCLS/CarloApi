package carlo.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.smartcar.sdk.data.ApplicationPermissions;
import com.smartcar.sdk.data.Auth;

import java.util.ArrayList;
import java.util.Arrays;

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
    public String[] getListFromJson(String token, String id,String body) {
        JsonObject object = new Gson().fromJson(body,JsonObject.class);
        String type = object.get("type").toString();
        switch (type){
            case "PERMISSIONS":
                ApplicationPermissions applicationPermissions=
                        new SmartCarRepository().getVehiclePermissions(token,id);
               return applicationPermissions.getPermissions();
            case "SELECTION":
                ArrayList<String> paths = new ArrayList<>();
                for (JsonElement jsonObject: object.getAsJsonArray(ApiManager.SELECTION_BATCH) ){
                    paths.add(jsonObject.getAsString());
                }
                return (String[])paths.toArray();
            case "ALL":
                return (String[])ApiManager.BATCH_ARRAY_ALL.toArray();



        }
        return null;
    }
}
