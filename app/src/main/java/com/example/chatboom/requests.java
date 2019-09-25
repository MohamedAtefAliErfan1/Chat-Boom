package com.example.chatboom;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link requests.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link requests#newInstance} factory method to
 * create an instance of this fragment.
 */
public class requests extends Fragment {

    String mcur_user;
    //    String req_user;
    private RecyclerView recyclerView;
    //    private Query mreqref;
    private FirebaseAuth mfirebaseAuth;
    private DatabaseReference reqroot;
    private DatabaseReference friends;
    private DatabaseReference friend_request;
    private DatabaseReference mUsersDatabase;
    private FirebaseRecyclerAdapter mFirebaseRecyclerAdapter;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public requests() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment requests.
     */
    // TODO: Rename and change types and number of parameters
    public static requests newInstance(String param1, String param2) {
        requests fragment = new requests();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_requests, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.reqRlist);
        mfirebaseAuth = FirebaseAuth.getInstance();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference("users");
        mcur_user = mfirebaseAuth.getCurrentUser().getUid();
        reqroot = FirebaseDatabase.getInstance().getReference().child("friend_req").child(mcur_user);
        friend_request = FirebaseDatabase.getInstance().getReference().child("friend_req");
        friends = FirebaseDatabase.getInstance().getReference().child("friends");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Reqmodel> options =
                new FirebaseRecyclerOptions.Builder<Reqmodel>()
                        .setQuery(reqroot, Reqmodel.class)
                        .setLifecycleOwner(this)
                        .build();
        mFirebaseRecyclerAdapter =new FirebaseRecyclerAdapter<Reqmodel,ReqHolder>(options) {
            @NonNull
            @Override
            public ReqHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new ReqHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.req_item, viewGroup, false));
            }
            //
            @Override
            protected void onBindViewHolder(@NonNull final ReqHolder holder, int position, @NonNull Reqmodel model) {
                final String   req_user = getRef(position).getKey();
                final DatabaseReference get_type_ref=getRef(position).child("req_stat").getRef();
//
                get_type_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String req_type=dataSnapshot.getValue().toString();
                            if (req_type.equals("received")){
                                mUsersDatabase.child(req_user).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        String userName = dataSnapshot.child("name").getValue().toString();
                                        String userThumb = dataSnapshot.child("image").getValue().toString();


                                        holder.setName(userName);
                                        holder.setUserImage(userThumb);
                                        holder.approve(mcur_user,req_user);
                                        holder.reject(mcur_user,req_user);



                                    }

                                    @Override
                                    public void onCancelled(DatabaseError dataError) {
                                        Toast.makeText(getContext(),"Error at "+ dataError.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            else if (req_type.equals("sent")){
                                mUsersDatabase.child(req_user).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        String userName = dataSnapshot.child("name").getValue().toString();
                                        String userThumb = dataSnapshot.child("image").getValue().toString();

//
                                        holder.setName(userName);
                                        holder.setUserImage(userThumb);
                                        holder.remove_req(mcur_user,req_user);
//
//
//
//
                                    }
                                    //
                                    @Override
                                    public void onCancelled(DatabaseError dataError) {
                                        Toast.makeText(getContext(), "Error in "+dataError.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                        else { Toast.makeText(getContext(),"List is empty",Toast.LENGTH_SHORT).show();}
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(),"Error in "+ databaseError.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });

            }

        };
        recyclerView.setAdapter(mFirebaseRecyclerAdapter);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private class ReqHolder  extends RecyclerView.ViewHolder{
        View mView;
        public ReqHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setName(final String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.req_name_layout);
            userNameView.setText(name);

        }
        public void setUserImage( final String thumb_image){

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.req_profile_layout);
            Picasso.get().load(thumb_image).placeholder(R.drawable.profile).into(userImageView);

        }
        public void approve(final String mcurrent, final String user_id){
            Button req_approve_btn=(Button)mView.findViewById(R.id.req_approve_btn);
            req_approve_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String curdate= DateFormat.getDateTimeInstance().format(new Date());
                    friends.child(mcurrent).child(user_id).child("date").setValue(curdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friends.child(user_id).child(mcurrent).child("date").setValue(curdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    friend_request.child(mcurrent).child(user_id).child("req_stat").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                friend_request.child(user_id).child(mcurrent).child("req_stat").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {



                                                    }
                                                });

                                            }

                                        }
                                    });
                                }
                            });
                        }
                    });

                }
            });
        }
        public void reject(final String mcurrent, final String user_id){
            Button req_Reject_btn=(Button)mView.findViewById(R.id.req_Reject_btn);
            req_Reject_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    friend_request.child(mcurrent).child(user_id).child("req_stat").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                friend_request.child(user_id).child(mcurrent).child("req_stat").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {



                                    }
                                });

                            }
                            else {
                                Toast.makeText(getContext(),"failed to reject request",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });
        }
        public void remove_req(final String mcurrent, final String user_id){
            Button req_Reject_btn=(Button)mView.findViewById(R.id.req_Reject_btn);
            Button req_approve_btn=(Button)mView.findViewById(R.id.req_approve_btn);
            req_Reject_btn.setVisibility(View.INVISIBLE);
            req_approve_btn.setText("Cancel");
            req_approve_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    friend_request.child(mcurrent).child(user_id).child("req_stat").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                friend_request.child(user_id).child(mcurrent).child("req_stat").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {



                                    }
                                });

                            }
                            else {
                                Toast.makeText(getContext(),"failed to delete request",Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
            });

        }

    }
}
