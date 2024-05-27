function createTheRepresentInfo(repositoryId, titleText) {
  var divForRepresentInfo = document.getElementById("divForRepresentInfo");
  divForRepresentInfo.innerHTML = "";

  //create the Left Container
  var tempOption = document.createElement("option");
  tempOption.setAttribute("value", "none");
  tempOption.textContent = "--- select one ---";
  var tempOption2 = document.createElement("option");
  tempOption2.setAttribute("value", "none");
  tempOption2.textContent = "--- select one ---";
  var allOption = document.createElement("option");
  allOption.setAttribute("value", "all");
  allOption.textContent = "all sensors";

  var leftContainer = document.createElement("div");
  leftContainer.classList.add("leftRepresentInfo");
  leftContainer.setAttribute("id", "leftRepresentInfo");

  var title = document.createElement("h3");
  title.textContent = titleText;
  title.classList.add("titleInRepresentInfo");

  var table = document.createElement("table");
  table.setAttribute("id", "tableForSelect");
  table.classList.add("tableAtLeftInCenter");

  var tbody = document.createElement("tbody");
  tbody.classList.add("tBodyForLeftTable");
  tbody.setAttribute("id", "tBodyForLeftContainer");
  table.appendChild(tbody);

  //create the container for Feature Of Interest
  var trForFeatureOfInterest = document.createElement("tr");
  trForFeatureOfInterest.classList.add("trForleftTable");
  var td1 = document.createElement("td");
  td1.classList.add("tdForLeftTable");
  var td2 = document.createElement("td");

  var labelFeature = document.createElement("label");
  labelFeature.setAttribute("for", "featureOfInterestSelector");
  labelFeature.textContent = "Feature Of Interest: ";
  td1.appendChild(labelFeature);

  var selectorFeature = document.createElement("select");
  selectorFeature.setAttribute("id", "featureOfInterestSelector");
  selectorFeature.setAttribute("name", "featureOfInterestSelector");
  selectorFeature.addEventListener("change", function () {
    var sensorSelector = document.getElementById("sensorSelector");
    var selectorFeatureValue = document.getElementById(
      "featureOfInterestSelector"
    ).value;
    document.getElementById("featureOfInterestSelector").style.borderColor = "";
    var tempOption2 = document.createElement("option");
    tempOption2.setAttribute("value", "none");
    tempOption2.textContent = "--- select one ---";
    var allOption = document.createElement("option");
    allOption.setAttribute("value", "all");
    allOption.textContent = "all sensors";

    sensorSelector.innerHTML = "";
    sensorSelector.appendChild(tempOption2);
    sensorSelector.appendChild(allOption);

    sensorSelector.value = "none";

    if(titleText==="Physical Activity"){
      var tempOption3 = document.createElement("option");
      tempOption3.setAttribute("value", "none");
      tempOption3.textContent = "--- select one ---";

      var allOption2 = document.createElement("option");
      allOption2.setAttribute("value", "all");
      allOption2.textContent = "all activities";

      var activitySelector = document.getElementById("activitySelector");
      activitySelector.innerHTML = "";
      activitySelector.appendChild(tempOption3);
      activitySelector.appendChild(allOption2);

      activitySelector.value = "none";
    }

    var forObservation = titleText.replace(/\s/g, "");

    fetch(
      api +
        "?task=getSensors&repoName=" +
        repositoryId +
        "&featureOfInterest=" +
        selectorFeatureValue +
        "&observation=" +
        forObservation +
        "Observation"
    )
      .then((response) => response.text())
      .then((data) => {
        var sensorSelector = document.getElementById("sensorSelector");
        var sensors = [];
        sensors = data.split("\n");

        if (sensors.length > 0 && sensors[0] !== "error") {
          for (var i = 0; i < sensors.length; i++) {
            var tempOption = document.createElement("option");
            tempOption.setAttribute("value", sensors[i]);
            tempOption.textContent = sensors[i];

            sensorSelector.appendChild(tempOption);
          }
        }
      })
      .catch((error) => {
        console.log(error);
      });
  });
  selectorFeature.appendChild(tempOption);
  td2.appendChild(selectorFeature);

  trForFeatureOfInterest.appendChild(td1);
  trForFeatureOfInterest.appendChild(td2);

  //create the container for Sensor
  var trForSensor = document.createElement("tr");
  trForSensor.classList.add("trForleftTable");
  var td3 = document.createElement("td");
  td3.classList.add("tdForLeftTable");
  var td4 = document.createElement("td");

  var labelSensor = document.createElement("label");
  labelSensor.setAttribute("for", "sensorSelector");
  labelSensor.textContent = "Sensor: ";
  td3.appendChild(labelSensor);

  var selectorSensor = document.createElement("select");
  selectorSensor.setAttribute("id", "sensorSelector");
  selectorSensor.setAttribute("name", "sensorSelector");
  selectorSensor.addEventListener("change", function () {
    document.getElementById("sensorSelector").style.borderColor = "";
    if(titleText==="Physical Activity"){
      var sensorSelectorValue = document.getElementById("sensorSelector").value;
      var selectorFeatureValue = document.getElementById("featureOfInterestSelector").value;
      var activitySelector = document.getElementById("activitySelector");

      var tempOption = document.createElement("option");
      tempOption.setAttribute("value", "none");
      tempOption.textContent = "--- select one ---";

      var allOption = document.createElement("option");
      allOption.setAttribute("value", "all");
      allOption.textContent = "all activities";

      activitySelector.innerHTML = "";
      activitySelector.appendChild(tempOption);
      activitySelector.appendChild(allOption);

      activitySelector.value = "none";

      var bodyToSend= selectorFeatureValue+"\n";
      bodyToSend+= sensorSelectorValue;

      fetch(
              api +
                "?task=getActivities&repoName=" +
                repositoryId,
              {
                method: "POST",
                body: bodyToSend,
              }
            )
              .then((response) => response.text())
              .then((data) => {

                var activitySelector = document.getElementById("activitySelector");

                var activities= [];
                activities= data.split("\n");

                if(activities.length > 0 && activities[0] != "error"){
                  for (var i = 0; i < activities.length; i++) {
                    var tempOption = document.createElement("option");
                    tempOption.setAttribute("value", activities[i]);
                    tempOption.textContent = activities[i];
        
                    activitySelector.appendChild(tempOption);
                  }
                }

              })
              .catch((error) => {
                console.log(error);
              });

    }

  });

  selectorSensor.appendChild(tempOption2);
  selectorSensor.appendChild(allOption);
  td4.appendChild(selectorSensor);

  trForSensor.appendChild(td3);
  trForSensor.appendChild(td4);

  //create the container for Start Date
  var today = new Date().toISOString().split("T")[0];

  var trForStartDate = document.createElement("tr");
  trForStartDate.classList.add("trForleftTable");
  var td5 = document.createElement("td");
  td5.classList.add("tdForLeftTable");
  var td6 = document.createElement("td");

  var labelStartDate = document.createElement("label");
  labelStartDate.setAttribute("for", "forStartDate");
  labelStartDate.textContent = "Start Date: ";
  td5.appendChild(labelStartDate);

  var forStartDate = document.createElement("input");
  forStartDate.setAttribute("type", "date");
  forStartDate.setAttribute("id", "forStartDate");
  forStartDate.setAttribute("name", "forStartDate");
  forStartDate.classList.add("dateSelect");
  forStartDate.addEventListener("change", function () {
    var startDateValue = document.getElementById("forStartDate").value;
    document.getElementById("forStartDate").style.borderColor = "";

    var endDate = document.getElementById("forEndDate");
    var endDateValue = endDate.value;

    if (endDateValue < startDateValue) {
      endDate.value = startDateValue;
    }

    endDate.setAttribute("min", startDateValue);
  });

  forStartDate.setAttribute("max", today);

  td6.appendChild(forStartDate);

  trForStartDate.appendChild(td5);
  trForStartDate.appendChild(td6);

  //create the container for End Date
  var trForEndDate = document.createElement("tr");
  trForEndDate.classList.add("trForleftTable");
  var td7 = document.createElement("td");
  td7.classList.add("tdForLeftTable");
  var td8 = document.createElement("td");

  var labelEndDate = document.createElement("label");
  labelEndDate.setAttribute("for", "forEndDate");
  labelEndDate.textContent = "End Date: ";
  td7.appendChild(labelEndDate);

  var forEndDate = document.createElement("input");
  forEndDate.setAttribute("type", "date");
  forEndDate.setAttribute("id", "forEndDate");
  forEndDate.setAttribute("name", "forEndDate");
  forEndDate.classList.add("dateSelect");
  forEndDate.addEventListener("change", function () {
    document.getElementById("forEndDate").style.borderColor = "";
  });

  forEndDate.setAttribute("max", today);
  forEndDate.setAttribute("value", today);

  td8.appendChild(forEndDate);

  trForEndDate.appendChild(td7);
  trForEndDate.appendChild(td8);

  tbody.appendChild(trForFeatureOfInterest);
  tbody.appendChild(trForSensor);
  tbody.appendChild(trForStartDate);
  tbody.appendChild(trForEndDate);

  leftContainer.appendChild(title);
  leftContainer.appendChild(table);

  //create the Right Container
  var rightContainer = document.createElement("div");
  rightContainer.classList.add("rightRepresentInfo");
  rightContainer.setAttribute("id", "rightContainer");

  var canvasRightContainer = document.createElement("canvas");
  canvasRightContainer.classList.add("canvasRightContainer");
  canvasRightContainer.setAttribute("id", "canvasRightContainer");

  rightContainer.appendChild(canvasRightContainer);
  divForRepresentInfo.appendChild(leftContainer);
  divForRepresentInfo.appendChild(rightContainer);

  new Chart("canvasRightContainer", {
    type: "line",
    data: {
      datasets: [
        {
          fill: false,
          lineTension: 0,
          backgroundColor: "rgba(0,0,255,1.0)",
          borderColor: "rgba(0,0,255,0.1)",
        },
      ],
    },
    options: {
      plugins: {
        legend: { display: false },
      },
      scales: {
        x: {
          title: {
            display: true,
            text: "Date",
          },
        },
        y: {
          min: 0, 
          max: 10 ,
        },
      },
      responsive: false,
    },
  });
  

  var forObservation = titleText.replace(/\s/g, "");

  //to set the values in the feature of interest select
  fetch(
    api +
      "?task=getFeatureOfInterest&repoName=" +
      repositoryId +
      "&observation=" +
      forObservation +
      "Observation"
  )
    .then((response) => response.text())
    .then((data) => {
      if (data !== "error") {
        var featureOfInterestSelector = document.getElementById(
          "featureOfInterestSelector"
        );
        var featureOfInterest = [];
        featureOfInterest = data.split("\n");

        for (var i = 0; i < featureOfInterest.length; i++) {
          var tempOption = document.createElement("option");
          tempOption.setAttribute("value", featureOfInterest[i]);
          tempOption.textContent = featureOfInterest[i];

          featureOfInterestSelector.appendChild(tempOption);
        }
      } else {
        // console.log("error at featureOfInterestSelector");
      }
    })
    .catch((error) => {
      console.log(error);
    });

  //to set the min value in the startDate and endDate
  fetch(
    api +
      "?task=getMaxOrMinDate&repoName=" +
      repositoryId +
      "&kind=min&observation=" +
      forObservation +
      "Observation"
  )
    .then((response) => response.text())
    .then((data) => {
      if (data !== "error") {
        var startDateInput = document.getElementById("forStartDate");
        startDateInput.setAttribute("min", data.substring(1, 11)); //in order to take only the date from the data
        startDateInput.setAttribute("value", data.substring(1, 11)); //in order to take only the date from the data

        var endDateInput = document.getElementById("forEndDate");
        endDateInput.setAttribute("min", data.substring(1, 11)); //in order to take only the date from the data
      } else {
        // console.log("error at dates");
      }
    })
    .catch((error) => {
      console.log(error);
    });
}

