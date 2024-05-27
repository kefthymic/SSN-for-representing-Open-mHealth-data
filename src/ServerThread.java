import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryInfo;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ServerThread extends Thread{
    private Socket clientSocket;
    private BufferedReader reader;
    private OutputStream output;

    public ServerThread(Socket clientSocket){
        try{
            this.clientSocket= clientSocket;
            reader= new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            output= this.clientSocket.getOutputStream();
        }catch (IOException e){
            System.err.println("Error: No Connection\n" + e.getMessage());
        }
    }

    public void run(){
        //create the helpful directory, where all the files with the data will be saved temporarily
        File directory= new File("folderForFiles/");
        if(!directory.exists()){
            directory.mkdir();
        }

        try{
            String requestLine= reader.readLine();
            String textForOutput="Nothing here";

            if(requestLine!= null){
                System.out.println(requestLine);
                String[] requestLineAsArray= requestLine.split(" ");
                String method = requestLineAsArray[0];
                String urlString = requestLineAsArray[1].substring(1); //get rid of the '/' in the url
                urlString= urlString.substring(urlString.indexOf("?")+1);

                String[] params= urlString.split("&");

                if(method.equalsIgnoreCase("GET") && !urlString.equalsIgnoreCase("") && params.length>0){
                    String taskParam= params[0];
                    String task;

                    if(taskParam.substring(0, taskParam.indexOf("=")).equalsIgnoreCase("task")){
                        task= taskParam.substring(taskParam.indexOf("=")+1);

                        if(task.equalsIgnoreCase("connectionStatus")){
                            textForOutput= String.valueOf(checkConnection());
                        } else if (task.equalsIgnoreCase("getAllRepositories")) {
                            ArrayList<String> tempArrayList= getAllOpenMhealthRepositories(); //in order the web app show only the repositories that have open mHealth data

                            if(tempArrayList!= null){
                                textForOutput= "";
                                for(String tempString : tempArrayList){
                                    textForOutput+= tempString + ",";
                                }
                                textForOutput= textForOutput.substring(0, textForOutput.length()-1); //to ignore the last ','
                            }
                        } else if(task.equalsIgnoreCase("clickDatabase")){
                            if(params.length==2){
                                String repoNameParam= params[1];
                                String repoName;

                                if(repoNameParam.substring(0, repoNameParam.indexOf("=")).equalsIgnoreCase("repoName")){
                                    repoName= repoNameParam.substring(repoNameParam.indexOf("=")+1);

                                    if(RepositoryManager.isOpenMHealthRepository("http://localhost:7200", repoName)){
                                        textForOutput= "true";
                                    }
                                    else {
                                        textForOutput= "false";
                                    }
                                }
                            }
                        } else if (task.equalsIgnoreCase("deleteRepository")){
                            if(params.length==2){
                                String repoNameParam= params[1];
                                String repoName;

                                if(repoNameParam.substring(0, repoNameParam.indexOf("=")).equalsIgnoreCase("repoName")){
                                    repoName= repoNameParam.substring(repoNameParam.indexOf("=")+1);

                                    if(RepositoryManager.isOpenMHealthRepository("http://localhost:7200", repoName)){
                                        RepositoryManager repositoryManager= new RepositoryManager("http://localhost:7200", repoName);
                                        if(repositoryManager.deleteRepository()== 0){
                                            textForOutput= "Successful deletion";
                                        }
                                        else {
                                            textForOutput= repositoryManager.getErrorMessage().get(repositoryManager.deleteRepository());
                                        }
                                    }
                                    else {
                                        textForOutput= "false";
                                    }
                                }
                            }
                        } else if (task.equalsIgnoreCase("getFeatureOfInterest")){
                            if(params.length==3){
                                String repoNameParam= params[1];
                                String repoName;

                                String observationParam= params[2];
                                String observation;

                                if(repoNameParam.substring(0, repoNameParam.indexOf("=")).equalsIgnoreCase("repoName") && observationParam.substring(0, observationParam.indexOf("=")).equalsIgnoreCase("observation")) {
                                    repoName= repoNameParam.substring(repoNameParam.indexOf("=")+1);
                                    observation = observationParam.substring(observationParam.indexOf("=") + 1);

                                    if(RepositoryManager.isOpenMHealthRepository("http://localhost:7200", repoName)){
                                        String query= "PREFIX : <http://example.org/efthymiadisKonstantinos/project#>\n" +
                                                "PREFIX sosa: <http://www.w3.org/ns/sosa/>\n" +
                                                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                                                "\n" +
                                                "select distinct ?featureOfInterest\n" +
                                                "WHERE {\n" +
                                                "    ?featureOfInterest rdf:type sosa:FeatureOfInterest .\n" +
                                                "    ?temp rdf:type :"+observation+" .\n" +
                                                "    ?temp sosa:hasFeatureOfInterest ?featureOfInterest .\n" +
                                                "}";

                                        RepositoryManager repositoryManager= new RepositoryManager("http://localhost:7200", repoName);
                                        Object answer= repositoryManager.sparqlQuery(query);
                                        if(answer==null){
                                            textForOutput= "error";
                                        } else if (answer instanceof ArrayList<?> tableWithAnswer) {
                                            if(!tableWithAnswer.isEmpty() && tableWithAnswer.get(0) instanceof ArrayList<?> element){
                                                if(!element.isEmpty() && element.get(0) instanceof String){
                                                    textForOutput="";
                                                    int i;
                                                    //the first ArrayList has the names of the variables from the sparql query. So, there is no need to write them in the answer. For this reason the i starts with 1 and not with 0
                                                    for(i=1;i<tableWithAnswer.size()-1;i++){
                                                        ArrayList<String> tempArrayListWithElements= (ArrayList<String>) tableWithAnswer.get(i);
                                                        for(int j=0; j<tempArrayListWithElements.size();j++){
                                                            textForOutput+= tempArrayListWithElements.get(j).substring(tempArrayListWithElements.get(j).indexOf("#")+1); //in oder to return only the name, without the URI
                                                        }
                                                        textForOutput+="\n";
                                                    }
                                                    //the last repetition will be done there in order not have the \n in the end of the textForOutput
                                                    if(i<tableWithAnswer.size()){
                                                        ArrayList<String> tempArrayListWithElements= (ArrayList<String>) tableWithAnswer.get(i);
                                                        for(int j=0; j<tempArrayListWithElements.size();j++){
                                                            textForOutput+= tempArrayListWithElements.get(j).substring(tempArrayListWithElements.get(j).indexOf("#")+1); //in oder to return only the name, without the URI
                                                        }
                                                    }
                                                    if(textForOutput.trim().equalsIgnoreCase("")){
                                                        textForOutput="error";
                                                    }
                                                }else {
                                                    textForOutput= "error";
                                                }
                                            }else {
                                                textForOutput= "error";
                                            }
                                        }else {
                                            textForOutput= "error";
                                        }
                                    }
                                    else {
                                        textForOutput= "error";
                                    }
                                }
                            }
                        } else if (task.equalsIgnoreCase("getSensors")) {
                            if(params.length==4){
                                String repoNameParam= params[1];
                                String repoName;

                                String featureOfInterestParam= params[2];
                                String featureOfInterest;

                                String observationParam= params[3];
                                String observation;

                                if(repoNameParam.substring(0, repoNameParam.indexOf("=")).equalsIgnoreCase("repoName") && featureOfInterestParam.substring(0, featureOfInterestParam.indexOf("=")).equalsIgnoreCase("featureOfInterest") && observationParam.substring(0, observationParam.indexOf("=")).equalsIgnoreCase("observation")) {
                                    repoName = repoNameParam.substring(repoNameParam.indexOf("=") + 1);
                                    featureOfInterest= featureOfInterestParam.substring(featureOfInterestParam.indexOf("=") + 1);
                                    observation = observationParam.substring(observationParam.indexOf("=") + 1);

                                    if(RepositoryManager.isOpenMHealthRepository("http://localhost:7200", repoName)) {
                                        String query= "PREFIX : <http://example.org/efthymiadisKonstantinos/project#>\n" +
                                                "PREFIX sosa: <http://www.w3.org/ns/sosa/>\n" +
                                                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                                                "\n" +
                                                "select distinct ?sensor\n" +
                                                "WHERE {\n" +
                                                "    ?temp rdf:type :"+observation+" .\n" +
                                                "    ?temp sosa:hasFeatureOfInterest :"+featureOfInterest+" .\n" +
                                                "    ?temp sosa:madeBySensor ?sensor .\n" +
                                                "}";

                                        RepositoryManager repositoryManager= new RepositoryManager("http://localhost:7200", repoName);
                                        Object answer= repositoryManager.sparqlQuery(query);

                                        if(answer==null){
                                            textForOutput= "error";
                                        } else if (answer instanceof ArrayList<?> tableWithAnswer) {
                                            if(!tableWithAnswer.isEmpty() && tableWithAnswer.get(0) instanceof ArrayList<?> element){
                                                if(!element.isEmpty() && element.get(0) instanceof String){
                                                    textForOutput="";
                                                    int i;
                                                    //the first ArrayList has the names of the variables from the sparql query. So, there is no need to write them in the answer. For this reason the i starts with 1 and not with 0
                                                    for(i=1;i<tableWithAnswer.size()-1;i++){
                                                        ArrayList<String> tempArrayListWithElements= (ArrayList<String>) tableWithAnswer.get(i);
                                                        for(int j=0; j<tempArrayListWithElements.size();j++){
                                                            textForOutput+= tempArrayListWithElements.get(j).substring(tempArrayListWithElements.get(j).indexOf("#")+1); //in oder to return only the name, without the URI
                                                        }
                                                        textForOutput+="\n";
                                                    }
                                                    //the last repetition will be done there in order not have the \n in the end of the textForOutput
                                                    if(i<tableWithAnswer.size()){
                                                        ArrayList<String> tempArrayListWithElements= (ArrayList<String>) tableWithAnswer.get(i);
                                                        for(int j=0; j<tempArrayListWithElements.size();j++){
                                                            textForOutput+= tempArrayListWithElements.get(j).substring(tempArrayListWithElements.get(j).indexOf("#")+1); //in oder to return only the name, without the URI
                                                        }
                                                    }

                                                    if(textForOutput.trim().equalsIgnoreCase("")){
                                                        textForOutput="error";
                                                    }
                                                }else {
                                                    textForOutput= "error";
                                                }
                                            }else {
                                                textForOutput= "error";
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (task.equalsIgnoreCase("getMaxOrMinDate")) {
                            if(params.length==4){
                                String repoNameParam= params[1];
                                String repoName;

                                String kindParam= params[2];
                                String kind;

                                String observationParam= params[3];
                                String observation;

                                if(repoNameParam.substring(0, repoNameParam.indexOf("=")).equalsIgnoreCase("repoName") && kindParam.substring(0, kindParam.indexOf("=")).equalsIgnoreCase("kind") && observationParam.substring(0, observationParam.indexOf("=")).equalsIgnoreCase("observation")) {
                                    repoName = repoNameParam.substring(repoNameParam.indexOf("=") + 1);
                                    kind= kindParam.substring(kindParam.indexOf("=") + 1);
                                    observation= observationParam.substring(observationParam.indexOf("=") + 1);

                                    if(RepositoryManager.isOpenMHealthRepository("http://localhost:7200", repoName)) {
                                        String query;
                                        if(observation.equalsIgnoreCase("CaloriesBurnedObservation") || observation.equalsIgnoreCase("PhysicalActivityObservation") || observation.equalsIgnoreCase("StepCountObservation")){
                                            if(kind.equalsIgnoreCase("min")){
                                                query= "PREFIX time: <http://www.w3.org/2006/time#>\n" +
                                                        "PREFIX : <http://example.org/efthymiadisKonstantinos/project#>\n" +
                                                        "PREFIX sosa: <http://www.w3.org/ns/sosa/>\n" +
                                                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                                                        "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                                                        "\n" +
                                                        "select distinct ?date\n" +
                                                        "WHERE {\n" +
                                                        "    ?temp rdf:type :" + observation + " .\n" +
                                                        "    ?temp sosa:phenomenonTime ?phenomenonTime .\n" +
                                                        "    \n" +
                                                        "    ?phenomenonTime rdf:type time:Interval .\n" +
                                                        "    ?phenomenonTime time:hasBeginning ?time .\n" +
                                                        "    ?time rdf:type time:Instant .\n" +
                                                        "    ?time time:inXSDDateTime ?dateTime .\n" +
                                                        "    \n" +
                                                        "\tBIND(xsd:date(?dateTime) AS ?date)\n" +
                                                        "}\n" +
                                                        "order by ?date limit 1";
                                            }else {
                                                query= "PREFIX time: <http://www.w3.org/2006/time#>\n" +
                                                        "PREFIX : <http://example.org/efthymiadisKonstantinos/project#>\n" +
                                                        "PREFIX sosa: <http://www.w3.org/ns/sosa/>\n" +
                                                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                                                        "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                                                        "\n" +
                                                        "select distinct ?date\n" +
                                                        "WHERE {\n" +
                                                        "    ?temp rdf:type :" + observation + " .\n" +
                                                        "    ?temp sosa:phenomenonTime ?phenomenonTime .\n" +
                                                        "    \n" +
                                                        "    ?phenomenonTime rdf:type time:Interval .\n" +
                                                        "    ?phenomenonTime time:hasEnd ?time .\n" +
                                                        "    ?time rdf:type time:Instant .\n" +
                                                        "    ?time time:inXSDDateTime ?dateTime .\n" +
                                                        "    \n" +
                                                        "\tBIND(xsd:date(?dateTime) AS ?date)\n" +
                                                        "}\n" +
                                                        "order by desc(?date) limit 1";
                                            }
                                        }else {
                                            query= "PREFIX : <http://example.org/efthymiadisKonstantinos/project#>\n" +
                                                    "PREFIX sosa: <http://www.w3.org/ns/sosa/>\n" +
                                                    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                                                    "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                                                    "\n" +
                                                    "select distinct ?date\n" +
                                                    "WHERE {\n" +
                                                    "    ?temp rdf:type :" + observation+" .\n" +
                                                    "    ?temp sosa:resultTime ?dateTime .\n" +
                                                    "    \n" +
                                                    "\tBIND(xsd:date(?dateTime) AS ?date)\n" +
                                                    "}\n";
                                            if(kind.equalsIgnoreCase("min")){
                                                query+="order by ?date limit 1";
                                            }else {
                                                query+="order by desc(?date) limit 1";
                                            }
                                        }
                                        RepositoryManager repositoryManager= new RepositoryManager("http://localhost:7200", repoName);
                                        Object answer= repositoryManager.sparqlQuery(query);

                                        if(answer== null){
                                            textForOutput="error";
                                        } else if (answer instanceof ArrayList<?> tableWithAnswer) {
                                            if (!tableWithAnswer.isEmpty() && tableWithAnswer.get(0) instanceof ArrayList<?> element) {
                                                if (!element.isEmpty() && element.get(0) instanceof String) {
                                                    textForOutput = "";
                                                    int i;
                                                    //the first ArrayList has the names of the variables from the sparql query. So, there is no need to write them in the answer. For this reason the i starts with 1 and not with 0
                                                    for (i = 1; i < tableWithAnswer.size() - 1; i++) {
                                                        ArrayList<String> tempArrayListWithElements = (ArrayList<String>) tableWithAnswer.get(i);
                                                        for (int j = 0; j < tempArrayListWithElements.size(); j++) {
                                                            textForOutput += tempArrayListWithElements.get(j).substring(0,tempArrayListWithElements.get(j).indexOf("^^"));
                                                        }
                                                        textForOutput += "\n";
                                                    }
                                                    //the last repetition will be done there in order not have the \n in the end of the textForOutput
                                                    if (i < tableWithAnswer.size()) {
                                                        ArrayList<String> tempArrayListWithElements = (ArrayList<String>) tableWithAnswer.get(i);
                                                        for (int j = 0; j < tempArrayListWithElements.size(); j++) {
                                                            textForOutput += tempArrayListWithElements.get(j).substring(0,tempArrayListWithElements.get(j).indexOf("^^"));
                                                        }
                                                    }

                                                    if (textForOutput.trim().equalsIgnoreCase("")) {
                                                        textForOutput = "error";
                                                    }
                                                } else {
                                                    textForOutput = "error";
                                                }
                                            } else {
                                                textForOutput = "error";
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if(method.equalsIgnoreCase("POST") && !urlString.equalsIgnoreCase("") && params.length>0){
                    String taskParam= params[0];
                    String task;

                    if(taskParam.substring(0, taskParam.indexOf("=")).equalsIgnoreCase("task")) {
                        task = taskParam.substring(taskParam.indexOf("=") + 1);

                        if(task.equalsIgnoreCase("createFile")){
                            if(params.length==2) {
                                String fileNameParam= params[1];
                                String fileName;

                                if(fileNameParam.substring(0, fileNameParam.indexOf("=")).equalsIgnoreCase("fileName")) {
                                    fileName = fileNameParam.substring(fileNameParam.indexOf("=") + 1);

                                    while(!(requestLine = reader.readLine()).isEmpty()); //the headers are not used in this program

                                    StringBuilder stringBuilder= new StringBuilder();
                                    while(reader.ready()){
                                        stringBuilder.append((char) reader.read());
                                    }
                                    String text= stringBuilder.toString();

                                    File file= new File("folderForFiles/"+fileName);
                                    if(file.exists()){
                                        file.delete();
                                    }

                                    file.createNewFile();

                                    try(FileWriter myWriter = new FileWriter("folderForFiles/"+fileName)){
                                        myWriter.write(text);
                                        textForOutput= "success";
                                    }catch (Throwable t){
                                        textForOutput= "error";
                                    }
                                }
                            }
                        }else if(task.equalsIgnoreCase("addData")){
                            if(params.length==2) {
                                String repoNameParam= params[1];
                                String repoName;

                                if(repoNameParam.substring(0, repoNameParam.indexOf("=")).equalsIgnoreCase("repoName")) {
                                    repoName = repoNameParam.substring(repoNameParam.indexOf("=") + 1);

                                    while(!(requestLine = reader.readLine()).isEmpty()); //the headers are not used in this program

                                    StringBuilder stringBuilder= new StringBuilder();
                                    while(reader.ready()){
                                        stringBuilder.append((char) reader.read());
                                    }

                                    String[] fileNamesWithFeatureOfInterests = stringBuilder.toString().split("\n"); //in the evens positions are the filenames and in the odds are the feature of interests
                                    String[] fileNames;
                                    if(fileNamesWithFeatureOfInterests.length%2==0){
                                        fileNames= new String[fileNamesWithFeatureOfInterests.length/2];
                                    }else {
                                        fileNames= new String[fileNamesWithFeatureOfInterests.length/2+1];
                                    }
                                    ArrayList<String> featureOfInterests= new ArrayList<>();
                                    int j=0;
                                    for(int i=0;i<fileNamesWithFeatureOfInterests.length;i++){
                                        if(i%2==0){
                                            fileNames[j++]=fileNamesWithFeatureOfInterests[i];
                                        }else{
                                            featureOfInterests.add(fileNamesWithFeatureOfInterests[i]);
                                        }
                                    }

                                    ReadJsonFile readJsonFile= new ReadJsonFile(repoName+".owl"); //all the shimmer files will be saved in a file named: <repoName>.owl
                                    readJsonFile.readFiles(fileNames, featureOfInterests);

                                    RepositoryManager repositoryManager= new RepositoryManager("http://localhost:7200", repoName);
                                    if(!RepositoryManager.isOpenMHealthRepository("http://localhost:7200", repoName)){ //if there isn't any repository with this name, this means that the user want to create a new repository with this name
                                        repositoryManager.createRepositoryWithValidationFile();
                                    }
                                    String answer= repositoryManager.getErrorMessage().get(repositoryManager.uploadData(repoName+".owl"));

                                    if(answer==null){
                                        textForOutput= "success";
                                    }else {
                                        textForOutput= answer;
                                    }
                                }else {
                                    textForOutput= "error";
                                }
                            }
                        } else if(task.equalsIgnoreCase("checkIfRepoExists")){
                            while(!(requestLine = reader.readLine()).isEmpty()); //the headers are not used in this program

                            StringBuilder stringBuilder= new StringBuilder();
                            while(reader.ready()){
                                stringBuilder.append((char) reader.read());
                            }

                            String repoName= stringBuilder.toString();
                            if(RepositoryManager.isOpenMHealthRepository("http://localhost:7200", repoName)){
                                textForOutput= "yes";
                            }else{
                                textForOutput= "no";
                            }
                        } else if (task.equalsIgnoreCase("query")) {
                            if(params.length==3) {
                                String repoNameParam = params[1];
                                String repoName;

                                String observationParam= params[2];
                                String observation;

                                if(repoNameParam.substring(0, repoNameParam.indexOf("=")).equalsIgnoreCase("repoName") && observationParam.substring(0, observationParam.indexOf("=")).equalsIgnoreCase("observation")) {
                                    repoName = repoNameParam.substring(repoNameParam.indexOf("=") + 1);
                                    observation= observationParam.substring(observationParam.indexOf("=") + 1);

                                    if(RepositoryManager.isOpenMHealthRepository("http://localhost:7200", repoName)){
                                        while(!(requestLine = reader.readLine()).isEmpty()); //the headers are not used in this program

                                        StringBuilder stringBuilder= new StringBuilder();
                                        while(reader.ready()){
                                            stringBuilder.append((char) reader.read());
                                        }

                                        String[] variables= stringBuilder.toString().split("\n");
                                        if(variables.length==4 || observation.equalsIgnoreCase("PhysicalActivityObservation")){ //only the PhysicalActivityObservation queries have lenght of variables equal to 5
                                            String featureOfInterest= variables[0];
                                            String sensor= variables[1];
                                            String startDate= variables[2];
                                            String endDate= variables[3];
                                            String query="";

                                            if(observation.equalsIgnoreCase("CaloriesBurnedObservation")){
                                                query=  "PREFIX time: <http://www.w3.org/2006/time#>\n" +
                                                        "PREFIX : <http://example.org/efthymiadisKonstantinos/project#>\n" +
                                                        "PREFIX sosa: <http://www.w3.org/ns/sosa/>\n" +
                                                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                                                        "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                                                        "\n" +
                                                        "select ?dateStart (SUM(xsd:double(?valueData)) AS ?totalCalories) ?unit\n" +
                                                        "WHERE {\n" +
                                                        "    ?temp rdf:type :CaloriesBurnedObservation .\n" +
                                                        "    ?temp sosa:hasFeatureOfInterest :"+featureOfInterest+" .\n";

                                                if(!sensor.equalsIgnoreCase("all")){
                                                    query+="?temp sosa:madeBySensor :"+sensor+" .\n";
                                                }

                                                query+= "    ?temp sosa:phenomenonTime ?phenomenonTime .\n" +
                                                        "    ?phenomenonTime rdf:type time:Interval .\n" +
                                                        "    \n" +
                                                        "    ?phenomenonTime time:hasBeginning ?beginning .\n" +
                                                        "    ?beginning rdf:type time:Instant .\n" +
                                                        "    ?beginning time:inXSDDateTime ?dateTimeStart .\n" +
                                                        "    \n" +
                                                        "    ?temp sosa:hasResult ?result .\n" +
                                                        "    ?result rdf:type sosa:Result .\n" +
                                                        "    ?result :unit ?unit .\n" +
                                                        "    ?result :valueData ?valueData .\n" +
                                                        "    \n" +
                                                        "\tBIND(xsd:date(?dateTimeStart) AS ?dateStart)\n" +
                                                        "    \n" +
                                                        "    FILTER(?dateStart>= \""+startDate+"-00:00\"^^xsd:date && ?dateStart<= \""+endDate+"-00:00\"^^xsd:date)\n" +
                                                        "}\n" +
                                                        "group by ?dateStart ?unit\n" +
                                                        "order by ?dateStart";
                                            }else if(observation.equalsIgnoreCase("StepCountObservation")){
                                                query="PREFIX time: <http://www.w3.org/2006/time#>\n" +
                                                        "PREFIX : <http://example.org/efthymiadisKonstantinos/project#>\n" +
                                                        "PREFIX sosa: <http://www.w3.org/ns/sosa/>\n" +
                                                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                                                        "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                                                        "\n" +
                                                        "select ?dateStart (SUM(xsd:double(?valueData)) AS ?totalCalories)\n" +
                                                        "WHERE {\n" +
                                                        "    ?temp rdf:type :StepCountObservation .\n" +
                                                        "    ?temp sosa:hasFeatureOfInterest :"+featureOfInterest+" .\n";

                                                if(!sensor.equalsIgnoreCase("all")){
                                                    query+="?temp sosa:madeBySensor :"+sensor+" .\n";
                                                }

                                                query+= "    ?temp sosa:phenomenonTime ?phenomenonTime .\n" +
                                                        "    ?phenomenonTime rdf:type time:Interval .\n" +
                                                        "    \n" +
                                                        "    ?phenomenonTime time:hasBeginning ?beginning .\n" +
                                                        "    ?beginning rdf:type time:Instant .\n" +
                                                        "    ?beginning time:inXSDDateTime ?dateTimeStart .\n" +
                                                        "    \n" +
                                                        "    ?temp sosa:hasSimpleResult ?valueData .\n" +
                                                        "    \n" +
                                                        "\tBIND(xsd:date(?dateTimeStart) AS ?dateStart)\n" +
                                                        "    \n" +
                                                        "    FILTER(?dateStart>= \""+startDate+"-00:00\"^^xsd:date && ?dateStart<= \""+endDate+"-00:00\"^^xsd:date)\n" +
                                                        "}\n" +
                                                        "group by ?dateStart\n" +
                                                        "order by ?dateStart";

                                                System.out.println(query);
                                            }else if(observation.equalsIgnoreCase("SpeedObservation") || observation.equalsIgnoreCase("HeartRateObservation")){
                                                query= "PREFIX : <http://example.org/efthymiadisKonstantinos/project#>\n" +
                                                        "PREFIX sosa: <http://www.w3.org/ns/sosa/>\n" +
                                                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                                                        "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                                                        "\n" +
                                                        "select ?date (max(xsd:double(?valueData)) AS ?maxValue) (min(xsd:double(?valueData)) AS ?minValue) (avg(xsd:double(?valueData)) AS ?avgValue) ?unit\n" +
                                                        "WHERE {\n" +
                                                        "    ?temp rdf:type :"+observation+" .\n" +
                                                        "    ?temp sosa:hasFeatureOfInterest :"+featureOfInterest+" .\n";

                                                if(!sensor.equalsIgnoreCase("all")){
                                                    query+="?temp sosa:madeBySensor :"+sensor+" .\n";
                                                }

                                                query+="    ?temp sosa:resultTime ?dateTime .\n" +
                                                        "    ?temp sosa:hasResult ?result .\n" +
                                                        "    \n" +
                                                        "    ?result rdf:type sosa:Result .\n" +
                                                        "    ?result :unit ?unit .\n" +
                                                        "    ?result :valueData ?valueData .\n" +
                                                        "    \n" +
                                                        "\tBIND(xsd:date(?dateTime) AS ?date)\n" +
                                                        "    \n" +
                                                        "    FILTER(?date>= \""+startDate+"-00:00\"^^xsd:date && ?date<= \""+endDate+"-00:00\"^^xsd:date)\n" +
                                                        "}\n" +
                                                        "group by ?date ?unit\n" +
                                                        "order by ?date";
                                            } else if (observation.equalsIgnoreCase("PhysicalActivityObservation")) {
                                                String activity= variables[4];

                                                query= "PREFIX : <http://example.org/efthymiadisKonstantinos/project#>\n" +
                                                        "PREFIX sosa: <http://www.w3.org/ns/sosa/>\n" +
                                                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                                                        "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                                                        "\n" +
                                                        "PREFIX time: <http://www.w3.org/2006/time#>\n" +
                                                        "\n" +
                                                        "select ?dateStart (SUM(xsd:double(?totalMinutes)) AS ?totalMinutes)\n" +
                                                        "WHERE {\n" +
                                                        "    ?temp rdf:type :PhysicalActivityObservation .\n" +
                                                        "    ?temp sosa:hasFeatureOfInterest :"+featureOfInterest+" .\n";
                                                if(!sensor.equalsIgnoreCase("all")){
                                                    query+="?temp sosa:madeBySensor :"+sensor+" .\n";
                                                }

                                                if(!activity.equalsIgnoreCase("all")){
                                                    query+="?temp sosa:hasSimpleResult \""+activity+"\"^^xsd:string .\n";
                                                }


                                                query+= "    ?temp sosa:phenomenonTime ?phenomenonTime .\n" +
                                                        "    ?phenomenonTime rdf:type time:Interval .\n" +
                                                        "    \n" +
                                                        "    ?phenomenonTime time:hasBeginning ?beginning .\n" +
                                                        "    ?beginning rdf:type time:Instant .\n" +
                                                        "    ?beginning time:inXSDDateTime ?dateTimeStart .\n" +
                                                        "    \n" +
                                                        "    ?phenomenonTime time:hasEnd ?ending .\n" +
                                                        "    ?ending rdf:type time:Instant .\n" +
                                                        "    ?ending time:inXSDDateTime ?dateTimeEnd .\n" +
                                                        "    \n" +
                                                        "    # Calculate the difference in seconds\n" +
                                                        "  \tBIND((xsd:dateTime(?dateTimeEnd) - xsd:dateTime(?dateTimeStart)) AS ?duration)\n" +
                                                        "    \n" +
                                                        "  \tBIND(REPLACE(STR(?duration), \"P([^T]*T)?\", \"\") AS ?timePart)\n" +
                                                        "    BIND(IF(CONTAINS(?timePart, \"H\"), xsd:integer(STRBEFORE(?timePart, \"H\")), 0) AS ?hours)\n" +
                                                        "  \tBIND(IF(CONTAINS(?timePart, \"M\"), xsd:integer(STRAFTER(STRBEFORE(?timePart, \"M\"), \"H\")), 0) AS ?minutes)\n" +
                                                        "  \tBIND(IF(CONTAINS(?timePart, \"S\"), xsd:double(STRAFTER(STRBEFORE(?timePart, \"S\"), \"M\")), 0) AS ?seconds)\n" +
                                                        "\n" +
                                                        "\t#convert everything to seconds\n" +
                                                        "  \tBIND((?hours * 3600 + ?minutes * 60 + ?seconds) AS ?tempSeconds)\n" +
                                                        "    \n" +
                                                        "    #convert everything to minutes\n" +
                                                        "    BIND((?tempSeconds / 60) AS ?tempMinutes)\n" +
                                                        "    BIND(ROUND(?tempMinutes) AS ?totalMinutes)\n" +
                                                        "    \n" +
                                                        "    BIND(xsd:date(?dateTimeStart) AS ?dateStart)\n" +
                                                        "    FILTER(?dateStart>= \""+startDate+"-00:00\"^^xsd:date && ?dateStart<= \""+endDate+"-00:00\"^^xsd:date)\n" +
                                                        "        \n" +
                                                        "}\n" +
                                                        "group by ?dateStart\n" +
                                                        "order by ?dateStart";
                                            } else if (observation.equalsIgnoreCase("GeopositionObservation")) {
                                                query= "PREFIX : <http://example.org/efthymiadisKonstantinos/project#>\n" +
                                                        "PREFIX sosa: <http://www.w3.org/ns/sosa/>\n" +
                                                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                                                        "\n" +
                                                        "select ?dateTime ?lat ?unitLat ?long ?unitLong\n" +
                                                        "WHERE {\n" +
                                                        "    ?temp rdf:type :GeopositionObservation .\n" +
                                                        "    ?temp sosa:hasFeatureOfInterest :"+featureOfInterest+" .\n";
                                                if(!sensor.equalsIgnoreCase("all")){
                                                    query+="?temp sosa:madeBySensor :"+sensor+" .\n";
                                                }

                                                query+= "    ?temp sosa:resultTime ?dateTime .\n" +
                                                        "    ?temp sosa:hasResult ?result .\n" +
                                                        "    \n" +
                                                        "    ?result rdf:type sosa:Result .\n" +
                                                        "    ?result :unitLatitude ?unitLat .\n" +
                                                        "    ?result :valueLatitude ?lat .\n" +
                                                        "    ?result :unitLongitude ?unitLong .\n" +
                                                        "    ?result :valueLongitude ?long .\n" +
                                                        "    BIND(xsd:date(?dateTime) AS ?dateOnly)\n" +
                                                        "    FILTER(?dateOnly>= \""+startDate+"-00:00\"^^xsd:date && ?dateOnly<= \""+endDate+"-00:00\"^^xsd:date)\n" +
                                                        "}\n" +
                                                        "order by ?dateTime";
                                            } else {
                                                query=  "PREFIX : <http://example.org/efthymiadisKonstantinos/project#>\n" +
                                                        "PREFIX sosa: <http://www.w3.org/ns/sosa/>\n" +
                                                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                                                        "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                                                        "\n" +
                                                        "select distinct ?date ?value ?unit\n" +
                                                        "WHERE {\n" +
                                                        "    ?temp rdf:type :"+observation+" .\n" +
                                                        "    ?temp sosa:hasFeatureOfInterest :"+featureOfInterest+" .\n";

                                                if(!sensor.equalsIgnoreCase("all")){
                                                    query+="?temp sosa:madeBySensor :"+sensor+" .\n";
                                                }

                                                query+="?temp sosa:resultTime ?dateTime .\n" +
                                                        "    ?temp sosa:hasResult ?result .\n" +
                                                        "    ?result rdf:type sosa:Result .\n" +
                                                        "    ?result :unit ?unit .\n" +
                                                        "    ?result :valueData ?value .\n" +
                                                        "    \n" +
                                                        "\tBIND(xsd:date(?dateTime) AS ?date)\n" +
                                                        "    FILTER(?date>= \""+startDate+"-00:00\"^^xsd:date && ?date<= \""+endDate+"-00:00\"^^xsd:date)\n" +
                                                        "} \n" +
                                                        "order by ?date";
                                            }

                                            RepositoryManager repositoryManager= new RepositoryManager("http://localhost:7200", repoName);
                                            Object answer= repositoryManager.sparqlQuery(query);

                                            if(answer==null){
                                                textForOutput="error";
                                            }else if (answer instanceof ArrayList<?> tableWithAnswer) {
                                                if(!tableWithAnswer.isEmpty() && tableWithAnswer.get(0) instanceof ArrayList<?> element){
                                                    if(!element.isEmpty() && element.get(0) instanceof String){
                                                        textForOutput="";
                                                        int i;
                                                        //the first ArrayList has the names of the variables from the sparql query. So, there is no need to write them in the answer. For this reason the i starts with 1 and not with 0
                                                        for(i=1;i<tableWithAnswer.size()-1;i++){
                                                            ArrayList<String> tempArrayListWithElements= (ArrayList<String>) tableWithAnswer.get(i);

                                                            textForOutput+= tempArrayListWithElements.get(0).substring(tempArrayListWithElements.get(0).indexOf("\"")+1, tempArrayListWithElements.get(0).indexOf("\"^^")); //in oder to return only the name, without the URI
                                                            textForOutput+=",";
                                                            textForOutput+= tempArrayListWithElements.get(1).substring(tempArrayListWithElements.get(1).indexOf("\"")+1, tempArrayListWithElements.get(1).indexOf("\"^^")); //in oder to return only the name, without the URI
                                                            if(tempArrayListWithElements.size()>=3){ //Not all the queries have the same columns. StepCountObservation queries have only 2
                                                                textForOutput+=",";
                                                                textForOutput+= tempArrayListWithElements.get(2).substring(tempArrayListWithElements.get(2).indexOf("\"")+1, tempArrayListWithElements.get(2).lastIndexOf("\""));
                                                                if(observation.equalsIgnoreCase("SpeedObservation") || observation.equalsIgnoreCase("HeartRateObservation") || observation.equalsIgnoreCase("GeopositionObservation")){
                                                                    textForOutput+=",";
                                                                    textForOutput+= tempArrayListWithElements.get(3).substring(tempArrayListWithElements.get(3).indexOf("\"")+1, tempArrayListWithElements.get(3).lastIndexOf("\""));
                                                                    textForOutput+=",";
                                                                    textForOutput+= tempArrayListWithElements.get(4).substring(tempArrayListWithElements.get(4).indexOf("\"")+1, tempArrayListWithElements.get(4).lastIndexOf("\""));
                                                                }
                                                            }
                                                            textForOutput+="\n";
                                                        }
                                                        //the last repetition will be done there in order not have the \n in the end of the textForOutput
                                                        if(i<tableWithAnswer.size()){
                                                            ArrayList<String> tempArrayListWithElements= (ArrayList<String>) tableWithAnswer.get(i);
                                                            textForOutput+= tempArrayListWithElements.get(0).substring(tempArrayListWithElements.get(0).indexOf("\"")+1, tempArrayListWithElements.get(0).indexOf("\"^^")); //in oder to return only the name, without the URI
                                                            textForOutput+=",";
                                                            textForOutput+= tempArrayListWithElements.get(1).substring(tempArrayListWithElements.get(1).indexOf("\"")+1, tempArrayListWithElements.get(1).indexOf("\"^^")); //in oder to return only the name, without the URI
                                                            if(tempArrayListWithElements.size()>=3){ //Not all the queries have the same columns. StepCountObservation has only 2
                                                                textForOutput+=",";
                                                                textForOutput+= tempArrayListWithElements.get(2).substring(tempArrayListWithElements.get(2).indexOf("\"")+1, tempArrayListWithElements.get(2).lastIndexOf("\""));
                                                                if(observation.equalsIgnoreCase("SpeedObservation") || observation.equalsIgnoreCase("HeartRateObservation") || observation.equalsIgnoreCase("GeopositionObservation")){
                                                                    textForOutput+=",";
                                                                    textForOutput+= tempArrayListWithElements.get(3).substring(tempArrayListWithElements.get(3).indexOf("\"")+1, tempArrayListWithElements.get(3).lastIndexOf("\""));
                                                                    textForOutput+=",";
                                                                    textForOutput+= tempArrayListWithElements.get(4).substring(tempArrayListWithElements.get(4).indexOf("\"")+1, tempArrayListWithElements.get(4).lastIndexOf("\""));
                                                                }
                                                            }
                                                        }

                                                        if(textForOutput.trim().equalsIgnoreCase("")){
                                                            textForOutput="error";
                                                        }
                                                    }else {
                                                        textForOutput= "error";
                                                    }
                                                }else {
                                                    textForOutput= "error";
                                                }
                                            }
                                        }else {
                                            textForOutput= "error";
                                        }
                                    }else{
                                        textForOutput= "error";
                                    }
                                }
                            }
                        } else if (task.equalsIgnoreCase("getActivities")) {
                            if(params.length==2){
                                String repoNameParam= params[1];
                                String repoName;

                                String featureOfInterest;
                                String sensor;

                                if(repoNameParam.substring(0, repoNameParam.indexOf("=")).equalsIgnoreCase("repoName")) {
                                    repoName = repoNameParam.substring(repoNameParam.indexOf("=") + 1);

                                    while(!(requestLine = reader.readLine()).isEmpty()); //the headers are not used in this program

                                    StringBuilder stringBuilder= new StringBuilder();
                                    while(reader.ready()){
                                        stringBuilder.append((char) reader.read());
                                    }

                                    String[] variables= stringBuilder.toString().split("\n");

                                    featureOfInterest= variables[0];
                                    sensor= variables[1];

                                    String query=   "PREFIX : <http://example.org/efthymiadisKonstantinos/project#>\n" +
                                                    "PREFIX sosa: <http://www.w3.org/ns/sosa/>\n" +
                                                    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                                                    "\n" +
                                                    "select distinct ?result\n" +
                                                    "WHERE {\n" +
                                                    "    ?temp rdf:type :PhysicalActivityObservation .\n" +
                                                    "    ?temp sosa:hasFeatureOfInterest :"+featureOfInterest+" .\n";

                                    if(!sensor.equalsIgnoreCase("all")){
                                        query+="?temp sosa:madeBySensor :"+sensor+" .\n";
                                    }

                                    query+=         "    ?temp sosa:hasSimpleResult ?result .\n" +
                                                    "}";

                                    if(RepositoryManager.isOpenMHealthRepository("http://localhost:7200", repoName)) {
                                        RepositoryManager repositoryManager = new RepositoryManager("http://localhost:7200", repoName);

                                        Object answer = repositoryManager.sparqlQuery(query);

                                        if (answer == null) {
                                            textForOutput = "error";
                                        } else if (answer instanceof ArrayList<?> tableWithAnswer) {
                                            if (!tableWithAnswer.isEmpty() && tableWithAnswer.get(0) instanceof ArrayList<?> element) {
                                                if (!element.isEmpty() && element.get(0) instanceof String) {
                                                    textForOutput = "";
                                                    int i;
                                                    //the first ArrayList has the names of the variables from the sparql query. So, there is no need to write them in the answer. For this reason the i starts with 1 and not with 0
                                                    for (i = 1; i < tableWithAnswer.size() - 1; i++) {
                                                        ArrayList<String> tempArrayListWithElements = (ArrayList<String>) tableWithAnswer.get(i);

                                                        textForOutput += tempArrayListWithElements.get(0).substring(tempArrayListWithElements.get(0).indexOf("\"") + 1, tempArrayListWithElements.get(0).lastIndexOf("\"")); //in oder to return only the name, without the URI
                                                        textForOutput += "\n";
                                                    }
                                                    //the last repetition will be done there in order not have the \n in the end of the textForOutput
                                                    if (i < tableWithAnswer.size()) {
                                                        ArrayList<String> tempArrayListWithElements = (ArrayList<String>) tableWithAnswer.get(i);
                                                        textForOutput += tempArrayListWithElements.get(0).substring(tempArrayListWithElements.get(0).indexOf("\"") + 1, tempArrayListWithElements.get(0).lastIndexOf("\"")); //in oder to return only the name, without the URI
                                                    }

                                                    if (textForOutput.trim().equalsIgnoreCase("")) {
                                                        textForOutput = "error";
                                                    }
                                                } else {
                                                    textForOutput = "error";
                                                }
                                            } else {
                                                textForOutput = "error";
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            String response="HTTP/1.1 200 OK\r\n" +
                    "Access-Control-Allow-Origin: *\r\n" +
                    "Content-Type: text/plain\r\n\r\n" +
                    textForOutput;

            output.write(response.getBytes(StandardCharsets.UTF_8));
            output.flush();
        }catch (IOException e){
            System.err.println("Error: " + e.getMessage());
        }finally {
            try{
                output.close();
                reader.close();
                clientSocket.close();
            }catch (IOException e){
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private boolean checkConnection(){
        try (Socket socket= new Socket("localhost", 7200)) {
            return true;
        } catch (IOException e) {
            System.out.println("Error with the connection to graphDB server");
        }
        return false;
    }

    private ArrayList<String> getAllOpenMhealthRepositories(){
        ArrayList<String> allRepositories= getAllRepositories();
        if(allRepositories== null || allRepositories.isEmpty()){
            return null;
        }

        ArrayList<String> forReturn= new ArrayList<>();

        for(String tempRepoName: allRepositories){
            if(RepositoryManager.isOpenMHealthRepository("http://localhost:7200", tempRepoName)){
                forReturn.add(tempRepoName);
            }
        }

        return forReturn;
    }

    private ArrayList<String> getAllRepositories(){
        if(!checkConnection()){
            return null;
        }

        RemoteRepositoryManager remoteRepositoryManager= new RemoteRepositoryManager("http://localhost:7200");
        remoteRepositoryManager.init();

        List<RepositoryInfo> repositories= (List<RepositoryInfo>) remoteRepositoryManager.getAllRepositoryInfos();

        ArrayList<String> repositoriesNames= new ArrayList<>();
        
        for(RepositoryInfo repositoryInfo: repositories){
            repositoriesNames.add(repositoryInfo.getId());
        }
        
        return repositoriesNames;

    }



}
