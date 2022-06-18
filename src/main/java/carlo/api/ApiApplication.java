package carlo.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.smartcar.sdk.*;
import com.smartcar.sdk.data.*;
import com.sun.xml.internal.ws.api.message.Message;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.List;

@RestController
@SpringBootApplication
public class ApiApplication {

    private ApiHelper apiHelper = new ApiHelper();
    private SmartCarRepository smartCarRepository = new SmartCarRepository();
    private FirebaseRepository firebaseRepository = new FirebaseRepository();




    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
    @GetMapping("/")//Done
    public String start(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }
    @RequestMapping(value = "/user/{uid}/vehicle/{id}/attributes",method = RequestMethod.GET)
    public DeferredResult<String> getVehicleAttributes(
            @RequestHeader("api-code") String apiCode,
            @RequestHeader("access-token-smart-car") String token,
            @PathVariable String uid,
            @PathVariable String id)
    {
        //TODO do something with uid

        ResponseBuilder responseBuilder = new ResponseBuilder();
        DeferredResult<String> result = new DeferredResult<>();
        if(manageApiCode(apiCode, responseBuilder)) {
            result.setResult(responseBuilder.create());
            return result;
        }
        firebaseRepository.updateUserApiCall();
        VehicleAttributes vehicleAttributes = smartCarRepository.getVehicleAttributes(token,id);
        responseBuilder.add(ApiManager.VEHICLE_ID,vehicleAttributes.getId());
        responseBuilder.add(ApiManager.VEHICLE_MAKE,vehicleAttributes.getMake());
        responseBuilder.add(ApiManager.VEHICLE_MODEL,vehicleAttributes.getModel());
        responseBuilder.add(ApiManager.VEHICLE_YEAR,vehicleAttributes.getYear());
        responseBuilder.setSuccessfulAction(true);

        result.setResult(responseBuilder.create());
        return result;
    }


    @RequestMapping(value = "user/{uid}/exchange/auth",method = RequestMethod.GET)
    public DeferredResult<String> exchangeAuthCode(@RequestHeader("api-code") String apiCode,
                                                   @RequestHeader("access-token-smart-car") String token,
                                                   @PathVariable String uid) {
        ResponseBuilder responseBuilder = new ResponseBuilder();
        DeferredResult<String> result = new DeferredResult<>();
        if(manageApiCode(apiCode, responseBuilder)) {
            result.setResult(responseBuilder.create());
            return result;
        }
        AuthClient authClient = smartCarRepository.createAuthClient();
        Auth auth = smartCarRepository.exchangeAuth(token,authClient);
        ParserTool<AuthClient> parserToolClient = new Converter();
        String authClientJson = parserToolClient.doTask(authClient);
        ParserTool<Auth> parserToolAuth = new Converter();
        String authJson = parserToolAuth.doTask(auth);
        responseBuilder.add(ApiManager.ACCESS_TOKEN,auth.getAccessToken());
        responseBuilder.add(ApiManager.AUTH_CLIENT,authClientJson);
        responseBuilder.add(ApiManager.AUTH,authJson);
        responseBuilder.setSuccessfulAction(true);

        result.setResult(responseBuilder.create());
        return result;

    }

