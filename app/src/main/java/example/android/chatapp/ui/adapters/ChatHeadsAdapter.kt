package example.android.chatapp.ui.adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import example.android.chatapp.R
import example.android.chatapp.models.UserDataClass
import example.android.chatapp.ui.activities.ChatActivity
import kotlinx.android.synthetic.main.item_chat_heads.view.*
import java.util.*

/**
 * Created by sheri on 2/10/2018.
 */
class ChatHeadsAdapter(var context: Context, var friendsList: ArrayList<UserDataClass>) : RecyclerView.Adapter<ChatHeadsAdapter.ChatViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_chat_heads, parent, false)
        val viewHolder = ChatViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return friendsList.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder?, position: Int) {
        val v = holder?.view

        v?.user_name?.text = friendsList.get(position).name
        v?.last_message?.text = friendsList.get(position).description
        val photoUrl = friendsList.get(position).photoUrl
        v?.chat_head_image?.visibility = View.VISIBLE
        Glide.with(v?.chat_head_image?.context)
                .load(photoUrl)
                .into(v?.chat_head_image)
        v?.chat_head_item?.setOnClickListener({
            val myFriend = friendsList?.get(position)?.name
            // Toast.makeText(getApplicationContext(), myFriend, Toast.LENGTH_LONG).show();

            val chatIdDB: DatabaseReference
            chatIdDB = FirebaseDatabase.getInstance().reference.child("friends").child(FirebaseAuth.getInstance().currentUser?.uid).child(friendsList.get(position).id)

            chatIdDB.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (userSnapshot in dataSnapshot.children) {
                        // TODO: handle the post
                        val chatId = userSnapshot.key
                        //  Toast.makeText(getApplicationContext(), chatId, Toast.LENGTH_LONG).show();
                        val intent = Intent(context, ChatActivity::class.java)
                        intent.putExtra("chatId", chatId)
                        intent.putExtra("myFriend", myFriend)
                        context.startActivity(intent)
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })

        })

    }


    inner class ChatViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    }
}
