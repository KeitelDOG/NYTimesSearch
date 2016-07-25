package com.megalobiz.nytimessearch.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.megalobiz.nytimessearch.R;
import com.megalobiz.nytimessearch.models.Article;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by KeitelRobespierre on 7/20/2016.
 */
public class ArticleArrayAdapter extends ArrayAdapter<Article> {

    public ArticleArrayAdapter(Context context, List<Article> articles) {
        super(context, android.R.layout.simple_list_item_1, articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get the article at the position
        Article article = this.getItem(position);

        // check if existing view is being reused
        // if not using a recycled view -> inflate the layout
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_article_result, parent, false);
        }

        // find the image view
        ImageView imageView = (ImageView) convertView.findViewById(R.id.ivImage);

        // clear out recycled image from convertView from last time
        imageView.setImageResource(0);

        // find other views
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        TextView tvNewsDesk = (TextView) convertView.findViewById(R.id.tvNewsDesk);

        //Set views values
        tvTitle.setText(article.getHeadLine());
        if(!article.getNewsDesk().equals("null"))
            tvNewsDesk.setText(article.getNewsDesk());

        // populate the thumbnail image
        // remote download the image in background
        String thumbnail = article.getThumbnail();

        if(!TextUtils.isEmpty(thumbnail)) {
            Picasso.with(getContext()).load(thumbnail)
                    .placeholder(R.drawable.article_placeholder)
                    .error(R.drawable.article_placeholder)
                    .into(imageView);
        } else {
            Picasso.with(getContext()).load(R.drawable.article_placeholder)
                    .into(imageView);
        }

        return convertView;
    }
}
