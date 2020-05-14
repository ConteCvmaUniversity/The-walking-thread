package it.thewalkingthread.talky.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import it.thewalkingthread.talky.Model.Chat;
import it.thewalkingthread.talky.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.Holder>{

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT= 1;

    Context context;
    private List<Chat> mChat;
    private String url;

    FirebaseUser user;

    public MessageAdapter(Context context,List<Chat> mChat){
        this.mChat = mChat;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            context = parent.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_right, parent, false);

            return new MessageAdapter.Holder(view);
        }
        if (viewType == MSG_TYPE_LEFT) {
            context = parent.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_left, parent, false);

            return new MessageAdapter.Holder(view);
        }
        return null;
    }



    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.Holder holder, int position) {
        Chat chat = mChat.get(position);
        holder.tv_message.setText(chat.getMessage());
    }



    @Override
    public int getItemCount() {
        return mChat.size();
    }




    @Override
    public int getItemViewType(int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(user.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else return MSG_TYPE_LEFT;

    }

    //HOLDER
    static class Holder extends RecyclerView.ViewHolder {
        TextView tv_message;


        Holder(@NonNull View itemView) {
            super(itemView);
            tv_message = itemView.findViewById(R.id.tv_message);

        }
    }
}
