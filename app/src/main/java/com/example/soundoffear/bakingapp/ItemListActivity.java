package com.example.soundoffear.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.soundoffear.bakingapp.Utilities.JSON_Utilis;
import com.example.soundoffear.bakingapp.interfaces.OnRecyclerViewClickListener;
import com.example.soundoffear.bakingapp.models.StepsModel;
import com.example.soundoffear.bakingapp.recipeFragments.DetailsFragmentStep;
import com.example.soundoffear.bakingapp.recipeFragments.IngredientsFragment;

import java.util.ArrayList;
import java.util.List;


public class ItemListActivity extends AppCompatActivity implements OnRecyclerViewClickListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private String ingredients;
    private String steps;
    private List<StepsModel> stepsModelList;
    private List<String> titlesSteps;
    public int lastPosition;
    static final String LAST_POSITION = "last_open_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        Intent receivedIntent = getIntent();
        ingredients = receivedIntent.getStringExtra(RecipeListActivity.INGREDIENTS_KEY);
        steps = receivedIntent.getStringExtra(RecipeListActivity.STEPS_KEY);

        stepsModelList = JSON_Utilis.allSteps(steps);

        titlesSteps = new ArrayList<>();
        titlesSteps.clear();
        titlesSteps.add("Ingredients");
        for(int i = 0; i<stepsModelList.size();i++) {
            titlesSteps.add(stepsModelList.get(i).getStepShortDescription());
        }

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            if(savedInstanceState == null) {
                mTwoPane = true;
                IngredientsFragment ingredientsFragment = new IngredientsFragment();
                Bundle ingredientsBundle = new Bundle();
                ingredientsBundle.putString(RecipeDetailActivity.ING_ARG_FRAGMENT, ingredients);
                ingredientsFragment.setArguments(ingredientsBundle);
                getSupportFragmentManager().beginTransaction().add(R.id.item_detail_container, ingredientsFragment).commit();
            } else {
                int positionRestored = savedInstanceState.getInt(LAST_POSITION);
                if(positionRestored == 0) {
                    IngredientsFragment ingredientsFragment = new IngredientsFragment();
                    Bundle ingredientsBundle = new Bundle();
                    ingredientsBundle.putString(RecipeDetailActivity.ING_ARG_FRAGMENT, ingredients);
                    ingredientsFragment.setArguments(ingredientsBundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, ingredientsFragment).commit();
                } else {
                    positionRestored = positionRestored - 1;
                    DetailsFragmentStep detailsFragmentStep = new DetailsFragmentStep();
                    Bundle fragmentDataBundle = new Bundle();
                    fragmentDataBundle.putString(StepDetailsActivity.BUNDLE_ID, stepsModelList.get(positionRestored).getStepId());
                    fragmentDataBundle.putString(StepDetailsActivity.BUNDLE_DESC, stepsModelList.get(positionRestored).getStepDescription());
                    fragmentDataBundle.putString(StepDetailsActivity.BUNDLE_TITLE, stepsModelList.get(positionRestored).getStepShortDescription());
                    fragmentDataBundle.putString(StepDetailsActivity.BUNDLE_VIDEO, stepsModelList.get(positionRestored).getStepVideoURL());
                    fragmentDataBundle.putString(StepDetailsActivity.BUNDLE_THUMB, stepsModelList.get(positionRestored).getStepThumbnailURL());
                    detailsFragmentStep.setArguments(fragmentDataBundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, detailsFragmentStep).commit();
                }
            }

        }


    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(titlesSteps, new OnRecyclerViewClickListener() {
            @Override
            public void onRecyclerViewClick(int position) {
                if(position == 0) {
                    lastPosition = position;
                    IngredientsFragment ingredientsFragment = new IngredientsFragment();
                    Bundle ingredientsBundle = new Bundle();
                    ingredientsBundle.putString(RecipeDetailActivity.ING_ARG_FRAGMENT, ingredients);
                    ingredientsFragment.setArguments(ingredientsBundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, ingredientsFragment).commit();
                } else {
                    lastPosition = position;
                    position = position - 1;
                    DetailsFragmentStep detailsFragmentStep = new DetailsFragmentStep();
                    Bundle fragmentDataBundle = new Bundle();
                    fragmentDataBundle.putString(StepDetailsActivity.BUNDLE_ID, stepsModelList.get(position).getStepId());
                    fragmentDataBundle.putString(StepDetailsActivity.BUNDLE_DESC, stepsModelList.get(position).getStepDescription());
                    fragmentDataBundle.putString(StepDetailsActivity.BUNDLE_TITLE, stepsModelList.get(position).getStepShortDescription());
                    fragmentDataBundle.putString(StepDetailsActivity.BUNDLE_VIDEO, stepsModelList.get(position).getStepVideoURL());
                    fragmentDataBundle.putString(StepDetailsActivity.BUNDLE_THUMB, stepsModelList.get(position).getStepThumbnailURL());
                    detailsFragmentStep.setArguments(fragmentDataBundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, detailsFragmentStep).commit();
                }
            }
        }));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(LAST_POSITION, lastPosition);
    }

    @Override
    public void onRecyclerViewClick(int position) {

    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.StepsViewHolder> {

        List<String> stepsModelList;
        OnRecyclerViewClickListener onRecyclerViewClickListener;

        public SimpleItemRecyclerViewAdapter(List<String> stepsModelList, OnRecyclerViewClickListener onRecyclerViewClickListener) {
            this.stepsModelList = stepsModelList;
            this.onRecyclerViewClickListener = onRecyclerViewClickListener;
        }

        @NonNull
        @Override
        public StepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View stepsView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.recipe_steps_item_list, parent, false);

            return new StepsViewHolder(stepsView);
        }

        @Override
        public void onBindViewHolder(@NonNull StepsViewHolder holder, int position) {

            holder.tv_title.setText(stepsModelList.get(position));

        }

        @Override
        public int getItemCount() {
            return stepsModelList.size();
        }

        class StepsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView tv_title;

            StepsViewHolder(View itemView) {
                super(itemView);

                tv_title = itemView.findViewById(R.id.steps_title);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                onRecyclerViewClickListener.onRecyclerViewClick(getLayoutPosition());
            }
        }
    }
}
