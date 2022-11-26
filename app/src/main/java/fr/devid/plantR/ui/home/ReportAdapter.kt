package fr.devid.plantR.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import fr.devid.plantR.R
import fr.devid.plantR.databinding.RvReportBugBinding
import fr.devid.plantR.models.*


data class RapportModel(
    var userId: String = "",
    var ticket : Ticket? = null
)

class ReportAdapter() : ListAdapter<RapportModel, ReportAdapter.ReportBugHolder>(UserDiffUtil()) {

    lateinit var currentEtage: String
    lateinit var gardenerId: String
    private lateinit var gardenersPlantsRef: DatabaseReference
    private lateinit var gardenersPlantsData: ValueEventListener
    private lateinit var storageRef: StorageReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportBugHolder {
        val view = RvReportBugBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReportBugHolder(view)
    }

    class ReportBugHolder(val binding: RvReportBugBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onBindViewHolder(holder: ReportBugHolder, position: Int) {

        val ticket: RapportModel = getItem(position)
        holder.itemView.setOnClickListener {
            if (position == RecyclerView.NO_POSITION) return@setOnClickListener
            getItem(position)?.let { key ->
                //VERS TEL NAVIGATION
            }
        }

        holder.binding.tvTitleSujetBug.text = ticket.ticket?.title
        holder.binding.tvMessageRapport.text = ticket.ticket?.messages?.get(0)?.message

            //     holder.binding.tvMessageRapport.text = ticket.ticket?.messages?.
        when(ticket.ticket?.priority) {
            0 -> {
                Glide.with(holder.itemView.context)
                    .load(holder.itemView.resources.getDrawable(R.drawable.important, null))
                    .into(holder.binding.ivTicketPriority)
            }
            1 -> {
                Glide.with(holder.itemView.context)
                    .load(holder.itemView.resources.getDrawable(R.drawable.important, null))
                    .into(holder.binding.ivTicketPriority)
            }
            2 -> {
                Glide.with(holder.itemView.context)
                    .load(holder.itemView.resources.getDrawable(R.drawable.important, null))
                    .into(holder.binding.ivTicketPriority)
            }
        }

        holder.binding.ivArrow.setOnClickListener {

        }
    }


    internal class UserDiffUtil : DiffUtil.ItemCallback<RapportModel>() {
        override fun areItemsTheSame(oldItem: RapportModel, newItem: RapportModel): Boolean {
            println("areItemsTheSame $oldItem est $newItem")
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: RapportModel, newItem: RapportModel): Boolean {
            return oldItem == newItem
        }
    }
}

