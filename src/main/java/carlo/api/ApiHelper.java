package carlo.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ApiHelper {
    private ArrayList<String> apiCodes = new ArrayList<String>(Collections.singletonList("oaiu3490S=?nD2103_qß094Pn-+0"));

    public boolean isValid(String apiCode) {
        return apiCodes.contains(apiCode);
    }
}
