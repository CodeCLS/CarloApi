package carlo.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.smartcar.sdk.*;
import com.smartcar.sdk.data.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@SpringBootApplication
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
    @GetMapping("/")//Done
    public String start(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }
    @GetMapping("/exchange")
    public DeferredResult<String> exchangeAuthCode(@RequestParam(value = "code", defaultValue = "null") String code) {
        DeferredResult<String> output = new DeferredResult<>();
        // Setup
        String clientId = "3b683bb7-48a3-4b4c-8a2f-7337a8a0ee19";
        String clientSecret = "bdefd1ee-de23-4df5-aa61-c9ef1d6ac724";
        String redirectUri = "sc3b683bb7-48a3-4b4c-8a2f-7337a8a0ee19://myapp.com/callback";
        boolean testMode = true;

// Initialize a new AuthClient with your credentials.
        AuthClient authClient = null;
        try {
            authClient = new AuthClient.Builder()
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .redirectUri(redirectUri)
                    .testMode(testMode).build();



            Auth auth = authClient.exchangeCode(code);
            VehicleIds response = Smartcar.getVehicles(auth.getAccessToken());


            Gson gson = new Gson();
            String jsonStringClient = gson.toJson(authClient);
            String jsonStringAuth = gson.toJson(auth);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("accessCode" , auth.getAccessToken());
            jsonObject.addProperty("client" , jsonStringClient);
            jsonObject.addProperty("auth" , jsonStringAuth);

            output.setResult(jsonObject.toString());


        } catch (Exception e) {
            e.printStackTrace();
            output.setResult(e.getMessage());
        }

        return output;

    }

    @GetMapping("/vehicle/location")
    public String location(@RequestParam(value = "code", defaultValue = "null") String code,@RequestParam(value = "id", defaultValue = "null") String id) throws Exception {
        VehicleIds response = Smartcar.getVehicles(code);
        String[] vehicleIds = response.getVehicleIds();
        Vehicle vehicle =new Vehicle(vehicleIds[0],code);
        JsonObject jsonObject = new JsonObject();
        VehicleLocation location = vehicle.location();
        jsonObject.addProperty("latitude",""+location.getLatitude());
        jsonObject.addProperty("longitude",location.getLongitude());


        return jsonObject.toString();
    }

    @GetMapping("/vehicle/info")
    public String vehicleInformation(@RequestParam(value = "code", defaultValue = "null") String code,@RequestParam(value = "id", defaultValue = "null") String id) throws Exception {
        VehicleIds response = Smartcar.getVehicles(code);
        Vehicle vehicle =new Vehicle(id,code);
        JsonObject jsonObject = new JsonObject();
        VehicleAttributes vehicleInfo = vehicle.attributes();
        String vehicleId = vehicleInfo.getId();
        String vehicleMake = vehicleInfo.getMake();
        String vehicleYear = ""+vehicleInfo.getYear();
        String vehicleModel = vehicleInfo.getModel();

        jsonObject.addProperty("vehicleId",""+vehicleId);
        jsonObject.addProperty("vehicleMake",vehicleMake);
        jsonObject.addProperty("vehicleYear",""+vehicleYear);
        jsonObject.addProperty("vehicleModel",vehicleModel);
        return jsonObject.toString();
    }
    @GetMapping("/user/vehicles")

    public String allVehicleIds(@RequestParam(value = "code", defaultValue = "null") String code) throws Exception{
        VehicleIds response = Smartcar.getVehicles(code);

        JsonArray jsonObject = new JsonArray();
        String[] vehicleIds = response.getVehicleIds();
        for(String s : vehicleIds){
            JsonObject jsonObject1 = new JsonObject();
            jsonObject1.addProperty("id",s);
            jsonObject.add(jsonObject1);
        }
        return jsonObject.toString();

    }
    @GetMapping("/all_attributes")
    public String attributes(@RequestParam(value = "code", defaultValue = "null") String code,@RequestParam(value = "id", defaultValue = "null") String id) throws Exception {
        JsonObject odometer = new JsonObject();
        JsonObject location = new JsonObject();
        JsonObject vehicleInfo = new JsonObject();

        try {
            odometer = new JsonObject().getAsJsonObject(odometer(code,id));
        }catch (Exception e){};
        try {
            location = new JsonObject().getAsJsonObject(location(code,id));
        }catch (Exception e){};
        try {
            vehicleInfo = new JsonObject().getAsJsonObject(vehicleInformation(code,id));
        }catch (Exception e){};




        odometer.add("",location);
        odometer.add("",vehicleInfo);
        return odometer.toString();
    }
    @RequestMapping(value = "/refresh",method = RequestMethod.POST)
    public String refresh(@RequestParam(value = "client", defaultValue = "null") String client,@RequestParam(value = "auth", defaultValue = "null") String auth) throws Exception {
        Gson gson = new Gson();
        AuthClient authClient = gson.fromJson(client,AuthClient.class);
        System.out.println("access: " + client + " " + auth);
        Auth access = gson.fromJson(auth,Auth.class);
        if (Smartcar.isExpired(access.getExpiration())) {
            access = authClient.exchangeRefreshToken(access.getRefreshToken());
        }
        return "";
    }

    @GetMapping("/validate")
    public String validate(@RequestParam(value = "code", defaultValue = "null") String code) {
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("id", Smartcar.getUser(code).getId());
            jsonObject.addProperty("isValid", "true");

        }
        catch (Exception e){
            System.out.println(e.getMessage());
            jsonObject.addProperty("isValid", "false");
            jsonObject.addProperty("msg", e.getMessage());

        }
        System.out.println(jsonObject);
        return jsonObject.toString();
    }
    @GetMapping("/vehicle/odometer")
    public String odometer(@RequestParam(value = "code", defaultValue = "null") String code,@RequestParam(value = "id", defaultValue = "null") String id) throws Exception {
        VehicleIds response = Smartcar.getVehicles(code);
        Vehicle vehicle =new Vehicle(id,code);
        JsonObject jsonObject = new JsonObject();
        VehicleOdometer odometer = vehicle.odometer();

        jsonObject.addProperty("odometer",""+odometer.getDistance());

        return jsonObject.toString();
    }
    @GetMapping("vehicle/vin")
    public String vin(@RequestParam(value = "code", defaultValue = "null") String code,@RequestParam(value = "id", defaultValue = "null") String id) throws Exception {
        VehicleIds response = Smartcar.getVehicles(code);
        Vehicle vehicle =new Vehicle(id,code);
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("vin",""+vehicle.vin());
        return jsonObject.toString();
    }
    @GetMapping("vehicle/battery")
    public String battery(@RequestParam(value = "code", defaultValue = "null") String code,@RequestParam(value = "id", defaultValue = "null") String id) throws Exception {
        VehicleIds response = Smartcar.getVehicles(code);
        Vehicle vehicle =new Vehicle(id,code);
        JsonObject jsonObject = new JsonObject();
        VehicleBattery battery = vehicle.battery();

        jsonObject.addProperty("battery_percent",""+battery.getPercentRemaining());
        jsonObject.addProperty("battery_range",""+battery.getRange());

        return jsonObject.toString();
    }
    @GetMapping("vehicle/range/non_electric")
    public String rangeNonElectric(@RequestParam(value = "code", defaultValue = "null") String code,@RequestParam(value = "id", defaultValue = "null") String id) throws Exception {
        VehicleIds response = Smartcar.getVehicles(code);
        Vehicle vehicle =new Vehicle(id,code);
        JsonObject jsonObject = new JsonObject();
        VehicleFuel fuel = vehicle.fuel();

        jsonObject.addProperty("percent",""+fuel.getPercentRemaining());
        jsonObject.addProperty("range",""+fuel.getRange());

        return jsonObject.toString();
    }
    @GetMapping("/vehicle/lock")
    public String lock(@RequestParam(value = "code", defaultValue = "null") String code,@RequestParam(value = "id", defaultValue = "null") String id) throws Exception {
        VehicleIds response = Smartcar.getVehicles(code);
        Vehicle vehicle =new Vehicle(id,code);
        JsonObject jsonObject = new JsonObject();
        ActionResponse lock = vehicle.lock();

        jsonObject.addProperty("lock_status",""+lock.getStatus());
        jsonObject.addProperty("lock_msg",""+lock.getStatus());

        return jsonObject.toString();
    }
    @GetMapping("/vehicle/unlock")
    public String unlock(@RequestParam(value = "code", defaultValue = "null") String code,@RequestParam(value = "id", defaultValue = "null") String id) throws Exception {
        VehicleIds response = Smartcar.getVehicles(code);
        String[] vehicleIds = response.getVehicleIds();
        Vehicle vehicle =new Vehicle(id,code);
        JsonObject jsonObject = new JsonObject();
        ActionResponse unlock = vehicle.unlock();

        jsonObject.addProperty("unlock_status",""+unlock.getStatus());
        jsonObject.addProperty("unlock_msg",""+unlock.getStatus());

        return jsonObject.toString();
    }
    @GetMapping("/vehicle/fuel")
    public String fuel(@RequestParam(value = "code", defaultValue = "null") String code,@RequestParam(value = "id", defaultValue = "null") String id) throws Exception {
        VehicleIds response = Smartcar.getVehicles(code);
        Vehicle vehicle =new Vehicle(id,code);
        JsonObject jsonObject = new JsonObject();
        VehicleFuel fuel = vehicle.fuel();

        jsonObject.addProperty("fuel_percent",""+fuel.getPercentRemaining());
        jsonObject.addProperty("fuel_range",""+fuel.getRange());
        jsonObject.addProperty("fuel_amount",""+fuel.getAmountRemaining());

        return jsonObject.toString();
    }
    @GetMapping("/vehicle/is_electric")
    public String isElectric(@RequestParam(value = "code", defaultValue = "null") String code,@RequestParam(value = "id", defaultValue = "null") String id) throws Exception{
        VehicleIds response = Smartcar.getVehicles(code);
        String[] vehicleIds = response.getVehicleIds();
        Vehicle vehicle =new Vehicle(id,code);
        JsonObject jsonObject = new JsonObject();
        VehicleFuel fuel = null;
        try {
            vehicle.fuel();
            jsonObject.addProperty("is_electric","false");
            return jsonObject.toString();


        } catch (SmartcarException e) {
            e.printStackTrace();
        }

        jsonObject.addProperty("is_electric","true");

        return jsonObject.toString();
    }
    @GetMapping("/vehicle/charge")
    public String charge(@RequestParam(value = "code", defaultValue = "null") String code,@RequestParam(value = "id", defaultValue = "null") String id) throws Exception {
        VehicleIds response = Smartcar.getVehicles(code);
        Vehicle vehicle =new Vehicle(id,code);
        JsonObject jsonObject = new JsonObject();
        VehicleCharge charge = vehicle.charge();

        jsonObject.addProperty("charge_state",""+charge.getState());
        jsonObject.addProperty("charge_plugged_in",""+charge.getIsPluggedIn());

        return jsonObject.toString();
    }

    //@RequestMapping(
    //        value = CONSTANT_MESSAGE_SEND_URL,
    //        method = RequestMethod.POST)
    //public DeferredResult<String> verifyMessage(@RequestBody String body){
//
    //    DeferredResult<String> output = new DeferredResult<>();
    //    new JSONUtil().createMessageFromJSON(body, new FirebaseManager.OnActionListener<ChatrApiMessage>() {
    //        @Override
    //        public void success(ChatrApiMessage message) {
    //            ApiRepository.getInstance().verifyMessage(message,output);
    //        }
//
    //        @Override
    //        public void failure(BaseException e) {
    //            System.out.println(e.getMessage());
//
    //        }
    //    });
    //    return output;
    //}




}
