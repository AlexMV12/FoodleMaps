import com.mashape.unirest.http.HttpResponse;

import org.apache.commons.io.FileUtils;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;

import java.io.*;

import org.json.*;

public class Main {
    public static void main(String[] args) {
        String fileName= "ontology.owl";
        Model m = ModelFactory.createDefaultModel();

        InputStream in = FileManager.get().open( fileName);

        if (in == null) {
            throw new IllegalArgumentException("File: " + fileName + " not found.");
        } else {
            m.read(in, null, "RDF/XML");
        }

        InfModel inf = ModelFactory.createRDFSModel(m);

//        String NS = "http://example.com/test#";
//        Resource r = m.createResource(NS + "r");
//        Property p = m.createProperty(NS + "p");
//        r.addProperty(p, "HelloWorld", XSDDatatype.XSDstring);
//        m.write(System.out,"RDF/XML");
        Dataset ds = DatasetFactory.create();

        ds.addNamedModel("model1", inf);
        ds.setDefaultModel(ds.getUnionModel());

        String q1 = "SELECT * WHERE { ?s a <http://dbpedia.org/ontology/Food> }";

        Query query = QueryFactory.create(q1);
        QueryExecution exec = QueryExecutionFactory.create(query, ds);

        ResultSet res = exec.execSelect();

        ResultSetFormatter.out(System.out, res, query);


        //get 1st page of restaurants and menuItems for 305256
        //convert to csv
        menuAPI();

        //          rules.yml are yarrrml rules
        //
        //          yarrrml-parser needed to translate .yml rules to rml turtle rules   (https://rml.io/yarrrml/tutorial/getting-started/#writing-rules-on-your-computer)
        //              yarrrml-parser -i rules.yml -o rules.yml.ttl
        //
        //          rmlmapper.jar to generate triples (to be added as external library for RmlMapper class)
        //              java -jar rmlmapper.jar -m rules.yml.ttl


        //RmlMapper rmlMapper = new RmlMapper();
        //rmlMapper.mapping();

    }


    public static void menuAPI(){
        RakutenAPI rakutenAPI = new RakutenAPI();
        HttpResponse<String> restaurantResponse = rakutenAPI.getRestaurant(1);
        String jsonFile = restaurantResponse.getBody();

        /*
        try (PrintWriter out = new PrintWriter("restaurants.json")){
            out.println(jsonFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        */

        restaurantToCsv(jsonFile);

        HttpResponse<String> menusResponse = rakutenAPI.getMenuItem("305256", 1);
        jsonFile = menusResponse.getBody();

        menusToCsv(jsonFile);

    }

    public static void restaurantToCsv(String jsonFile){
        JSONObject output;

        try{
            output = new JSONObject(jsonFile);
            JSONObject result = output.getJSONObject("result");
            JSONArray docs = result.getJSONArray("data");

            File file = new File("restaurant.csv");
            String csv = CDL.toString(docs);
            FileUtils.writeStringToFile(file,csv);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }


    }

    public static void menusToCsv(String jsonFile){
        JSONObject output;

        try{
            output = new JSONObject(jsonFile);
            JSONObject result = output.getJSONObject("result");
            JSONArray docs = result.getJSONArray("data");

            File file = new File("menus.csv");
            String csv = CDL.toString(docs);
            FileUtils.writeStringToFile(file,csv);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }


    }
}
