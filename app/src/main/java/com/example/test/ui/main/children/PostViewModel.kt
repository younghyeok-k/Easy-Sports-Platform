package com.example.test.ui.main.children

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.api.PostApi
import com.example.test.api.RetrofitInstance
import com.example.test.model.post.Post
import com.example.test.model.post.PostsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.suspendCoroutine

class PostViewModel : ViewModel() {
    private val apiService = RetrofitInstance.retrofit.create(PostApi::class.java)

    private val query = MutableStateFlow("")
    val posts = MutableStateFlow<List<Post>>(listOf())

    private val _sortType = MutableStateFlow(PostSortType.LATEST)
    val sortType: StateFlow<PostSortType> get() = _sortType

    private var page = 0;
    var isLast = false
        private set
    val isLoading = MutableStateFlow<Boolean>(false)

    init {
        viewModelScope.launch {
            combine(listOf(query.debounce(150), _sortType)) { }
                .collectLatest {
                    page = 0
                    isLast = false
                    loadMore()
                }
        }
    }

    fun setQuery(query: String) {
        this.query.value = query
    }

    fun setSortType(type: PostSortType) {
        if (_sortType.value == type) return

        _sortType.value = type
    }

    suspend fun refresh() {
        page = 0
        isLast = false
        loadMore()
    }

    suspend fun loadMore() {
        if (isLoading.value) return
        if (isLast) return

        isLoading.value = true

        val response = suspendCoroutine<PostsResponse> {
            apiService.searchPosts("title", query.value, _sortType.value.value, 20, page)
                .enqueue(object : Callback<PostsResponse> {
                    override fun onResponse(
                        call: Call<PostsResponse>,
                        response: Response<PostsResponse>
                    ) {
                        it.resumeWith(Result.success(response.body() ?: PostsResponse().apply {
                            last = true
                        }))
                    }

                    override fun onFailure(call: Call<PostsResponse>, t: Throwable) {
                        t.printStackTrace()

                        it.resumeWith(Result.success(PostsResponse().apply {
                            last = true
                        }))
                    }
                })
        }

        isLoading.value = false

        val result = if (page == 0) {
            response.content
        } else {
            posts.value + response.content
        }

        page += 1
        isLast = response.last
        posts.emit(result)
    }
}

enum class PostSortType(val value: String) {
    LATEST("latest"),
    OLDEST("oldest")
}