import org.eclipse.rdf4j.common.exception.ValidationException;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.RDF4J;
import org.eclipse.rdf4j.query.*;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.net.Socket;
import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class RepositoryManager {
    private String serverUrl;
    private String repositoryName;
    private String shacl;

    private HashMap<Short, String> errorMessage;

    public RepositoryManager(String serverUrl, String repositoryName, String shacl){
        this.serverUrl= serverUrl;
        this.repositoryName= repositoryName;
        this.shacl= shacl;

        this.errorMessage= new HashMap<>();
        errorMessage.put((short) -2, "Error with the connection with the: " + this.serverUrl);
        errorMessage.put((short) -1, "The repository with name: \" + this.repositoryName + \" does not exist");
        errorMessage.put((short) 0, "Success");
        errorMessage.put((short) 1, "Error with the creation of config file");
        errorMessage.put((short) 2, "The repository with name: "+ this.repositoryName + " already exists");
        errorMessage.put((short) 3, "Unexpected error at the creation of the repository");
        errorMessage.put((short) 4, "The shacl file (" + this.shacl + ") does not exist");
        errorMessage.put((short) 5, "Unexpected error during the addition of the shacl file");
        errorMessage.put((short) 6, "Error while writing the validation report");
        errorMessage.put((short) 7, "Violation at shacl rules");
        errorMessage.put((short) 8, "Unexpected error during the addition of the data");
        errorMessage.put((short) 9, "Unexpected error during the deletion of the data from the repository");
    }

    public RepositoryManager(String serverUrl, String repositoryName){
        this.serverUrl= serverUrl;
        this.repositoryName= repositoryName;
        this.shacl= "shacl.ttl";

        this.errorMessage= new HashMap<>();
        errorMessage.put((short) -2, "Error with the connection with the: " + this.serverUrl);
        errorMessage.put((short) -1, "The repository with name: \" + this.repositoryName + \" does not exist");
        errorMessage.put((short) 1, "Error with the creation of config file");
        errorMessage.put((short) 2, "The repository with name: "+ this.repositoryName + " already exists");
        errorMessage.put((short) 3, "Unexpected error at the creation of the repository");
        errorMessage.put((short) 4, "The shacl file (" + this.shacl + ") does not exist");
        errorMessage.put((short) 5, "Unexpected error during the addition of the shacl file");
        errorMessage.put((short) 6, "Error while writing the validation report");
        errorMessage.put((short) 7, "Violation at shacl rules");
        errorMessage.put((short) 8, "Unexpected error during the addition of the data");
        errorMessage.put((short) 9, "Unexpected error during the deletion of the data from the repository");
    }

    public HashMap<Short, String> getErrorMessage(){
        return this.errorMessage;
    }
    public short createRepositoryWithValidationFile(){
        if(!checkConnection()){
            return -2;
        }

        short temp= createRepository();

        if(temp == 0){
            return uploadValidationFile();
        }else{
            return temp;
        }
    }

    public short uploadData(String data) {
        if(!checkConnection()){
            return -2;
        }

        Repository repository= new HTTPRepository(serverUrl, repositoryName);
        repository.init();

        RepositoryConnection repositoryConnection= repository.getConnection();
        File file= new File(data);

        try{
            repositoryConnection.add(file, RDFFormat.TURTLE);
        }catch (RepositoryException e){
            Throwable cause = e.getCause();
            if (cause instanceof ValidationException) {
                Model validationReportModel = ((ValidationException) cause).validationReportAsModel();

                try(FileOutputStream fileForValidationReport= new FileOutputStream("validationReport.ttl")){
                    Rio.write(validationReportModel, fileForValidationReport, RDFFormat.TURTLE);
                }catch (IOException e2){
                    return 6;
                }
            }else if(cause == null){
                return -1;
            }

            return 7;
        }
        catch (Throwable t){
            return 8;
        } finally {
            repositoryConnection.close();
            repository.shutDown();
        }

        return 0;
    }

    public short clearAllData(){
        if(!checkConnection()){
            return -2;
        }

        Repository repository= new HTTPRepository(serverUrl, repositoryName);
        repository.init();

        try(RepositoryConnection repositoryConnection= repository.getConnection()){
            repositoryConnection.begin();
            repositoryConnection.clear();
            repositoryConnection.commit();
        }catch (Throwable t){
            Throwable cause = t.getCause();
            if(cause == null){
                return -1;
            }

            return 9;
        }finally {
            repository.shutDown();
        }

        return 0;
    }

    public short deleteRepository(){
        if(!checkConnection()){
            return -2;
        }

        RemoteRepositoryManager remoteRepositoryManager= new RemoteRepositoryManager(this.serverUrl);
        remoteRepositoryManager.init();

        if(!remoteRepositoryManager.hasRepositoryConfig(this.repositoryName)){
            return -1;
        }

        remoteRepositoryManager.removeRepository(this.repositoryName);

        return 0;
    }

    public Object sparqlQuery(String query){
        if(!checkConnection()){
            return null;
        }

        Repository repository= new HTTPRepository(serverUrl, repositoryName);
        repository.init();

        try(RepositoryConnection repositoryConnection= repository.getConnection()){

            Query tempQuery= repositoryConnection.prepareQuery(query);

            if(tempQuery instanceof BooleanQuery temp){

                return temp.evaluate();
            }else if(tempQuery instanceof TupleQuery temp){

                try(TupleQueryResult result= temp.evaluate()){
                    ArrayList<String> elements;
                    ArrayList<ArrayList<String>> forReturn= new ArrayList<>();

                    List<String> bindingNames= result.getBindingNames();

                    elements= new ArrayList<>(bindingNames);
                    forReturn.add(elements);

                    for(BindingSet bindingSet: result){
                        elements= new ArrayList<>();

                        for (String bindingName : bindingNames) {
                            elements.add(bindingSet.getValue(bindingName).toString());
                        }

                        forReturn.add(elements);
                    }

                    return forReturn;
                }

            }else if(tempQuery instanceof GraphQuery temp){
                try(GraphQueryResult result= temp.evaluate()) {
                    ArrayList<String> element;
                    ArrayList<ArrayList<String>> forReturn= new ArrayList<>();

                    for(Statement statement: result){
                        element= new ArrayList<>();
                        element.add(statement.getObject().toString());
                        element.add(statement.getPredicate().toString());
                        element.add(statement.getSubject().toString());

                        forReturn.add(element);
                    }

                    return forReturn;
                }
            }
        }

        return null;
    }

    private boolean checkConnection(){
        try (Socket socket= new Socket("localhost", 7200)) {
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Start of private helpful functions
     */

    private short createRepository(){

        if(!createConfigFile()){
            return 1;
        }

        //curl -X POST localhost:7200/rest/repositories -H 'Content-Type: multipart/form-data' -F config=@config.ttl

        HttpClient httpClient= HttpClients.createDefault();
        HttpPost httpPost= new HttpPost("http://localhost:7200/rest/repositories");

        File configFile= new File("config.ttl");
        String boundary= UUID.randomUUID().toString();
        HttpEntity multipartEntity = MultipartEntityBuilder.create()
                .setBoundary(boundary)
                .addBinaryBody("config", configFile, ContentType.APPLICATION_OCTET_STREAM, "config.ttl")
                .build();

        httpPost.setEntity(multipartEntity);

        httpPost.setHeader("Content-Type", "multipart/form-data; boundary=" + boundary);

        try {
            HttpResponse response= httpClient.execute(httpPost);

            String jsonResponse= EntityUtils.toString(response.getEntity());

            //if the repository was not existed before, then the response will be an empty string;
            if(!jsonResponse.trim().equalsIgnoreCase("")){
                JSONParser jsonParser= new JSONParser();
                JSONObject obj= (JSONObject) jsonParser.parse(jsonResponse);
                String message= (String) obj.get("message");

                if(message.equalsIgnoreCase("Repository "+ this.repositoryName +" already exists.")){
                    return 2;
                }
            }

        } catch (Exception e) {
            return 3;
        }

        return 0;
    }

    private short uploadValidationFile(){
        File shaclFile= new File(this.shacl);

        if(!shaclFile.exists()){
            return 4;
        }

        HTTPRepository repository= new HTTPRepository(serverUrl, repositoryName);

        try(RepositoryConnection connection= repository.getConnection()){
            connection.begin();
            connection.clear(RDF4J.SHACL_SHAPE_GRAPH);
            connection.add(shaclFile, RDFFormat.TURTLE, RDF4J.SHACL_SHAPE_GRAPH);
            connection.commit();

        }catch (Throwable e){
            return 5;
        }finally {
            repository.shutDown();
        }

        return 0;
    }

    private String readFileToString(String filePath) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    private boolean createConfigFile(){
        String tempOne= "#\n" +
                "# RDF4J configuration template for a GraphDB repository with SHACL\n" +
                "#\n" +
                "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.\n" +
                "@prefix rep: <http://www.openrdf.org/config/repository#>.\n" +
                "@prefix sr: <http://www.openrdf.org/config/repository/sail#>.\n" +
                "@prefix sail: <http://www.openrdf.org/config/sail#>.\n" +
                "@prefix graphdb: <http://www.ontotext.com/config/graphdb#>.\n" +
                "@prefix shacl: <http://rdf4j.org/config/sail/shacl#>.\n" +
                "\n" +
                "\n" +
                "[] a rep:Repository ;\n" +
                "    rep:repositoryID \"";

        String tempTwo= "\" ;\n" +
                "    rdfs:label \"\" ;\n" +
                "    rep:repositoryImpl [\n" +
                "        rep:repositoryType \"graphdb:SailRepository\" ;\n" +
                "        sr:sailImpl [\n" +
                "            sail:sailType \"rdf4j:ShaclSail\";\n" +
                "\n" +
                "            sail:delegate [\n" +
                "                sail:sailType \"graphdb:Sail\" ;\n" +
                "\n" +
                "                graphdb:read-only \"false\" ;\n" +
                "\n" +
                "                # Inference and Validation\n" +
                "                graphdb:ruleset \"owl-horst-optimized\" ;\n" +
                "                graphdb:disable-sameAs \"false\" ;\n" +
                "                graphdb:check-for-inconsistencies \"false\" ;\n" +
                "\n" +
                "                # Indexing\n" +
                "                graphdb:entity-id-size \"32\" ;\n" +
                "                graphdb:enable-context-index \"false\" ;\n" +
                "                graphdb:enablePredicateList \"true\" ;\n" +
                "                graphdb:enable-fts-index \"false\" ;\n" +
                "                graphdb:fts-indexes (\"default\" \"iri\") ;\n" +
                "                graphdb:fts-string-literals-index \"default\" ;\n" +
                "                graphdb:fts-iris-index \"none\" ;\n" +
                "\n" +
                "                # Queries and Updates\n" +
                "                graphdb:query-timeout \"0\" ;\n" +
                "                graphdb:throw-QueryEvaluationException-on-timeout \"false\" ;\n" +
                "                graphdb:query-limit-results \"0\" ;\n" +
                "\n" +
                "                # Settable in the file but otherwise hidden in the UI and in the RDF4J console\n" +
                "                graphdb:base-URL \"http://example.org/owlim#\" ;\n" +
                "                graphdb:defaultNS \"\" ;\n" +
                "                graphdb:imports \"\" ;\n" +
                "                graphdb:repository-type \"file-repository\" ;\n" +
                "                graphdb:storage-folder \"storage\" ;\n" +
                "                graphdb:entity-index-size \"10000000\" ;\n" +
                "                graphdb:in-memory-literal-properties \"true\" ;\n" +
                "                graphdb:enable-literal-index \"true\" ;\n" +
                "            ] ;\n" +
                "\n" +
                "\n" +
                "            # Settable in the UI and in the RDF4J console\n" +
                "            shacl:cacheSelectNodes \"true\" ;\n" +
                "            shacl:dashDataShapes \"true\" ;\n" +
                "            shacl:logValidationPlans \"false\" ;\n" +
                "            shacl:logValidationViolations \"false\" ;\n" +
                "            shacl:parallelValidation \"true\" ;\n" +
                "            shacl:globalLogValidationExecution \"false\" ;\n" +
                "            shacl:performanceLogging \"false\" ;\n" +
                "            shacl:eclipseRdf4jShaclExtensions \"true\" ;\n" +
                "            shacl:validationResultsLimitTotal \"1000000\" ;\n" +
                "            shacl:validationResultsLimitPerConstraint \"1000\" ;\n" +
                "            shacl:shapesGraph <http://rdf4j.org/schema/rdf4j#SHACLShapeGraph> ;\n" +
                "\n" +
                "            # Settable in the file but otherwise hidden in the UI and in the RDF4J console\n" +
                "            shacl:validationEnabled \"true\" ;\n" +
                "            shacl:rdfsSubClassReasoning \"true\" ;\n" +
                "            shacl:transactionalValidationLimit \"500000\" ;\n" +
                "        ]\n" +
                "    ].";

        try(BufferedWriter writer = new BufferedWriter(new FileWriter("config.ttl"))){
            writer.write(tempOne);
            writer.write(this.repositoryName);
            writer.write(tempTwo);
            writer.newLine();

        } catch (Throwable t){
            return false;
        }

        return true;
    }

    public static boolean isOpenMHealthRepository(String serverUrl, String repositoryName){

        Repository repository= new HTTPRepository(serverUrl, repositoryName);
        repository.init();

        try(RepositoryConnection repositoryConnection= repository.getConnection()){

            String queryString= "PREFIX sosa: <http://www.w3.org/ns/sosa/>\n" +
                    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                    "\n" +
                    "ask\n" +
                    "WHERE {\n" +
                    "    ?s rdf:type sosa:Observation . \n" +
                    "}";

            BooleanQuery query= repositoryConnection.prepareBooleanQuery(queryString);

            return query.evaluate();
        } catch (Throwable t){
            System.out.println("No connection");
            return false;
        }
    }
}