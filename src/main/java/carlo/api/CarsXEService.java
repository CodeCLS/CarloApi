package carlo.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CarsXEService {
    ///marketvalue?key=<CarsXE_API_Key>&format=json
    @GET("marketvalue?format=json")
    Call<ResponseBody> getMarketValue(@Query("key") String key, @Query("vin") String vin);
}
