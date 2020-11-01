package com.arsenal.mnnite_community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommunityRecyclerAdapter extends RecyclerView.Adapter<CommunityRecyclerAdapter.ViewHolder> {

    public Context  context;
    public List<CommunityPost> communityPostList;

    private FirebaseFirestore firebaseFirestore;
    public CommunityRecyclerAdapter(List<CommunityPost> communityPostList){

        this.communityPostList=communityPostList;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.communitylistitem,parent,false);

        context=parent.getContext();

        firebaseFirestore=FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String descData = communityPostList.get(position).getDesc();

        holder.setDesctext(descData);

        String image_url = communityPostList.get(position).getUrl();
        holder.setPostImage(image_url);

        String user_id =communityPostList.get(position).getUser_id();
        

    }

    @Override
    public int getItemCount() {
        return communityPostList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;

        private TextView descView;
        private ImageView postImageView;
        private  TextView postUsername;
        private CircleImageView postuserImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;

        }
        public void setDesctext(String text){
            descView=mView.findViewById(R.id.blog_desc);
            descView.setText(text) ;
        }

        public void setPostImage(String downloadUri){

            postImageView = mView.findViewById(R.id.blog_image);
            Glide.with(context).load(downloadUri).into(postImageView);

        }



    }

}
