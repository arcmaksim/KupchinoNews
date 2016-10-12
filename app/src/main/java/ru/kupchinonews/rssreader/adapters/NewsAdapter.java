package ru.kupchinonews.rssreader.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.kupchinonews.rssreader.NewsItem;
import ru.kupchinonews.rssreader.R;
import ru.kupchinonews.rssreader.ui.activity.BaseActivity;
import ru.kupchinonews.rssreader.ui.activity.MainActivity;

public class NewsAdapter extends ArrayAdapter<NewsItem> {

    public NewsAdapter(Context context, ArrayList<NewsItem> news, MainActivity act) {
        super(context, 0, news);
        mActivity = act;
    }

    private MainActivity mActivity;

    static class ViewHolderSimpleItem {
        TextView mTitle;
    }

    static class ViewHolderDetailedItem {
        TextView mTitle;
        TextView mDescription;
        ImageView mImage;
    }

    static class ViewHolderDetailedItemWithoutImage {
        TextView mTitle;
        TextView mDescription;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolderSimpleItem mViewHolder = null;
        ViewHolderDetailedItem mViewHolder1 = null;
        ViewHolderDetailedItemWithoutImage mViewHolder2 = null;

        if (mActivity.getFlag(position)) {

            if (getItem(position).getImage() == null) {

                view = LayoutInflater.from(getContext()).inflate(R.layout.item_detailed_news_without_image, parent, false);

                if (mViewHolder2 == null) {

                    mViewHolder2 = new ViewHolderDetailedItemWithoutImage();
                    mViewHolder2.mTitle = (TextView) view.findViewById(R.id.title);
                    mViewHolder2.mTitle.setTypeface(BaseActivity.getDefaultFont());
                    mViewHolder2.mDescription = (TextView) view.findViewById(R.id.description);
                    mViewHolder2.mDescription.setTypeface(BaseActivity.getDefaultFont());
                    view.setTag(mViewHolder2);

                } else {

                    mViewHolder2 = (ViewHolderDetailedItemWithoutImage) view.getTag();

                }

                mViewHolder2.mTitle.setText(Html.fromHtml(getItem(position).getTitle()));
                mViewHolder2.mDescription.setText(Html.fromHtml(getItem(position).getDescription()));

            } else {

                view = LayoutInflater.from(getContext()).inflate(R.layout.item_detailed_news, parent, false);

                if (mViewHolder1 == null) {

                    mViewHolder1 = new ViewHolderDetailedItem();
                    mViewHolder1.mTitle = (TextView) view.findViewById(R.id.title);
                    mViewHolder1.mTitle.setTypeface(BaseActivity.getDefaultFont());
                    mViewHolder1.mDescription = (TextView) view.findViewById(R.id.description);
                    mViewHolder1.mDescription.setTypeface(BaseActivity.getDefaultFont());
                    mViewHolder1.mImage = (ImageView) view.findViewById(R.id.image);
                    view.setTag(mViewHolder1);

                } else {

                    mViewHolder1 = (ViewHolderDetailedItem) view.getTag();

                }

                mViewHolder1.mTitle.setText(Html.fromHtml(getItem(position).getTitle()));
                mViewHolder1.mDescription.setText(Html.fromHtml(getItem(position).getDescription()));
                mViewHolder1.mImage.setImageDrawable(getItem(position).getImage());

            }

        } else {

            view = LayoutInflater.from(getContext()).inflate(R.layout.item_news, parent, false);

            if (mViewHolder == null) {

                mViewHolder = new ViewHolderSimpleItem();
                mViewHolder.mTitle = (TextView) view.findViewById(R.id.item_text_view);
                mViewHolder.mTitle.setTypeface(BaseActivity.getDefaultFont());
                view.setTag(mViewHolder);

            } else {

                mViewHolder = (ViewHolderSimpleItem) view.getTag();

            }

            mViewHolder.mTitle.setText(getItem(position).getTitle());

        }

        /*if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_news, parent, false);

            mViewHolder = new ViewHolderItem();
            mViewHolder.mTitle = (TextView) view.findViewById(R.id.item_text_view);
            mViewHolder.mTitle.setTypeface(BaseActivity.getDefaultFont());

            view.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolderItem) view.getTag();
        }*/
        //mViewHolder.mTitle.setText(Html.fromHtml(getItem(position).getTitle()));

        return view;
    }
}
