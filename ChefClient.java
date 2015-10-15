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
    public static void main(String argv[]) {
    	ChefClient client = new ChefClient();
        try {
			client.runTest();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void runTest() throws IOException {
        String client = "wujiang";
	String organization = "mycorp";
	String pemFile = System.getProperty("user.home") + "/.chef/" + client + ".pem";
	String credential = Files.toString(new File(pemFile), Charsets.UTF_8);

	ChefContext context = ContextBuilder.newBuilder("chef")
    				.endpoint("https://chefserver/organizations/" + organization)
    				.credentials(client, credential)
    				.buildView(ChefContext.class);

	// The raw API has access to all chef features, as exposed in the Chef REST API
	ChefApi api = context.unwrapApi(ChefApi.class);
	Set<String> databags = api.listDatabags();

	// ChefService has helpers for common commands
	String nodeName = "192.168.206.142";
	List<String> runlist = new RunListBuilder().addRecipe("nginx").build();
	Node node = context.getChefService().createNodeAndPopulateAutomaticAttributes(nodeName, runlist);
	
	// Release resources
	context.close();
    }
}

