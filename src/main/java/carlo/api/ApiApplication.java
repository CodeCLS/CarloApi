package carlo.api;

import com.smartcar.sdk.AuthClient;
import com.smartcar.sdk.Smartcar;
import com.smartcar.sdk.data.Auth;
import com.smartcar.sdk.data.VehicleIds;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@SpringBootApplication
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
    @GetMapping("/exchange")
    public DeferredResult<String> exchangeAuthCode(@RequestParam(value = "code", defaultValue = "null") String code) throws Exception {
        DeferredResult<String> output = new DeferredResult<>();
        // Setup
        String clientId = "3b683bb7-48a3-4b4c-8a2f-7337a8a0ee19\n";
        String clientSecret = "bdefd1ee-de23-4df5-aa61-c9ef1d6ac724";
        String redirectUri = "sc3b683bb7-48a3-4b4c-8a2f-7337a8a0ee19://myapp.com/callback";
        String[] scope = {"read_vehicle_info", "read_odometer"};
        boolean testMode = true;

// Initialize a new AuthClient with your credentials.
        AuthClient authClient = new AuthClient.Builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .redirectUri(redirectUri)
                .testMode(testMode).build();


        Auth auth = authClient.exchangeCode(code);
        VehicleIds response = Smartcar.getVehicles(auth.getAccessToken());
        String[] vehicleIds = response.getVehicleIds();
        output.setResult(vehicleIds[0]);


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
