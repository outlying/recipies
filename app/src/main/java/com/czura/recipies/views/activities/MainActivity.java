package com.czura.recipies.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.czura.recipies.R;
import com.czura.recipies.RecipesApplication;
import com.czura.recipies.injector.components.DaggerMainActivityComponent;
import com.czura.recipies.injector.modules.ActivityModule;
import com.czura.recipies.model.entities.Recipe;
import com.czura.recipies.mvp.presenters.RecipesListPresenter;
import com.czura.recipies.mvp.views.RecipesListView;
import com.czura.recipies.utils.Constants;
import com.czura.recipies.views.RecyclerViewClick;
import com.czura.recipies.views.adapters.RecipeListAdapter;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;


//TODO: rotation change
public class MainActivity extends AppCompatActivity implements RecipesListView {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.recipesList)
    RecyclerView recipesListView;

    @Bind(R.id.refreshRecipesLayout)
    SwipeRefreshLayout refreshLayout;

    @Inject
    RecipesListPresenter recipesListPresenter;

    private RecipeListAdapter recipeListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initializeToolbar();
        initializeInjector();
        initializePresenter();
        initializeRecyclerView();
    }

    private void initializeToolbar() {
        toolbar.setTitle(R.string.app_name);
    }

    private void initializeInjector() {
        RecipesApplication application = (RecipesApplication) getApplication();

        DaggerMainActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .appComponent(application.getAppComponent())
                .build().inject(this);
    }

    private void initializePresenter() {
        recipesListPresenter.attachView(this);
        recipesListPresenter.onCreate();
    }

    private void initializeRecyclerView() {
        recipesListView.setLayoutManager(new LinearLayoutManager(this));
        refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recipesListPresenter.downloadRecipes();
            }
        });
    }

    @Override
    public void bindRecipeList(List<Recipe> recipes) {
        recipeListAdapter = new RecipeListAdapter(this, recipes, onRecipeClick);
        recipesListView.setAdapter(recipeListAdapter);
    }

    private RecyclerViewClick<Recipe> onRecipeClick = new RecyclerViewClick<Recipe>() {
        @Override
        public void onItemClicked(Recipe item) {
            recipesListPresenter.onRecipeClick(item);
        }
    };

    @Override
    public void showLoading() {
//        refreshLayout.post(new Runnable() {
//            @Override
//            public void run() {
        refreshLayout.setRefreshing(true);
//            }
//        });
    }

    @Override
    public void hideLoading() {
//        refreshLayout.post(new Runnable() {
//            @Override
//            public void run() {
        refreshLayout.setRefreshing(false);
//            }
//        });
    }

    @Override
    public void showRecipeDetails(Recipe recipe) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra(Constants.RECIPE_TAG, recipe);
        startActivity(intent);
    }
}
