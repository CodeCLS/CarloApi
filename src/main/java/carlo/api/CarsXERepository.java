package carlo.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class CarsXERepository {

    private static CarsXEService service;
    private static final String KEY ="u1x59frkq_f1by967iw_j7avd3rqn" ;
    private static CarsXERepository instance;

    public static CarsXERepository getInstance(){
        if (instance == null) {
            instance = new CarsXERepository();

        }
        return instance;
    }

    public CarsXERepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.carsxe.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(CarsXEService.class);
    }

    public void getMarketValue(String vin, CallbackCars callback){
        ContentPackage contentPackage = new ContentPackage();

        service.getMarketValue(KEY,vin).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        String body = null;
                        if (response.body() != null) {
                            body = response.body().string();
                        }
                        if (body != null){
                            contentPackage.setValue(new Converter().convertMarketValueCar(body));
                            callback.getResult(contentPackage);

                        }
                        else{
                            contentPackage.setException(new Exception("Body was null CarsXE"));
                            callback.getResult(contentPackage);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        contentPackage.setException(e);
                        callback.getResult(contentPackage);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                contentPackage.setException(new Exception(t.getMessage()));
                callback.getResult(contentPackage);

            }
        });
    }
    public interface CallbackCars{
        void getResult(ContentPackage result);

    }

    //Get http://api.carsxe.com/marketvalue?key=<CarsXE_API_Key>&vin=1FT8X3BT0BEA61538&format=json
}