function showBodyHeightDetails(repositoryId) {
  createTheRepresentInfo(repositoryId, "Body Height");

  //add the submit button
  var submitButtonForQuery = document.createElement("button");
  submitButtonForQuery.classList.add("submitButtonForQuery");
  submitButtonForQuery.setAttribute("id", "submitButtonForQuery");
  submitButtonForQuery.textContent = "Submit";
  submitButtonForQuery.addEventListener("click", function () {
    var featureOfInterestSelector = document.getElementById(
      "featureOfInterestSelector"
    );
    var sensorSelector = document.getElementById("sensorSelector");
    var forStartDate = document.getElementById("forStartDate");
    var forEndDate = document.getElementById("forEndDate");

    var toBeFilled = true;

    if (featureOfInterestSelector.value === "none") {
      featureOfInterestSelector.style.borderColor = "red";
      toBeFilled = false;
    }
    if (sensorSelector.value === "none") {
      sensorSelector.style.borderColor = "red";
      toBeFilled = false;
    }
    if (forStartDate.value === "") {
      forStartDate.style.borderColor = "red";
      toBeFilled = false;
    }
    if (forEndDate.value === "") {
      forEndDate.style.borderColor = "red";
      toBeFilled = false;
    }

    if (!toBeFilled) {
      window.alert("Fill all the inputs!");
    } else {
      var bodyToSend =
        featureOfInterestSelector.value +
        "\n" +
        sensorSelector.value +
        "\n" +
        forStartDate.value +
        "\n" +
        forEndDate.value;
      fetch(
        api +
          "?task=query&repoName=" +
          repositoryId +
          "&observation=BodyHeightObservation",
        {
          method: "POST",
          body: bodyToSend,
        }
      )
        .then((response) => response.text())
        .then((data) => {
          var xValues = [];
          var yValues = [];
          var tempForOneLine;
          var lineFromAnswer = [];

          lineFromAnswer = data.split("\n");

          for (var i = 0; i < lineFromAnswer.length; i++) {
            tempForOneLine = lineFromAnswer[i].split(",");
            if (tempForOneLine[2] === "m") {
              xValues.push(tempForOneLine[0].substring(0, 10));
              yValues.push(tempForOneLine[1]);
            }
          }

          //delete the previous chart
          var canvasRightContainer = document.getElementById(
            "canvasRightContainer"
          );
          canvasRightContainer.remove();

          //create the new chart
          var rightContainer = document.getElementById("rightContainer");
          var canvasRightContainer = document.createElement("canvas");
          canvasRightContainer.classList.add("canvasRightContainer");
          canvasRightContainer.setAttribute("id", "canvasRightContainer");

          rightContainer.appendChild(canvasRightContainer);

          new Chart("canvasRightContainer", {
            type: "line",
            data: {
              labels: xValues,
              datasets: [
                {
                  label: "meters",
                  fill: false,
                  lineTension: 0,
                  backgroundColor: "rgba(0,0,255,1.0)",
                  borderColor: "rgba(0,0,255,0.1)",
                  data: yValues,
                },
              ],
            },
            options: {
              plugins: {
                legend: { display: false },
              },
              scales: {
                x: {
                  title: {
                    display: true,
                    text: "Date",
                  },
                },
                y: {
                  title: {
                    display: true,
                    text: "Meters",
                  },
                  min: 0,
                  max: 2.5,
                },
              },
              responsive: false,
            },
          });
          
        })
        .catch((error) => {
          console.log(error);
        });
    }
  });

  var leftRepresentInfo = document.getElementById("leftRepresentInfo");

  leftRepresentInfo.appendChild(submitButtonForQuery);
}

