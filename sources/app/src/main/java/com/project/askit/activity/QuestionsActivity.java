package com.project.askit.activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.project.askit.QuestionsAdapter;
import com.project.askit.R;
import com.project.askit.RecyclerViewItemClick;
import com.project.askit.entity.Category;
import com.project.askit.entity.Question;
import com.project.askit.entity.QuestionStatistics;
import com.project.askit.entity.User;
import com.project.askit.entity.Wrapper;
import com.project.askit.model.NotificationModel;
import com.project.askit.model.QuestionItemModel;
import com.project.askit.model.StorageFileModel;
import com.project.askit.rest.CategoryRestApi;
import com.project.askit.rest.QuestionRestApi;
import com.project.askit.rest.RetrofitApi;
import com.project.askit.rest.UserRestApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class QuestionsActivity extends AppCompatActivity implements RecyclerViewItemClick {

    private static Logger logger;
    private static Context context;
    private RecyclerView questionRecyclerView;
    private EditText searchEditText;
    private Spinner categorySpinner;
    private Spinner sortBySpinner;
    private ConstraintLayout expandableView;
    private CategoryRestApi categoryRestApi;
    private QuestionRestApi questionRestApi;
    private UserRestApi userRestApi;
    private QuestionsAdapter questionsAdapter;
    private NestedScrollView questionsNestedScrollView;
    private LinearLayout filtersLinearLayout;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    public static Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        context = this;

        // Set up drawer button
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Initialize APIs
        categoryRestApi = (CategoryRestApi) RetrofitApi.getInstance(CategoryRestApi.class);
        questionRestApi = (QuestionRestApi) RetrofitApi.getInstance(QuestionRestApi.class);
        userRestApi = (UserRestApi) RetrofitApi.getInstance(UserRestApi.class);

        // Get components
        categorySpinner = findViewById(R.id.category_spinner);
        searchEditText = findViewById(R.id.search_edittext);
        sortBySpinner = findViewById(R.id.sort_by_spinner);

        // Fill spinners
        fillCategorySpinner();
        fillSortBySpinner();

        // Set recycler view
        questionRecyclerView = findViewById(R.id.question_recyclerview);
        questionRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Display items
        generateQuestionAdapterItems(getString(R.string.sort_by), getString(R.string.select_category), null);

        // Handle filter results animation
        expandableView = findViewById(R.id.filter_expandableview);
        TextView filterResultsTextView = findViewById(R.id.filter_results_textview);
        questionsNestedScrollView = findViewById(R.id.questions_nestedscrollview);
        filtersLinearLayout = findViewById(R.id.filters_linearlayout);
        filterResultsTextView.setOnClickListener(v -> {
            if (expandableView.getVisibility() == View.GONE) {
                filterResultsTextView.setTextColor(getResources().getColor(R.color.focused_text));
                expandableView.setVisibility(View.VISIBLE);
            } else {
                expandableView.setVisibility(View.GONE);
                filterResultsTextView.setTextColor(getResources().getColor(R.color.disabled_text));
            }

            TransitionManager.beginDelayedTransition(questionsNestedScrollView, new AutoTransition());
            TransitionManager.beginDelayedTransition(filtersLinearLayout, new AutoTransition());
        });

        // Handle on click event
        MaterialButton applyFiltersButton = findViewById(R.id.apply_filters_materialbutton);
        applyFiltersButton.setOnClickListener(v -> applyFilters());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void applyFilters() {
        String search = searchEditText.getText().toString();
        String categoryTitle = categorySpinner.getSelectedItem().toString();
        String sortBy = sortBySpinner.getSelectedItem().toString();

        generateQuestionAdapterItems(sortBy, categoryTitle, search);
    }

    private void fillCategorySpinner() {
        // Get data
        Call<Wrapper<Category>> call = categoryRestApi.findAllByFields(null, null, null, null, null);
        call.enqueue(new Callback<Wrapper<Category>>() {
            @Override
            public void onResponse(Call<Wrapper<Category>> call, Response<Wrapper<Category>> response) {
                if (response.isSuccessful()) {
                    List<String> categoryList = new ArrayList<>();

                    for (Category category : response.body().getContent()) {
                        categoryList.add(category.getTitle());
                    }

                    setCategorySpinnerAdapter(categoryList);
                    return;
                }

                // Response with error code
                logger.log(Level.SEVERE, response.body().toString());
            }

            @Override
            public void onFailure(Call<Wrapper<Category>> call, Throwable t) {
                handleError(t);
            }
        });
    }

    private void setCategorySpinnerAdapter(List<String> categoryList) {
        // Add default value
        categoryList.add(0, getString(R.string.select_category));

        // Create adapter and assign data to it
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(arrayAdapter);
    }

    public void fillSortBySpinner() {
        // Get data
        String[] sortByList = getResources().getStringArray(R.array.sort_by_array);

        // Create adapter and assign data to it
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sortByList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBySpinner.setAdapter(arrayAdapter);
    }

    public void generateQuestionAdapterItems(String sortBy, String categoryTitle, String search) {
        // Set sort and order
        String sort = null;
        String order = null;

        if (sortBy.equals(getString(R.string.oldest_first))) {
            sort = "created_date";
        } else if (sortBy.equals(getString(R.string.newest_first))) {
            sort = "created_date";
            order = "desc";
        } else if (sortBy.equals(getString(R.string.least_votes))) {
            sort = "votes";
        } else if (sortBy.equals(getString(R.string.most_votes))) {
            sort = "votes";
            order = "desc";
        } else if (sortBy.equals(getString(R.string.least_answers))) {
            sort = "answers";

        } else if (sortBy.equals(getString(R.string.most_answers))) {
            sort = "answers";
            order = "desc";
        } else if (sortBy.equals(getString(R.string.title_a_to_z))) {
            sort = "subject";

        } else if (sortBy.equals(getString(R.string.title_z_to_a))) {
            sort = "subject";
            order = "desc";
        }

        if (categoryTitle.equals(getString(R.string.select_category))) {
            categoryTitle = null;
        }

        // Create request
        Call<Wrapper<Question>> call = questionRestApi.findByQuery(null, 10, sort, order, categoryTitle, null, search, 1);
        List<QuestionItemModel> questionItemModelList = new ArrayList<>();

        call.enqueue(new Callback<Wrapper<Question>>() {
            @Override
            public void onResponse(Call<Wrapper<Question>> call, Response<Wrapper<Question>> response) {
                if (response.isSuccessful()) {
                    try {
                        for (Question question : response.body().getContent()) {
                            // Get category for each question
                            Call<Category> categoryCall = categoryRestApi.findById(question.getCategoryId());
                            Response<Category> categoryResponse = categoryCall.execute();

                            // Get question statistics
                            Call<QuestionStatistics> questionStatisticsCall = questionRestApi.getStatistics(question.getId());
                            Response<QuestionStatistics> questionStatisticsResponse = questionStatisticsCall.execute();

                            // Get user for each question
                            Call<User> userCall = userRestApi.findById(question.getUserId());
                            Response<User> userResponse = userCall.execute();

                            // Add data
                            questionItemModelList.add(new QuestionItemModel(
                                    question.getId(),
                                    categoryResponse.body().getTitle(),
                                    userResponse.body().getUsername(),
                                    question.getCreatedDate().toString(),
                                    question.getSubject(),
                                    questionStatisticsResponse.body().getVotes(),
                                    questionStatisticsResponse.body().getAnswers()
                            ));

                            // Update UI data
                            runOnUiThread(() -> setQuestionAdapter(questionItemModelList));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                // Response with error code
                logger.log(Level.SEVERE, response.body().toString());
            }

            @Override
            public void onFailure(Call<Wrapper<Question>> call, Throwable t) {
                handleError(t);
            }
        });
    }

    private void setQuestionAdapter(List<QuestionItemModel> questionItemModelList) {
        questionsAdapter = new QuestionsAdapter(questionItemModelList, this);
        questionRecyclerView.setAdapter(questionsAdapter);
    }

    private void handleError(Throwable throwable) {
        View currentView = findViewById(android.R.id.content);
        String message = "Couldn't fetch data. Please try again!";

        Snackbar snackbar = Snackbar.make(currentView, message, Snackbar.LENGTH_LONG)
                .setTextColor(ContextCompat.getColor(this, R.color.error_text))
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                .setDuration(5000)
                .setAction("Close", view -> {
                })
                .setActionTextColor(ContextCompat.getColor(this, R.color.error_background));
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.error_light_background));
        snackbar.show();

        throwable.printStackTrace();
    }

    @Override
    public void onItemClick(int position) {
        // Get data
        int questionId = questionsAdapter
                .getQuestionItemModelList()
                .get(position)
                .getQuestionId();

        // Put data
        Intent intent = new Intent(this, ViewQuestionActivity.class);
        intent.putExtra(ViewQuestionActivity.QUESTION_ID, questionId);

        startActivity(intent);
    }

    @Override
    public void onLongItemClick(int position) {
        String questionSubject = questionsAdapter
                .getQuestionItemModelList()
                .get(position)
                .getQuestionSubject();
        Toast.makeText(this, questionSubject, Toast.LENGTH_LONG).show();
    }

}