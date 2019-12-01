package CommitSearchTransaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHCommitQueryBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.PagedIterable;

public class CommitSearchTransaction {

	private static int commitId = 0;
	private static List<String> getAthur = new ArrayList<>();

	/*
	 * kendi repositolerimi çekmek için kullandýðým metod
	 */
	public static void getMyRepository() throws IOException {
		GitHubClient client = new GitHubClient();
		client.setCredentials("cedellalahmet@gmail.com", "****");
		RepositoryService repositoryService = new RepositoryService(client);

		List<Repository> repositories = repositoryService.getRepositories();

		for (Repository repository : repositories) {

			System.out.println("Repository Name:" + repository.getName());
		}
	}

	/*
	 * github api de bulunan tüm repolarý taramak için kullandýðým metod--- Rate
	 * limit hatasý çýkýyor.
	 */

	public static void getAllCommitUrlReader() throws MalformedURLException, IOException {

		HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(
				"https://api.github.com/repositories?since=" + commitId).openConnection();
		httpURLConnection.addRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");

		BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

		String line = "", inputLine;
		while ((inputLine = in.readLine()) != null) {

			line += "\n" + inputLine;
		}

		in.close();
		Arrays.stream(line.split("\"full_name\":")).skip(1).map(av -> av.split(",")[0]).forEach(av -> {
			String owner = av.substring(2, av.indexOf("/")).trim();
			String repoName = av.substring(av.indexOf("/") + 1, av.length() - 1).trim();
			searchCommit(owner, repoName);
		});

		if (in.readLine() != null) {
			List<String> idList = new ArrayList<>();
			Arrays.stream(line.split("\"id\":")).skip(1).map(av -> av.split(",")[0]).forEach(av -> {
				idList.add(av);
			});
			commitId = Integer.valueOf(idList.get(idList.size() - 1));
			getAllCommitUrlReader();
		}
	}

	/*
	 * Her gelen repodaki commitleri çeken ve en çok commit atan kiþiyi bulan
	 * metod..
	 */
	public static void searchCommit(String owner, String repoName) {

		final RepositoryId repo = new RepositoryId(owner, repoName);
		final String message = "   {0} by {1} on {2}";
		final CommitService service = new CommitService();
		Calendar cal = Calendar.getInstance();
		cal.set(2019, 1, 1);
		Date since = cal.getTime();
		cal.set(2020, 1, 1);

		Date until = cal.getTime();

		for (Collection<RepositoryCommit> commits : service.pageCommits(repo)) {
			for (RepositoryCommit commit : commits) {
				Date date = commit.getCommit().getAuthor().getDate();
				if (date.after(since) && date.before(until)) {
					getAthur.add(commit.getCommit().getAuthor().getName());
					String sha = commit.getSha().substring(0, 7);
					String author = commit.getCommit().getAuthor().getName();
					System.out.println(MessageFormat.format(message, sha, author, date));
				} else {
					commits.iterator().next();
				}
			}
		}
		Map<String, Long> counted = getAthur.stream()
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		counted.entrySet().stream().forEach(av -> {
			if (av.getValue().equals(Collections.max(counted.values()))) {
				System.out.println("The" + av.getKey() + " added maximum commits :" + av.getValue());
			}
		});

	}

	/*
	 * Kendi repomdaki bir repodaki commit miktarýný gösteren ve sýralayan metod.
	 */
	public static void example() throws IOException {

		Properties props = new Properties();
		props.setProperty("login", "cedellalahmet@gmail.com");
		props.setProperty("password", "*****");

		GitHub gitHub = GitHubBuilder.fromProperties(props).build();

		GHRepository repository = gitHub.getRepository("ahmetdellall/SampleExProject");

		Calendar cal = Calendar.getInstance();
		cal.set(2019, 1, 1);
		Date since = cal.getTime();
		cal.set(2020, 1, 1);
		Date until = cal.getTime();

		GHCommitQueryBuilder queryBuilder = repository.queryCommits().since(since).until(until);
		PagedIterable<GHCommit> commits = queryBuilder.list();
		Iterator<GHCommit> iterator = commits.iterator();

		int count = 0;
		while (iterator.hasNext()) {
			count++;
			GHCommit commit = iterator.next();

			System.out.println("Commit: " + commit.getSHA1() + ", info: " + commit.getCommitShortInfo().getMessage()
					+ ", author: " + commit.getAuthor());
		}
		System.out.println(count);

	}

}