function showBodyWeightDetails(repositoryId) {
  createTheRepresentInfo(repositoryId, "Body Weight");

  //add the submit button
  var submitButtonForQuery = document.createElement("button");
  submitButtonForQuery.classList.add("submitButtonForQuery");
  submitButtonForQuery.setAttribute("id", "submitButtonForQuery");
  submitButtonForQuery.textContent = "Submit";
  submitButtonForQuery.addEventListener("click", function () {
    var featureOfInterestSelector = document.getElementById(
      "featureOfInterestSelector"
    );
    var sensorSelector = document.getElementById("sensorSelector");
    var forStartDate = document.getElementById("forStartDate");
    var forEndDate = document.getElementById("forEndDate");

    var toBeFilled = true;

    if (featureOfInterestSelector.value === "none") {
      featureOfInterestSelector.style.borderColor = "red";
      toBeFilled = false;
    }
    if (sensorSelector.value === "none") {
      sensorSelector.style.borderColor = "red";
      toBeFilled = false;
    }
    if (forStartDate.value === "") {
      forStartDate.style.borderColor = "red";
      toBeFilled = false;
    }
    if (forEndDate.value === "") {
      forEndDate.style.borderColor = "red";
      toBeFilled = false;
    }

    if (!toBeFilled) {
      window.alert("Fill all the inputs!");
    } else {
      var bodyToSend =
        featureOfInterestSelector.value +
        "\n" +
        sensorSelector.value +
        "\n" +
        forStartDate.value +
        "\n" +
        forEndDate.value;
      fetch(
        api +
          "?task=query&repoName=" +
          repositoryId +
          "&observation=BodyWeightObservation",
        {
          method: "POST",
          body: bodyToSend,
        }
      )
        .then((response) => response.text())
        .then((data) => {
          var xValues = [];
          var yValues = [];
          var tempForOneLine;
          var lineFromAnswer = [];

          lineFromAnswer = data.split("\n");

          for (var i = 0; i < lineFromAnswer.length; i++) {
            tempForOneLine = lineFromAnswer[i].split(",");
            if (tempForOneLine[2] === "kg") {
              xValues.push(tempForOneLine[0].substring(0, 10));
              yValues.push(tempForOneLine[1]);
            }
          }

          var maxForChart= 0;
          for(var i=0;i<lineFromAnswer.length; i++){
            if(maxForChart< Math.floor(yValues[i])){
              maxForChart= Math.floor(yValues[i]);
            }
          }

          maxForChart+= 10- maxForChart%10;

          //delete the previous chart
          var canvasRightContainer = document.getElementById(
            "canvasRightContainer"
          );
          canvasRightContainer.remove();

          //create the new chart
          var rightContainer = document.getElementById("rightContainer");
          var canvasRightContainer = document.createElement("canvas");
          canvasRightContainer.classList.add("canvasRightContainer");
          canvasRightContainer.setAttribute("id", "canvasRightContainer");

          rightContainer.appendChild(canvasRightContainer);

          new Chart("canvasRightContainer", {
            type: "line",
            data: {
              labels: xValues,
              datasets: [
                {
                  label: "kg",
                  fill: false,
                  lineTension: 0,
                  backgroundColor: "rgba(0,0,255,1.0)",
                  borderColor: "rgba(0,0,255,0.1)",
                  data: yValues,
                },
              ],
            },
            options: {
              plugins: {
                legend: { display: false },
              },
              scales: {
                x: {
                  title: {
                    display: true,
                    text: "Date",
                  },
                },
                y: {
                  title: {
                    display: true,
                    text: "Weight (kg)",
                  },
                  min: 0, 
                  max: maxForChart,
                },
              },
              responsive: false,
            },
          });
          
        })
        .catch((error) => {
          console.log(error);
        });
    }
  });

  var leftRepresentInfo = document.getElementById("leftRepresentInfo");

  leftRepresentInfo.appendChild(submitButtonForQuery);
}

