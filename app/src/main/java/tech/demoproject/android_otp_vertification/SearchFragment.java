package tech.demoproject.android_otp_vertification;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchFragment extends Fragment {

    private SearchView searchView;
    private Spinner spinnerFilter;
    private ListView listViewRecipes;
    private List<Recipe> recipeList;
    private ArrayAdapter<Recipe> recipeAdapter;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        RecipeData recipeData = new RecipeData();
        recipeData.addSampleRecipes();

        searchView = view.findViewById(R.id.search_view);
        spinnerFilter = view.findViewById(R.id.spinner_filter);
        listViewRecipes = view.findViewById(R.id.list_view_recipes);

        recipeList = new ArrayList<>();
        recipeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, recipeList);
        listViewRecipes.setAdapter(recipeAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("recipes");

        setupSearchView();
        setupSpinnerFilter();
        setupListViewClickListener();

        loadAllRecipes();

        return view;
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchRecipes(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchRecipes(newText);
                return true;
            }
        });
    }

    private void setupSpinnerFilter() {
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterRecipes(spinnerFilter.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupListViewClickListener() {
        listViewRecipes.setOnItemClickListener((parent, view, position, id) -> {
            Recipe selectedRecipe = recipeList.get(position);
            Intent intent = new Intent(getActivity(), recipeDetailsActivity.class);
            intent.putExtra("recipeId", selectedRecipe.getId());
            startActivity(intent);
        });
    }

    private void loadAllRecipes() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        recipeList.add(recipe);
                    }
                }

                // Filter out duplicate recipes
                List<Recipe> uniqueRecipes = new ArrayList<>();
                Set<String> recipeNames = new HashSet<>();

                for (Recipe recipe : recipeList) {
                    if (!recipeNames.contains(recipe.getName())) {
                        uniqueRecipes.add(recipe);
                        recipeNames.add(recipe.getName());
                    }
                }

                // Update the recipeList with unique recipes
                recipeList = uniqueRecipes;

                recipeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("SearchFragment", "Failed to load recipes", databaseError.toException());
                Toast.makeText(getContext(), "Failed to load recipes. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchRecipes(String query) {
        if (TextUtils.isEmpty(query)) {
            loadAllRecipes();
        } else {
            List<Recipe> filteredList = new ArrayList<>();
            for (Recipe recipe : recipeList) {
                if (recipe.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(recipe);
                }
            }
            updateListView(filteredList);
        }
    }

    private void filterRecipes(String filter) {
        List<Recipe> filteredRecipes = new ArrayList<>();
        for (Recipe recipe : recipeList) {
            if (filter.equals("All") ||
                    recipe.getMealType().equalsIgnoreCase(filter) ||
                    recipe.getCuisine().equalsIgnoreCase(filter) ||
                    recipe.getDietaryPreferences().contains(filter)) {
                filteredRecipes.add(recipe);
            }
        }
        updateListView(filteredRecipes);
    }

    private void updateListView(List<Recipe> filteredList) {
        recipeAdapter.clear();
        recipeAdapter.addAll(filteredList);
        recipeAdapter.notifyDataSetChanged();
    }
}
