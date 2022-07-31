package carlo.api;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.smartcar.sdk.*;
import com.smartcar.sdk.data.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.Semaphore;

@RestController
@SpringBootApplication
@Configuration
@EnableScheduling
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
        firebaseRepository.updateUserApiCall(uid,ApiManager.CAR_ATTRIBUTE_ENDPOINT);
        if(manageApiCode(apiCode, responseBuilder) || canRequest(uid,ApiManager.CAR_ATTRIBUTE_ENDPOINT,responseBuilder)) {
            result.setResult(responseBuilder.create());
            return result;
        }
        VehicleAttributes vehicleAttributes = smartCarRepository.getVehicleAttributes(token,id,responseBuilder);
        if (vehicleAttributes != null) {
            responseBuilder.add(ApiManager.VEHICLE_ID, vehicleAttributes.getId());
            responseBuilder.add(ApiManager.VEHICLE_MAKE, vehicleAttributes.getMake());
            responseBuilder.add(ApiManager.VEHICLE_MODEL, vehicleAttributes.getModel());
            responseBuilder.add(ApiManager.VEHICLE_YEAR, vehicleAttributes.getYear());
            responseBuilder.add(ApiManager.VIN, smartCarRepository.getVehicleVin(token,id,responseBuilder));
            responseBuilder.setSuccessfulAction(true);
            result.setResult(responseBuilder.create());
        }
        else if (responseBuilder.getErrorMsg() == null){
            result.setResult(ErrorManager.createErrorResponse(
                    ErrorManager.INTERNAL_ERROR_KEY_CODE,
                    ErrorManager.INTERNAL_ERROR_KEY_MSG));
        }
        return result;
    }

    private boolean canRequest(String uid, String carAttributeEndpoint,ResponseBuilder responseBuilder) {
        final Semaphore semaphore = new Semaphore(0);
        final int[] val = {0};
        System.out.println("0: " + val[0]);

        firebaseRepository.getUserApiCallAmount(uid, carAttributeEndpoint, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){
                    val[0] = -1;

                }
                else{
                    val[0] = (int)dataSnapshot.getValue();
                }
                semaphore.release();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("2: " + val[0]);

                semaphore.release();

            }
        });
        try {
            semaphore.acquire();
            System.out.println("4: " + val[0]);

            if (ApiManager.MARKET_VALUE_ENDPOINT.equals(carAttributeEndpoint)) {
                boolean result = val[0] < 1;
                if (!result){
                    createGeneralError(responseBuilder);
                }
                return result;
            }
            boolean result = val[0] < 20;
            if (!result){
                createGeneralError(responseBuilder);
            }
            return result;
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("5: " + val[0]);
            createGeneralError(responseBuilder);


            return false;
        }


    }


    @RequestMapping(value = "user/{uid}/exchange/auth",method = RequestMethod.GET)
    public DeferredResult<String> exchangeAuthCode(@RequestHeader("api-code") String apiCode,
                                                   @RequestHeader("access-token-smart-car") String token,
                                                   @PathVariable("uid") String uid) {
        ResponseBuilder responseBuilder = new ResponseBuilder();
        DeferredResult<String> result = new DeferredResult<>();
        firebaseRepository.updateUserApiCall(uid,ApiManager.GET_ACCESS_TOKEN);
        if(manageApiCode(apiCode, responseBuilder) || canRequest(uid,ApiManager.GET_ACCESS_TOKEN,responseBuilder)) {
            result.setResult(responseBuilder.create());
            return result;
        }
        AuthClient authClient = smartCarRepository.createAuthClient();
        Auth auth = smartCarRepository.exchangeAuth(token,authClient,responseBuilder);
        String authClientJson = Converter.doTask(authClient);
        String authJson = Converter.doTask(auth);
        responseBuilder.add(ApiManager.ACCESS_TOKEN,auth.getAccessToken());
        responseBuilder.add(ApiManager.REFRESH_TOKEN,auth.getRefreshToken());

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
        firebaseRepository.updateUserApiCall(uid,ApiManager.LOCATION_ENDPOINT);
        if(manageApiCode(apiCode, responseBuilder) || canRequest(uid,ApiManager.LOCATION_ENDPOINT,responseBuilder)) {
            result.setResult(responseBuilder.create());
            return result;
        }
        VehicleLocation vehicleLocation = smartCarRepository.getVehicleLocation(token,id,responseBuilder);
        if (vehicleLocation != null) {
            responseBuilder.add(ApiManager.LATITUDE, vehicleLocation.getLatitude());
            responseBuilder.add(ApiManager.LONGITUDE, vehicleLocation.getLongitude());
            responseBuilder.setSuccessfulAction(true);
            result.setResult(responseBuilder.create());
        }
        else if (responseBuilder.getErrorMsg() == null){
            result.setResult(ErrorManager.createErrorResponse(
                    ErrorManager.INTERNAL_ERROR_KEY_CODE,
                    ErrorManager.INTERNAL_ERROR_KEY_MSG));
        }

        result.setResult(responseBuilder.create());

        return result;
    }
    @RequestMapping(value = "/user/{uid}/vehicle/{id}/oil",method = RequestMethod.GET)
    public DeferredResult<String> getOil(
            @RequestHeader("api-code") String apiCode,
            @RequestHeader("access-token-smart-car") String token,
            @PathVariable("uid") String uid,
            @PathVariable String id)
    {
        //TODO do something with uid

        ResponseBuilder responseBuilder = new ResponseBuilder();
        DeferredResult<String> result = new DeferredResult<>();
        firebaseRepository.updateUserApiCall(uid,ApiManager.OIL_ENDPOINT);
        if(manageApiCode(apiCode, responseBuilder) || canRequest(uid,ApiManager.OIL_ENDPOINT,responseBuilder)) {
            result.setResult(responseBuilder.create());
            return result;
        }
        VehicleEngineOil vehicleEngineOil = smartCarRepository.getVehicleOil(token,id,responseBuilder);
        if (vehicleEngineOil != null) {
            responseBuilder.add(ApiManager.OIL, vehicleEngineOil.getLifeRemaining());
            responseBuilder.setSuccessfulAction(true);
            result.setResult(responseBuilder.create());
        }
        else if (responseBuilder.getErrorMsg() == null){
            result.setResult(ErrorManager.createErrorResponse(
                    ErrorManager.INTERNAL_ERROR_KEY_CODE,
                    ErrorManager.INTERNAL_ERROR_KEY_MSG));
        }

        result.setResult(responseBuilder.create());

        return result;
    }
    @RequestMapping(value = "/user/{uid}/vehicle",method = RequestMethod.GET)
    public DeferredResult<String> getLocation(
            @RequestHeader("api-code") String apiCode,
            @RequestHeader("access-token-smart-car") String token,
            @PathVariable("uid") String uid)
    {
        //TODO do something with uid

        ResponseBuilder responseBuilder = new ResponseBuilder();
        DeferredResult<String> result = new DeferredResult<>();
        firebaseRepository.updateUserApiCall(uid,ApiManager.GET_VEHICLES_ENDPOINT);
        if(manageApiCode(apiCode, responseBuilder) || canRequest(uid,ApiManager.GET_VEHICLES_ENDPOINT,responseBuilder)) {
            result.setResult(responseBuilder.create());
            return result;
        }
        VehicleIds vehicleIds = smartCarRepository.getVehicles(token,responseBuilder);
        if (vehicleIds != null) {
            responseBuilder.add(ApiManager.VEHICLE_IDS, vehicleIds.getVehicleIds());
            responseBuilder.setSuccessfulAction(true);
            result.setResult(responseBuilder.create());
        }
        else if (responseBuilder.getErrorMsg() == null){
            result.setResult(ErrorManager.createErrorResponse(
                    ErrorManager.INTERNAL_ERROR_KEY_CODE,
                    ErrorManager.INTERNAL_ERROR_KEY_MSG));
        }
        result.setResult(responseBuilder.create());

        return result;
    }
    @PostMapping(value = "/user/{uid}/refresh")
    public DeferredResult<String> refresh(
            @RequestHeader("api-code") String apiCode,
            @RequestHeader("access-token-smart-car") String token,
            @PathVariable("uid") String uid,
            @RequestParam(value = "auth", defaultValue = "null") String auth,
            @RequestParam(value = "client", defaultValue = "null") String client)
    {
        //TODO do something with uid

        ResponseBuilder responseBuilder = new ResponseBuilder();
        DeferredResult<String> result = new DeferredResult<>();
        firebaseRepository.updateUserApiCall(uid,ApiManager.REFRESH_TOKEN_ENDPOINT);
        if(manageApiCode(apiCode, responseBuilder) || canRequest(uid,ApiManager.REFRESH_TOKEN_ENDPOINT,responseBuilder)) {
            result.setResult(responseBuilder.create());
            return result;
        }
        System.out.println("AUTH: " + auth);
        System.out.println("CLIENT: " + client);

        Gson gson = new Gson();
        AuthClient authClient = gson.fromJson(client,AuthClient.class);
        Auth access = gson.fromJson(auth,Auth.class);
        Auth newAuth = null;
        try {
            newAuth= smartCarRepository.refreshToken(authClient,access.getRefreshToken(),responseBuilder);

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.print("NEWAUTH" + newAuth);
        if (newAuth != null) {
            responseBuilder.setSuccessfulAction(true);
            responseBuilder.add(ApiManager.AUTH, gson.toJson(newAuth));
            responseBuilder.add(ApiManager.AUTH_CLIENT, gson.toJson(authClient));
            result.setResult(responseBuilder.create());
        }
        else if (responseBuilder.getErrorMsg() == null){
            result.setResult(ErrorManager.createErrorResponse(
                    ErrorManager.INTERNAL_ERROR_KEY_CODE,
                    ErrorManager.INTERNAL_ERROR_KEY_MSG));
        }
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

        firebaseRepository.updateUserApiCall(uid,ApiManager.VALIDATE_ENDPOINT);

        if(!manageApiCode(apiCode, responseBuilder) || canRequest(uid,ApiManager.VALIDATE_ENDPOINT,responseBuilder)) {
            try {
                Smartcar.getUser(token);
            } catch (SmartcarException e) {
                e.printStackTrace();
                responseBuilder.setSuccessfulAction(false);
            }
        }
        else
            responseBuilder.setSuccessfulAction(false);
        result.setResult(responseBuilder.create());
        return result;
    }
    @RequestMapping(value = "/user/{uid}/vehicle/{id}/permissions",method = RequestMethod.GET)
    public DeferredResult<String> getPermissions(
            @RequestHeader("api-code") String apiCode,
            @RequestHeader("access-token-smart-car") String token,
            @PathVariable("uid") String uid,
            @PathVariable String id)
    {
        //TODO do something with uid

        ResponseBuilder responseBuilder = new ResponseBuilder();
        DeferredResult<String> result = new DeferredResult<>();
        firebaseRepository.updateUserApiCall(uid,ApiManager.PERMISSIONS_ENDPOINT);
        if(manageApiCode(apiCode, responseBuilder) || canRequest(uid,ApiManager.PERMISSIONS_ENDPOINT,responseBuilder)) {
            result.setResult(responseBuilder.create());
            return result;
        }
        ApplicationPermissions permissions = smartCarRepository.getVehiclePermissions(token,id,responseBuilder);
        if (permissions != null) {
            responseBuilder.add(ApiManager.PERMISSIONS,permissions.getPermissions());
            responseBuilder.setSuccessfulAction(true);
            result.setResult(responseBuilder.create());
        }
        else if (responseBuilder.getErrorMsg() == null){
            result.setResult(ErrorManager.createErrorResponse(
                    ErrorManager.INTERNAL_ERROR_KEY_CODE,
                    ErrorManager.INTERNAL_ERROR_KEY_MSG));
        }
        result.setResult(responseBuilder.create());

        return result;
    }
    @RequestMapping(value = "/user/{uid}/vehicle/{id}/batch",method = RequestMethod.GET)
    public DeferredResult<String> getBatchWithPermissions(
            @RequestHeader("api-code") String apiCode,
            @RequestHeader("access-token-smart-car") String token,
            @PathVariable("uid") String uid,
            @PathVariable("id")String id,
            @RequestBody String body)
    {
        //TODO do something with uid

        ResponseBuilder responseBuilder = new ResponseBuilder();
        String[] paths =new Converter().getListFromJson(token,id,body,responseBuilder);

        DeferredResult<String> result = new DeferredResult<>();
        firebaseRepository.updateUserApiCall(uid,ApiManager.BATCH_ENDPOINT);
        if(manageApiCode(apiCode, responseBuilder) || canRequest(uid,ApiManager.BATCH_ENDPOINT,responseBuilder)) {
            result.setResult(responseBuilder.create());
            return result;
        }
        BatchResponse response = smartCarRepository.getBatch(token,id,paths,responseBuilder);
        if (response != null) {
            result.setResult(responseBuilder.createBatchResponse(response));
        }
        else if (responseBuilder.getErrorMsg() == null){
            result.setResult(ErrorManager.createErrorResponse(
                    ErrorManager.INTERNAL_ERROR_KEY_CODE,
                    ErrorManager.INTERNAL_ERROR_KEY_MSG));
        }
        result.setResult(responseBuilder.create());

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
        firebaseRepository.updateUserApiCall(uid,ApiManager.ODOMETER_ENDPOINT);
        if(manageApiCode(apiCode, responseBuilder) || canRequest(uid,ApiManager.ODOMETER_ENDPOINT,responseBuilder)) {
            result.setResult(responseBuilder.create());
            return result;
        }
        VehicleOdometer vehicleOdometer = smartCarRepository.getVehicleOdometer(token,id,responseBuilder);
        if (vehicleOdometer != null) {
            responseBuilder.add(ApiManager.ODOMETER, vehicleOdometer.getDistance());
            responseBuilder.setSuccessfulAction(true);
            result.setResult(responseBuilder.create());
        }
        else if (responseBuilder.getErrorMsg() == null){
            result.setResult(ErrorManager.createErrorResponse(
                    ErrorManager.INTERNAL_ERROR_KEY_CODE,
                    ErrorManager.INTERNAL_ERROR_KEY_MSG));
        }
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
        firebaseRepository.updateUserApiCall(uid,ApiManager.VIN_ENDPOINT);
        if(manageApiCode(apiCode, responseBuilder) || canRequest(uid,ApiManager.VIN_ENDPOINT,responseBuilder)) {
            result.setResult(responseBuilder.create());
            return result;
        }
        String vin = smartCarRepository.getVehicleVin(token,id,responseBuilder);
        if (vin != null) {
            responseBuilder.add(ApiManager.VIN, vin);
            responseBuilder.setSuccessfulAction(true);
            result.setResult(responseBuilder.create());
        }
        else if (responseBuilder.getErrorMsg() == null){
            result.setResult(ErrorManager.createErrorResponse(
                    ErrorManager.INTERNAL_ERROR_KEY_CODE,
                    ErrorManager.INTERNAL_ERROR_KEY_MSG));
        }
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
        firebaseRepository.updateUserApiCall(uid,ApiManager.VEHICLE_RANGE_ENDPOINT);
        if(manageApiCode(apiCode, responseBuilder) || canRequest(uid,ApiManager.VEHICLE_RANGE_ENDPOINT,responseBuilder)) {
            result.setResult(responseBuilder.create());
            return result;
        }
        Object range = smartCarRepository.getVehicleRange(token,id,responseBuilder);
        if (range != null) {
            if (range instanceof VehicleBattery) {
                responseBuilder.add(ApiManager.RANGE_PERCENT, ((VehicleBattery) range).getPercentRemaining());
                responseBuilder.add(ApiManager.RANGE_RADIUS, ((VehicleBattery) range).getRange());
                responseBuilder.add(ApiManager.RANGE_AMOUNT, "Unavailable");
            } else if (range instanceof VehicleFuel) {
                responseBuilder.add(ApiManager.RANGE_PERCENT, ((VehicleFuel) range).getPercentRemaining());
                responseBuilder.add(ApiManager.RANGE_RADIUS, ((VehicleFuel) range).getRange());
                responseBuilder.add(ApiManager.RANGE_AMOUNT, ((VehicleFuel) range).getAmountRemaining());
            }
            responseBuilder.setSuccessfulAction(true);
            result.setResult(responseBuilder.create());
        }
        else if (responseBuilder.getErrorMsg() == null){
            result.setResult(ErrorManager.createErrorResponse(
                    ErrorManager.INTERNAL_ERROR_KEY_CODE,
                    ErrorManager.INTERNAL_ERROR_KEY_MSG));
        }
        result.setResult(responseBuilder.create());
        return result;
    }
    @RequestMapping(value = "/user/{uid}/vehicle/{id}/lock",method = RequestMethod.POST)
    public DeferredResult<String> lock(
            @RequestHeader("api-code") String apiCode,
            @RequestHeader("access-token-smart-car") String token,
            @PathVariable("uid") String uid,
            @PathVariable String id)
    {
        //TODO do something with uid

        ResponseBuilder responseBuilder = new ResponseBuilder();
        DeferredResult<String> result = new DeferredResult<>();
        firebaseRepository.updateUserApiCall(uid,ApiManager.LOCK_ENDPOINT);
        if(manageApiCode(apiCode, responseBuilder) || canRequest(uid,ApiManager.LOCK_ENDPOINT,responseBuilder)) {
            result.setResult(responseBuilder.create());
            return result;
        }
        ActionResponse actionResponse = smartCarRepository.lock(token,id,responseBuilder);
        if (actionResponse != null) {
            responseBuilder.add(ApiManager.ACTION_MSG, actionResponse.getMessage());
            responseBuilder.setSuccessfulAction(actionResponse.getStatus().equals("success"));
            result.setResult(responseBuilder.create());
        }
        else if (responseBuilder.getErrorMsg() == null){
            result.setResult(ErrorManager.createErrorResponse(
                    ErrorManager.INTERNAL_ERROR_KEY_CODE,
                    ErrorManager.INTERNAL_ERROR_KEY_MSG));
        }
        result.setResult(responseBuilder.create());

        return result;
    }

    @RequestMapping(value = "/user/{uid}/vehicle/{id}/unlock",method = RequestMethod.POST)
    public DeferredResult<String> unlock(
            @RequestHeader("api-code") String apiCode,
            @RequestHeader("access-token-smart-car") String token,
            @PathVariable("uid") String uid,
            @PathVariable String id)
    {
        //TODO do something with uid

        ResponseBuilder responseBuilder = new ResponseBuilder();
        DeferredResult<String> result = new DeferredResult<>();
        firebaseRepository.updateUserApiCall(uid,ApiManager.UNLOCK_ENDPOINT);
        if(manageApiCode(apiCode, responseBuilder) || canRequest(uid,ApiManager.UNLOCK_ENDPOINT,responseBuilder)) {
            result.setResult(responseBuilder.create());
            return result;
        }
        ActionResponse actionResponse = smartCarRepository.unlock(token,id,responseBuilder);
        if (actionResponse != null) {
            responseBuilder.add(ApiManager.ACTION_MSG, actionResponse.getMessage());
            responseBuilder.setSuccessfulAction(actionResponse.getStatus().equals("success"));
            result.setResult(responseBuilder.create());
        }
        else if (responseBuilder.getErrorMsg() == null){
            result.setResult(ErrorManager.createErrorResponse(
                    ErrorManager.INTERNAL_ERROR_KEY_CODE,
                    ErrorManager.INTERNAL_ERROR_KEY_MSG));
        }
        result.setResult(responseBuilder.create());

        return result;
    }
    @Scheduled(cron="0 0 0 * * ?", zone="Europe/Berlin")
    public void methodC() {
        firebaseRepository.resetAllUserRequests();
    }
    @RequestMapping(value = "/user/{uid}/vehicle/{id}/car_market_value",method = RequestMethod.GET)
    public DeferredResult<String> getMarketValue(
            @RequestHeader("api-code") String apiCode,
            @RequestHeader("access-token-smart-car") String token,
            @PathVariable("uid") String uid,
            @PathVariable String id)
    {
        //TODO do something with uid

        ResponseBuilder responseBuilder = new ResponseBuilder();
        DeferredResult<String> result = new DeferredResult<>();
        firebaseRepository.updateUserApiCall(uid,ApiManager.MARKET_VALUE_ENDPOINT);
        if(manageApiCode(apiCode, responseBuilder) || canRequest(uid,ApiManager.MARKET_VALUE_ENDPOINT,responseBuilder)) {
            result.setResult(responseBuilder.create());
            return result;
        }
        String vin =smartCarRepository.getVehicleVin(token,id,responseBuilder);
        CarsXERepository.getInstance().getMarketValue(vin, new CarsXERepository.CallbackCars() {
            @Override
            public void getResult(ContentPackage result1) {

                if (result1 != null){
                    if (result1.getValue() != null && result1.getValue() instanceof CarMarketValue){
                        System.out.println("CODES" + ((CarMarketValue)result1.getValue()).toJson().toString());
                        responseBuilder.add(ApiManager.CAR_MARKET_VALUE_ENDPOINT, ((CarMarketValue)result1.getValue()).toJson().toString());
                        responseBuilder.setSuccessfulAction(true);
                        result.setResult(responseBuilder.create());



                    }
                    if (result1.getException() != null){
                        result.setResult(ErrorManager.createErrorResponse(
                                ErrorManager.INTERNAL_ERROR_KEY_CODE,
                                result1.getException().getMessage()));
                    }
                }
                else{
                    result.setResult(ErrorManager.createErrorResponse(
                            ErrorManager.INTERNAL_ERROR_KEY_CODE,
                            ErrorManager.INTERNAL_ERROR_KEY_MSG));
                    result.setResult(responseBuilder.create());

                }
            }
        });

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
        firebaseRepository.updateUserApiCall(uid,ApiManager.IS_ELECTRIC_ENDPOINT);
        if(manageApiCode(apiCode, responseBuilder) || canRequest(uid,ApiManager.CAR_ATTRIBUTE_ENDPOINT,responseBuilder)) {
            result.setResult(responseBuilder.create());
            return result;
        }
        Object range = smartCarRepository.getVehicleRange(token,id,responseBuilder);
        if (range != null) {
            if (range instanceof VehicleBattery) {
                responseBuilder.add(ApiManager.IS_ELECTRIC, true);

            } else if (range instanceof VehicleFuel) {
                responseBuilder.add(ApiManager.IS_ELECTRIC, false);

            }
            responseBuilder.setSuccessfulAction(true);
            result.setResult(responseBuilder.create());
        }
        else if (responseBuilder.getErrorMsg() == null){
            result.setResult(ErrorManager.createErrorResponse(
                    ErrorManager.INTERNAL_ERROR_KEY_CODE,
                    ErrorManager.INTERNAL_ERROR_KEY_MSG));
        }
        result.setResult(responseBuilder.create());

        return result;
    }
    @RequestMapping(value = "/user/{uid}",method = RequestMethod.GET)
    public DeferredResult<String> getUser(
            @RequestHeader("api-code") String apiCode,
            @RequestHeader("access-token-smart-car") String token,
            @PathVariable("uid") String uid)
    {
        //TODO do something with uid

        ResponseBuilder responseBuilder = new ResponseBuilder();
        DeferredResult<String> result = new DeferredResult<>();
        firebaseRepository.updateUserApiCall(uid,ApiManager.GET_USER_ENDPOINT);
        if(manageApiCode(apiCode, responseBuilder) || canRequest(uid,ApiManager.GET_USER_ENDPOINT,responseBuilder)) {
            result.setResult(responseBuilder.create());
            return result;
        }
        firebaseRepository.getUserViaId(uid, new Callback<User>() {
            @Override
            public void value(User value) {
                if (value != null){
                    responseBuilder.add(ApiManager.USER,value.toJson());
                    responseBuilder.setSuccessfulAction(true);
                    result.setResult(responseBuilder.create());
                }
                else{
                    result.setResult(ErrorManager.createErrorResponse(
                            ErrorManager.INTERNAL_ERROR_KEY_CODE,
                            ErrorManager.INTERNAL_ERROR_KEY_MSG));
                }
            }

            @Override
            public void exception(Exception e) {
                result.setResult(ErrorManager.createErrorResponse(
                        ErrorManager.INTERNAL_ERROR_KEY_CODE,
                        e.getMessage()));
            }
        });
        return result;
    }
    @RequestMapping(value = "/user/",method = RequestMethod.POST)
    public DeferredResult<String> addUser(
            @RequestHeader("api-code") String apiCode,
            @RequestHeader("access-token-smart-car") String token,
            @RequestBody String body)
    {
        //TODO do something with uid

        ResponseBuilder responseBuilder = new ResponseBuilder();
        DeferredResult<String> result = new DeferredResult<>();
        if(manageApiCode(apiCode, responseBuilder)) {
            result.setResult(responseBuilder.create());
            return result;
        }
        firebaseRepository.addUser((User)Converter.convertUser(body), new Callback<User>() {
            @Override
            public void value(User value) {
                if (value != null){
                    responseBuilder.add(ApiManager.USER,value.toJson());
                    responseBuilder.setSuccessfulAction(true);
                    result.setResult(responseBuilder.create());
                }
                else{

                    result.setResult(ErrorManager.createErrorResponse(
                            ErrorManager.INTERNAL_ERROR_KEY_CODE,
                            ErrorManager.INTERNAL_ERROR_KEY_MSG));
                }

            }

            @Override
            public void exception(Exception e) {

                result.setResult(ErrorManager.createErrorResponse(
                        ErrorManager.INTERNAL_ERROR_KEY_CODE,
                        e.getMessage()));
            }
        });
        return result;
    }
    @RequestMapping(value = "/user/{uid}/update",method = RequestMethod.POST)
    public DeferredResult<String> updateUser(
            @RequestHeader("api-code") String apiCode,
            @RequestHeader("access-token-smart-car") String token,
            @PathVariable("uid") String uid,
            @RequestBody String body)
    {
        //TODO do something with uid

        ResponseBuilder responseBuilder = new ResponseBuilder();
        DeferredResult<String> result = new DeferredResult<>();
        firebaseRepository.updateUserApiCall(uid,ApiManager.MARKET_VALUE_ENDPOINT);
        if(manageApiCode(apiCode, responseBuilder) || canRequest(uid,ApiManager.MARKET_VALUE_ENDPOINT,responseBuilder)) {
            result.setResult(responseBuilder.create());
            return result;
        }
        firebaseRepository.updateUser((User)Converter.convertJson(body,User.class), new Callback<User>() {
            @Override
            public void value(User value) {
                if (value != null){
                    responseBuilder.add(ApiManager.USER,value.toJson());
                    responseBuilder.setSuccessfulAction(true);
                    result.setResult(responseBuilder.create());
                }
                else{
                    result.setResult(ErrorManager.createErrorResponse(
                            ErrorManager.INTERNAL_ERROR_KEY_CODE,
                            ErrorManager.INTERNAL_ERROR_KEY_MSG));
                }
            }

            @Override
            public void exception(Exception e) {
                result.setResult(ErrorManager.createErrorResponse(
                        ErrorManager.INTERNAL_ERROR_KEY_CODE,
                        e.getMessage()));
            }
        });
        return result;
    }



    private Boolean manageApiCode(String apiCode, ResponseBuilder responseBuilder) {
        if(!apiHelper.isValid(apiCode)){
            System.out.println("Api COde not Valid");
            createGeneralError(responseBuilder);
            return true;

        }
        return false;
    }

    private void createGeneralError(ResponseBuilder responseBuilder) {
        responseBuilder.setSuccessfulAction(false);
        responseBuilder.setErrorCode(ErrorManager.INVALID_API_KEY_CODE);
        responseBuilder.setErrorMsg(ErrorManager.INVALID_API_KEY_MSG);
    }
}
