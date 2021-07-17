package com.project.askit;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.project.askit.activity.QuestionsActivity;
import com.project.askit.model.NotificationModel;
import com.project.askit.model.QuestionItemModel;
import com.project.askit.model.StorageFileModel;
import com.project.askit.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

    private final RecyclerViewItemClick recyclerViewItemClick;
    private final List<QuestionItemModel> questionItemModelList;

    // Initialize data set of Adapter.
    public QuestionsAdapter(List<QuestionItemModel> questionItemModelList, RecyclerViewItemClick recyclerViewItemClick) {
        this.questionItemModelList = questionItemModelList;
        this.recyclerViewItemClick = recyclerViewItemClick;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_question, viewGroup, false);
        return new ViewHolder(view);
    }

    // Replace the contents of view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get data and replace data in view
        viewHolder.getCategoryTitleTextView().setText(questionItemModelList.get(position).getCategoryTitle());
        viewHolder.getUsernameTextView().setText(questionItemModelList.get(position).getUsername());
        viewHolder.getQuestionTimeTextView().setText(Utility.getRelativeTime(questionItemModelList.get(position).getQuestionCreatedDate(), QuestionsActivity.getContext()));
        viewHolder.getQuestionSubjectTextView().setText(questionItemModelList.get(position).getQuestionSubject());
        viewHolder.getVotesTextView().setText(questionItemModelList.get(position).getVotes() + " " + viewHolder.itemView.getContext().getString(R.string.votes));
        viewHolder.getAnswersTextView().setText(questionItemModelList.get(position).getAnswers() + " " + viewHolder.itemView.getContext().getString(R.string.answers));

        // Check for followed questions
        StorageFileModel storageFileModel = new StorageFileModel(QuestionsActivity.getContext());
        storageFileModel.setFileName("followed_questions");
        String fileContent = storageFileModel.readFromInternalStorage();

        if (fileContent == null || fileContent.length() == 0) {
            return;
        }

        String[] followedQuestions = fileContent.split("<!>");
        List<Integer> followedQuestionIds = new ArrayList<>();
        for (String questionId : followedQuestions) {
            followedQuestionIds.add(Integer.parseInt(questionId));
        }
        if (followedQuestionIds.contains(new Integer(questionItemModelList.get(position).getQuestionId()))) {
            viewHolder.getFollowImageButton().setBackground(ContextCompat.getDrawable(QuestionsActivity.getContext(), R.drawable.ic_star));
        }
    }

    // Get size of data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.questionItemModelList.size();
    }

    public List<QuestionItemModel> getQuestionItemModelList() {
        return questionItemModelList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView categoryTitleTextView;
        private final TextView usernameTextView;
        private final TextView questionTimeTextView;
        private final TextView questionSubjectTextView;
        private final TextView votesTextView;
        private final TextView answersTextView;
        private final ImageButton followImageButton;

        public ViewHolder(View itemView) {
            super(itemView);

            // Initialize data
            this.categoryTitleTextView = itemView.findViewById(R.id.category_title_textview);
            this.usernameTextView = itemView.findViewById(R.id.username_textview);
            this.questionTimeTextView = itemView.findViewById(R.id.question_time_textview);
            this.questionSubjectTextView = itemView.findViewById(R.id.questions_subject_textview);
            this.votesTextView = itemView.findViewById(R.id.votes_textview);
            this.answersTextView = itemView.findViewById(R.id.answers_textview);
            this.followImageButton = itemView.findViewById(R.id.follow_imagebutton);

            // Define click listener
            itemView.setOnClickListener(v -> recyclerViewItemClick.onItemClick(getAdapterPosition()));

            // Define long click listener
            itemView.setOnLongClickListener(v -> {
                recyclerViewItemClick.onLongItemClick(getAdapterPosition());
                return true;
            });

            // Handle follow button click
            followImageButton.setOnClickListener(v -> {
                String messageTitle;
                String messageContent;
                StorageFileModel storageFileModel = new StorageFileModel(itemView.getContext());

                // Toggle image
                Drawable.ConstantState drawable = followImageButton.getBackground().getConstantState();
                if (drawable.equals(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_star_outline).getConstantState())) {
                    // Follow question
                    followImageButton.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_star));
                    messageTitle = "Followed question";
                    messageContent = "Followed \"" + questionItemModelList.get(getAdapterPosition()).getQuestionSubject() + "\"";

                    // Write question id to internal file
                    storageFileModel.setFileName("followed_questions");
                    String content = questionItemModelList.get(getAdapterPosition()).getQuestionId().toString() + "<!>";
                    storageFileModel.appendToInternalStorage(content);
                } else {
                    // Unfollow question
                    followImageButton.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_star_outline));
                    messageTitle = "Unfollowed question";
                    messageContent = "Unfollowed \"" + questionItemModelList.get(getAdapterPosition()).getQuestionSubject() + "\"";

                    // Delete question id from internal file
                    storageFileModel.setFileName("followed_questions");
                    String fileContent = storageFileModel.readFromInternalStorage();

                    if (fileContent != null && fileContent.length() > 0) {
                        String[] followedQuestions = fileContent.split("<!>");
                        List<Integer> followedQuestionIds = new ArrayList<>();
                        for (String questionId : followedQuestions) {
                            followedQuestionIds.add(Integer.parseInt(questionId));
                        }
                        followedQuestionIds.remove(questionItemModelList.get(getAdapterPosition()).getQuestionId());
                        fileContent = "";
                        for (Integer questionId : followedQuestionIds) {
                            fileContent += questionId + "<!>";
                        }
                        storageFileModel.writeToInternalStorage(fileContent);
                    }


                }

                // Write log to external file
                storageFileModel.setFileName("logs.txt");
                storageFileModel.appendToExternalStorage(messageContent + "\n\r");

                // Display notification
                NotificationModel notificationModel = new NotificationModel(
                        itemView.getContext(),
                        messageTitle,
                        messageContent);
                notificationModel.show();
            });
        }

        public TextView getCategoryTitleTextView() {
            return categoryTitleTextView;
        }

        public TextView getUsernameTextView() {
            return usernameTextView;
        }

        public TextView getQuestionTimeTextView() {
            return questionTimeTextView;
        }

        public TextView getQuestionSubjectTextView() {
            return questionSubjectTextView;
        }

        public TextView getVotesTextView() {
            return votesTextView;
        }

        public TextView getAnswersTextView() {
            return answersTextView;
        }

        public ImageButton getFollowImageButton() {
            return followImageButton;
        }
    }
}
