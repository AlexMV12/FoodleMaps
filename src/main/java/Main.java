import com.mashape.unirest.http.HttpResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Functions;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.util.FileManager;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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



        //get 1st page of restaurants and menuItems for 129697
        //convert to csv
        //menuAPI();

        //          rules.yml are yarrrml rules
        //
        //          yarrrml-parser needed to translate .yml rules to rml turtle rules   (https://rml.io/yarrrml/tutorial/getting-started/#writing-rules-on-your-computer)
        //              yarrrml-parser -i rules.yml -o rules.yml.ttl
        //
        //          rmlmapper.jar to generate triples (to be added as external library for RmlMapper class)
        //              java -jar rmlmapper.jar -m rules.yml.ttl

        //RmlMapper rmlMapper = new RmlMapper();
        //rmlMapper.mapping();
        
        Model apiModel = ModelFactory.createDefaultModel();

        InputStream in2 = FileManager.get().open( "output.rdf");

        if (in2 == null) {
            throw new IllegalArgumentException("File: " + "output.rdf" + " not found.");
        } else {
            apiModel.read(in2, null, "N-TRIPLE");
        }
        /*
        Iterator itr = apiModel.listStatements();
        while(itr.hasNext()){
            String s = String.valueOf(itr.next());
            System.out.println(s);
        }
        */

        InfModel apiInf = ModelFactory.createRDFSModel(apiModel);

        Dataset apiDataSet = DatasetFactory.create();

        apiDataSet.addNamedModel("model2", apiInf);
        apiDataSet.setDefaultModel(apiDataSet.getUnionModel());

        //Item_description selection
        String q2 = "SELECT ?recipe ?description WHERE { " +
                "?m <https://alexmv12.github.io/FoodleMaps/#hasRecipe> ?recipe." +
                "?recipe a <http://schema.org/Recipe>." +
                "?m <http://schema.org/description> ?description. }";

        Query query2 = QueryFactory.create(q2);

        QueryExecution exec2 = QueryExecutionFactory.create(query2, apiDataSet);
        ResultSet res2 = exec2.execSelect();

        String description;
        String recipe;
        String[] ingredients;
        String qI;
        String qR;
        while(res2.hasNext()){
            QuerySolution currentSet = res2.next();
            description = currentSet.get("description").toString();
            recipe = currentSet.get("recipe").toString();
            ingredients = splitAndMatch(description);
            for(String ingredient : ingredients){
                qI = "INSERT DATA { <https://alexmv12.github.io/FoodleMaps/#" + ingredient + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://alexmv12.github.io/FoodleMaps/#Ingredient> ;" +
                        " <http://dbpedia.org/ontology/ingredientName> \"" + ingredient + "\". }";
                UpdateAction.parseExecute(qI, apiDataSet);
                qR = "INSERT DATA { <" + recipe + "> <https://alexmv12.github.io/FoodleMaps/#hasIngredient> <https://alexmv12.github.io/FoodleMaps/#" + ingredient + "> }";
                UpdateAction.parseExecute(qR,apiDataSet);
            }
        }

        //ResultSetFormatter.out(System.out, res2, query2);

        System.out.println("Which restaurants serve dish whose recipe has ingredient 'salad' ?");
        String q3 = "SELECT DISTINCT ?restaurant WHERE { " +
                "?restaurant <http://schema.org/hasMenu> ?menu." +
                "?menu <https://alexmv12.github.io/FoodleMaps/#servesRecipe> ?recipe." +
                "?recipe <https://alexmv12.github.io/FoodleMaps/#hasIngredient> ?ingredient."+
                "?ingredient <http://dbpedia.org/ontology/ingredientName> \"salad\".}";

        Query query3 = QueryFactory.create(q3);
        QueryExecution exec3 = QueryExecutionFactory.create(query3, apiDataSet);
        ResultSet res3 = exec3.execSelect();
        ResultSetFormatter.out(System.out, res3, query3);

        System.out.println("Which dishes served by 'Nori Sushi' has the ingredient 'salad' ?");
        String q4 = "SELECT ?menuitem WHERE {" +
                "?restaurant <http://schema.org/name> \"Nori Sushi\"." +
                "?restaurant <http://schema.org/hasMenu> ?menu." +
                "?menu <https://alexmv12.github.io/FoodleMaps/#servesRecipe> ?recipe." +
                "?menuitem <https://alexmv12.github.io/FoodleMaps/#hasRecipe> ?recipe." +
                "?recipe <https://alexmv12.github.io/FoodleMaps/#hasIngredient> ?ingredient. " +
                "?ingredient <http://dbpedia.org/ontology/ingredientName> \"salad\".}";

        Query query4 = QueryFactory.create(q4);
        QueryExecution exec4 = QueryExecutionFactory.create(query4, apiDataSet);
        ResultSet res4 = exec4.execSelect();
        ResultSetFormatter.out(System.out, res4, query4);

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

        HttpResponse<String> menusResponse = rakutenAPI.getMenuItem("162093", 1);
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

    private static String[] splitAndMatch(String inputString) {
        String[] vocabulary = getWordsFromVocabulary();
        List<String> matched = new ArrayList<>();
        for (String s : inputString.split(" ")) {
            if(s.length()>3 && matches(vocabulary,s))
                matched.add(s);
        }
        return matched.toArray(new String[0]);

    }

    private static boolean matches(String[] foods, String inputWord){
        for (String word : foods){
            if(word.contains(inputWord)) return true;
        }
        return false;
    }

    private static String[] getWordsFromVocabulary() {
        List<String> words = new ArrayList<>();
        try {
            File file = new File("src/main/resources/foodVoc.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                words.add(line);
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return words.toArray(new String[0]);
    }
}
