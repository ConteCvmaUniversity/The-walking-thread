package it.thewalkingthread.talky.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import it.thewalkingthread.talky.Model.User;
import it.thewalkingthread.talky.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.Holder> {

    Context context;
    private List<User> users;

    public UserAdapter(List<User> users){

        this.users = users;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_user,parent,false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        User user = users.get(position);
        holder.tv_username.setText(user.getUsername());
        if(user.getImageURL().equals("default")){
            holder.civ_profileImage.setImageResource(R.mipmap.ic_launcher);

        }
        else {
            Glide.with(context).load(user.getImageURL()).into(holder.civ_profileImage);
        }
    }



    @Override
    public int getItemCount() {
        return users.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView tv_username;
        CircleImageView civ_profileImage;

        Holder(@NonNull View itemView) {
            super(itemView);
            tv_username = itemView.findViewById(R.id.tv_username);
            civ_profileImage = itemView.findViewById(R.id.civ_profileImage);
        }
    }
}