function showCaloriesBurnedDetails(repositoryId) {
  createTheRepresentInfo(repositoryId, "Calories Burned");

  //add the submit button
  var submitButtonForQuery = document.createElement("button");
  submitButtonForQuery.classList.add("submitButtonForQuery");
  submitButtonForQuery.setAttribute("id", "submitButtonForQuery");
  submitButtonForQuery.textContent = "Submit";
  submitButtonForQuery.addEventListener("click", function () {
    var featureOfInterestSelector = document.getElementById(
      "featureOfInterestSelector"
    );
    var sensorSelector = document.getElementById("sensorSelector");
    var forStartDate = document.getElementById("forStartDate");
    var forEndDate = document.getElementById("forEndDate");

    var toBeFilled = true;

    if (featureOfInterestSelector.value === "none") {
      featureOfInterestSelector.style.borderColor = "red";
      toBeFilled = false;
    }
    if (sensorSelector.value === "none") {
      sensorSelector.style.borderColor = "red";
      toBeFilled = false;
    }
    if (forStartDate.value === "") {
      forStartDate.style.borderColor = "red";
      toBeFilled = false;
    }
    if (forEndDate.value === "") {
      forEndDate.style.borderColor = "red";
      toBeFilled = false;
    }

    if (!toBeFilled) {
      window.alert("Fill all the inputs!");
    } else {
      var bodyToSend =
        featureOfInterestSelector.value +
        "\n" +
        sensorSelector.value +
        "\n" +
        forStartDate.value +
        "\n" +
        forEndDate.value;
      fetch(
        api +
          "?task=query&repoName=" +
          repositoryId +
          "&observation=CaloriesBurnedObservation",
        {
          method: "POST",
          body: bodyToSend,
        }
      )
        .then((response) => response.text())
        .then((data) => {
          var xValues = [];
          var yValues = [];
          var tempForOneLine;
          var lineFromAnswer = [];

          lineFromAnswer = data.split("\n");

          for (var i = 0; i < lineFromAnswer.length; i++) {
            tempForOneLine = lineFromAnswer[i].split(",");
            if (tempForOneLine[2] === "kcal") {
              xValues.push(tempForOneLine[0].substring(0, 10));
              yValues.push(tempForOneLine[1]);
            }
          }

          //delete the previous chart
          var canvasRightContainer = document.getElementById(
            "canvasRightContainer"
          );
          canvasRightContainer.remove();

          //create the new chart
          var rightContainer = document.getElementById("rightContainer");
          var canvasRightContainer = document.createElement("canvas");
          canvasRightContainer.classList.add("canvasRightContainer");
          canvasRightContainer.setAttribute("id", "canvasRightContainer");

          rightContainer.appendChild(canvasRightContainer);

          new Chart("canvasRightContainer", {
            type: "line",
            data: {
              labels: xValues,
              datasets: [
                {
                  label: "calories burned",
                  fill: false,
                  lineTension: 0,
                  backgroundColor: "rgba(0,0,255,1.0)",
                  borderColor: "rgba(0,0,255,0.1)",
                  data: yValues,
                },
              ],
            },
            options: {
              plugins: {
                legend: { display: false },
              },
              scales: {
                x: {
                  title: {
                    display: true,
                    text: "Date",
                  },
                },
                y: {
                  title: {
                    display: true,
                    text: "Calories Burned",
                  },
                  ticks: { min: 0, max: 5000 },
                },
              },
              responsive: false,
            },
          });
          
        })
        .catch((error) => {
          console.log(error);
        });
    }
  });

  var leftRepresentInfo = document.getElementById("leftRepresentInfo");

  leftRepresentInfo.appendChild(submitButtonForQuery);
}

