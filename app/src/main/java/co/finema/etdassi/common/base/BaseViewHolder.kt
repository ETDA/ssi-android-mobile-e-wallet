package co.finema.etdassi.common.base

import android.text.TextWatcher
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

open class BaseViewHolder<out T : ViewDataBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root) {
    var watcher: TextWatcher? = null
}