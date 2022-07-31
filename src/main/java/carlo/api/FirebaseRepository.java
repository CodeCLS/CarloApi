package carlo.api;

import com.google.firebase.database.ValueEventListener;

public class FirebaseRepository {
    private FirebaseManager manager = new FirebaseManager();

    public FirebaseRepository() {
    }

    public void updateUserApiCall(String uid, String apiCallType) {
        manager.noteApiCall(uid,apiCallType);

    }
    public void getUserApiCallAmount(String uid, String apiCallType, ValueEventListener callback){
        manager.getUserApiCallAmount(uid, apiCallType,callback);
    }

    public void getUserViaId(String uid, Callback<User> callback) {
        manager.getUserFromDatabase(uid,callback);
    }

    public void addUser(User convertJson, Callback<User> callback) {
        manager.createUserAuth(convertJson.getEmail(), convertJson.getFirstName(), convertJson.getSecondName(), new Callback<String>() {
            @Override
            public void value(String uid) {
                convertJson.setUid(uid);
                if (uid != null){
                    manager.createUserDb(convertJson,callback);
                }
                else{
                    callback.exception(new Exception("Failed to create uid"));
                }

            }

            @Override
            public void exception(Exception e) {
                System.out.println("TEST IIIIIIIVVVV");

                callback.exception(e);
            }
        });

    }

    public void updateUser(User convertJson, Callback<User> callback) {
        manager.updateUserDb(convertJson,callback);
    }

    public void resetAllUserRequests() {
        manager.resetUserRequestsDatabase();
    }
}
