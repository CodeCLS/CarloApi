package carlo.api;

public class FirebaseRepository {
    private FirebaseManager manager = new FirebaseManager();

    public FirebaseRepository() {
    }

    public void updateUserApiCall() {

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
                    callback.value(null);

                }

            }
        });

    }

    public void updateUser(User convertJson, Callback<User> callback) {
        manager.updateUserDb(convertJson,callback);
    }
}
