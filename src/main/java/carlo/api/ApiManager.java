package carlo.api;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ApiManager {
    public static final String VEHICLE_ID = "vehicle_id";
    public static final String VEHICLE_MAKE = "vehicle_make";
    public static final String VEHICLE_MODEL = "vehicle_model";
    public static final String VEHICLE_YEAR = "vehicle_year";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String AUTH_CLIENT = "auth_client";
    public static final String AUTH = "auth";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String VEHICLE_IDS = "vehicle_ids";
    public static final String ODOMETER = "odometer";
    public static final String VIN = "vin";
    public static final String RANGE_PERCENT = "range_percent";
    public static final String RANGE_RADIUS = "radius";
    public static final String RANGE_AMOUNT = "product_amount";
    public static final String ACTION_MSG = "status_msg";
    public static final String IS_ELECTRIC = "is_electric";
    public static final String SUCCESSFUL_ACTION = "successful_action";
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR_CODE = "error_code";
    public static final String USER = "user";
    public static final String PERMISSIONS = "permissions";
    public static final String BATCH = "batch";
    public static final List<String> BATCH_ARRAY_ALL = Arrays.asList("/location", "/odometer", "/attributes");
    public static final String SELECTION_BATCH = "selection";
    public static final String REFRESH_TOKEN = "refresh_token";
}
