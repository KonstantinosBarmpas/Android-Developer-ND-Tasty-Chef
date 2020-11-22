package thesonid.com.bakingapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;
import static java.lang.System.load;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeAdapterViewHolder> {
    private String[] mRecipeData;
    private String[] mImageData;
    private final RecipesAdapterOnClickHandler mClickHandler;


    public interface RecipesAdapterOnClickHandler {
        void onClick(String movieForThis);
    }

    public RecipesAdapter(RecipesAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class RecipeAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mTitle;
        public ImageView mImageView;
        public Context context;

        public RecipeAdapterViewHolder(View view) {
            super(view);
            mTitle=(TextView)view.findViewById(R.id.recipe_title);
            mImageView=(ImageView)view.findViewById(R.id.image_thumb_2);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String detailsForRecipes = mRecipeData[adapterPosition];
            mClickHandler.onClick(detailsForRecipes);
        }
    }

    @Override
    public RecipeAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RecipeAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeAdapterViewHolder recipeAdapterViewHolder, int position) {
        String recipesDetails = mRecipeData[position];
        recipeAdapterViewHolder.mTitle.setText(recipesDetails);
        if (mImageData!=null) {
            recipesDetails = mImageData[position];
            if (recipesDetails.equals("-")) {
                recipeAdapterViewHolder.mImageView.setVisibility(View.GONE);
            } else {
                recipeAdapterViewHolder.mImageView.setVisibility(View.VISIBLE);
                Picasso.with(recipeAdapterViewHolder.mImageView.getContext()).load(recipesDetails).into(recipeAdapterViewHolder.mImageView);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (null == mRecipeData) return 0;
        return mRecipeData.length;
    }

    public void setRecipeData(String[] data) {
        mRecipeData = data;
        notifyDataSetChanged();
    }

    public void setImageData(String[] data) {
        mImageData = data;
        notifyDataSetChanged();
    }

}