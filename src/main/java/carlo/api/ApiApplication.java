package carlo.api;

import com.google.gson.JsonObject;
import com.smartcar.sdk.AuthClient;
import com.smartcar.sdk.Smartcar;
import com.smartcar.sdk.Vehicle;
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
            Vehicle vehicle = new Vehicle(response.getVehicleIds()[0],auth.getAccessToken());
            String[] vehicleIds = response.getVehicleIds();
            output.setResult(auth.getAccessToken());


        } catch (Exception e) {
            e.printStackTrace();
            output.setResult(e.getMessage());
        }

        return output;

    }

    @GetMapping("/location")
    public JsonObject location(@RequestParam(value = "code", defaultValue = "null") String code) throws Exception {
        VehicleIds response = Smartcar.getVehicles(code);
        String[] vehicleIds = response.getVehicleIds();
        Vehicle vehicle =new Vehicle(vehicleIds[0],code);
        JsonObject jsonObject = new JsonObject();
        VehicleLocation location = vehicle.location();
        jsonObject.addProperty("latitude",""+location.getLatitude());
        jsonObject.addProperty("longitude",location.getLongitude());


        return jsonObject;
    }
    @GetMapping("/vehicle_info")
    public JsonObject vehicleInformation(@RequestParam(value = "code", defaultValue = "null") String code) throws Exception {
        VehicleIds response = Smartcar.getVehicles(code);
        String[] vehicleIds = response.getVehicleIds();
        Vehicle vehicle =new Vehicle(vehicleIds[0],code);
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
        return jsonObject;
    }
    @GetMapping("/odometer")
    public JsonObject odometer(@RequestParam(value = "code", defaultValue = "null") String code) throws Exception {
        VehicleIds response = Smartcar.getVehicles(code);
        String[] vehicleIds = response.getVehicleIds();
        Vehicle vehicle =new Vehicle(vehicleIds[0],code);
        JsonObject jsonObject = new JsonObject();
        VehicleOdometer odometer = vehicle.odometer();

        jsonObject.addProperty("odometer",""+odometer.getDistance());

        return jsonObject;
    }
    @GetMapping("/battery")
    public JsonObject battery(@RequestParam(value = "code", defaultValue = "null") String code) throws Exception {
        VehicleIds response = Smartcar.getVehicles(code);
        String[] vehicleIds = response.getVehicleIds();
        Vehicle vehicle =new Vehicle(vehicleIds[0],code);
        JsonObject jsonObject = new JsonObject();
        VehicleBattery battery = vehicle.battery();

        jsonObject.addProperty("battery_percent",""+battery.getPercentRemaining());
        jsonObject.addProperty("battery_range",""+battery.getRange());

        return jsonObject;
    }
    @GetMapping("/lock")
    public JsonObject lock(@RequestParam(value = "code", defaultValue = "null") String code) throws Exception {
        VehicleIds response = Smartcar.getVehicles(code);
        String[] vehicleIds = response.getVehicleIds();
        Vehicle vehicle =new Vehicle(vehicleIds[0],code);
        JsonObject jsonObject = new JsonObject();
        ActionResponse lock = vehicle.lock();

        jsonObject.addProperty("lock_status",""+lock.getStatus());
        jsonObject.addProperty("lock_msg",""+lock.getStatus());

        return jsonObject;
    }
    @GetMapping("/unlock")
    public JsonObject unlock(@RequestParam(value = "code", defaultValue = "null") String code) throws Exception {
        VehicleIds response = Smartcar.getVehicles(code);
        String[] vehicleIds = response.getVehicleIds();
        Vehicle vehicle =new Vehicle(vehicleIds[0],code);
        JsonObject jsonObject = new JsonObject();
        ActionResponse unlock = vehicle.unlock();

        jsonObject.addProperty("unlock_status",""+unlock.getStatus());
        jsonObject.addProperty("unlock_msg",""+unlock.getStatus());

        return jsonObject;
    }
    @GetMapping("/fuel")
    public JsonObject fuel(@RequestParam(value = "code", defaultValue = "null") String code) throws Exception {
        VehicleIds response = Smartcar.getVehicles(code);
        String[] vehicleIds = response.getVehicleIds();
        Vehicle vehicle =new Vehicle(vehicleIds[0],code);
        JsonObject jsonObject = new JsonObject();
        VehicleFuel fuel = vehicle.fuel();

        jsonObject.addProperty("fuel_percent",""+fuel.getPercentRemaining());
        jsonObject.addProperty("fuel_range",""+fuel.getRange());
        jsonObject.addProperty("fuel_amount",""+fuel.getAmountRemaining());

        return jsonObject;
    }
    @GetMapping("/charge")
    public JsonObject charge(@RequestParam(value = "code", defaultValue = "null") String code) throws Exception {
        VehicleIds response = Smartcar.getVehicles(code);
        String[] vehicleIds = response.getVehicleIds();
        Vehicle vehicle =new Vehicle(vehicleIds[0],code);
        JsonObject jsonObject = new JsonObject();
        VehicleCharge charge = vehicle.charge();

        jsonObject.addProperty("charge_state",""+charge.getState());
        jsonObject.addProperty("charge_plugged_in",""+charge.getIsPluggedIn());

        return jsonObject;
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
