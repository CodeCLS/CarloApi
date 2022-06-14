package carlo.api;

import com.google.gson.JsonObject;
import com.smartcar.sdk.Smartcar;
import com.smartcar.sdk.SmartcarException;
import com.smartcar.sdk.Vehicle;
import com.smartcar.sdk.data.VehicleAttributes;
import com.smartcar.sdk.data.VehicleIds;

public class SmartCarRepository {
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
}
