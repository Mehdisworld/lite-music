package com.ultimate.music.downloader.autoComplete;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ultimate.music.downloader.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class AutoCompleteMusicAdapter extends ArrayAdapter<MusicItem> {
  private List<MusicItem> autoListFull;

  public AutoCompleteMusicAdapter(@NonNull Context context, @NonNull List<MusicItem> rowList) {
    super(context, 0, rowList);
    autoListFull = new ArrayList<>(rowList);
  }

  @NonNull
  @Override
  public Filter getFilter() {
    return rowFilter;
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(
              R.layout.music_autocomplete_row, parent, false
      );
    }

    TextView textViewName = convertView.findViewById(R.id.text_view_name);
    ImageView imageViewFlag = convertView.findViewById(R.id.image_view_cover);

    MusicItem countryItem = getItem(position);
    String countName = countryItem.getArtistName();
    String imgUrl = countryItem.getImageUrl();

    if (countryItem != null) {
      textViewName.setText(countName);
      Picasso.get().load(imgUrl).error(R.drawable.not_loading).into(imageViewFlag);

    }

    return convertView;
  }

  private Filter rowFilter = new Filter() {
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
      FilterResults results = new FilterResults();
      List<MusicItem> suggestions = new ArrayList<>();

      if (constraint == null || constraint.length() == 0) {
        suggestions.addAll(autoListFull);
      } else {
        String filterPattern = constraint.toString().toLowerCase().trim();

        for (MusicItem item : autoListFull) {
          if (item.getArtistName().toLowerCase().contains(filterPattern)) {
            suggestions.add(item);
          }
        }
      }

      results.values = suggestions;
      results.count = suggestions.size();

      return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
      clear();
      addAll((List) results.values);
      notifyDataSetChanged();
    }

    @Override
    public CharSequence convertResultToString(Object resultValue) {
      return ((MusicItem) resultValue).getArtistName();
    }
  };
}