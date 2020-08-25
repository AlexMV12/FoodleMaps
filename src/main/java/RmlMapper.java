/*
import be.ugent.rml.Executor;
import be.ugent.rml.Utils;
import be.ugent.rml.functions.FunctionLoader;
import be.ugent.rml.functions.lib.IDLabFunctions;
import be.ugent.rml.records.RecordsFactory;
import be.ugent.rml.store.QuadStore;
import be.ugent.rml.store.QuadStoreFactory;
import be.ugent.rml.store.RDF4JStore;
*/
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class RmlMapper {
    /*
    public void mapping() {
        try {
            String mapPath = "./rules.yml.ttl";
            File mappingFile = new File(mapPath);

            InputStream mappingStream = new FileInputStream(mappingFile);

            QuadStore rmlStore = QuadStoreFactory.read(mappingStream);

            RecordsFactory factory = new RecordsFactory(mappingFile.getParent());

            Map<String, Class> libraryMap = new HashMap<>();
            libraryMap.put("IDLabFunctions", IDLabFunctions.class);

            FunctionLoader functionLoader =  new FunctionLoader(null, libraryMap);

            QuadStore outputStore = new RDF4JStore();

            Executor executor = new Executor(rmlStore,factory,functionLoader,outputStore, Utils.getBaseDirectiveTurtle(mappingStream));

            QuadStore result = executor.execute(null);

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));
            result.write(out, "turtle");
            out.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

     */
}
