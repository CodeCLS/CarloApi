package carlo.api;

import com.smartcar.sdk.AuthClient;
import com.smartcar.sdk.Smartcar;
import com.smartcar.sdk.Vehicle;
import com.smartcar.sdk.data.Auth;
import com.smartcar.sdk.data.VehicleIds;
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

    @GetMapping("/attributes")
    public DeferredResult<String> attributes(@RequestParam(value = "code", defaultValue = "null") String code) throws Exception {
        DeferredResult<String> output = new DeferredResult<>();
        // Setup
        VehicleIds response = Smartcar.getVehicles(code);
        String[] vehicleIds = response.getVehicleIds();
        Vehicle vehicle =new Vehicle(vehicleIds[0],code);
        output.setResult(vehicle.attributes().toString() + " " + vehicle.odometer().getDistance() + " " + vehicle.location() );

        return output;

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
