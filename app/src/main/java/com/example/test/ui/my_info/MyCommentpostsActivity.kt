package com.example.test.ui.my_info

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.recyclerview.widget.*
import com.example.test.R
import com.example.test.api.AuthApi
import com.example.test.api.PostApi
import com.example.test.api.RetrofitInstance
import com.example.test.databinding.ActivityMyCommentpostsBinding
import com.example.test.databinding.ActivityMypostBinding
import com.example.test.databinding.ActivityNickNameEditBinding
import com.example.test.databinding.ItemPostBinding
import com.example.test.model.post.Post
import com.example.test.model.post.PostsResponse
import com.example.test.ui.main.children.PostFragment
import com.example.test.ui.post.PostViewerActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MyCommentpostsActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMyCommentpostsBinding.inflate(layoutInflater) }
    private val adapter = MyCommnetPostAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        with(binding) {
            toolbaar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
            mycommentposts()
            recyclerView.adapter = adapter
            recyclerView.apply {
                layoutManager = LinearLayoutManager(this@MyCommentpostsActivity)
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                adapter = this@MyCommentpostsActivity.adapter.apply {
                    onItemClickListener = { post ->
                        // 아이템 클릭 시 PostViewerActivity로 이동
                        startActivity(PostViewerActivity.getIntent(this@MyCommentpostsActivity, post))
                    }
                }
            }
//            recyclerView.adapter = adapter.apply {
//                onItemClickListener = {
//                    launchViewer.launch(PostViewerActivity.getIntent(view.context, it))
//                }
//            }
        }


    }

    private fun mycommentposts() {
        RetrofitInstance.retrofit.create(PostApi::class.java)
            .mycommnets()
            .enqueue(object : Callback<PostsResponse> {
                override fun onResponse(
                    call: Call<PostsResponse>,
                    response: Response<PostsResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        response.body()?.let { dto ->
                            with(binding) {
                                adapter.submitList(dto.content)
//                                Log.d("내가쓴글",dto.content[0].content)
                            }
                        }

                    }
                }

                override fun onFailure(call: Call<PostsResponse>, t: Throwable) {

                }
            })

    }
}

private class MyCommnetPostAdapter :
    ListAdapter<Post, MyCommnetPostAdapter.PostItemViewHolder>(object : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.title == newItem.title &&
                    oldItem.content == newItem.content &&
                    oldItem.commentSize == newItem.commentSize &&
                    oldItem.user.username == newItem.user.username &&
                    oldItem.user.nickname == newItem.user.nickname

        }
    }) {
    var onItemClickListener: ((Post) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPostBinding.inflate(inflater, parent, false)
        return PostItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostItemViewHolder, position: Int) {
        val post = currentList[position]

        with(holder.binding) {
            root.setOnClickListener {
                onItemClickListener?.invoke(post)
            }

            titleTextView.text = post.title
            contentTextView.text = post.content
            commentCountTextView.text = "${post.commentSize}"
            if (post.commentSize > 0) {
                commentCountTextView.isVisible = true
                verticalDivider1.isVisible = true
            } else {
                commentCountTextView.isVisible = false
                verticalDivider1.isVisible = false
            }

            if (post.createdAt != null) {
                datetimeTextView.text = formatDate(post.createdAt)
                verticalDivider2.isVisible = true
            } else {
                datetimeTextView.text = null
                verticalDivider2.isVisible = false
            }

            nameTextView.text = post.user.nickname
        }
    }
    private fun formatDate(dateString: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = dateFormat.parse(dateString)
        val currentDate = Date()

        val isSameDate = android.text.format.DateUtils.isToday(date.time)

        return if (isSameDate) {
            val diff = currentDate.time - date.time
            val minutes = diff / (60 * 1000)
            val hours = diff / (60 * 60 * 1000)

            when {
                hours < 1 -> {
                    if (minutes < 60) {
                        "${minutes}분 전"
                    } else {
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
                    }
                }
                else -> SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
            }
        } else {
            SimpleDateFormat("MM/dd", Locale.getDefault()).format(date)
        }
    }
    class PostItemViewHolder(val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root)
}