function showGeopositionDetails(repositoryId) {
  createTheRepresentInfo(repositoryId, "Geoposition");

  //add the submit button
  var submitButtonForQuery = document.createElement("button");
  submitButtonForQuery.classList.add("submitButtonForQuery");
  submitButtonForQuery.setAttribute("id", "submitButtonForQuery");
  submitButtonForQuery.textContent = "Submit";
  submitButtonForQuery.addEventListener("click", function () {
    var featureOfInterestSelector = document.getElementById(
      "featureOfInterestSelector"
    );
    var sensorSelector = document.getElementById("sensorSelector");
    var forStartDate = document.getElementById("forStartDate");
    var forEndDate = document.getElementById("forEndDate");

    var toBeFilled = true;

    if (featureOfInterestSelector.value === "none") {
      featureOfInterestSelector.style.borderColor = "red";
      toBeFilled = false;
    }
    if (sensorSelector.value === "none") {
      sensorSelector.style.borderColor = "red";
      toBeFilled = false;
    }
    if (forStartDate.value === "") {
      forStartDate.style.borderColor = "red";
      toBeFilled = false;
    }
    if (forEndDate.value === "") {
      forEndDate.style.borderColor = "red";
      toBeFilled = false;
    }

    if (!toBeFilled) {
      window.alert("Fill all the inputs!");
    } else {
      var bodyToSend =
        featureOfInterestSelector.value +
        "\n" +
        sensorSelector.value +
        "\n" +
        forStartDate.value +
        "\n" +
        forEndDate.value;
      fetch(
        api +
          "?task=query&repoName=" +
          repositoryId +
          "&observation=GeopositionObservation",
        {
          method: "POST",
          body: bodyToSend,
        }
      )
        .then((response) => response.text())
        .then((data) => {

          var xValues = [];
          var yLatValues = [];
          var yLongValues= [];
          var tempForOneLine;
          var lineFromAnswer = [];

          lineFromAnswer = data.split("\n");

          for (var i = 0; i < lineFromAnswer.length; i++) {
            tempForOneLine = lineFromAnswer[i].split(",");
            if (tempForOneLine[2] === "deg" && tempForOneLine[4]==="deg") {
              xValues.push(tempForOneLine[0]);
              yLatValues.push(parseFloat(tempForOneLine[1]));
              yLongValues.push(parseFloat(tempForOneLine[3]));
            }
          }

           //delete the previous chart
           var canvasRightContainer = document.getElementById(
            "canvasRightContainer"
          );
          canvasRightContainer.remove();

          //create the new chart
          var rightContainer = document.getElementById("rightContainer");
          var canvasRightContainer = document.createElement("canvas");
          canvasRightContainer.classList.add("canvasRightContainer");
          canvasRightContainer.setAttribute("id", "canvasRightContainer");

          rightContainer.appendChild(canvasRightContainer);

          var data = {
            labels: xValues,
            datasets: [
                {
                    label: 'Latitude',
                    data: yLatValues,
                    yAxisID: 'y-axis-1',
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                    borderColor: 'rgba(75, 192, 192, 1)',
                    borderWidth: 1,
                    pointBackgroundColor: 'rgba(75, 192, 192, 1)'
                },
                {
                    label: 'Longitude',
                    data: yLongValues,
                    yAxisID: 'y-axis-2',
                    backgroundColor: 'rgba(255, 99, 132, 0.2)',
                    borderColor: 'rgba(255, 99, 132, 1)',
                    borderWidth: 1,
                    pointBackgroundColor: 'rgba(255, 99, 132, 1)'
                }
            ]
        };

        var config = {
            type: 'line',
            data: data,
            options: {
                scales: {
                    'y-axis-1': {
                        type: 'linear',
                        position: 'left',
                        title: {
                            display: true,
                            text: 'Latitude',
                            color: 'rgba(75, 192, 192, 1)'
                        },
                        ticks: {
                            color: 'rgba(75, 192, 192, 1)',
                            min: 0,
                            max: 90
                        },
                        grid: {
                            color: 'rgba(75, 192, 192, 0.2)'
                        }
                    },
                    'y-axis-2': {
                        type: 'linear',
                        position: 'right',
                        title: {
                            display: true,
                            text: 'Longitude',
                            color: 'rgba(255, 99, 132, 1)'
                        },
                        ticks: {
                            color: 'rgba(255, 99, 132, 1)',
                            min: 0,
                            max: 180
                        },
                        grid: {
                            drawOnChartArea: false,
                            color: 'rgba(255, 99, 132, 0.2)'
                        }
                    },
                    x: {
                        title: {
                            display: true,
                            text: 'DateTime'
                        }
                    }
                },
                interaction: {
                    mode: 'index',
                    intersect: false
                },
                plugins: {
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                let label = context.dataset.label || '';
                                if (label) {
                                    label += ': ';
                                }
                                if (context.parsed.y !== null) {
                                    label += context.parsed.y;
                                }
                                return label;
                            }
                        }
                    }
                }
            }
        };

        new Chart("canvasRightContainer",config);

        })
        .catch((error) => {
          console.log(error);
        });
    }
  });

  var leftRepresentInfo = document.getElementById("leftRepresentInfo");

  leftRepresentInfo.appendChild(submitButtonForQuery);
}

