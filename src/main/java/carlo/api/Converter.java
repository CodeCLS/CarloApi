package carlo.api;

import com.google.gson.Gson;
import com.smartcar.sdk.data.Auth;

public class Converter implements ParserTool {

    @Override
    public static String doTask(Object value) {
        Gson gson = new Gson();
        return gson.toJson(value);


    }


}
