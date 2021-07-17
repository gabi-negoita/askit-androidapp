package com.project.askit.activity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.project.askit.R;
import com.project.askit.entity.Category;
import com.project.askit.entity.Question;
import com.project.askit.entity.QuestionStatistics;
import com.project.askit.entity.User;
import com.project.askit.model.QuestionItemModel;
import com.project.askit.model.StorageFileModel;
import com.project.askit.rest.CategoryRestApi;
import com.project.askit.rest.QuestionRestApi;
import com.project.askit.rest.RetrofitApi;
import com.project.askit.rest.UserRestApi;
import com.project.askit.util.Utility;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewQuestionActivity extends AppCompatActivity {

    public static final String QUESTION_ID = ViewQuestionActivity.class.getCanonicalName() + ".QUESTION_ID";

    Logger logger;

    private CategoryRestApi categoryRestApi;
    private QuestionRestApi questionRestApi;
    private UserRestApi userRestApi;

    private TextView categoryTitleTextView;
    private TextView usernameTextView;
    private TextView questionTimeTextView;
    private TextView questionSubjectTextView;
    private TextView votesTextView;
    private TextView answersTextView;
    private WebView htmlTextWebView;
    private TextView htmlTextTextView;
    private ImageButton upVoteImageButton;
    private ImageButton downVoteImageButton;

    private EditText logsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_question);

        // check if data exists
        if (!getIntent().hasExtra(QUESTION_ID)) {
            String message = "No data found";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            logger.log(Level.SEVERE, message);
            return;
        }

        // Initialize APIs
        categoryRestApi = (CategoryRestApi) RetrofitApi.getInstance(CategoryRestApi.class);
        questionRestApi = (QuestionRestApi) RetrofitApi.getInstance(QuestionRestApi.class);
        userRestApi = (UserRestApi) RetrofitApi.getInstance(UserRestApi.class);

        // Initialize question components
        categoryTitleTextView = findViewById(R.id.category_title_textview);
        usernameTextView = findViewById(R.id.username_textview);
        questionTimeTextView = findViewById(R.id.question_time_textview);
        questionSubjectTextView = findViewById(R.id.questions_subject_textview);
        votesTextView = findViewById(R.id.votes_textview);
        answersTextView = findViewById(R.id.answers_textview);
        htmlTextWebView = findViewById(R.id.html_text_webview);
        htmlTextTextView = findViewById(R.id.html_text_textview);
        upVoteImageButton = findViewById(R.id.up_vote_imagebutton);
        downVoteImageButton = findViewById(R.id.down_vote_imagebutton);


        // Render UI data
        renderQuestion();

        // TODO: Generate answers

        // Set buttons color
        upVoteImageButton.setColorFilter(getResources().getColor(R.color.black));
        downVoteImageButton.setColorFilter(getResources().getColor(R.color.black));

        // Handle up vote
        upVoteImageButton.setOnClickListener(v -> {
            upVoteImageButton.setColorFilter(getResources().getColor(R.color.askit_green));
            downVoteImageButton.setColorFilter(getResources().getColor(R.color.black));
        });

        // Handle down vote
        downVoteImageButton.setOnClickListener(v -> {
            downVoteImageButton.setColorFilter(getResources().getColor(R.color.askit_orange));
            upVoteImageButton.setColorFilter(getResources().getColor(R.color.black));
        });


        // Display logs
        logsTextView = findViewById(R.id.logs_textview);

        StorageFileModel storageFileModel = new StorageFileModel(this, "logs.txt");
        String fileContent = storageFileModel.readFromExternalStorage();

        logsTextView.setText(fileContent);
        System.out.println(fileContent);
    }

    private void renderQuestion() {
        // Get data
        int questionId = getIntent().getIntExtra(QUESTION_ID, -1);

        // Fetch data
        Call<Question> call = questionRestApi.findById(questionId);
        call.enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {
                if (response.isSuccessful()) {
                    QuestionItemModel questionItemModel;
                    Question question = response.body();
                    try {
                        // Get category
                        Call<Category> categoryCall = categoryRestApi.findById(question.getCategoryId());
                        Response<Category> categoryResponse = categoryCall.execute();

                        // Check if call was successful
                        if (!categoryResponse.isSuccessful()) {
                            logger.log(Level.SEVERE, categoryResponse.body().toString());
                            return;
                        }
                        Category category = categoryResponse.body();

                        // Get question statistics
                        Call<QuestionStatistics> questionStatisticsCall = questionRestApi.getStatistics(question.getId());
                        Response<QuestionStatistics> questionStatisticsResponse = questionStatisticsCall.execute();

                        // Check if call was successful
                        if (!questionStatisticsResponse.isSuccessful()) {
                            logger.log(Level.SEVERE, questionStatisticsResponse.body().toString());
                            return;
                        }
                        QuestionStatistics questionStatistics = questionStatisticsResponse.body();


                        // Get user
                        Call<User> userCall = userRestApi.findById(question.getUserId());
                        Response<User> userResponse = userCall.execute();

                        // Check if call was successful
                        if (!userResponse.isSuccessful()) {
                            logger.log(Level.SEVERE, userResponse.body().toString());
                            return;
                        }
                        User user = userResponse.body();

                        // Set data
                        questionItemModel = new QuestionItemModel(
                                question.getId(),
                                category.getTitle(),
                                user.getUsername(),
                                question.getCreatedDate().toString(),
                                question.getSubject(),
                                questionStatistics.getVotes(),
                                questionStatistics.getAnswers());

                        // Update UI data
                        runOnUiThread(() -> setQuestionData(questionItemModel, question.getHtmlText()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                // Response with error code
                logger.log(Level.SEVERE, response.body().toString());
            }

            @Override
            public void onFailure(Call<Question> call, Throwable t) {
                handleError(t);
            }
        });
    }

    private void setQuestionData(QuestionItemModel questionItemModel, String htmlText) {
        categoryTitleTextView.setText(questionItemModel.getCategoryTitle());
        usernameTextView.setText(questionItemModel.getUsername());
        questionTimeTextView.setText(Utility.getPrettyTimeFromString(questionItemModel.getQuestionCreatedDate()));
        questionSubjectTextView.setText(questionItemModel.getQuestionSubject());

        votesTextView.setText(questionItemModel.getVotes() + " " + getResources().getString(R.string.votes));
        answersTextView.setText(questionItemModel.getAnswers() + " " + getResources().getString(R.string.answers));

        htmlTextWebView.getSettings().setJavaScriptEnabled(true);
        htmlTextWebView.getSettings().setBlockNetworkImage(false);
        htmlTextWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // Load html
        htmlTextWebView.loadData(htmlText, "text/html; charset=utf-8", "UTF-8");

        htmlTextTextView.setText(Html.fromHtml(htmlText, Html.FROM_HTML_OPTION_USE_CSS_COLORS));
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
}