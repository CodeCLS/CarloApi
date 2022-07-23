package carlo.api;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.database.*;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FirebaseManager {
    public static final String UID = "uid";
    public static final String FIRST_NAME = "first_name";
    public static final String SECOND_NAME = "second_name";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";
    public static final String BIRTHDAY = "birthday";
    public static final String BILLING_TYPE = "billing_type";
    public static final String VEHICLE_IDS = "vehicle_ids";
    public static final String SMARTCAR_ID = "smartcar_id";

    FirebaseOptions optionsUserDatabase = null;
    FirebaseApp defaultApp;
    FirebaseAuth userAuth;
    FirebaseDatabase userDatabase;
    private DatabaseReference userRef;
    ExecutorService executors = Executors.newFixedThreadPool(4);

    public FirebaseManager() {
        try {
            optionsUserDatabase = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.getApplicationDefault())
                    .setDatabaseUrl("https://simplecar-users.europe-west1.firebasedatabase.app/")
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        defaultApp = FirebaseApp.initializeApp(optionsUserDatabase);
        init();
    }

    private void init() {
        userAuth = FirebaseAuth.getInstance(defaultApp);
        userDatabase = FirebaseDatabase.getInstance("https://simplecar-users.europe-west1.firebasedatabase.app/");
        userRef = userDatabase.getReference();

    }

    public UserRecord createUserAuth(String email, String firstName, String secondName, Callback<String> callback){
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setEmailVerified(false)
                .setDisplayName(firstName + " " + secondName)
                .setDisabled(false);

        UserRecord userRecord = null;
        try {
            userRecord = FirebaseAuth.getInstance().createUser(request);
            callback.value(userRecord.getUid());
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            callback.value(userRecord.getUid());
        }
        return userRecord;


    }
    public UserRecord retrieveUserAuthById(String uid, Callback<UserRecord> callback){
        UserRecord userRecord = null;
        try {
            userRecord = FirebaseAuth.getInstance().getUser(uid);
            callback.value(userRecord);
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            callback.value(null);

        }
        return userRecord;

    }
    public UserRecord retrieveUserAuthByEmail(String email, Callback<UserRecord> callback){
        UserRecord userRecord = null;
        try {
            userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
            callback.value(userRecord);
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            callback.value(null);

        }
        return userRecord;

    }
    public UserRecord updateUserAuthById(String uid,
                                         String email,
                                         String phone,
                                         boolean isVerifiedEmail,
                                         String password,
                                         String displayName,
                                         String photoUrl,
                                         boolean isDisabled,
                                         Callback<UserRecord> callback){
        UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid)
                .setEmail(email)
                .setPhoneNumber(phone)
                .setEmailVerified(isVerifiedEmail)
                .setPassword(password)
                .setDisplayName(displayName)
                .setPhotoUrl(photoUrl)
                .setDisabled(isDisabled);

        UserRecord userRecord = null;
        try {
            userRecord = FirebaseAuth.getInstance().updateUser(request);
            callback.value(userRecord);

        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            callback.value(null);

        }
        return userRecord;

    }
    public void deleteUser(String uid, Callback<Boolean> callback){
        try {
            FirebaseAuth.getInstance().deleteUser(uid);
            callback.value(true);
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            callback.value(false);

        }

    }
    public UserRecord revokeRefreshToken(String uid, Callback<UserRecord> callback){
        UserRecord user = null;
        try {
            FirebaseAuth.getInstance().revokeRefreshTokens(uid);
            user = FirebaseAuth.getInstance().getUser(uid);
            callback.value(user);

        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            callback.value(null);
        }
        return user;

    }
    public void addUserToDatabase(User user, Callback<User> callback){
        userRef.setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference ref) {

            }
        });

    }
    public void getUserFromDatabase(String uid, Callback<User> callback){
        userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                callback.value(User.parseUser(snapshot));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.value(null);

            }
        });

    }

    public void createUserDb(User convertJson, Callback<User> callback) {
        userRef.child(convertJson.getUid()).setValue(convertJson.toHash(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                if (error == null){
                    callback.value(convertJson);
                }
                else{
                    callback.value(null);
                }
            }
        });

    }

    public void updateUserDb(User convertJson, Callback<User> callback) {
        userRef.child(convertJson.getUid()).updateChildren(convertJson.toHash(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                if (error == null){
                    callback.value(convertJson);
                }
                else{
                    callback.value(null);
                }
            }
        });
    }
}
