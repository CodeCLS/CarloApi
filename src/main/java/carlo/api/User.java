package carlo.api;

import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class User implements JsonAble, HashAble {
    private String uid;
    private String firstName;
    private String secondName;
    private String email;
    private String phone;
    private Long birthday;
    private Integer billingType;
    private ArrayList<String> vehicleIds;
    private String smartCarId;

    public static User parseUser(DataSnapshot snapshot) {
        User user = new User();
        user.setUid(snapshot.child(FirebaseManager.UID).getValue().toString());
        user.setFirstName(snapshot.child(FirebaseManager.FIRST_NAME).getValue().toString());
        user.setSecondName(snapshot.child(FirebaseManager.SECOND_NAME).getValue().toString());
        user.setEmail(snapshot.child(FirebaseManager.EMAIL).getValue().toString());
        user.setPhone(snapshot.child(FirebaseManager.PHONE).getValue().toString());
        user.setBirthday((Long)snapshot.child(FirebaseManager.BIRTHDAY).getValue());
        user.setBillingType((Integer) snapshot.child(FirebaseManager.BILLING_TYPE).getValue());
        snapshot.child(FirebaseManager.VEHICLE_IDS).getChildren().forEach(new Consumer<DataSnapshot>() {
            @Override
            public void accept(DataSnapshot snapshot) {
                user.vehicleIds.add(snapshot.getValue().toString());
            }
        });
        user.setSmartCarId(snapshot.child(FirebaseManager.SMARTCAR_ID).getValue().toString());
        return user;
    }

    public User(String uid, String firstName, String secondName, String email, String phone, Long birthday, Integer billingType, ArrayList<String> vehicleIds, String smartCarId) {
        this.uid = uid;
        this.firstName = firstName;
        this.secondName = secondName;
        this.email = email;
        this.phone = phone;
        this.birthday = birthday;
        this.billingType = billingType;
        this.smartCarId = smartCarId;
    }

    public User() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }


    public Integer getBillingType() {
        return billingType;
    }

    public void setBillingType(Integer billingType) {
        this.billingType = billingType;
    }

    public ArrayList<String> getVehicleIds() {
        return vehicleIds;
    }

    public void setVehicleIds(ArrayList<String> vehicleIds) {
        this.vehicleIds = vehicleIds;
    }

    public String getSmartCarId() {
        return smartCarId;
    }

    public void setSmartCarId(String smartCarId) {
        this.smartCarId = smartCarId;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FirebaseManager.UID,uid);
        jsonObject.addProperty(FirebaseManager.FIRST_NAME,firstName);
        jsonObject.addProperty(FirebaseManager.SECOND_NAME,secondName);
        jsonObject.addProperty(FirebaseManager.EMAIL,email);
        jsonObject.addProperty(FirebaseManager.PHONE,phone);
        jsonObject.addProperty(FirebaseManager.BILLING_TYPE,billingType);
        jsonObject.addProperty(FirebaseManager.BIRTHDAY,birthday);
        if (vehicleIds != null) {
            JsonArray jsonArray = new JsonArray();

            for (String i : vehicleIds) {
                jsonArray.add(i);
            }
            jsonObject.add(FirebaseManager.VEHICLE_IDS,jsonArray);

        }
        jsonObject.addProperty(FirebaseManager.SMARTCAR_ID,smartCarId);


        return jsonObject;
    }
    @Override
    public HashMap<String, Object> toHash() {
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put(FirebaseManager.UID,uid);
        hashMap.put(FirebaseManager.FIRST_NAME,firstName);
        hashMap.put(FirebaseManager.SECOND_NAME,secondName);
        hashMap.put(FirebaseManager.EMAIL,email);
        hashMap.put(FirebaseManager.PHONE,phone);
        hashMap.put(FirebaseManager.BIRTHDAY,birthday);
        hashMap.put(FirebaseManager.BILLING_TYPE,billingType);
        hashMap.put(FirebaseManager.VEHICLE_IDS,vehicleIds);
        hashMap.put(FirebaseManager.SMARTCAR_ID,smartCarId);

        return hashMap;
    }
}
