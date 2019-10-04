package yopy.modutalk.fragment;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;


import yopy.modutalk.MainActivity;
import yopy.modutalk.R;
import yopy.modutalk.chat.ChatActivity;
import yopy.modutalk.common.FirestoreAdapter;
import yopy.modutalk.common.Util9;
import yopy.modutalk.model.UserModel;



import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import com.google.android.gms.ads.MobileAds;

import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.mediation.customevent.CustomEventAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.google.firebase.firestore.FirebaseFirestore.getInstance;

public class UserListFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirestoreAdapter firestoreAdapter;
    private InterstitialAd mInterstitialAd;
    private FirebaseFirestore mFireStore;
    private List usersList;
    private Query mQuery;
    private DocumentReference mUserDataBase;
    private RecyclerView mResultList;
    ImageButton mSearchBtn;
    EditText mSearchField;
    String name;
//private ArrayList<DocumentSnapshot> Users = new ArrayList<>();

    public UserListFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getInstance();


        MobileAds.initialize(getContext(),
                "ca-app-pub-9778515385069911~5724000209");



        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-9778515385069911/6935700019");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
                                          @Override
                                          public void onAdClosed() {
                                              // Load the next interstitial.
                                              mInterstitialAd.loadAd(new AdRequest.Builder().build());
                                          }
                                      });
       // MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
        //    @Override
         //   public void onInitializationComplete(InitializationStatus initializationStatus) {
          //  }

      //  });


    }


    @Override
    public void onStart() {
        super.onStart();
        if (firestoreAdapter != null) {
            firestoreAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (firestoreAdapter != null) {
            firestoreAdapter.stopListening();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userlist, container, false);

        firestoreAdapter = new RecyclerViewAdapter(FirebaseFirestore.getInstance().collection("users").orderBy("usernm"));

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager( new LinearLayoutManager((inflater.getContext())));
        recyclerView.setAdapter(firestoreAdapter);

        return view;
    }

    class RecyclerViewAdapter extends FirestoreAdapter<CustomViewHolder> {
        final private RequestOptions requestOptions = new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(90));
        private StorageReference storageReference;
        private String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        RecyclerViewAdapter(Query query) {
            super(query);
            storageReference  = FirebaseStorage.getInstance().getReference();
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CustomViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user, parent, false));
        }

        @Override
        public void onBindViewHolder(CustomViewHolder viewHolder, int position) {
            DocumentSnapshot documentSnapshot = getSnapshot(position);
            final UserModel user = documentSnapshot.toObject(UserModel.class);

            if (myUid.equals(user.getUid())) {
                viewHolder.itemView.setVisibility(View.INVISIBLE);
                viewHolder.itemView.getLayoutParams().height = 0;
                return;
            }
            viewHolder.user_name.setText(user.getUsernm());
            viewHolder.user_msg.setText(user.getUsermsg());

            if (user.getUserphoto()==null) {
                Glide.with(getActivity()).load(R.drawable.user)
                        .apply(requestOptions)
                        .into(viewHolder.user_photo);
            } else{
                Glide.with(getActivity())
                        .load(storageReference.child("userPhoto/"+user.getUserphoto()))
                        .apply(requestOptions)
                        .into(viewHolder.user_photo);
            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getView().getContext(), ChatActivity.class);
                    intent.putExtra("toUid", user.getUid());
                    startActivity(intent);
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        Log.d("TAG","광고 로드 실패");
                    }
                }
            });

        }
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public ImageView user_photo;
        public TextView user_name;
        public TextView user_msg;

        CustomViewHolder(View view) {
            super(view);
            user_photo = view.findViewById(R.id.user_photo);
            user_name = view.findViewById(R.id.user_name);
            user_msg = view.findViewById(R.id.user_msg);
        }
    }
}