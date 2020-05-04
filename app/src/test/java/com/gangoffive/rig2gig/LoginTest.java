//package com.gangoffive.rig2gig;
//
//import androidx.annotation.NonNull;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.iid.FirebaseInstanceId;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//
//public class LoginTest{
//    private CountDownLatch authSignal = null;
//    private FirebaseAuth auth;
//    private FirebaseFirestore fStore;
//
//
//    @Before
//    public void setUp() throws InterruptedException {
//        authSignal = new CountDownLatch(1);
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        auth = FirebaseAuth.getInstance();
//        if(auth.getCurrentUser() == null) {
//            auth.signInWithEmailAndPassword("test@test.com", "Password123").addOnCompleteListener(
//                    new OnCompleteListener<AuthResult>() {
//
//                        @Override
//                        public void onComplete(@NonNull final Task<AuthResult> task) {
//
//                            final AuthResult result = task.getResult();
//                            final FirebaseUser user = result.getUser();
//                            authSignal.countDown();
//                        }
//                    });
//        } else {
//            authSignal.countDown();
//        }
//        authSignal.await(10, TimeUnit.SECONDS);
//    }
//
//    @Test
//    public void tearDown() throws Exception {
//        if(auth != null) {
//            auth.signOut();
//            auth = null;
//        }
//    }
//
//    @Test
//    public void testWrite() throws InterruptedException {
//        final CountDownLatch writeSignal = new CountDownLatch(1);
//        fStore = FirebaseFirestore.getInstance();
//        DocumentReference doc = fStore.collection("users").document("MuyFooIIkAb4ni1ABgynU51qSlk1");
//        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                writeSignal.countDown();
//            }
//        });
//        writeSignal.await(10, TimeUnit.SECONDS);
//    }
//}