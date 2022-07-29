package carlo.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CarsXEService {
    ///marketvalue?key=<CarsXE_API_Key>&format=json
    @GET("marketvalue?key={key}&vin=1FT8X3BT0BEA61538&format=json")
    Call<ResponseBody> getMarketValue(@Path("key") String key, @Query("vin") String vin);
}