function showHeartRateDetails(repositoryId) {
  createTheRepresentInfo(repositoryId, "Heart Rate");

  //add the submit button
  var submitButtonForQuery = document.createElement("button");
  submitButtonForQuery.classList.add("submitButtonForQuery");
  submitButtonForQuery.setAttribute("id", "submitButtonForQuery");
  submitButtonForQuery.textContent = "Submit";
  submitButtonForQuery.addEventListener("click", function () {
    var featureOfInterestSelector = document.getElementById(
      "featureOfInterestSelector"
    );
    var sensorSelector = document.getElementById("sensorSelector");
    var forStartDate = document.getElementById("forStartDate");
    var forEndDate = document.getElementById("forEndDate");

    var toBeFilled = true;

    if (featureOfInterestSelector.value === "none") {
      featureOfInterestSelector.style.borderColor = "red";
      toBeFilled = false;
    }
    if (sensorSelector.value === "none") {
      sensorSelector.style.borderColor = "red";
      toBeFilled = false;
    }
    if (forStartDate.value === "") {
      forStartDate.style.borderColor = "red";
      toBeFilled = false;
    }
    if (forEndDate.value === "") {
      forEndDate.style.borderColor = "red";
      toBeFilled = false;
    }

    if (!toBeFilled) {
      window.alert("Fill all the inputs!");
    } else {
      var bodyToSend =
        featureOfInterestSelector.value +
        "\n" +
        sensorSelector.value +
        "\n" +
        forStartDate.value +
        "\n" +
        forEndDate.value;
      fetch(
        api +
          "?task=query&repoName=" +
          repositoryId +
          "&observation=HeartRateObservation",
        {
          method: "POST",
          body: bodyToSend,
        }
      )
        .then((response) => response.text())
        .then((data) => {

          var xValues = [];
          var yMaxValues = [];
          var yMinValues = [];
          var yAvgValues = [];
          var tempForOneLine;
          var lineFromAnswer = [];

          lineFromAnswer = data.split("\n");

          for (var i = 0; i < lineFromAnswer.length; i++) {
            tempForOneLine = lineFromAnswer[i].split(",");
            if(tempForOneLine[4]==="beats/min"){
              xValues.push(tempForOneLine[0].substring(0, 10));
              yMaxValues.push(tempForOneLine[1]);
              yMinValues.push(tempForOneLine[2]);
              yAvgValues.push(tempForOneLine[3]);
            }
          }

          var maxForChart= 0;
          for(var i=0;i<lineFromAnswer.length;i++){
            if(yMaxValues[i]>maxForChart){
              maxForChart= Math.floor(yMaxValues[i]);
            }
          }
          
          maxForChart+= 20- maxForChart%20;

          
          //delete the previous chart
          var canvasRightContainer = document.getElementById(
            "canvasRightContainer"
          );
          canvasRightContainer.remove();

          //create the new chart
          var rightContainer = document.getElementById("rightContainer");
          var canvasRightContainer = document.createElement("canvas");
          canvasRightContainer.classList.add("canvasRightContainer");
          canvasRightContainer.setAttribute("id", "canvasRightContainer");

          rightContainer.appendChild(canvasRightContainer);

          new Chart("canvasRightContainer", {
            type: "line",
            data: {
              labels: xValues,
              datasets: [
                {
                  label: "Min",
                  backgroundColor: "rgba(75, 192, 192, 0.2)",
                  borderColor: "rgba(75, 192, 192, 1)",
                  borderWidth: 1,
                  data: yMinValues,
                },
                {
                  label: "Max",
                  backgroundColor: "rgba(255, 99, 132, 0.2)",
                  borderColor: "rgba(255, 99, 132, 1)",
                  borderWidth: 1,
                  data: yMaxValues,
                },
                {
                  label: "Avg",
                  backgroundColor: "rgba(54, 162, 235, 0.2)",
                  borderColor: "rgba(54, 162, 235, 1)",
                  borderWidth: 1,
                  data: yAvgValues,
                },
              ],
            },
            options: {
              plugins: {
                legend: { display: true },
              },
              scales: {
                x: {
                  title: {
                    display: true,
                    text: "Date",
                  },
                },
                y: {
                  title: {
                    display: true,
                    labelString: "Heart Rate (beats/min)",
                  },
                  ticks: { min: 0, max: maxForChart },
                },
              },
              responsive: false,
            },
          });
          


        })
        .catch((error) => {
          console.log(error);
        });
    }
  });

  var leftRepresentInfo = document.getElementById("leftRepresentInfo");

  leftRepresentInfo.appendChild(submitButtonForQuery);

}

