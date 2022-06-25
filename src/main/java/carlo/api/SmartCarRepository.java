package carlo.api;

import com.smartcar.sdk.AuthClient;
import com.smartcar.sdk.Smartcar;
import com.smartcar.sdk.SmartcarException;
import com.smartcar.sdk.Vehicle;
import com.smartcar.sdk.data.*;

public class SmartCarRepository {
    String clientId = "3b683bb7-48a3-4b4c-8a2f-7337a8a0ee19";
    String clientSecret = "bdefd1ee-de23-4df5-aa61-c9ef1d6ac724";
    String redirectUri = "sc3b683bb7-48a3-4b4c-8a2f-7337a8a0ee19://myapp.com/callback";
    boolean testMode = true;


    public SmartCarRepository() {
    }

    public Vehicle getVehicle(String accessToken,String id){
        return new Vehicle(id,accessToken);
    }

    public VehicleAttributes getVehicleAttributes(String token, String vehicleId) {
        try {
            return getVehicle(token,vehicleId).attributes();
        } catch (SmartcarException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Auth exchangeAuth(String token,AuthClient authClient) {
        try {
            return authClient.exchangeCode(token);
        } catch (SmartcarException e) {
            e.printStackTrace();
            return null;
        }
    }
    public AuthClient createAuthClient() {
        AuthClient authClient = null;
        try {
            authClient = new AuthClient.Builder()
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .redirectUri(redirectUri)
                    .testMode(testMode).build();
            return authClient;

        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    public VehicleLocation getVehicleLocation(String token, String id) {
        try {
            return getVehicle(token,id).location();
        } catch (SmartcarException e) {
            e.printStackTrace();
            return null;
        }
}

    public VehicleIds getVehicles(String access) {
        try {
            return Smartcar.getVehicles(access);
        } catch (SmartcarException e) {
            e.printStackTrace();
            return null;
        }

    }

    public Auth refreshToken(AuthClient authClient, Auth access) {
        try {
            return authClient.exchangeRefreshToken(access.getRefreshToken());
        } catch (SmartcarException e) {
            e.printStackTrace();
            return null;
        }
    }

    public VehicleOdometer getVehicleOdometer(String token, String id) {
        try {
            return getVehicle(token,id).odometer();
        } catch (SmartcarException e) {
            e.printStackTrace();
            return null;
        }
}

    public String getVehicleVin(String token, String id) {
        try {
            return getVehicle(token,id).vin().getVin();
        } catch (SmartcarException e) {
            e.printStackTrace();
            return null;
        }
}

    public Object getVehicleRange(String token, String id) {
        try {
            return getVehicle(token,id).fuel();
        } catch (SmartcarException e) {
            try{
                return getVehicle(token,id).battery();
            }
            catch (Exception e2){
                e2.printStackTrace();
            }
            return null;
        }
    }

    public ActionResponse lock(String token, String id) {
        try {
            return getVehicle(token,id).lock();
        } catch (SmartcarException e) {
            e.printStackTrace();
            return null;
        }
    }
    public ActionResponse unlock(String token, String id) {
        try {
            return getVehicle(token,id).unlock();
        } catch (SmartcarException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ApplicationPermissions getVehiclePermissions(String token, String id) {
        try {
            return getVehicle(token,id).permissions();
        } catch (SmartcarException e) {
            e.printStackTrace();
            return null;
        }
    }

    public BatchResponse getBatch(String token, String id, String[] paths) {
        try {
            return getVehicle(token,id).batch(paths);
        } catch (SmartcarException e) {
            e.printStackTrace();
            return null;
        }
    }
}
