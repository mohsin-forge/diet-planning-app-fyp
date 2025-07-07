package tech.demoproject.android_otp_vertification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;  // Make sure this import is included

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class recipeDetailsActivity extends AppCompatActivity { // Capitalize class name

    private TextView tvRecipeName, tvIngredients, tvPreparationSteps, tvCookingTime, tvNutritionalContent;
    private DatabaseReference databaseReference;
    private String recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        tvRecipeName = findViewById(R.id.tv_recipe_name);
        tvIngredients = findViewById(R.id.tv_ingredients);
        tvPreparationSteps = findViewById(R.id.tv_preparation_steps);
        tvCookingTime = findViewById(R.id.tv_cooking_time);
        tvNutritionalContent = findViewById(R.id.tv_nutritional_content);

        recipeId = getIntent().getStringExtra("recipeId");
        databaseReference = FirebaseDatabase.getInstance().getReference("recipes").child(recipeId);

        loadRecipeDetails();
    }

    private void loadRecipeDetails() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                if (recipe != null) {
                    tvRecipeName.setText(recipe.getName());
                    tvIngredients.setText("Ingredients: " + recipe.getIngredients());
                    tvPreparationSteps.setText("Preparation Steps: " + recipe.getPreparationSteps());
                    tvCookingTime.setText("Cooking Time: " + recipe.getCookingTime() + " minutes");
                    tvNutritionalContent.setText("Nutritional Content (per serving): " + recipe.getNutritionalContent());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
}
