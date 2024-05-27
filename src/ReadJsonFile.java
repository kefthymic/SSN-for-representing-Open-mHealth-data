import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ReadJsonFile {
    String owlFileName;
    private Scanner input;
    ArrayList<String> filePaths;
    File file;
    HashMap<String, String> prefix;

    public ReadJsonFile(String owlFileName){
        input= new Scanner(System.in);
        filePaths= new ArrayList<>();
        prefix= new HashMap<>();
        this.owlFileName= owlFileName;
        addPrefixes();
    }
    private void addPrefixes(){
        prefix.put("rdf", "<http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
        prefix.put("rdfs", "<http://www.w3.org/2000/01/rdf-schema#>");
        prefix.put("owl", "<http://www.w3.org/2002/07/owl#>");
        prefix.put("sosa", "<http://www.w3.org/ns/sosa/>");
        prefix. put("xsd", "<http://www.w3.org/2001/XMLSchema#>");
        prefix.put("time", "<http://www.w3.org/2006/time#>");

    }

    protected boolean readFiles(String[] files, ArrayList<String> featureOfInterests){
        for( String tempFile : files){
            file= new File(tempFile);
            if (!file.exists()){
                System.out.println("The file with name: '" + tempFile + "' does not exist");
            }else{
                filePaths.add(tempFile);
            }
        }
        return convertJsonToRdf(featureOfInterests);
    }

    protected boolean readFiles() {
        String pathToFile;
        String answer;
        do {
            System.out.println("Please insert the path to the json file with the open mHealth data");
            pathToFile = input.nextLine();
            file = new File(pathToFile);
            if (!file.exists()) {
                System.out.println("There is not this file");
            } else {
                filePaths.add(pathToFile);
            }
            System.out.println("Do you want to add another file? If not write 'exit'");
            answer = input.nextLine();
        } while (!answer.equalsIgnoreCase("exit"));
        return convertJsonToRdf();
    }

    private void createTheFile(){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(this.owlFileName))) {
            writer.write("@prefix : <http://example.org/efthymiadisKonstantinos/project#> .");
            writer.newLine();
            for(String key : prefix.keySet()){
                writer.write("@prefix " + key + ": " + prefix.get(key) + " .");
                writer.newLine();
            }

            writer.newLine();
            writer.write("<http://example.org/efthymiadisKonstantinos/project> rdf:type owl:Ontology .");
            writer.newLine();

            writer.newLine();
            writer.write(   "[ rdf:type owl:Ontology ;\n" +
                                "   owl:imports sosa:\n" +
                                " ] .");
            writer.newLine();

            writer.newLine();
            writer.write("################################################################");
            writer.newLine();
            writer.write("################################################################");
            writer.newLine();
            writer.write("#####################Declare my Properties######################");
            writer.newLine();
            writer.write("################################################################");
            writer.newLine();
            writer.write("################################################################");
            writer.newLine();
            writer.newLine();

            writer.write(":platformName rdf:type owl:DatatypeProperty ;");
            writer.newLine();
            writer.write("  rdfs:domain sosa:Platform ;");
            writer.newLine();
            writer.write("  rdfs:range xsd:string .");
            writer.newLine();
            writer.newLine();

            writer.write(":sensorName rdf:type owl:DatatypeProperty ;");
            writer.newLine();
            writer.write("  rdfs:domain sosa:Sensor ;");
            writer.newLine();
            writer.write("  rdfs:range xsd:string .");
            writer.newLine();
            writer.newLine();

            writer.write(":versionOfObservableProperty rdf:type owl:DatatypeProperty ;");
            writer.newLine();
            writer.write("  rdfs:domain sosa:ObservableProperty ;");
            writer.newLine();
            writer.write("  rdfs:range xsd:string .");
            writer.newLine();
            writer.newLine();

            writer.write(":namespaceOfObservableProperty rdf:type owl:DatatypeProperty ;");
            writer.newLine();
            writer.write("  rdfs:domain sosa:ObservableProperty ;");
            writer.newLine();
            writer.write("  rdfs:range xsd:string .");
            writer.newLine();
            writer.newLine();

            writer.write(":unit rdf:type owl:DatatypeProperty ;");
            writer.newLine();
            writer.write("  rdfs:domain sosa:Result ;");
            writer.newLine();
            writer.write("  rdfs:range xsd:string .");
            writer.newLine();
            writer.newLine();

            writer.write(":unitLatitude rdf:type owl:DatatypeProperty ;");
            writer.newLine();
            writer.write("  rdfs:subPropertyOf :unit ;");
            writer.newLine();
            writer.write("  rdfs:domain sosa:Result ;");
            writer.newLine();
            writer.write("  rdfs:range xsd:string .");
            writer.newLine();
            writer.newLine();

            writer.write(":unitLongitude rdf:type owl:DatatypeProperty ;");
            writer.newLine();
            writer.write("  rdfs:subPropertyOf :unit ;");
            writer.newLine();
            writer.write("  rdfs:domain sosa:Result ;");
            writer.newLine();
            writer.write("  rdfs:range xsd:string .");
            writer.newLine();
            writer.newLine();

            writer.write(":valueData rdf:type owl:DatatypeProperty ;");
            writer.newLine();
            writer.write("  rdfs:domain sosa:Result ;");
            writer.newLine();
            writer.write("  rdfs:range xsd:double .");
            writer.newLine();
            writer.newLine();

            writer.write(":valueLatitude rdf:type owl:DatatypeProperty ;");
            writer.newLine();
            writer.write("  rdfs:subPropertyOf :valueData ;");
            writer.newLine();
            writer.write("  rdfs:domain sosa:Result ;");
            writer.newLine();
            writer.write("  rdfs:range xsd:double .");
            writer.newLine();
            writer.newLine();

            writer.write(":valueLongitude rdf:type owl:DatatypeProperty ;");
            writer.newLine();
            writer.write("  rdfs:subPropertyOf :valueData ;");
            writer.newLine();
            writer.write("  rdfs:domain sosa:Result ;");
            writer.newLine();
            writer.write("  rdfs:range xsd:double .");
            writer.newLine();
            writer.newLine();

            writer.write(":positionSystem rdf:type owl:DatatypeProperty ;");
            writer.newLine();
            writer.write("  rdfs:domain sosa:Result ;");
            writer.newLine();
            writer.write("  rdfs:range xsd:string .");
            writer.newLine();
            writer.newLine();

            writer.newLine();
            writer.write("################################################################");
            writer.newLine();
            writer.write("################################################################");
            writer.newLine();
            writer.write("######################Declare my Classes########################");
            writer.newLine();
            writer.write("################################################################");
            writer.newLine();
            writer.write("################################################################");
            writer.newLine();
            writer.newLine();

            writer.write(":BodyHeightObservation rdf:type owl:Class ;");
            writer.newLine();
            writer.write("  rdfs:subClassOf sosa:Observation ;");
            writer.newLine();
            writer.write("  rdfs:label \"A class for the observations with observable property: body height\" .");
            writer.newLine();
            writer.newLine();

            writer.write(":BodyWeightObservation rdf:type owl:Class ;");
            writer.newLine();
            writer.write("  rdfs:subClassOf sosa:Observation ;");
            writer.newLine();
            writer.write("  rdfs:label \"A class for the observations with observable property: body weight\" .");
            writer.newLine();
            writer.newLine();

            writer.write(":CaloriesBurnedObservation rdf:type owl:Class ;");
            writer.newLine();
            writer.write("  rdfs:subClassOf sosa:Observation ;");
            writer.newLine();
            writer.write("  rdfs:label \"A class for the observations with observable property: calories burned\" .");
            writer.newLine();
            writer.newLine();

            writer.write(":GeopositionObservation rdf:type owl:Class ;");
            writer.newLine();
            writer.write("  rdfs:subClassOf sosa:Observation ;");
            writer.newLine();
            writer.write("  rdfs:label \"A class for the observations with observable property: geoposition\" .");
            writer.newLine();
            writer.newLine();

            writer.write(":HeartRateObservation rdf:type owl:Class ;");
            writer.newLine();
            writer.write("  rdfs:subClassOf sosa:Observation ;");
            writer.newLine();
            writer.write("  rdfs:label \"A class for the observations with observable property: heart rate\" .");
            writer.newLine();
            writer.newLine();

            writer.write(":PhysicalActivityObservation rdf:type owl:Class ;");
            writer.newLine();
            writer.write("  rdfs:subClassOf sosa:Observation ;");
            writer.newLine();
            writer.write("  rdfs:label \"A class for the observations with observable property: physical activity\" .");
            writer.newLine();
            writer.newLine();

            writer.write(":SpeedObservation rdf:type owl:Class ;");
            writer.newLine();
            writer.write("  rdfs:subClassOf sosa:Observation ;");
            writer.newLine();
            writer.write("  rdfs:label \"A class for the observations with observable property: speed\" .");
            writer.newLine();
            writer.newLine();

            writer.write(":StepCountObservation rdf:type owl:Class ;");
            writer.newLine();
            writer.write("  rdfs:subClassOf sosa:Observation ;");
            writer.newLine();
            writer.write("  rdfs:label \"A class for the observations with observable property: step count\" .");
            writer.newLine();
            writer.newLine();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private boolean convertJsonToRdf(ArrayList<String> featureOfInterests){

        if(featureOfInterests.size()!= filePaths.size()){
            return false;
        }

        String sensor;
        String platform;
        String idForPlatformObject="";
        String idForSensorObject;

        String userName;
        String observableProperty;
        createTheFile();

        JSONParser jsonParser= new JSONParser();

        for(int i=0;i< filePaths.size();i++){
            try(FileReader reader= new FileReader(filePaths.get(i))){
                userName= featureOfInterests.get(i);
                userName= userName.replaceAll("\\s", "");
                BufferedWriter writer = new BufferedWriter(new FileWriter(this.owlFileName, true));


                JSONObject obj= (JSONObject) jsonParser.parse(reader);

                JSONArray dataArray= (JSONArray) obj.get("body");

                writer.newLine();
                writer.write("################################################################");
                writer.newLine();
                writer.write("################################################################");
                writer.newLine();
                writer.write("########################Declare the Data########################");
                writer.newLine();
                writer.write("################################################################");
                writer.newLine();
                writer.write("################################################################");
                writer.newLine();
                writer.newLine();

                writer.newLine();
                writer.write("###");
                writer.newLine();
                writer.write("### Declare the Data of the file: " + filePaths.get(i));
                writer.newLine();
                writer.write("###");
                writer.newLine();
                writer.newLine();


                writer.write(":"+userName+ " rdf:type sosa:FeatureOfInterest ;\n");
                writer.write("  rdfs:label \"The username of the person with the data\" .");
                writer.newLine();
                writer.close();

                for(int j=0;j<dataArray.size();j++){
                    writer = new BufferedWriter(new FileWriter(this.owlFileName, true));

                    writer.newLine();
                    writer.write("################### Declare the Element No:" + j + " ###################");
                    writer.newLine();
                    writer.newLine();

                    JSONObject tempElement= (JSONObject) dataArray.get(j);

                    JSONObject header= (JSONObject) tempElement.get("header");
                    String id= (String) header.get("id");
                    if(id==null){
                        return false;
                    }else {
                        id= id.replaceAll("\\s", "");
                    }

                    JSONObject acquisitionProvenance= (JSONObject) header.get("acquisition_provenance");

                    sensor= (String) acquisitionProvenance.get("source_origin_id");
                    platform= (String) acquisitionProvenance.get("source_name");


                    if(sensor==null){
                        sensor= platform;
                        platform= null;
                    }else{
                        idForPlatformObject= platform.replaceAll("\\s", "_");
                    }

                    idForSensorObject= sensor.replaceAll("\\s", "_");


                    JSONObject schemaId= (JSONObject) header.get("schema_id");
                    observableProperty= (String) schemaId.get("name");
                    observableProperty= observableProperty.replaceAll("\\s", "");
                    String namespace= (String) schemaId.get("namespace");
                    String version= (String) schemaId.get("version");

                    idForSensorObject= observableProperty + "_" + idForSensorObject;
                    idForPlatformObject= observableProperty + "_" + idForPlatformObject;

                    writer.write(":"+idForSensorObject+" rdf:type sosa:Sensor ;\n");
                    writer.write("  :sensorName \"" + sensor + "\"^^xsd:string .");
                    if(platform!=null){
                        writer.newLine();
                        writer.write(":"+ idForPlatformObject + " rdf:type sosa:Platform ;\n");
                        writer.write("  :platformName \"" + platform + "\"^^xsd:string ;\n");
                        writer.write("  sosa:hosts :"+idForSensorObject +" .");
                    }
                    writer.newLine();
                    writer.newLine();

                    writer.write(":"+ observableProperty + " rdf:type sosa:ObservableProperty ;\n");
                    writer.write("  :versionOfObservableProperty \"" + version + "\"^^xsd:string ;\n");
                    writer.write("  :namespaceOfObservableProperty \"" + namespace + "\"^^xsd:string ;\n");
                    writer.write("  sosa:isObservedBy :"+idForSensorObject + " .");
                    writer.newLine();
                    writer.newLine();

                    writer.close();

                    JSONObject body= (JSONObject) tempElement.get("body");
                    switch (observableProperty) {
                        case "body-height" ->
                                bodyHeightReader(id, userName, idForSensorObject, observableProperty, body);
                        case "body-weight" ->
                                bodyWeightReader(id, userName, idForSensorObject, observableProperty, body);
                        case "calories-burned" ->
                                caloriesBurnedReader(id, userName, idForSensorObject, observableProperty, body);
                        case "geoposition" ->
                                geopositionReader(id, userName, idForSensorObject, observableProperty, body);
                        case "heart-rate" ->
                                heartRateReader(id, userName, idForSensorObject, observableProperty, body);
                        case "physical-activity" ->
                                physicalActivityReader(id, userName, idForSensorObject, observableProperty, body);
                        case "speed" ->
                                speedReader(id, userName, idForSensorObject, observableProperty, body);
                        case "step-count" ->
                                stepCountReader(id, userName, idForSensorObject, observableProperty, body);
                    }
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    private boolean convertJsonToRdf(){
        String sensor;
        String platform;
        String idForPlatformObject="";
        String idForSensorObject;

        String userName;
        String observableProperty;
        createTheFile();

        JSONParser jsonParser= new JSONParser();

        for(int i=0;i< filePaths.size();i++){
            try(FileReader reader= new FileReader(filePaths.get(i))){
                System.out.println("Write a username for the person that the data is contained in the file: " + filePaths.get(i));
                userName= input.nextLine();
                userName= userName.replaceAll("\\s", "");
                BufferedWriter writer = new BufferedWriter(new FileWriter(this.owlFileName, true));


                JSONObject obj= (JSONObject) jsonParser.parse(reader);

                JSONArray dataArray= (JSONArray) obj.get("body");

                writer.newLine();
                writer.write("################################################################");
                writer.newLine();
                writer.write("################################################################");
                writer.newLine();
                writer.write("########################Declare the Data########################");
                writer.newLine();
                writer.write("################################################################");
                writer.newLine();
                writer.write("################################################################");
                writer.newLine();
                writer.newLine();

                writer.newLine();
                writer.write("###");
                writer.newLine();
                writer.write("### Declare the Data of the file: " + filePaths.get(i));
                writer.newLine();
                writer.write("###");
                writer.newLine();
                writer.newLine();


                writer.write(":"+userName+ " rdf:type sosa:FeatureOfInterest ;\n");
                writer.write("  rdfs:label \"The username of the person with the data\" .");
                writer.newLine();
                writer.close();

                for(int j=0;j<dataArray.size();j++){
                    writer = new BufferedWriter(new FileWriter(this.owlFileName, true));

                    writer.newLine();
                    writer.write("################### Declare the Element No:" + j + " ###################");
                    writer.newLine();
                    writer.newLine();

                    JSONObject tempElement= (JSONObject) dataArray.get(j);

                    JSONObject header= (JSONObject) tempElement.get("header");
                    String id= (String) header.get("id");
                    if(id==null){
                        return false;
                    }else {
                        id= id.replaceAll("\\s", "");
                    }

                    JSONObject acquisitionProvenance= (JSONObject) header.get("acquisition_provenance");

                    sensor= (String) acquisitionProvenance.get("source_origin_id");
                    platform= (String) acquisitionProvenance.get("source_name");


                    if(sensor==null){
                        sensor= platform;
                        platform= null;
                    }else{
                        idForPlatformObject= platform.replaceAll("\\s", "_");
                    }

                    idForSensorObject= sensor.replaceAll("\\s", "_");


                    JSONObject schemaId= (JSONObject) header.get("schema_id");
                    observableProperty= (String) schemaId.get("name");
                    observableProperty= observableProperty.replaceAll("\\s", "");
                    String namespace= (String) schemaId.get("namespace");
                    String version= (String) schemaId.get("version");

                    idForSensorObject= observableProperty + "_" + idForSensorObject;
                    idForPlatformObject= observableProperty + "_" + idForPlatformObject;

                    writer.write(":"+idForSensorObject+" rdf:type sosa:Sensor ;\n");
                    writer.write("  :sensorName \"" + sensor + "\"^^xsd:string .");
                    if(platform!=null){
                        writer.newLine();
                        writer.write(":"+ idForPlatformObject + " rdf:type sosa:Platform ;\n");
                        writer.write("  :platformName \"" + platform + "\"^^xsd:string ;\n");
                        writer.write("  sosa:hosts :"+idForSensorObject +" .");
                    }
                    writer.newLine();
                    writer.newLine();

                    writer.write(":"+ observableProperty + " rdf:type sosa:ObservableProperty ;\n");
                    writer.write("  :versionOfObservableProperty \"" + version + "\"^^xsd:string ;\n");
                    writer.write("  :namespaceOfObservableProperty \"" + namespace + "\"^^xsd:string ;\n");
                    writer.write("  sosa:isObservedBy :"+idForSensorObject + " .");
                    writer.newLine();
                    writer.newLine();

                    writer.close();

                    JSONObject body= (JSONObject) tempElement.get("body");
                    switch (observableProperty) {
                        case "body-height" ->
                                bodyHeightReader(id, userName, idForSensorObject, observableProperty, body);
                        case "body-weight" ->
                                bodyWeightReader(id, userName, idForSensorObject, observableProperty, body);
                        case "calories-burned" ->
                                caloriesBurnedReader(id, userName, idForSensorObject, observableProperty, body);
                        case "geoposition" ->
                                geopositionReader(id, userName, idForSensorObject, observableProperty, body);
                        case "heart-rate" ->
                                heartRateReader(id, userName, idForSensorObject, observableProperty, body);
                        case "physical-activity" ->
                                physicalActivityReader(id, userName, idForSensorObject, observableProperty, body);
                        case "speed" ->
                                speedReader(id, userName, idForSensorObject, observableProperty, body);
                        case "step-count" ->
                                stepCountReader(id, userName, idForSensorObject, observableProperty, body);
                    }
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void writeTriplesForObservation(String kindOfObservation, String id, String userName, String sensor, String observableProperty, String textForBody){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(this.owlFileName, true))){
            writer.write(":" + id + " rdf:type :" + kindOfObservation +" ;");
            writer.newLine();
            writer.write("  sosa:hasFeatureOfInterest :"+ userName + " ;");
            writer.newLine();
            writer.write("  sosa:madeBySensor :"+sensor + " ;");
            writer.newLine();
            writer.write("  sosa:observedProperty :" + observableProperty + " ;");
            writer.newLine();
            writer.write(textForBody);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void bodyHeightReader(String id, String userName, String sensor, String observableProperty, JSONObject body){
        String temp;

        JSONObject effectiveTimeFrame= (JSONObject) body.get("effective_time_frame");
        String dateTime= (String) effectiveTimeFrame.get("date_time");

        JSONObject bodyHeight= (JSONObject) body.get("body_height");
        String unitOfHeight= (String) bodyHeight.get("unit");
        Double valueOfHeight= (Double) bodyHeight.get("value");

        temp= "  sosa:resultTime \"" + dateTime + "\"^^xsd:dateTime ;\n";
        temp+="  sosa:hasResult [\n";
        temp+="    rdf:type sosa:Result ;\n";
        temp+="    :unit \"" + unitOfHeight + "\"^^xsd:string ;\n";
        temp+="    :valueData \"" + valueOfHeight + "\"^^xsd:double ] .\n";

        writeTriplesForObservation("BodyHeightObservation", id, userName, sensor, observableProperty, temp);
    }

    private void bodyWeightReader(String id, String userName, String sensor, String observableProperty, JSONObject body){
        String temp;

        JSONObject effectiveTimeFrame= (JSONObject) body.get("effective_time_frame");
        String dateTime= (String) effectiveTimeFrame.get("date_time");

        JSONObject bodyWeight= (JSONObject) body.get("body_weight");
        String unitOfWeight= (String) bodyWeight.get("unit");
        Double valueOfWeight= (Double) bodyWeight.get("value");

        temp= "  sosa:resultTime \"" + dateTime + "\"^^xsd:dateTime ;\n";
        temp+="  sosa:hasResult [\n";
        temp+="    rdf:type sosa:Result ;\n";
        temp+="    :unit \"" + unitOfWeight + "\"^^xsd:string ;\n";
        temp+="    :valueData \"" + valueOfWeight + "\"^^xsd:double ] .\n";


        writeTriplesForObservation("BodyWeightObservation", id, userName, sensor, observableProperty, temp);
    }

    private void caloriesBurnedReader(String id, String userName, String sensor, String observableProperty, JSONObject body){
        String temp;

        JSONObject effectiveTimeFrame= (JSONObject) body.get("effective_time_frame");
        JSONObject timeInterval= (JSONObject) effectiveTimeFrame.get("time_interval");
        String startDateTime= (String) timeInterval.get("start_date_time");
        String endDateTime= (String) timeInterval.get("end_date_time");

        JSONObject kcalBurned= (JSONObject) body.get("kcal_burned");
        String unitOfCalories= (String) kcalBurned.get("unit");
        Double valueOfCalories= (Double) kcalBurned.get("value");

        temp= "    sosa:phenomenonTime [\n";
        temp+="        rdf:type time:Interval ;\n";
        temp+="        time:hasBeginning  [\n";
        temp+="            rdf:type time:Instant ;\n";
        temp+="            time:inXSDDateTime \"" + startDateTime + "\"^^xsd:dateTime ] ;\n";
        temp+="        time:hasEnd [\n";
        temp+="            rdf:type time:Instant ;\n";
        temp+="            time:inXSDDateTime \""+ endDateTime + "\"^^xsd:dateTime ] ] ;\n";

        temp+="    sosa:hasResult [\n";
        temp+="        rdf:type sosa:Result ;\n";
        temp+="        :unit \"" + unitOfCalories + "\"^^xsd:string ;\n";
        temp+="        :valueData \"" + valueOfCalories + "\"^^xsd:double ] .\n";

        writeTriplesForObservation("CaloriesBurnedObservation", id, userName, sensor, observableProperty, temp);
    }

    private void geopositionReader(String id, String userName, String sensor, String observableProperty, JSONObject body){
        String temp;

        JSONObject effectiveTimeFrame= (JSONObject) body.get("effective_time_frame");
        String dateTime= (String) effectiveTimeFrame.get("date_time");

        JSONObject latitude= (JSONObject) body.get("latitude");
        String unitLatitude= (String) latitude.get("unit");
        Double valueLatitude= (Double) latitude.get("value");

        JSONObject longitude= (JSONObject) body.get("longitude");
        String unitLongitude= (String) longitude.get("unit");
        Double valueLongitude= (Double) longitude.get("value");

        String positionSystem= (String) body.get("positioning_system");

        temp= "  sosa:resultTime \"" + dateTime + "\"^^xsd:dateTime ;\n";
        temp+="  sosa:hasResult [\n";
        temp+="     rdf:type sosa:Result ;\n";
        temp+="     :unitLatitude \"" + unitLatitude + "\"^^xsd:string ;\n";
        temp+="     :valueLatitude \"" + valueLatitude + "\"^^xsd:double ;\n";
        temp+="     :unitLongitude \"" + unitLongitude + "\"^^xsd:string ;\n";
        temp+="     :valueLongitude \"" + valueLongitude + "\"^^xsd:double ;\n";
        temp+="     :positionSystem \"" + positionSystem + "\"^^xsd:string ] .\n";

        writeTriplesForObservation("GeopositionObservation", id, userName, sensor, observableProperty, temp);
    }

    private void heartRateReader(String id, String userName, String sensor, String observableProperty, JSONObject body){
        String temp="";

        JSONObject effectiveTimeFrame= (JSONObject) body.get("effective_time_frame");
        String dateTime= (String) effectiveTimeFrame.get("date_time");

        JSONObject heartRate= (JSONObject) body.get("heart_rate");
        String unitOfHeartRate= (String) heartRate.get("unit");
        Double valueOfHeartRate= (Double) heartRate.get("value");

        temp= "  sosa:resultTime \"" + dateTime + "\"^^xsd:dateTime ;\n";
        temp+="  sosa:hasResult [\n";
        temp+="    rdf:type sosa:Result ;\n";
        temp+="    :unit \"" + unitOfHeartRate + "\"^^xsd:string ;\n";
        temp+="    :valueData \"" + valueOfHeartRate + "\"^^xsd:double ] .\n";

        writeTriplesForObservation("HeartRateObservation", id, userName, sensor, observableProperty, temp);
    }

    private void physicalActivityReader(String id, String userName, String sensor, String observableProperty, JSONObject body){
        String temp="";

        JSONObject effectiveTimeFrame= (JSONObject) body.get("effective_time_frame");
        JSONObject timeInterval= (JSONObject) effectiveTimeFrame.get("time_interval");
        String startDateTime= (String) timeInterval.get("start_date_time");
        String endDateTime= (String) timeInterval.get("end_date_time");

        String activityName= (String) body.get("activity_name");

        temp= "    sosa:phenomenonTime [\n";
        temp+="        rdf:type time:Interval ;\n";
        temp+="        time:hasBeginning  [\n";
        temp+="            rdf:type time:Instant ;\n";
        temp+="            time:inXSDDateTime \"" + startDateTime + "\"^^xsd:dateTime ] ;\n";
        temp+="        time:hasEnd [\n";
        temp+="            rdf:type time:Instant ;\n";
        temp+="            time:inXSDDateTime \""+ endDateTime + "\"^^xsd:dateTime ] ] ;\n";

        temp+= "    sosa:hasSimpleResult \"" + activityName + "\"^^xsd:string .\n";

        writeTriplesForObservation("PhysicalActivityObservation", id, userName, sensor, observableProperty, temp);
    }

    private void speedReader(String id, String userName, String sensor, String observableProperty, JSONObject body){
        String temp;

        JSONObject effectiveTimeFrame= (JSONObject) body.get("effective_time_frame");
        String dateTime= (String) effectiveTimeFrame.get("date_time");

        JSONObject speed= (JSONObject) body.get("speed");
        String unitOfSpeed= (String) speed.get("unit");
        Double valueOfSpeed= (Double) speed.get("value");

        temp= "  sosa:resultTime \"" + dateTime + "\"^^xsd:dateTime ;\n";
        temp+="  sosa:hasResult [\n";
        temp+="    rdf:type sosa:Result ;\n";
        temp+="    :unit \"" + unitOfSpeed + "\"^^xsd:string ;\n";
        temp+="    :valueData \"" + valueOfSpeed + "\"^^xsd:double ] .\n";

        writeTriplesForObservation("SpeedObservation", id, userName, sensor, observableProperty, temp);
    }

    private void stepCountReader(String id, String userName, String sensor, String observableProperty, JSONObject body){
        String temp="";

        JSONObject effectiveTimeFrame= (JSONObject) body.get("effective_time_frame");
        JSONObject timeInterval= (JSONObject) effectiveTimeFrame.get("time_interval");
        String startDateTime= (String) timeInterval.get("start_date_time");
        String endDateTime= (String) timeInterval.get("end_date_time");

        Long stepCount= (Long) body.get("step_count");

        temp= "    sosa:phenomenonTime [\n";
        temp+="        rdf:type time:Interval ;\n";
        temp+="        time:hasBeginning  [\n";
        temp+="            rdf:type time:Instant ;\n";
        temp+="            time:inXSDDateTime \"" + startDateTime + "\"^^xsd:dateTime ] ;\n";
        temp+="        time:hasEnd [\n";
        temp+="            rdf:type time:Instant ;\n";
        temp+="            time:inXSDDateTime \""+ endDateTime + "\"^^xsd:dateTime ] ] ;\n";

        temp+= "    sosa:hasSimpleResult \"" + stepCount + "\"^^xsd:long .\n";

        writeTriplesForObservation("StepCountObservation", id, userName, sensor, observableProperty, temp);
    }
}