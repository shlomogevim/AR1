package com.sg.ar1

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_model.view.*

const val SELECTED_MODEL_COLOR = Color.YELLOW
const val UNSELECTED_MODEL_COLOR = Color.LTGRAY

class ModelAdapter(val models: List<Model>) : RecyclerView.Adapter<ModelAdapter.ModelViewHolder>() {

    var selectedModel = MutableLiveData<Model>()
    private var selectedModelIndex = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_model, parent, false)
        return ModelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        if (selectedModelIndex==holder.layoutPosition){
            holder.itemView.setBackgroundColor(SELECTED_MODEL_COLOR)
            selectedModel.value=models[holder.layoutPosition]
        }else{
            holder.itemView.setBackgroundColor(UNSELECTED_MODEL_COLOR)
        }
        holder.itemView.apply {
            ivTumbail.setImageResource(models[position].imageResourceId)
            tvTitle.text = models[position].title

            setOnClickListener {
                selectedModel(holder)
            }

        }
    }

    override fun getItemCount() = models.size

    inner class ModelViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) { }

    private fun selectedModel(holder: ModelViewHolder){
        if (selectedModelIndex != holder.layoutPosition){
            holder.itemView.setBackgroundColor(SELECTED_MODEL_COLOR)
            notifyItemChanged(selectedModelIndex)
            selectedModelIndex=holder.layoutPosition
            selectedModel.value=models[holder.layoutPosition]
        }


    }
}