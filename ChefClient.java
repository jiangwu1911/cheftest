import java.io.*;
import java.util.List;
import java.util.Set;

import org.jclouds.chef.ChefApi;
import org.jclouds.ContextBuilder;
import org.jclouds.chef.ChefContext;
import org.jclouds.chef.domain.Node;
import org.jclouds.chef.util.RunListBuilder;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class ChefClient {
    String user = "wujiang";
    String organization = "mycorp";
    String pemFile = System.getProperty("user.home") + "/.chef/" + user + ".pem";
    String serverUrl = "https://chefserver";
    ChefContext context;
    ChefApi api;

    public static void main(String argv[]) {
    	ChefClient cli = new ChefClient();
        try {
            cli.connect();
			cli.runTest();
            cli.disconnect();

		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public void connect() throws IOException {
	    String credential = Files.toString(new File(pemFile), Charsets.UTF_8);
	    this.context = ContextBuilder.newBuilder("chef")
    				        .endpoint(serverUrl + "/organizations/" + organization)
    				        .credentials(user, credential)
    				        .buildView(ChefContext.class);
	    this.api = context.unwrapApi(ChefApi.class);
    }

    public void disconnect() throws IOException {
        this.context.close();
    }

    public void runTest() {
	    Set<String> databags = this.api.listDatabags();

	    String nodeName = "192.168.206.142";
	    List<String> runlist = new RunListBuilder().addRecipe("nginx").build();
	    Node node = this.context.getChefService().createNodeAndPopulateAutomaticAttributes(nodeName, runlist);
    }
}
