package fr.devid.plantR.ui.myPlants

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import fr.devid.plantR.R
import fr.devid.plantR.databinding.RvToolsBinding

class ToolsMissionAdapter() : ListAdapter<ToolsList, ToolsMissionAdapter.ToolsHolder>(UserDiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolsHolder {

        val view = RvToolsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ToolsHolder(view)
    }

    class ToolsHolder(val binding: RvToolsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ToolsHolder, position: Int) {
        val tools: ToolsList = getItem(position)

        when(tools.tools) {
            "pelle " -> {
                    Glide.with(holder.itemView.context)
                        .load(holder.itemView.resources.getDrawable(R.drawable.pelle, null))
                        .into(holder.binding.ivTools)
                }
            "gants" -> {
            Glide.with(holder.itemView.context)
                .load(holder.itemView.resources.getDrawable(R.drawable.gants, null))
                .into(holder.binding.ivTools)
            }
            "rateau" -> {
                Glide.with(holder.itemView.context)
                    .load(holder.itemView.resources.getDrawable(R.drawable.rateau, null))
                    .into(holder.binding.ivTools)
            }
            "secateur" -> {
                Glide.with(holder.itemView.context)
                    .load(holder.itemView.resources.getDrawable(R.drawable.secateur, null))
                    .into(holder.binding.ivTools)
            }
            "binette" -> {
                Glide.with(holder.itemView.context)
                    .load(holder.itemView.resources.getDrawable(R.drawable.binette, null))
                    .into(holder.binding.ivTools)
            }
            "ficelle" -> {
                Glide.with(holder.itemView.context)
                    .load(holder.itemView.resources.getDrawable(R.drawable.ficelle, null))
                    .into(holder.binding.ivTools)
            }
            "arrosoir" -> {
                Glide.with(holder.itemView.context)
                    .load(holder.itemView.resources.getDrawable(R.drawable.arrosoir, null))
                    .into(holder.binding.ivTools)
            }
            "couteau" -> {
                Glide.with(holder.itemView.context)
                    .load(holder.itemView.resources.getDrawable(R.drawable.couteau, null))
                    .into(holder.binding.ivTools)
            }
            "verre" -> {
                Glide.with(holder.itemView.context)
                    .load(holder.itemView.resources.getDrawable(R.drawable.verre, null))
                    .into(holder.binding.ivTools)
            }
            "tuteur" -> {
                Glide.with(holder.itemView.context)
                    .load(holder.itemView.resources.getDrawable(R.drawable.tuteur, null))
                    .into(holder.binding.ivTools)
            }
            "semoir" -> {
                Glide.with(holder.itemView.context)
                    .load(holder.itemView.resources.getDrawable(R.drawable.semoir, null))
                    .into(holder.binding.ivTools)
            }
            "pinceau" -> {
                Glide.with(holder.itemView.context)
                    .load(holder.itemView.resources.getDrawable(R.drawable.pinceau, null))
                    .into(holder.binding.ivTools)
            }
            "fertilisant" -> {
                Glide.with(holder.itemView.context)
                    .load(holder.itemView.resources.getDrawable(R.drawable.fertilisant, null))
                    .into(holder.binding.ivTools)
            }
            "godets" -> {
                Glide.with(holder.itemView.context)
                    .load(holder.itemView.resources.getDrawable(R.drawable.godets, null))
                    .into(holder.binding.ivTools)
            }
            "ciseaux" -> {
                Glide.with(holder.itemView.context)
                    .load(holder.itemView.resources.getDrawable(R.drawable.ciseaux, null))
                    .into(holder.binding.ivTools)
            }
            "savon" -> {
                Glide.with(holder.itemView.context)
                    .load(holder.itemView.resources.getDrawable(R.drawable.savon, null))
                    .into(holder.binding.ivTools)
            }
            "spray" -> {
                Glide.with(holder.itemView.context)
                    .load(holder.itemView.resources.getDrawable(R.drawable.spray, null))
                    .into(holder.binding.ivTools)
            }
        }

    }

    internal class UserDiffUtil : DiffUtil.ItemCallback<ToolsList>() {
        override fun areItemsTheSame(
            oldItem: ToolsList,
            newItem: ToolsList
        ): Boolean {
            println("areItemsTheSame $oldItem est $newItem")
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: ToolsList,
            newItem: ToolsList
        ): Boolean {
            println("areContentsTheSame$oldItem est $newItem")
            return oldItem == newItem
        }
    }
}