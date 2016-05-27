package com.example.changgg.elderstalk.RecyclerView;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.changgg.elderstalk.MainActivity;
import com.example.changgg.elderstalk.R;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    MainActivity mainActivity = new MainActivity();
    private String TAG = "ContactsAdapter";
    private boolean isRunning = true;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView nameTextView;
        public MyViewHolderClick mListener;
        public ViewHolder(View itemView, MyViewHolderClick listener){
            super(itemView);
            mListener = listener;

            nameTextView = (TextView) itemView.findViewById(R.id.tv_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.clickOnView(v, getLayoutPosition());
        }

        public interface MyViewHolderClick {
            void clickOnView(View v, int position);
        }
    }

    private List<Contact> mContacts;

    public ContactsAdapter(List<Contact> mContacts) {
        this.mContacts = mContacts;
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        View contactView = LayoutInflater.from(context).inflate(R.layout.item_contact, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(contactView, new ViewHolder.MyViewHolderClick() {
            @Override
            public void clickOnView(final View v, final int position) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (isRunning){
                            try {
                                for (int i=position; i<getItemCount(); i++) {
                                    Contact contact = mContacts.get(i);
                                    mainActivity.playRecorded(contact.getName());
                                    Snackbar.make(v, contact.getName(), Snackbar.LENGTH_LONG).show();
                                }

                                Thread.sleep(1000);
                                Thread.interrupted();
                                isRunning = false;

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
                isRunning = true;
//                for (int i=position; i<getItemCount(); i++) {
//                    mainActivity.initMediaPlayer();
//                    Contact contact = mContacts.get(i);
//                    mainActivity.playRecorded(contact.getName());
//                    Snackbar.make(v, contact.getName(), Snackbar.LENGTH_LONG).show();
//                }
//                mainActivity.releaseMediaPlayer();
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Contact contact = mContacts.get(position);
        TextView nameTextView = viewHolder.nameTextView;
        nameTextView.setText(contact.getName());
    }



}
