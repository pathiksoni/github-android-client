package in.pathik.githubclient.network;


import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import in.pathik.githubclient.model.CommitContainer;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;


public class ApiClient {

    private static final String TAG = ApiClient.class.getSimpleName();
    private static ApiClient instance;
    private Retrofit retrofit;
    private GithubService service;


    public static ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    private ApiClient() {
        final Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(GithubService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        service = retrofit.create(GithubService.class);
    }

    public Call<List<CommitContainer>>  getCommits(String owner, String repo) {
        return service.getCommits(owner, repo);
    }
}
