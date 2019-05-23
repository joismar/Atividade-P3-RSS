package br.ufpe.cin.if710.rss

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

// Adapter básico da Recycler View
class RssFeedAdapter(private val ItemRSS: List<ItemRSS>) : RecyclerView.Adapter<RssFeedAdapter.FeedViewHolder>() {

    class FeedViewHolder(val rssFeedView: View) : RecyclerView.ViewHolder(rssFeedView)

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): FeedViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.itemlista, parent, false)
        return FeedViewHolder(v)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val rssFeedModel = ItemRSS.get(position)
        (holder.rssFeedView.findViewById(R.id.item_titulo) as TextView).text = rssFeedModel.title
        (holder.rssFeedView.findViewById(R.id.item_data) as TextView).text = rssFeedModel.description
    }

    override fun getItemCount(): Int {
        return ItemRSS.size
    }

}