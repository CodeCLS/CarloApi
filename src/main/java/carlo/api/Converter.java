package carlo.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.smartcar.sdk.data.ApplicationPermissions;
import com.smartcar.sdk.data.Auth;
import org.json.JSONArray;
import org.json.JSONObject;

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

    public static User convertUser(String body) {
        System.out.println("TEST IIIII");

        JsonObject jsonObject = (JsonObject) convertJson(body, JsonObject.class);
        User user = new User();
        user.setFirstName(jsonObject.get(ApiManager.FIRST_NAME).getAsString());
        user.setSecondName(jsonObject.get(ApiManager.SECOND_NAME).getAsString());
        user.setPhone(jsonObject.get(ApiManager.PHONE).getAsString());
        user.setEmail(jsonObject.get(ApiManager.EMAIL).getAsString());
        System.out.println("TEST IIIIII" + user.toJson());

        return user;
    }

    @Override
    public String[] getListFromJson(String token, String id,String body,ResponseBuilder responseBuilder) {
        JsonObject object = new Gson().fromJson(body,JsonObject.class);
        String type = object.get("type").toString();
        type = type.replace("\"","");
        System.out.println("type" + type);
        if (type.equals("PERMISSIONS")) {
            System.out.println("PERMISSIONS");
            ApplicationPermissions applicationPermissions=
                    new SmartCarRepository().getVehiclePermissions(token,id,null);

            ArrayList<String> paths = new ArrayList<>();
            for(String s : applicationPermissions.getPermissions()){
                String i = convert(s);
                if(i!= null)
                    paths.add(i);
            }

            return paths.stream().toArray(size -> new String[paths.size()]);
        }
        else if(type.equals("SELECTION")) {
            System.out.println("SELECTION");

            ArrayList<String> paths = new ArrayList<>();
            for (JsonElement jsonObject: object.getAsJsonArray(ApiManager.SELECTION_BATCH) ){
                paths.add(jsonObject.getAsString());
            }
            return paths.stream().toArray(size -> new String[paths.size()]);
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

    public ContentPackage convertMarketValueCar(String body){
        CarMarketValue carMarketValue = new CarMarketValue();
        ContentPackage contentPackage = new ContentPackage();
        try {
            JSONObject jsonObject = new JSONObject(body);
            String vin = jsonObject.getString("vin");
            boolean success = jsonObject.getBoolean("success");
            try {
                Long retail = jsonObject.getLong("retail");
                Long tradeIn = jsonObject.getLong("tradeIn");
                Long roughTradeIn = jsonObject.getLong("roughTradeIn");
                Long averageTradeIn = jsonObject.getLong("averageTradeIn");
                Long loanValue = jsonObject.getLong("loanValue");
                Long msrp = jsonObject.getLong("msrp");
                carMarketValue.setVin(vin);
                carMarketValue.setSuccess(success);
                carMarketValue.setRetail(retail);
                carMarketValue.setTradeIn(tradeIn);
                carMarketValue.setRoughTradeIn(roughTradeIn);
                carMarketValue.setAverageTradeIn(averageTradeIn);
                carMarketValue.setLoanValue(loanValue);
                carMarketValue.setMsrp(msrp);
            }
            catch (Exception e){

            }
            contentPackage.setValue(carMarketValue);
        }catch (Exception e){
            contentPackage.setException(e);
        }
        return contentPackage;

            //JSONArray tradeInValues = jsonObject.getJSONArray("tradeInValues");

            //Long auctionValues = jsonObject.getString("auctionValues");



    }
}