function showPhysicalActivityDetails(repositoryId) {
  createTheRepresentInfo(repositoryId, "Physical Activity");

  var table= document.getElementById("tableForSelect");
  table.classList.remove("tableAtLeftInCenter");
  table.classList.add("tableAtLeftInCenter2");

  var tbody= document.getElementById("tBodyForLeftContainer");

  //create the container for Activities
  var trForActivity = document.createElement("tr");
  trForActivity.classList.add("trForleftTable");
  var td1 = document.createElement("td");
  td1.classList.add("tdForLeftTable");
  var td2 = document.createElement("td");

  var labelActivity = document.createElement("label");
  labelActivity.setAttribute("for", "activitySelector");
  labelActivity.textContent = "Activity: ";
  td1.appendChild(labelActivity);

  var selectorActivity = document.createElement("select");
  selectorActivity.setAttribute("id", "activitySelector");
  selectorActivity.setAttribute("name", "activitySelector");
  selectorActivity.addEventListener("change", function () {
    document.getElementById("activitySelector").style.borderColor = "";
  });

  var tempOption = document.createElement("option");
  tempOption.setAttribute("value", "none");
  tempOption.textContent = "--- select one ---";

  var allOption = document.createElement("option");
  allOption.setAttribute("value", "all");
  allOption.textContent = "all activities";

  selectorActivity.appendChild(tempOption);
  selectorActivity.appendChild(allOption);
  td2.appendChild(selectorActivity);

  trForActivity.appendChild(td1);
  trForActivity.appendChild(td2);

  tbody.appendChild(trForActivity);

  //add the submit button
  var submitButtonForQuery = document.createElement("button");
  submitButtonForQuery.classList.add("submitButtonForQuery");
  submitButtonForQuery.setAttribute("id", "submitButtonForQuery");
  submitButtonForQuery.textContent = "Submit";
  submitButtonForQuery.addEventListener("click", function () {
    var featureOfInterestSelector = document.getElementById(
      "featureOfInterestSelector"
    );
    var sensorSelector = document.getElementById("sensorSelector");
    var forStartDate = document.getElementById("forStartDate");
    var forEndDate = document.getElementById("forEndDate");
    var activity= document.getElementById("activitySelector");

    var toBeFilled = true;

    if (featureOfInterestSelector.value === "none") {
      featureOfInterestSelector.style.borderColor = "red";
      toBeFilled = false;
    }
    if (sensorSelector.value === "none") {
      sensorSelector.style.borderColor = "red";
      toBeFilled = false;
    }
    if (forStartDate.value === "") {
      forStartDate.style.borderColor = "red";
      toBeFilled = false;
    }
    if (forEndDate.value === "") {
      forEndDate.style.borderColor = "red";
      toBeFilled = false;
    }

    if (activity.value === "none") {
      activity.style.borderColor = "red";
      toBeFilled = false;
    }

    if (!toBeFilled) {
      window.alert("Fill all the inputs!");
    } else {
      var bodyToSend =
        featureOfInterestSelector.value +
        "\n" +
        sensorSelector.value +
        "\n" +
        forStartDate.value +
        "\n" +
        forEndDate.value +
        "\n" +
        activity.value;
      fetch(
        api +
          "?task=query&repoName=" +
          repositoryId +
          "&observation=PhysicalActivityObservation",
        {
          method: "POST",
          body: bodyToSend,
        }
      )
        .then((response) => response.text())
        .then((data) => {

          var xValues = [];
          var yValues = [];
          var tempForOneLine;
          var lineFromAnswer = [];

          lineFromAnswer = data.split("\n");

          for (var i = 0; i < lineFromAnswer.length; i++) {
            tempForOneLine = lineFromAnswer[i].split(",");

            xValues.push(tempForOneLine[0].substring(0, 10));
            yValues.push(Math.floor(tempForOneLine[1]));
          }

          var maxForChart=0;
          for(var i=0;i<lineFromAnswer.length;i++){
            if(maxForChart<yValues[i]){
              maxForChart= yValues[i];
            }
          }

          maxForChart+= 50- maxForChart%50;

          //delete the previous chart
          var canvasRightContainer = document.getElementById(
            "canvasRightContainer"
          );
          canvasRightContainer.remove();

          //create the new chart
          var rightContainer = document.getElementById("rightContainer");
          var canvasRightContainer = document.createElement("canvas");
          canvasRightContainer.classList.add("canvasRightContainer");
          canvasRightContainer.setAttribute("id", "canvasRightContainer");

          rightContainer.appendChild(canvasRightContainer);

          new Chart("canvasRightContainer", {
            type: "line",
            data: {
              labels: xValues,
              datasets: [
                {
                  label: "minutes",
                  fill: false,
                  lineTension: 0,
                  backgroundColor: "rgba(0,0,255,1.0)",
                  borderColor: "rgba(0,0,255,0.1)",
                  data: yValues,
                },
              ],
            },
            options: {
              plugins: {
                legend: { display: false },
              },
              scales: {
                x: {
                  title: {
                    display: true,
                    text: "Date",
                  },
                },
                y: {
                  title: {
                    display: true,
                    text: "Minutes of Activity",
                  },
                  ticks: { min: 0, max: maxForChart },
                },
              },
              responsive: false,
            },
          });
          
        })
        .catch((error) => {
          console.log(error);
        });
    }
  });

  var leftRepresentInfo = document.getElementById("leftRepresentInfo");

  leftRepresentInfo.appendChild(submitButtonForQuery);

}

function showSpeedDetails(repositoryId) {
  createTheRepresentInfo(repositoryId, "Speed");

  //add the submit button
  var submitButtonForQuery = document.createElement("button");
  submitButtonForQuery.classList.add("submitButtonForQuery");
  submitButtonForQuery.setAttribute("id", "submitButtonForQuery");
  submitButtonForQuery.textContent = "Submit";
  submitButtonForQuery.addEventListener("click", function () {
    var featureOfInterestSelector = document.getElementById(
      "featureOfInterestSelector"
    );
    var sensorSelector = document.getElementById("sensorSelector");
    var forStartDate = document.getElementById("forStartDate");
    var forEndDate = document.getElementById("forEndDate");

    var toBeFilled = true;

    if (featureOfInterestSelector.value === "none") {
      featureOfInterestSelector.style.borderColor = "red";
      toBeFilled = false;
    }
    if (sensorSelector.value === "none") {
      sensorSelector.style.borderColor = "red";
      toBeFilled = false;
    }
    if (forStartDate.value === "") {
      forStartDate.style.borderColor = "red";
      toBeFilled = false;
    }
    if (forEndDate.value === "") {
      forEndDate.style.borderColor = "red";
      toBeFilled = false;
    }

    if (!toBeFilled) {
      window.alert("Fill all the inputs!");
    } else {
      var bodyToSend =
        featureOfInterestSelector.value +
        "\n" +
        sensorSelector.value +
        "\n" +
        forStartDate.value +
        "\n" +
        forEndDate.value;
      fetch(
        api +
          "?task=query&repoName=" +
          repositoryId +
          "&observation=SpeedObservation",
        {
          method: "POST",
          body: bodyToSend,
        }
      )
        .then((response) => response.text())
        .then((data) => {
          var xValues = [];
          var yMaxValues = [];
          var yMinValues = [];
          var yAvgValues = [];
          var tempForOneLine;
          var lineFromAnswer = [];

          lineFromAnswer = data.split("\n");

          for (var i = 0; i < lineFromAnswer.length; i++) {
            tempForOneLine = lineFromAnswer[i].split(",");
            if(tempForOneLine[4]==="m/s"){
              xValues.push(tempForOneLine[0].substring(0, 10));
              yMaxValues.push(tempForOneLine[1]);
              yMinValues.push(tempForOneLine[2]);
              yAvgValues.push(tempForOneLine[3]);
            }
          }

          var maxForChart= 0;
          for(var i=0;i<lineFromAnswer.length;i++){
            if(yMaxValues[i]>maxForChart){
              maxForChart= yMaxValues[i];
            }
          }
          
          maxForChart= Math.floor(maxForChart)+1;

          //delete the previous chart
          var canvasRightContainer = document.getElementById(
            "canvasRightContainer"
          );
          canvasRightContainer.remove();

          //create the new chart
          var rightContainer = document.getElementById("rightContainer");
          var canvasRightContainer = document.createElement("canvas");
          canvasRightContainer.classList.add("canvasRightContainer");
          canvasRightContainer.setAttribute("id", "canvasRightContainer");

          rightContainer.appendChild(canvasRightContainer);

          new Chart("canvasRightContainer", {
            type: "line",
            data: {
              labels: xValues,
              datasets: [
                {
                  label: "Min",
                  backgroundColor: "rgba(75, 192, 192, 0.2)",
                  borderColor: "rgba(75, 192, 192, 1)",
                  borderWidth: 1,
                  data: yMinValues,
                },
                {
                  label: "Max",
                  backgroundColor: "rgba(255, 99, 132, 0.2)",
                  borderColor: "rgba(255, 99, 132, 1)",
                  borderWidth: 1,
                  data: yMaxValues,
                },
                {
                  label: "Avg",
                  backgroundColor: "rgba(54, 162, 235, 0.2)",
                  borderColor: "rgba(54, 162, 235, 1)",
                  borderWidth: 1,
                  data: yAvgValues,
                },
              ],
            },
            options: {
              plugins: {
                legend: { display: true },
              },
              scales: {
                x: {
                  title: {
                    display: true,
                    text: "Date",
                  },
                },
                y: {
                  title: {
                    display: true,
                    text: "Speed (m/s)",
                  },
                  ticks: { min: 0, max: maxForChart },
                },
              },
              responsive: false,
            },
          });
          


        })
        .catch((error) => {
          console.log(error);
        });
    }
  });

  var leftRepresentInfo = document.getElementById("leftRepresentInfo");

  leftRepresentInfo.appendChild(submitButtonForQuery);
}

