package carlo.api;

import com.google.gson.Gson;
import com.smartcar.sdk.data.Auth;

public class Converter implements ParserTool {

    public String doTask(Object value) {
        Gson gson = new Gson();
        return gson.toJson(value);


    }


}
