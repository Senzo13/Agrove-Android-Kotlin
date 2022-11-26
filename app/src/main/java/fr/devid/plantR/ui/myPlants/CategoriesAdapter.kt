package fr.devid.plantR.ui.myPlants

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import fr.devid.plantR.databinding.RvPlantsCategorieBinding
import fr.devid.plantR.models.*
import kotlinx.android.synthetic.main.rv_plants_filter.view.*
import java.util.*

class CategoriesAdapter(var actionToPage : (String) -> Unit) : ListAdapter<CategorieModels, CategoriesAdapter.CategoriesHolder>(UserDiffUtil()) {

    private val PAGE: String = "***** CategoriesAdapter *****"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesHolder {
        val view = RvPlantsCategorieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoriesHolder(view).apply {
            itemView.listePlants.setOnClickListener {
                val position = adapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener
                getItem(position).let {
                    when(Locale.getDefault().language.toString()) {
                        "fr" -> {
                            actionToPage(it.filtre.name)
                        }
                        "en" -> {
                            when(it.filtre.name) {
                                "Fruits" -> {
                                    actionToPage("Fruits")
                                }
                                "Dépolluantes" -> {
                                    actionToPage("Depolluting")
                                }
                                "Aromates" -> {
                                    actionToPage("Herbs")
                                }
                                "Légumes" -> {
                                    actionToPage("Vegetables")
                                }
                                "Fleurs" -> {
                                    actionToPage("Flowers")
                                }
                                "Médicinales" -> {
                                    actionToPage("Medicinal")
                                }
                                "Mellifères" -> {
                                    actionToPage("Melliferous")

                                }
                            }
                        }
                        else -> {
                                when(it.filtre.name) {
                                    "Fruits" -> {
                                        actionToPage("Fruits")
                                    }
                                    "Dépolluantes" -> {
                                        actionToPage("Depolluting")
                                    }
                                    "Aromates" -> {
                                        actionToPage("Herbs")
                                    }
                                    "Légumes" -> {
                                        actionToPage("Vegetables")
                                    }
                                    "Fleurs" -> {
                                        actionToPage("Flowers")
                                    }
                                    "Médicinales" -> {
                                        actionToPage("Medicinal")
                                    }
                                    "Mellifères" -> {
                                        actionToPage("Melliferous")
                                    }
                            }
                        }

                    }
                }
            }
        }
    }
    class CategoriesHolder(val binding : RvPlantsCategorieBinding) : RecyclerView.ViewHolder(binding.root)


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CategoriesHolder, position: Int) {

        val categorie:CategorieModels = getItem(position)

        when(Locale.getDefault().language.toString()) {
            "fr" -> {
                holder.binding.tvNameCategorie.text = categorie.filtre.name
            }
            "en" -> {
                when(categorie.filtre.name) {
                    "Fruits" -> {
                        holder.binding.tvNameCategorie.text = "Fruits"
                    }
                    "Dépolluantes" -> {
                        holder.binding.tvNameCategorie.text = "Depolluting"
                    }
                    "Aromates" -> {
                        holder.binding.tvNameCategorie.text = "Herbs"
                    }
                    "Légumes" -> {
                        holder.binding.tvNameCategorie.text = "Vegetables"
                    }
                    "Fleurs" -> {
                        holder.binding.tvNameCategorie.text = "Flowers"
                    }
                    "Médicinales" -> {
                        holder.binding.tvNameCategorie.text = "Medicinal"
                    }
                    "Mellifères" -> {
                        holder.binding.tvNameCategorie.text = "Melliferous"

                    }
                }
            }
            else -> {
                when(categorie.filtre.name) {
                    "Fruits" -> {
                        holder.binding.tvNameCategorie.text = "Fruits"
                    }
                    "Dépolluantes" -> {
                        holder.binding.tvNameCategorie.text = "Depolluting"
                    }
                    "Aromates" -> {
                        holder.binding.tvNameCategorie.text = "Herbs"
                    }
                    "Légumes" -> {
                        holder.binding.tvNameCategorie.text = "Vegetables"
                    }
                    "Fleurs" -> {
                        holder.binding.tvNameCategorie.text = "Flowers"
                    }
                    "Médicinales" -> {
                        holder.binding.tvNameCategorie.text = "Medicinal"
                    }
                    "Mellifères" -> {
                        holder.binding.tvNameCategorie.text = "Melliferous"

                    }
                }
            }

        }
        holder.binding.tvDescription.text = categorie.filtre.description

    }

    internal class UserDiffUtil : DiffUtil.ItemCallback<CategorieModels>() {

        override fun areItemsTheSame(oldItem:CategorieModels, newItem:CategorieModels): Boolean {
                return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: CategorieModels, newItem: CategorieModels): Boolean {
            return oldItem == newItem
        }

    }

}

