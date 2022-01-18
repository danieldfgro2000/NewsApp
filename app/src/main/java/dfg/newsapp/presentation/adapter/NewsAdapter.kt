package dfg.newsapp.presentation.adapter

import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dfg.newsapp.data.model.Article
import dfg.newsapp.databinding.NewsListItemBinding

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private var onClickListener: OnClickListener? = null

    inner class NewsViewHolder ( binding: NewsListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

            val cardView: CardView = binding.cvContent
            val title: TextView = binding.tvTitle
            val description: TextView = binding.tvDescription
            val publishDate: TextView = binding.tvPublishedAt
            val newsSource: TextView = binding.tvSource
            val image: ImageView = binding.ivArticleImage
    }

    private val callback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder{
        val binding = NewsListItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = differ.currentList[position]
        val context = holder.cardView.context

        holder.title.text = article.title
        holder.description.text = article.description
        holder.publishDate.text = article.publishedAt
        holder.newsSource.text = article.source?.name

        Glide.with(holder.image.context)
            .load(article.urlToImage)
            .into(holder.image)

        holder.cardView.setOnClickListener {
            onClickListener?.let {
                onClickListener!!.onClick(position, article)
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setOnClickListener (onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, article: Article)
    }
}