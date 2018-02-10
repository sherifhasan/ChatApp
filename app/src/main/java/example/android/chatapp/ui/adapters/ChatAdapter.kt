package example.android.chatapp.ui.adapters

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import example.android.chatapp.R
import example.android.chatapp.models.MessageDataClass
import kotlinx.android.synthetic.main.item_message.view.*
import java.util.*

/**
 * Created by sheri on 2/10/2018.
 */
class ChatAdapter(private var messages: ArrayList<MessageDataClass>
                  , private var mContext: Context) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChatAdapter.ChatViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_message, parent, false)
        val viewHolder = ChatViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder?, position: Int) {
        val v = holder?.view

        val messageBodyText = messages[position].msgBody
        val messageSenderID = messages[position].msgPublisherId
        val message = messages[position]
        val isPhoto = message.photoUrl != null
        if (isPhoto) {
            if (messageSenderID == FirebaseAuth.getInstance().currentUser!!.uid) {
                val colorValue = Color.parseColor("#ff0000")
                v?.card_view_message?.setCardBackgroundColor(colorValue)
                val params = v?.card_view_message?.layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                v?.card_view_message?.layoutParams = params
            } else {
                val colorValue = Color.parseColor("#E0E0E0")
                val params = v?.card_view_message?.layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                v?.card_view_message?.layoutParams = params
                v?.card_view_message?.setCardBackgroundColor(colorValue)
            }
            v?.photoImageView?.maxWidth = 200

            v?.photoImageView?.setVisibility(View.VISIBLE)
            Glide.with(v?.photoImageView?.context)
                    .load(message.photoUrl)
                    .into(v?.photoImageView)
        } else {
            if (messageSenderID == "app") {
                val colorValue = Color.parseColor("#ff0000")
                val textColor = Color.parseColor("#ffffff")
                v?.card_view_message?.setCardBackgroundColor(colorValue)
                val params = v?.card_view_message?.layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.CENTER_HORIZONTAL)
                v?.card_view_message?.layoutParams = params
                v?.messageTextView.setTextColor(textColor)

            } else if (messageSenderID == FirebaseAuth.getInstance().currentUser!!.uid) {
                val colorValue = Color.parseColor("#ff0000")
                val textColor = Color.parseColor("#ffffff")
                v?.card_view_message?.setCardBackgroundColor(colorValue)
                val params = v?.card_view_message?.layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                v?.card_view_message?.setLayoutParams(params)
                v?.messageTextView.setTextColor(textColor)


            } else {
                val colorValue = Color.parseColor("#E0E0E0")
                val textColor = Color.parseColor("#000000")
                val params = v?.card_view_message?.layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                v?.card_view_message?.setLayoutParams(params)
                v?.card_view_message?.setCardBackgroundColor(colorValue)
                v?.messageTextView.setTextColor(textColor)
            }
            v?.messageTextView.text = messageBodyText
        }

    }

    inner class ChatViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    }

}