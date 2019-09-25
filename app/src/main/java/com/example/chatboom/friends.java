package com.example.chatboom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link friends.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link friends#newInstance} factory method to
 * create an instance of this fragment.
 */
public class friends extends Fragment {
    private RecyclerView friend_recycler;
    FirebaseRecyclerAdapter mFirebaseRecyclerAdapter;


    private FirebaseAuth mfirebaseAuth;
    private DatabaseReference mfrienddb;
    private DatabaseReference mfriends;
    private DatabaseReference musers;
    String mcur_id;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public friends() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment friends.
     */
    // TODO: Rename and change types and number of parameters
    public static friends newInstance(String param1, String param2) {
        friends fragment = new friends();
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
        View view =inflater.inflate(R.layout.fragment_friends, container, false);


        friend_recycler=(RecyclerView) view.findViewById(R.id.friend_recycler);
        mfirebaseAuth=FirebaseAuth.getInstance();
        mcur_id=mfirebaseAuth.getCurrentUser().getUid();
        mfrienddb= FirebaseDatabase.getInstance().getReference().child("friends").child(mcur_id);
        mfriends= FirebaseDatabase.getInstance().getReference().child("friends");
        musers= FirebaseDatabase.getInstance().getReference().child("users");
        friend_recycler.setHasFixedSize(true);
        friend_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<friend> options=
                new FirebaseRecyclerOptions.Builder<friend>()
                        .setQuery(mfrienddb,friend.class)
                        .setLifecycleOwner(this)
                        .build();
        mFirebaseRecyclerAdapter= new FirebaseRecyclerAdapter<friend,friends.Friends_holder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final Friends_holder holder, int position, @NonNull final friend model) {
                holder.setdate(model.getDate());
                final String user_list_id=getRef(position).getKey();
                musers.child(user_list_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String name=dataSnapshot.child("name").getValue().toString();
                        final String img_url=dataSnapshot.child("image").getValue().toString();
                        if (dataSnapshot.hasChild("online")){

                            String online_status=dataSnapshot.child("online").getValue().toString();

                            holder.setOnline(online_status);
                        }
                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), Chat.class);
                                intent.putExtra("user_id", user_list_id);
                                intent.putExtra("user_name", name);
                                intent.putExtra("user_img", img_url);
                                startActivity(intent);
                            }
                        });
                        final CharSequence options[]=new CharSequence[]{"Show profile","Remove"};
                        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                                builder.setTitle("Select option")
                                        .setItems(options, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (which==0) {

                                                    Intent intent9 = new Intent(getContext(), profile.class);
                                                    intent9.putExtra("user_id", user_list_id);
                                                    startActivity(intent9);
                                                }
                                                if (which==1) {
                                                    mfriends.child(mcur_id).child(user_list_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                mfriends.child(user_list_id).child(mcur_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {


                                                                    }
                                                                });

                                                            }
                                                            else {
                                                                Toast.makeText(getContext(),"failed to remove friend",Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                                }



                                            }
                                        });
                                builder.create();
                                builder.show();
                                return false;

                            }


                        });
                        holder.setName(name);
                        holder.setimage(img_url);





                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public friends.Friends_holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new Friends_holder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.user_item, viewGroup, false));
            }


        };
        friend_recycler .setAdapter(mFirebaseRecyclerAdapter);
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
    public static class Friends_holder extends RecyclerView.ViewHolder{

        View mView;
        public Friends_holder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;

        }

        public void setName(String name) {
            TextView user_item_name=(TextView)mView.findViewById(R.id.user_item_name);
            user_item_name.setText(name);
        }

        public void setdate(String status) {
            TextView user_item_stat=(TextView)mView.findViewById(R.id.user_item_stat);
            user_item_stat.setText(status);
        }

        public void setimage(String image) {
            CircleImageView user_item_img=(CircleImageView)mView.findViewById(R.id.user_item_img);
            Picasso.get().load(image).placeholder(R.drawable.profile).into(user_item_img);

        }

        public void setOnline(String online_status) {
            ImageView img_online=(ImageView)mView.findViewById(R.id.img_online);
            if (online_status.equals("online")){

                img_online.setVisibility(View.VISIBLE);
            }
            else {
                img_online.setVisibility(View.INVISIBLE);

            }
        }
    }
}
