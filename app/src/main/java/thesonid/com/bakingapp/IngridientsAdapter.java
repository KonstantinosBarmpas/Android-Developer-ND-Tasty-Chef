package thesonid.com.bakingapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by user on 8/8/17.
 */

public class IngridientsAdapter extends RecyclerView.Adapter<IngridientsAdapter.RecipeAdapterViewHolder> {
    private String[] mRecipeData;
    private final RecipesAdapterOnClickHandler mClickHandler;


    public interface RecipesAdapterOnClickHandler {
        void onClick(String movieForThis);
    }

    public IngridientsAdapter(RecipesAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class RecipeAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mIngridients;

        public RecipeAdapterViewHolder(View view) {
            super(view);
            mIngridients=(TextView)view.findViewById(R.id.ingridients_text);
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
        int layoutIdForListItem = R.layout.list_item_2;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RecipeAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeAdapterViewHolder recipeAdapterViewHolder, int position) {
        String ingridientsDetails = mRecipeData[position];
        recipeAdapterViewHolder.mIngridients.setText(ingridientsDetails);
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

}