function showStepCountDetails(repositoryId) {
  createTheRepresentInfo(repositoryId, "Step Count");

  //add the submit button
  var submitButtonForQuery = document.createElement("button");
  submitButtonForQuery.classList.add("submitButtonForQuery");
  submitButtonForQuery.setAttribute("id", "submitButtonForQuery");
  submitButtonForQuery.textContent = "Submit";
  submitButtonForQuery.addEventListener("click", function () {
    var featureOfInterestSelector = document.getElementById(
      "featureOfInterestSelector"
    );
    var sensorSelector = document.getElementById("sensorSelector");
    var forStartDate = document.getElementById("forStartDate");
    var forEndDate = document.getElementById("forEndDate");

    var toBeFilled = true;

    if (featureOfInterestSelector.value === "none") {
      featureOfInterestSelector.style.borderColor = "red";
      toBeFilled = false;
    }
    if (sensorSelector.value === "none") {
      sensorSelector.style.borderColor = "red";
      toBeFilled = false;
    }
    if (forStartDate.value === "") {
      forStartDate.style.borderColor = "red";
      toBeFilled = false;
    }
    if (forEndDate.value === "") {
      forEndDate.style.borderColor = "red";
      toBeFilled = false;
    }

    if (!toBeFilled) {
      window.alert("Fill all the inputs!");
    } else {
      var bodyToSend =
        featureOfInterestSelector.value +
        "\n" +
        sensorSelector.value +
        "\n" +
        forStartDate.value +
        "\n" +
        forEndDate.value;
      fetch(
        api +
          "?task=query&repoName=" +
          repositoryId +
          "&observation=StepCountObservation",
        {
          method: "POST",
          body: bodyToSend,
        }
      )
        .then((response) => response.text())
        .then((data) => {
          var xValues = [];
          var yValues = [];
          var tempForOneLine;
          var lineFromAnswer = [];

          lineFromAnswer = data.split("\n");

          for (var i = 0; i < lineFromAnswer.length; i++) {
            tempForOneLine = lineFromAnswer[i].split(",");
            xValues.push(tempForOneLine[0].substring(0, 10));
            yValues.push(Math.floor(tempForOneLine[1]));
          }

          var maxForChart=0;
          for(var i=0;i<lineFromAnswer.length;i++){
            if(maxForChart<yValues[i]){
              maxForChart= yValues[i];
            }
          }

          maxForChart+= 5000 - maxForChart%5000;

          //delete the previous chart
          var canvasRightContainer = document.getElementById(
            "canvasRightContainer"
          );
          canvasRightContainer.remove();

          //create the new chart
          var rightContainer = document.getElementById("rightContainer");
          var canvasRightContainer = document.createElement("canvas");
          canvasRightContainer.classList.add("canvasRightContainer");
          canvasRightContainer.setAttribute("id", "canvasRightContainer");

          rightContainer.appendChild(canvasRightContainer);

          new Chart("canvasRightContainer", {
            type: "line",
            data: {
              labels: xValues,
              datasets: [
                {
                  label: "steps",
                  fill: false,
                  lineTension: 0,
                  backgroundColor: "rgba(0,0,255,1.0)",
                  borderColor: "rgba(0,0,255,0.1)",
                  data: yValues,
                },
              ],
            },
            options: {
              plugins: {
                legend: { display: false },
              },
              scales: {
                x: {
                  title: {
                    display: true,
                    text: "Date",
                  },
                },
                y: {
                  title: {
                    display: true,
                    text: "Steps",
                  },
                  ticks: { min: 0, max: maxForChart },
                },
              },
              responsive: false,
            },
          });
          
        })
        .catch((error) => {
          console.log(error);
        });
    }
  });

  var leftRepresentInfo = document.getElementById("leftRepresentInfo");

  leftRepresentInfo.appendChild(submitButtonForQuery);
}
