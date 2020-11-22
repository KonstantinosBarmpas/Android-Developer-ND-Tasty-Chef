package thesonid.com.movieapp1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import thesonid.com.movieapp1.utilities.NetworkUtils;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {
    private String[] mReviewData;
    public TextView mReviewText,mReviewNumber;

    private static final String TAG = NetworkUtils.class.getSimpleName();

    public ReviewAdapter() {
        mReviewText = null;
        mReviewNumber=null;
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder  {

        public ReviewAdapterViewHolder(View view) {
            super(view);
            mReviewText=(TextView) view.findViewById(R.id.text_of_display);
            mReviewNumber=(TextView)view.findViewById(R.id.review_number);
        }
    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_reviews;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder reviewAdapterViewHolder, int position) {
        String reviewDetails;
        if (mReviewData[0]!="(No reviews found)"){
            mReviewNumber.setText("Review"+ (position + 1));
            reviewDetails =mReviewData[position];}else{
            reviewDetails =mReviewData[position];
        }
        mReviewText.setText(reviewDetails);
    }

    @Override
    public int getItemCount() {
        if (null == mReviewData) return 0;
        return mReviewData.length;
    }

    public void setReviewData(String[] reviewData) {
            mReviewData = reviewData;
            notifyDataSetChanged();
    }

}
