package com.sarthak.takenotes.adapter

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Transaction.Handler
import com.sarthak.takenotes.R
import com.sarthak.takenotes.activity.EditNoteActivity
import com.sarthak.takenotes.databinding.MainActivityBinding
import com.sarthak.takenotes.holder.NoteDisplay

class NoteAdapter(private val binding:MainActivityBinding, private val context: Context, private val itemList: List<NoteDisplay>):RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private var globalSelected = false
    var toBeDeleted: MutableList<String> = mutableListOf()
    private var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var dialog:Dialog = Dialog(context)

    init{
        dialog.setContentView(R.layout.custom_dialog)
        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(AppCompatResources.getDrawable(context,R.drawable.custom_dialog_back))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.note_view_holder, parent, false)
        return NoteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.title.text = currentItem.title
        holder.des.text = currentItem.des
        holder.time.text = currentItem.time

        if(currentItem.title.isEmpty()){
            holder.title.visibility = View.GONE
        }else{
            holder.title.visibility = View.VISIBLE
        }
        if(currentItem.des.isEmpty()){
            holder.des.visibility = View.GONE
        }else{
            holder.des.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener{
            if(holder.selected){
                holder.linear.setBackgroundResource(R.drawable.note_view_back)
                toggleSelectionTick(holder)
                holder.selected = false
                toBeDeleted.remove(currentItem.id)
                if(toBeDeleted.isEmpty()){
                    globalSelected = false
                    binding.addNote.setImageResource(R.drawable.add_icon)
                }
            }else if(globalSelected){
                holder.linear.setBackgroundResource(R.drawable.note_view_back_selected)
                toggleSelectionTick(holder)
                holder.selected = true
                toBeDeleted.add(currentItem.id)
            } else{
                //clear search field focus
                binding.search.clearFocus()
                val intent = Intent(context,EditNoteActivity::class.java).apply {
                    putExtra("TITLE",currentItem.title)
                    putExtra("DES",currentItem.des)
                    putExtra("TIME",currentItem.time)
                    putExtra("ID",currentItem.id)
                }
                startActivity(context,intent,null)
            }
        }

        holder.itemView.setOnLongClickListener{
            if(!globalSelected){
                holder.linear.setBackgroundResource(R.drawable.note_view_back_selected)
                toggleSelectionTick(holder)
                holder.selected = true
                globalSelected = true
                toBeDeleted.add(currentItem.id)
                binding.addNote.setImageResource(R.drawable.delete_icon)
            }
            true
        }
    }
    override fun getItemCount() = itemList.size

    fun deleteSelected(callback: () -> Unit){
        dialog.findViewById<TextView>(R.id.des).setText("Are you sure you want to delete ${toBeDeleted.size} notes?")
        dialog.show()
        dialog.findViewById<TextView>(R.id.cancelBut).setOnClickListener{
            dialog.dismiss()
        }
        dialog.findViewById<TextView>(R.id.deleteBut).setOnClickListener{
            for(id in toBeDeleted){
                database.child("users").child(FirebaseAuth.getInstance().uid?:"").child("notes").child(id).removeValue()
            }
            globalSelected = false
            toBeDeleted.clear()
            binding.addNote.setImageResource(R.drawable.add_icon)
            dialog.dismiss()
            callback()
        }
    }

    fun deselectNotes(){
        toBeDeleted.clear()
        globalSelected = false
        binding.addNote.setImageResource(R.drawable.add_icon)
    }
    fun isNotesSelected():Boolean{
        return globalSelected
    }



    private fun toggleSelectionTick(holder:NoteViewHolder){
        val delay:Long = 300
        if(holder.selected){
            // Scale X animation
            val scaleX = ObjectAnimator.ofFloat(holder.tick, "scaleX", 1.0f, 0.0f)
            scaleX.duration = delay-100
            // scale Y animation
            val scaleY = ObjectAnimator.ofFloat(holder.tick, "scaleY", 1.0f, 0.0f)
            scaleY.duration = delay-100
            // Animatorset to play both scaleX and scaleY together
            val scaleAnimation = AnimatorSet()
            scaleAnimation.playTogether(scaleX, scaleY)
            scaleAnimation.start()
            android.os.Handler.createAsync(Looper.getMainLooper()).postDelayed({
                holder.tick.visibility = View.GONE
            },delay-100)
        }else{
            holder.tick.visibility = View.VISIBLE
            // Scale X animation
            val scaleX = ObjectAnimator.ofFloat(holder.tick, "scaleX", 0.0f, 1.0f)
            scaleX.duration = delay // 1 second
            // Scale Y animation
            val scaleY = ObjectAnimator.ofFloat(holder.tick, "scaleY", 0.0f, 1.0f)
            scaleY.duration = delay // 1 second
            // animatorSet to play both scaleX and scaleY together
            val scaleAnimation = AnimatorSet()
            scaleAnimation.playTogether(scaleX, scaleY)
            scaleAnimation.start()
        }
    }

    class NoteViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var selected = false
        val linear:LinearLayout = itemView.findViewById<LinearLayout>(R.id.linearLayout2)
        val tick:ImageView= itemView.findViewById(R.id.tick)
        val title: TextView = itemView.findViewById<TextView>(R.id.noteTitle)
        val des: TextView = itemView.findViewById<TextView>(R.id.noteDes)
        val time: TextView = itemView.findViewById<TextView>(R.id.noteTime)
    }
}