package com.example.test2.Adapter



import androidx.recyclerview.widget.RecyclerView


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.test.R
import com.example.test2.Model.ModelNews
import retrofit2.Callback


class NewsAdpater(private val context: Callback<String?>, var items: List<ModelNews>) : RecyclerView.Adapter<NewsAdpater.ViewHolder>() {
    // 뷰 홀더 만들어서 반환, 뷰릐 레이아웃은 list_item_weather.xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdpater.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.news_itme, parent, false)
        return ViewHolder(itemView)
    }

    // 전달받은 위치의 아이템 연결
    override fun onBindViewHolder(holder: NewsAdpater.ViewHolder, position: Int) {
        val item = items[position]
        holder.setItem(item)
    }
    interface OnItemClickListener{
        fun onItemClick(v:View, pos : Int)

    }
    private var listener : OnItemClickListener? = null
    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }

    // 아이템 갯수 리턴
    override fun getItemCount() = items.count()

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun setItem(item : ModelNews) {

            val title = itemView.findViewById<TextView>(R.id.title)           // 제목
            val pubDate = itemView.findViewById<TextView>(R.id.pubDate)   // 시간



            title.text=item.title
            pubDate.text=item.pubDate
            val pos = adapterPosition
            if(pos!= RecyclerView.NO_POSITION)
            {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView,pos)
                }
            }
        }
    }








}