    @RequestMapping(value = "/user/{uid}/vehicle/{id}/location",method = RequestMethod.GET)
    public DeferredResult<String> getLocation(
            @RequestHeader("api-code") String apiCode,
            @RequestHeader("access-token-smart-car") String token,
            @PathVariable String uid,
            @PathVariable String id)
    {
        //TODO do something with uid

        ResponseBuilder responseBuilder = new ResponseBuilder();
        DeferredResult<String> result = new DeferredResult<>();
        if(manageApiCode(apiCode, responseBuilder)) {
            result.setResult(responseBuilder.create());
            return result;
        }
        firebaseRepository.updateUserApiCall();
        VehicleLocation vehicleLocation = smartCarRepository.getVehicleLocation(token,id);
        responseBuilder.add(ApiManager.LATITUDE,vehicleLocation.getLatitude());
        responseBuilder.add(ApiManager.LONGITUDE,vehicleLocation.getLongitude());
        responseBuilder.setSuccessfulAction(true);

        result.setResult(responseBuilder.create());
        return result;
    }
    @RequestMapping(value = "/user/{uid}/vehicle/",method = RequestMethod.GET)
    public DeferredResult<String> getLocation(
            @RequestHeader("api-code") String apiCode,
            @RequestHeader("access-token-smart-car") String token,
            @PathVariable String uid)
    {
        //TODO do something with uid

        ResponseBuilder responseBuilder = new ResponseBuilder();
        DeferredResult<String> result = new DeferredResult<>();
        if(manageApiCode(apiCode, responseBuilder)) {
            result.setResult(responseBuilder.create());
            return result;
        }
        firebaseRepository.updateUserApiCall();
        VehicleIds vehicleIds = smartCarRepository.getVehicles(token);
        responseBuilder.add(ApiManager.VEHICLE_IDS,vehicleIds.getVehicleIds());
        responseBuilder.setSuccessfulAction(true);
        result.setResult(responseBuilder.create());
        return result;
    }
    @RequestMapping(value = "/user/{uid}/refresh/",method = RequestMethod.GET)
    public DeferredResult<String> refresh(
            @RequestHeader("api-code") String apiCode,
            @RequestHeader("access-token-smart-car") String token,
            @PathVariable String uid,
            @RequestParam(value = "client", defaultValue = "null") String client,
            @RequestParam(value = "auth", defaultValue = "null") String auth)
    {
        //TODO do something with uid

        ResponseBuilder responseBuilder = new ResponseBuilder();
        DeferredResult<String> result = new DeferredResult<>();
        if(manageApiCode(apiCode, responseBuilder)) {
            result.setResult(responseBuilder.create());
            return result;
        }
        firebaseRepository.updateUserApiCall();
        Gson gson = new Gson();
        AuthClient authClient = gson.fromJson(client,AuthClient.class);
        Auth access = gson.fromJson(auth,Auth.class);
        Auth newAuth = smartCarRepository.refreshToken(authClient,access);
        responseBuilder.setSuccessfulAction(true);
        responseBuilder.add(ApiManager.AUTH,gson.toJson(newAuth));
        result.setResult(responseBuilder.create());
        return result;
    }
    @RequestMapping(value = "/user/{uid}/validate/smartcar_token",method = RequestMethod.GET)
    public DeferredResult<String> validateSmartCarToken(
            @RequestHeader("api-code") String apiCode,
            @RequestHeader("access-token-smart-car") String token,
            @PathVariable String uid)
    {
        //TODO do something with uid
        ResponseBuilder responseBuilder = new ResponseBuilder();
        DeferredResult<String> result = new DeferredResult<>();
        if(manageApiCode(apiCode, responseBuilder)) {
            result.setResult(responseBuilder.create());
            return result;
        }
        return result;
    }
    @RequestMapping(value = "/user/{uid}/vehicle/{id}/odometer",method = RequestMethod.GET)
    public DeferredResult<String> getOdometer(
            @RequestHeader("api-code") String apiCode,
            @RequestHeader("access-token-smart-car") String token,
            @PathVariable String uid,
            @PathVariable String id)
    {
        //TODO do something with uid

        ResponseBuilder responseBuilder = new ResponseBuilder();
        DeferredResult<String> result = new DeferredResult<>();
        if(manageApiCode(apiCode, responseBuilder)) {
            result.setResult(responseBuilder.create());
            return result;
        }
        firebaseRepository.updateUserApiCall();
        VehicleOdometer vehicleOdometer = smartCarRepository.getVehicleOdometer(token,id);
        responseBuilder.add(ApiManager.ODOMETER,vehicleOdometer.getDistance());
        responseBuilder.setSuccessfulAction(true);
        result.setResult(responseBuilder.create());
        return result;
    }

    @RequestMapping(value = "/user/{uid}/vehicle/{id}/vin",method = RequestMethod.GET)
    public DeferredResult<String> getVin(
            @RequestHeader("api-code") String apiCode,
            @RequestHeader("access-token-smart-car") String token,
            @PathVariable String uid,
            @PathVariable String id)
    {
        //TODO do something with uid

        ResponseBuilder responseBuilder = new ResponseBuilder();
        DeferredResult<String> result = new DeferredResult<>();
        if(manageApiCode(apiCode, responseBuilder)) {
            result.setResult(responseBuilder.create());
            return result;
        }
        firebaseRepository.updateUserApiCall();
        String vin = smartCarRepository.getVehicleVin(token,id);
        responseBuilder.add(ApiManager.VIN,vin);
        responseBuilder.setSuccessfulAction(true);
        result.setResult(responseBuilder.create());
        return result;
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




    private Boolean manageApiCode(String apiCode, ResponseBuilder responseBuilder) {
        if(!apiHelper.isValid(apiCode)){
            responseBuilder.setSuccessfulAction(false);
            responseBuilder.setErrorCode(ErrorManager.INVALID_API_KEY_CODE);
            responseBuilder.setErrorMsg(ErrorManager.INVALID_API_KEY_MSG);
            return false;

        }
        return true;
    }
}
