package in.pathik.githubclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import in.pathik.githubclient.model.CommitContainer;
import in.pathik.githubclient.network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TableLayout tlCommits;
    private EditText etOwner;
    private EditText etGHRepo;
    private static final DateFormat DF = new SimpleDateFormat(("MM/dd/yyyy HH:mm a z"), Locale.US);
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tlCommits = findViewById(R.id.commits);
        etOwner = findViewById(R.id.etOwner);
        etGHRepo = findViewById(R.id.etGHRepo);
        spinner = findViewById(R.id.progressBar);
    }

    private void addHeader() {
        TableRow row = (TableRow) LayoutInflater.from(getBaseContext()).inflate(R.layout.commit_element, null, true);
        TextView tvSha = row.findViewById(R.id.tvSha);
        TextView tvCommitterName = row.findViewById(R.id.tvCommitterName);
        TextView tvCommittedDate = row.findViewById(R.id.tvCommittedDate);
        tvSha.setTextSize(16);
        tvCommitterName.setTextSize(16);
        tvCommittedDate.setTextSize(16);
        tlCommits.addView(row);
    }

    private void addRow(CommitContainer commit) {

        TableRow row = (TableRow) LayoutInflater.from(getBaseContext()).inflate(R.layout.commit_element, null, true);
        TextView tvSha = row.findViewById(R.id.tvSha);
        TextView tvCommitterName = row.findViewById(R.id.tvCommitterName);
        TextView tvCommittedDate = row.findViewById(R.id.tvCommittedDate);
        tvCommitterName.setText(commit.getCommit().getAuthor().getName());
        tvCommittedDate.setText(DF.format(commit.getCommit().getAuthor().getDate()));
        tvSha.setText(commit.getSha().substring(0, 7));
        tlCommits.addView(row);
    }

    public void fetchCommits(View view) {
        tlCommits.removeAllViews();
        String owner = etOwner.getText().toString();
        String repo = etGHRepo.getText().toString();
        if(owner.isEmpty()){
            Toast.makeText(this,"Owner Name can not be empty", Toast.LENGTH_LONG).show();
            return;
        }
        if(repo.isEmpty()){
            Toast.makeText(this,"Github Repository Name can not be empty", Toast.LENGTH_LONG).show();
            return;
        }
        spinner.setVisibility(View.VISIBLE);
        ApiClient client = ApiClient.getInstance();
        Call<List<CommitContainer>> response = client.getCommits(owner, repo);
        response.enqueue(new Callback<List<CommitContainer>>() {
            @Override
            public void onFailure(Call<List<CommitContainer>> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t);
                spinner.setVisibility(View.GONE);
                //Toast.makeText(this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call<List<CommitContainer>> call, Response<List<CommitContainer>> response) {
                spinner.setVisibility(View.GONE);
                if (response.body() != null && response.isSuccessful()) {
                    List<CommitContainer> commits = response.body();
                    addHeader();
                    for (CommitContainer commit : commits) {
                        Log.i(TAG, commit.getSha());
                        addRow(commit);
                    }
                } else {
                    Log.w(TAG, response.toString());
                    switch (response.code()) {
                        case 404:
                            Toast.makeText(MainActivity.this, "Repo not found", Toast.LENGTH_LONG).show();
                            break;
                        case 500:
                            Toast.makeText(MainActivity.this, "server broken", Toast.LENGTH_LONG).show();
                            break;
                        default:
                            Toast.makeText(MainActivity.this, "unknown error", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            }
        });
    }
}
