import java.io.*;
import java.util.List;
import java.util.Set;
import java.util.Map;

import org.jclouds.chef.ChefApi;
import org.jclouds.ContextBuilder;
import org.jclouds.chef.ChefContext;
import org.jclouds.chef.domain.Node;
import org.jclouds.chef.util.RunListBuilder;
import org.jclouds.chef.domain.Environment;
import org.jclouds.chef.domain.Environment.Builder;
import org.jclouds.domain.JsonBall;

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

    public void createEnvironment() throws IOException {
        String name = "env02";
        String description = "My test environment 02";
        api.createEnvironment(Environment.builder().name(name)
                                .description(description)
                                .attribute("nginx", new JsonBall("{\"worker_connections\":512}"))
                                .build()); 
    } 

    public void runTest() throws IOException {
        createEnvironment();
    }
}
