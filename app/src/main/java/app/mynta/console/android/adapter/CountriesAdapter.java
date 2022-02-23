package app.mynta.console.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.mynta.console.android.R;
import app.mynta.console.android.interfaces.OnCountryClickListener;
import app.mynta.console.android.models.Countries;

public class CountriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final List<Countries> countriesList;
    private final OnCountryClickListener listener;

    public CountriesAdapter(Context context, List<Countries> countriesList, OnCountryClickListener listener) {
        this.context = context;
        this.countriesList = countriesList;
        this.listener = listener;
    }

    class CountriesHolder extends RecyclerView.ViewHolder {

        ImageView flag;
        TextView title;

        CountriesHolder(@NonNull View itemView) {
            super(itemView);
            flag = itemView.findViewById(R.id.country_flag);
            title = itemView.findViewById(R.id.country_title);
        }

        public void setData(Countries data) {
            flag.setImageResource(context.getResources().getIdentifier(data.getFlag(),
                    "drawable", context.getPackageName()));
            title.setText(data.getTitle());
        }

        void bind(final Countries item, final OnCountryClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_country, parent, false);
        return new CountriesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Countries maps = countriesList.get(position);
        CountriesHolder countriesHolder = (CountriesHolder) holder;
        countriesHolder.setData(maps);
        countriesHolder.bind(countriesList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return countriesList.size();
    }
}
