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
            @PathVariable("uid") String uid,
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
                                                   @PathVariable("uid") String uid) {
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
            @PathVariable("uid") String uid,
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
            @PathVariable("uid") String uid)
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
            @PathVariable("uid") String uid,
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
            @PathVariable("uid") String uid)
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
            @PathVariable("uid") String uid,
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
            @PathVariable("uid") String uid,
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
    @RequestMapping(value = "/user/{uid}/vehicle/{id}/range",method = RequestMethod.GET)
    public DeferredResult<String> getElectricRange(
            @RequestHeader("api-code") String apiCode,
            @RequestHeader("access-token-smart-car") String token,
            @PathVariable("uid") String uid,
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
        Object range = smartCarRepository.getVehicleRange(token,id);
        if (range instanceof VehicleBattery){
            responseBuilder.add(ApiManager.RANGE_PERCENT,((VehicleBattery) range).getPercentRemaining());
            responseBuilder.add(ApiManager.RANGE_RADIUS,((VehicleBattery) range).getRange());
            responseBuilder.add(ApiManager.RANGE_AMOUNT,"Unavailable");
        }else if(range instanceof VehicleFuel){
            responseBuilder.add(ApiManager.RANGE_PERCENT,((VehicleFuel) range).getPercentRemaining());
            responseBuilder.add(ApiManager.RANGE_RADIUS,((VehicleFuel) range).getRange());
            responseBuilder.add(ApiManager.RANGE_AMOUNT,((VehicleFuel) range).getAmountRemaining());
        }
        responseBuilder.setSuccessfulAction(true);
        result.setResult(responseBuilder.create());
        return result;
    }
    @RequestMapping(value = "/user/{uid}/vehicle/{id}/lock",method = RequestMethod.GET)
    public DeferredResult<String> lock(
            @RequestHeader("api-code") String apiCode,
            @RequestHeader("access-token-smart-car") String token,
            @PathVariable("uid") String uid,
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
        ActionResponse actionResponse = smartCarRepository.lock(token,id);
        responseBuilder.add(ApiManager.ACTION_MSG,actionResponse.getMessage());
        responseBuilder.setSuccessfulAction(actionResponse.getStatus().equals("success"));
        result.setResult(responseBuilder.create());
        return result;
    }

    @RequestMapping(value = "/user/{uid}/vehicle/{id}/unlock",method = RequestMethod.GET)
    public DeferredResult<String> unlock(
            @RequestHeader("api-code") String apiCode,
            @RequestHeader("access-token-smart-car") String token,
            @PathVariable("uid") String uid,
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
        ActionResponse actionResponse = smartCarRepository.unlock(token,id);
        responseBuilder.add(ApiManager.ACTION_MSG,actionResponse.getMessage());
        responseBuilder.setSuccessfulAction(actionResponse.getStatus().equals("success"));
        result.setResult(responseBuilder.create());
        return result;
    }
    @RequestMapping(value = "/user/{uid}/vehicle/{id}/is_electric",method = RequestMethod.GET)
    public DeferredResult<String> isElectric(
            @RequestHeader("api-code") String apiCode,
            @RequestHeader("access-token-smart-car") String token,
            @PathVariable("uid") String uid,
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
        Object range = smartCarRepository.getVehicleRange(token,id);
        if (range instanceof VehicleBattery){
            responseBuilder.add(ApiManager.IS_ELECTRIC,true);

        }else if(range instanceof VehicleFuel){
            responseBuilder.add(ApiManager.IS_ELECTRIC,false);

        }
        responseBuilder.setSuccessfulAction(true);
        result.setResult(responseBuilder.create());
        return result;
    }

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
