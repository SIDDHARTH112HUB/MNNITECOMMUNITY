package com.arsenal.mnnite_community;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link homeFragment} factory method to
 * create an instance of this fragment.
 */
public class homeFragment extends Fragment {

    private FirebaseAuth firebaseAuth;

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView community_post_view;
    private CommunityRecyclerAdapter communityRecyclerAdapter;

    private List<CommunityPost> communityPostsList;

    public homeFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        communityPostsList = new ArrayList<>();
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        community_post_view=view.findViewById(R.id.communityPostView);

        communityRecyclerAdapter =new CommunityRecyclerAdapter(communityPostsList);

        community_post_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        community_post_view.setAdapter(communityRecyclerAdapter);
        firebaseAuth=FirebaseAuth.getInstance();


        // Inflate the layout for this fragment
        if(firebaseAuth.getCurrentUser()!=null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            firebaseFirestore.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for (DocumentChange doc : value.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            CommunityPost communityPost = doc.getDocument().toObject(CommunityPost.class);
                            communityPostsList.add(communityPost);
                            communityRecyclerAdapter.notifyDataSetChanged();

                        }
                    }
                }
            });


        }
        return view;
    }
}