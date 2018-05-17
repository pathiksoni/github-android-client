package in.pathik.githubclient.network;

import java.util.List;

import in.pathik.githubclient.model.CommitContainer;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GithubService {

    String BASE_URL = "https://api.github.com/";
    String COMMIT_URL = "repos/{owner}/{repo}/commits?page=1&per_page=10";

    @GET(COMMIT_URL)
    Call<List<CommitContainer>> getCommits(@Path("owner") String owner, @Path("repo") String repo);
}

