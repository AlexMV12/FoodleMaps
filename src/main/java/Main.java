import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
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


//        String NS = "http://example.com/test#";
//        Resource r = m.createResource(NS + "r");
//        Property p = m.createProperty(NS + "p");
//        r.addProperty(p, "HelloWorld", XSDDatatype.XSDstring);
        m.write(System.out,"RDF/XML");
    }
}
