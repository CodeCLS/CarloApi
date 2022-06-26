package carlo.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.smartcar.sdk.data.ApplicationPermissions;
import com.smartcar.sdk.data.Auth;

import java.util.ArrayList;
import java.util.Arrays;

public class Converter implements BatchPaths, PermissionsConverter {

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
        type = type.replace("\"","");
        System.out.println("type" + type);
        if (type.equals("PERMISSIONS")) {
            System.out.println("PERMISSIONS");
            ApplicationPermissions applicationPermissions=
                    new SmartCarRepository().getVehiclePermissions(token,id);

            ArrayList<String> paths = new ArrayList<>();
            for(String s : applicationPermissions.getPermissions()){
                paths.add(convert(s));
            }

            return (String[])paths.toArray();
        }
        else if(type.equals("SELECTION")) {
            System.out.println("SELECTION");

            ArrayList<String> paths = new ArrayList<>();
            for (JsonElement jsonObject: object.getAsJsonArray(ApiManager.SELECTION_BATCH) ){
                paths.add(jsonObject.getAsString());
            }
            return (String[])paths.toArray();
        }
        else if(type.equals("ALL") ){
            System.out.println("ALL");
            return (String[]) ApiManager.BATCH_ARRAY_ALL.toArray();
        }
        else {
            System.out.println("None");
            return null;
        }

    }

    @Override
    public String convert(String s) {
        switch(s){
            case "read_vehicle_info":
                return "/";
            case "read_location":
                return "/location";
            case "read_vin":
                return "/vin";
            case "read_odometer":
                return "/odometer";
            case "control_security":
                break;
            case "read_fuel":
                return "/fuel";
        }
        return null;
    }
}
