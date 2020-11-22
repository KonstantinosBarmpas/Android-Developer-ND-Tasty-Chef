package thesonid.com.movieapp1;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {
    private String[] mTrailerData;
    public TextView mTrailerText;
    private ImageView mPlay;
    private final TrailerAdapterOnClickHandler mClickHandler;


    public TrailerAdapter(TrailerAdapterOnClickHandler clickHandler) {
        mTrailerText = null;
        mClickHandler = clickHandler;
    }

    public interface TrailerAdapterOnClickHandler {
        void onClick(String trailerForThis);
    }


    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        public TrailerAdapterViewHolder(View view) {
            super(view);
            mTrailerText =(TextView) view.findViewById(R.id.text_of_display);
            mPlay=(ImageView) view.findViewById(R.id.play);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String detailsForTrailer = mTrailerData[adapterPosition];
            mClickHandler.onClick(detailsForTrailer);
        }
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_trailers;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder trailerAdapterViewHolder, int position) {
        String trailerDetails;
        if (mTrailerData[0] != "(No trailers found)") {
            trailerDetails="Trailer " + (position + 1) + " ";
            mTrailerText.setText(trailerDetails);
        } else {
            trailerDetails = mTrailerData[position];
            mTrailerText.setText(trailerDetails);
        }
    }

    @Override
    public int getItemCount() {
        if (null == mTrailerData) return 0;
        return mTrailerData.length;
    }

    public void setTrailerData(String[] trailerData) {
            mTrailerData = trailerData;
            notifyDataSetChanged();
    }

}
