package ru.kupchinonews.rssreader;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ru.kupchinonews.rssreader.activity.BaseActivity;

public class NewsAdapter2 extends RecyclerView.Adapter<NewsAdapter2.ViewHolder> implements View.OnClickListener{

    ArrayList<NewsItem> mNews;

    public NewsAdapter2(ArrayList<NewsItem> news){
        mNews = news;
    }

    /*public class TitleViewHoder extends ParentViewHolder {

    }*/

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView mTitle;
        TextView mDescription;
        ImageView mImage;
        LinearLayout mExpandArea;

        public ViewHolder(View itemView) {

            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mTitle.setTypeface(BaseActivity.getDefaultFont());
            mDescription = (TextView) itemView.findViewById(R.id.description);
            mDescription.setTypeface(BaseActivity.getDefaultFont());
            mImage = (ImageView) itemView.findViewById(R.id.image);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_detailed_news, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        holder.mTitle.setOnClickListener(this);

        return holder;

    }

    @Override
    public void onBindViewHolder(NewsAdapter2.ViewHolder holder, int position) {

        holder.mTitle.setText(mNews.get(position).getTitle());
        holder.mDescription.setText(mNews.get(position).getDescription());
        if(mNews.get(position).getImage() != null)
            holder.mImage.setImageDrawable(mNews.get(position).getImage());

        /*int colorIndex = randy.nextInt(bgColors.length);
        holder.tvTitle.setText(mDataset.get(position));
        holder.tvTitle.setBackgroundColor(bgColors[colorIndex]);
        holder.tvSubTitle.setBackgroundColor(sbgColors[colorIndex]);

        if (position == expandedPosition) {
            holder.llExpandArea.setVisibility(View.VISIBLE);
        } else {
            holder.llExpandArea.setVisibility(View.GONE);
        }*/
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onClick(View view) {
        /*ViewHolder holder = (ViewHolder) view.getTag();
        String theString = mDataset.get(holder.getPosition());

        // Check for an expanded view, collapse if you find one
        if (expandedPosition >= 0) {
            int prev = expandedPosition;
            notifyItemChanged(prev);
        }
        // Set the current position to "expanded"
        expandedPosition = holder.getPosition();
        notifyItemChanged(expandedPosition);

        Toast.makeText(mContext, "Clicked: "+theString, Toast.LENGTH_SHORT).show();*/
    }

    @Override
    public int getItemCount() {
        return mNews.size();
    }

}
