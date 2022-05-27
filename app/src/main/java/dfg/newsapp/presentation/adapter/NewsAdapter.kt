package dfg.newsapp.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dfg.newsapp.data.model.Article
import dfg.newsapp.databinding.NewsListItemBinding

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private var onItemClickListener : ((Article) -> Unit)? = null

    private val callback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.id == newItem.id
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
        val article: Article? = differ.currentList[position]

        holder.title.text = article?.title
        holder.description.text = article?.description
        holder.publishDate.text = article?.publishedAt
        holder.newsSource.text = article?.source?.name

        if (!article?.urlToImage.isNullOrBlank()){
            Glide.with(holder.image.context)
                .load(article?.urlToImage)
                .into(holder.image)
        }

        holder.cardView.setOnClickListener {
            onItemClickListener?.let {
                article?.let { article -> it(article) }
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setOnItemClickListener (listener : (Article?) -> Unit) {
        this.onItemClickListener = listener
    }

    inner class NewsViewHolder ( binding: NewsListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        val cardView: CardView = binding.cvContent
        val title: TextView = binding.tvTitle
        val description: TextView = binding.tvDescription
        val publishDate: TextView = binding.tvPublishedAt
        val newsSource: TextView = binding.tvSource
        val image: ImageView = binding.ivArticleImage
    }
}