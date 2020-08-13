import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;

import java.io.InputStream;

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
    }
}
