package com.picassos.mint.console.android.adapter

import com.picassos.mint.console.android.models.AuthorBadges
import com.picassos.mint.console.android.interfaces.OnAuthorBadgeClickListener
import androidx.recyclerview.widget.RecyclerView
import android.annotation.SuppressLint
import android.content.Context
import com.picassos.mint.console.android.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest

class AuthorBadgesAdapter(
    private val context: Context,
    private val authorBadgesList: List<AuthorBadges>,
    private val listener: OnAuthorBadgeClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    internal inner class AuthorBadgesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var thumbnail: ImageView = itemView.findViewById(R.id.badge_thumbnail)

        @SuppressLint("SetTextI18n")
        fun setData(data: AuthorBadges) {
            val imageLoader = ImageLoader.Builder(context)
                .componentRegistry {
                    add(SvgDecoder(context))
                }
                .build()
            val request = ImageRequest.Builder(context)
                .data(data.image)
                .target(thumbnail)
                .build()
            imageLoader.enqueue(request)
        }

        fun bind(item: AuthorBadges?, listener: OnAuthorBadgeClickListener) {
            itemView.setOnClickListener { v: View? -> listener.onItemClick(item) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_author_badge, parent, false)
        return AuthorBadgesHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val authorBadges = authorBadgesList[position]
        val authorBadgesHolder = holder as AuthorBadgesHolder
        authorBadgesHolder.setData(authorBadges)
        authorBadgesHolder.bind(authorBadgesList[position], listener)
    }

    override fun getItemCount(): Int {
        return authorBadgesList.size
    }
}