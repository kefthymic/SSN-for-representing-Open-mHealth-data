const api = "http://127.0.0.1:5000";
let checkConnection;
let repositoryIntervalId;
let additionIntervalId;

function checkConnectionWithGraphDbServer() {
  fetch(api + "?task=connectionStatus")
    .then((response) => response.text())
    .then((data) => {
      var h5Element = document.createElement("h5");
      h5Element.textContent = "Connection status with the graphDb server: ";

      var spanElement = document.createElement("span");

      if (data === "true") {
        spanElement.textContent = "Connected";
        spanElement.className = "green";
        varForReturn = true;
      } else {
        spanElement.textContent = "Disconnected";
        spanElement.className = "red";
      }

      h5Element.appendChild(spanElement);

      var divContainer = document.createElement("div");
      divContainer.appendChild(h5Element);

      var outputDiv = document.getElementById("h5ForServerCondition");
      outputDiv.innerHTML = "";
      outputDiv.appendChild(divContainer);
    })
    .catch((error) => {
      console.log(error);
    });
}

function addAdditionButton() {
  fetch(api + "?task=connectionStatus")
    .then((response) => response.text())
    .then((data) => {
      var containerForAdditionButton = document.getElementById(
        "containerForAdditionButton"
      );
      containerForAdditionButton.innerHTML = "";

      if (data === "true") {
        var button = document.createElement("button");
        button.classList.add("additionButton");
        button.textContent = "Create New Repository";
        button.setAttribute("id", "additionButton");
        button.addEventListener("click", function () {
          stopCheckConnectionWithGraphDbServer();
          stopIntervalForAdditionButton();
          stopIntervalForRepositories();

          var forCenter = document.getElementById("containerForAdditionButton");
          forCenter.innerHTML = "";
          var title = document.createElement("h3");
          title.textContent = "Add New Repository";

          var divForRepoName = document.createElement("div");

          var labelForInput = document.createElement("label");
          labelForInput.setAttribute("for", "repoNameInput");
          labelForInput.textContent = "Repository Name: ";

          var inputForRepoName = document.createElement("input");
          inputForRepoName.setAttribute("type", "text");
          inputForRepoName.setAttribute("id", "repoNameInput");
          inputForRepoName.setAttribute("name", "repoNameInput");

          divForRepoName.appendChild(labelForInput);
          divForRepoName.appendChild(inputForRepoName);
          divForRepoName.appendChild(document.createElement("br"));

          // Create form element
          var uploadForm = document.createElement("form");
          uploadForm.setAttribute("id", "uploadForm");
          uploadForm.classList.add("forForm");
          uploadForm.setAttribute("enctype", "multipart/form-data");

          // Create file input element
          var fileInput = document.createElement("input");
          fileInput.setAttribute("type", "file");
          fileInput.setAttribute("id", "fileInput");
          fileInput.setAttribute("name", "files[]");
          fileInput.setAttribute("multiple", "multiple");
          fileInput.setAttribute("accept", ".json");
          fileInput.addEventListener("change", function () {
            var divForFileNames = document.getElementById("divForFileNames");
            divForFileNames.innerHTML = "";

            var table = document.createElement("table");
            table.classList.add("tableForPopUp");
            var thead = document.createElement("thead");
            var tr = document.createElement("tr");
            var fileNameHeader = document.createElement("th");
            fileNameHeader.textContent = "Filename";
            var featureOfInterestHeader = document.createElement("th");
            featureOfInterestHeader.textContent = "Feature Of Interest Name";

            tr.appendChild(fileNameHeader);
            tr.appendChild(featureOfInterestHeader);
            thead.appendChild(tr);

            var tbody = document.createElement("tbody");

            table.appendChild(thead);
            table.appendChild(tbody);

            var fileInput = document.getElementById("fileInput");

            for (var i = 0; i < fileInput.files.length; i++) {
              var tempLabel = document.createElement("label");
              tempLabel.setAttribute(
                "id",
                "fileForfeatureOfInterest[" + i + "]"
              );
              tempLabel.setAttribute("for", "featureOfInterest[" + i + "]");
              tempLabel.textContent = fileInput.files[i].name;

              var inputForFeatureOfInterest = document.createElement("input");
              inputForFeatureOfInterest.setAttribute("type", "text");
              inputForFeatureOfInterest.setAttribute(
                "id",
                "featureOfInterest[" + i + "]"
              );
              inputForFeatureOfInterest.setAttribute(
                "name",
                "featureOfInterest[" + i + "]"
              );

              var tr = document.createElement("tr");
              var td1 = document.createElement("td");
              var td2 = document.createElement("td");

              td1.appendChild(tempLabel);
              td2.appendChild(inputForFeatureOfInterest);

              tr.appendChild(td1);
              tr.appendChild(td2);

              tbody.appendChild(tr);
            }

            if (fileInput.files.length > 0) {
              divForFileNames.appendChild(table);
            }
          });

          // Create button element
          var addButton = document.createElement("button");
          addButton.setAttribute("type", "button");
          addButton.classList.add("addDataButton");
          addButton.textContent = "Add";
          addButton.addEventListener("click", function () {
            var fileInput = document.getElementById("fileInput");

            //this var must become 0 in order to insure that the user fill all the inputs.
            //If it is initialized only with 1 (the repository name), this means that the user, didn't choose any file to upload
            var inputsToBeFilled = 1 + fileInput.files.length;

            if (inputsToBeFilled > 1) {
              var repoName = document.getElementById("repoNameInput").value;
              // console.log(repoName);

              repoName = repoName.replace(/\s/g, "");
              if (repoName.length > 0) {
                var featureOfInterestNames = [];
                inputsToBeFilled--;

                var booleanContinue = true;

                for (
                  var tempInt = 0;
                  inputsToBeFilled > 0 && booleanContinue;
                  tempInt++
                ) {
                  var tempInputFeatureOfInterest = document.getElementById(
                    "featureOfInterest[" + tempInt + "]"
                  ).value;
                  tempInputFeatureOfInterest =
                    tempInputFeatureOfInterest.replace(/\s/g, "");
                  if (tempInputFeatureOfInterest.length > 0) {
                    featureOfInterestNames.push(tempInputFeatureOfInterest);
                    inputsToBeFilled--;
                  } else {
                    booleanContinue = false;
                    window.alert("Fill all the Feature of Interest Names!");
                  }
                }

                if (booleanContinue) {
                  console.log(featureOfInterestNames);

                  var bodyToSend =
                    "folderForFiles/" +
                    fileInput.files[0].name +
                    "\n" +
                    featureOfInterestNames[0];
                  for (var i = 1; i < featureOfInterestNames.length; i++) {
                    bodyToSend += "\n";
                    bodyToSend +=
                      "folderForFiles/" +
                      fileInput.files[i].name +
                      "\n" +
                      featureOfInterestNames[i];
                  }

                  fetch(api + "?task=checkIfRepoExists", {
                    method: "POST",
                    body: repoName,
                  })
                    .then((response) => response.text())
                    .then((data) => {
                      if (data === "yes") {
                        window.alert(
                          "There is already a repository with this name!"
                        );
                      } else {
                        showLoadingMessage();

                        for (var i = 0; i < fileInput.files.length; i++) {
                          var temp = fileInput.files[i].name;
                          var file = fileInput.files[i];
                          var reader = new FileReader();

                          reader.onload = (function (temp) {
                            return function (event) {
                              var fileContents = event.target.result;
                              //send the files to the server and then add the then create the new Repository
                              fetch(api + "?task=createFile&fileName=" + temp, {
                                method: "POST",
                                body: fileContents,
                              })
                                .then((response) => response.text())
                                .then((data) => {
                                  if (data === "success") {
                                    fetch(
                                      api +
                                        "?task=addData&repoName=" +
                                        repoName,
                                      {
                                        method: "POST",
                                        body: bodyToSend,
                                      }
                                    )
                                      .then((response) => response.text())
                                      .then((data) => {
                                        if (data === "success") {
                                          window.alert(
                                            "The repository has been successfully created!"
                                          );
                                        
                                          var homeReturnButton =
                                            document.getElementById(
                                              "homeReturnButton"
                                            );
                                          homeReturnButton.click();
                                          startCheckConnectionWithGraphDbServer();
                                          startIntervalForAdditionButton();
                                          startIntervalForRepositories();
                                        } else if(data === "Violation at shacl rules"){
                                          window.alert(
                                            "Violation at shacl rules!\nRead the violation report at: validationReport.ttl"
                                          );

                                          window.open('validationReport.ttl', '_blank');

                                          var homeReturnButton =
                                            document.getElementById(
                                              "homeReturnButton"
                                            );
                                          homeReturnButton.click();
                                          startCheckConnectionWithGraphDbServer();
                                          startIntervalForAdditionButton();
                                          startIntervalForRepositories();
                                        }
                                        else {
                                          window.alert(
                                            "Error with the creation of the repository!"
                                          );

                                          var homeReturnButton =
                                            document.getElementById(
                                              "homeReturnButton"
                                            );
                                          homeReturnButton.click();
                                          startCheckConnectionWithGraphDbServer();
                                          startIntervalForAdditionButton();
                                          startIntervalForRepositories();
                                        }
                                      })
                                      .catch((error) => {
                                        console.log(error);
                                      });
                                  } else {
                                    window.alert(
                                      "Error with the reading of the files!"
                                    );
                                  }
                                })
                                .catch((error) => {
                                  console.log(error);
                                });
                            };
                          })(temp);

                          reader.onerror = function (event) {
                            console.error(
                              "File reading error:",
                              event.target.error
                            );
                          };

                          reader.readAsText(file);
                        }
                      }
                    })
                    .catch((error) => {
                      console.log(error);
                    });
                }
              } else {
                window.alert("Give a name for the new Repository!");
              }
            } else {
              window.alert("Choose at list on file to upload!");
            }
          });

          var cancelButton = document.createElement("button");
          cancelButton.setAttribute("type", "button");
          cancelButton.classList.add("cancelButton");
          cancelButton.textContent = "Cancel";
          cancelButton.addEventListener("click", function () {
            var overlay = document.getElementById("overlay");
            overlay.remove();
            startIntervalForAdditionButton();
            startCheckConnectionWithGraphDbServer();
            startIntervalForRepositories();
          });

          // Append file input and button to form
          var divForButtons = document.createElement("div");
          divForButtons.appendChild(document.createElement("br"));
          divForButtons.appendChild(document.createElement("br"));
          divForButtons.appendChild(addButton);
          divForButtons.appendChild(cancelButton);

          uploadForm.appendChild(fileInput);
          uploadForm.appendChild(divForButtons);

          var overlay = document.createElement("div");
          overlay.classList.add("overlay");
          overlay.setAttribute("id", "overlay");

          var descrForFileNames = document.createElement("h5");
          descrForFileNames.innerHTML =
            "Choose the <u><b>shimmer files</b></u> you want to add:";

          var divForFileNames = document.createElement("div");
          divForFileNames.setAttribute("id", "divForFileNames");
          divForFileNames.classList.add("divForFileNames");

          var popUp = document.createElement("div");
          popUp.classList.add("popUp");
          overlay.setAttribute("id", "overlay");
          popUp.appendChild(title);
          popUp.appendChild(divForRepoName);
          popUp.appendChild(descrForFileNames);
          popUp.appendChild(divForFileNames);
          popUp.appendChild(uploadForm);

          overlay.appendChild(popUp);

          forCenter.appendChild(overlay);
        });

        containerForAdditionButton.appendChild(button);
      }
    })
    .catch((error) => {
      console.log(error);
    });
}

function showAllOpenMHealthRepositories() {
  fetch(api + "?task=getAllRepositories")
    .then((response) => response.text())
    .then((data) => {
      var tableContainer = document.getElementById("centerContent");
      tableContainer.innerHTML = "";

      if (data !== "Nothing here") {
        var repositories = data.split(",");

        var table = document.createElement("table");
        var tbody = table.createTBody();

        var i;
        var j;
        for (i = 0; i < repositories.length; ) {
          j = 0;
          var row = tbody.insertRow();
          while (j < 3 && i + j < repositories.length) {
            var cell = row.insertCell();
            cell.classList.add("databaseContainer");
            cell.textContent = repositories[i + j];
            cell.setAttribute("id", repositories[i + j]);

            cell.addEventListener("click", function () {
              clickADataBase(this.id);
            });

            j++;
          }
          i += j;
        }

        tableContainer.appendChild(table);
        tableContainer.classList.add("scrollableTable");
      }
    })
    .catch((error) => {
      console.log(error);
    });
}

function clickADataBase(repositoryId) {
  stopIntervalForRepositories();
  stopIntervalForAdditionButton();

  fetch(api + "?task=clickDatabase&repoName=" + repositoryId)
    .then((response) => response.text())
    .then((data) => {
      if (data === "true") {
        var center = document.getElementById("centerContent");
        center.innerHTML = "";
        center.classList.remove("scrollableTable");
        center.classList.add("databaseDetailsCenter");

        var repoHeader = document.createElement("h2");
        repoHeader.classList.add("repoHeaderClass");
        repoHeader.textContent = repositoryId;

        center.appendChild(repoHeader);

        var divForRepresentInfo = document.createElement("div");
        divForRepresentInfo.classList.add("divForRepresentInfo");
        divForRepresentInfo.setAttribute("id", "divForRepresentInfo");
        center.appendChild(divForRepresentInfo);

        var leftContainer = document.getElementById("containerForRepoMenu");
        leftContainer.innerHTML = "";
        leftContainer.classList.add("repoChoices");

        var headerForLeftList = document.createElement("h3");
        headerForLeftList.classList.add("headerForLeftList");
        headerForLeftList.textContent = "Repository";

        //create the menu for Repository tool
        var ul = document.createElement("ul");

        //create the Data Collection element
        var liCollection = document.createElement("li");
        liCollection.textContent = "Data Collection";
        liCollection.classList.add("liCollection");
        liCollection.setAttribute("id", "dataColection." + repositoryId);
        ul.appendChild(liCollection);

        //create the Add Data element
        var liAddData = document.createElement("li");
        liAddData.textContent = "Add Data";
        liAddData.setAttribute("id", "addData." + repositoryId);
        liAddData.addEventListener("click", function () {
          clickAMenuOption(this.id);
        });
        ul.appendChild(liAddData);

        //create the Delete Repository element
        var liDeleteRepo = document.createElement("li");
        liDeleteRepo.textContent = "Delete Repository";
        liDeleteRepo.setAttribute("id", "deleteRepository." + repositoryId);
        liDeleteRepo.addEventListener("click", function () {
          clickAMenuOption(this.id);
        });
        ul.appendChild(liDeleteRepo);

        var ulInCollection = document.createElement("ul");
        liCollection.appendChild(ulInCollection);

        //create the subMenu for the data Collection

        //create the Body Height element
        var liBodyHeight = document.createElement("li");
        liBodyHeight.classList.add("subMenuElement");
        liBodyHeight.setAttribute("id", "bodyHeight." + repositoryId);
        var imgBodyHeight = document.createElement("img");
        imgBodyHeight.src = "images/forBodyHeight.png";
        imgBodyHeight.alt = "";
        imgBodyHeight.classList.add("imageForContainers");
        var textBodyHeight = document.createElement("div");
        textBodyHeight.classList.add("textSubMenu");
        textBodyHeight.textContent = "Body Height";
        liBodyHeight.appendChild(imgBodyHeight);
        liBodyHeight.appendChild(textBodyHeight);
        ulInCollection.appendChild(liBodyHeight);
        liBodyHeight.addEventListener("click", function () {
          clickAMenuOption(this.id);
        });

        //create the Body Weight element
        var liBodyWeight = document.createElement("li");
        liBodyWeight.classList.add("subMenuElement");
        liBodyWeight.setAttribute("id", "bodyWeight." + repositoryId);
        var imgBodyWeight = document.createElement("img");
        imgBodyWeight.src = "images/forBodyWeight.png";
        imgBodyWeight.alt = "";
        imgBodyWeight.classList.add("imageForContainers");
        var textBodyWeight = document.createElement("div");
        textBodyWeight.classList.add("textSubMenu");
        textBodyWeight.textContent = "Body Weight";
        liBodyWeight.appendChild(imgBodyWeight);
        liBodyWeight.appendChild(textBodyWeight);
        ulInCollection.appendChild(liBodyWeight);
        liBodyWeight.addEventListener("click", function () {
          clickAMenuOption(this.id);
        });

        //create the Calories Burned element
        var liCaloriesBurned = document.createElement("li");
        liCaloriesBurned.classList.add("subMenuElement");
        liCaloriesBurned.setAttribute("id", "caloriesBurned." + repositoryId);
        var imgCaloriesBurned = document.createElement("img");
        imgCaloriesBurned.src = "images/forCaloriesBurned.png";
        imgCaloriesBurned.alt = "";
        imgCaloriesBurned.classList.add("imageForContainers");
        var textCaloriesBurned = document.createElement("div");
        textCaloriesBurned.classList.add("textSubMenu");
        textCaloriesBurned.textContent = "Calories Burned";
        liCaloriesBurned.appendChild(imgCaloriesBurned);
        liCaloriesBurned.appendChild(textCaloriesBurned);
        ulInCollection.appendChild(liCaloriesBurned);
        liCaloriesBurned.addEventListener("click", function () {
          clickAMenuOption(this.id);
        });

        //create the Geoposition element
        var liGeoposition = document.createElement("li");
        liGeoposition.classList.add("subMenuElement");
        liGeoposition.setAttribute("id", "geoposition." + repositoryId);
        var imgGeoposition = document.createElement("img");
        imgGeoposition.src = "images/forGeoposition.png";
        imgGeoposition.alt = "";
        imgGeoposition.classList.add("imageForContainers");
        var textGeoposition = document.createElement("div");
        textGeoposition.classList.add("textSubMenu");
        textGeoposition.textContent = "Geoposition";
        liGeoposition.appendChild(imgGeoposition);
        liGeoposition.appendChild(textGeoposition);
        ulInCollection.appendChild(liGeoposition);
        liGeoposition.addEventListener("click", function () {
          clickAMenuOption(this.id);
        });

        //create the Heart Rate element
        var liHeartRate = document.createElement("li");
        liHeartRate.classList.add("subMenuElement");
        liHeartRate.setAttribute("id", "heartRate." + repositoryId);
        var imgHeartRate = document.createElement("img");
        imgHeartRate.src = "images/forHeartRate.png";
        imgHeartRate.alt = "";
        imgHeartRate.classList.add("imageForContainers");
        var textHeartRate = document.createElement("div");
        textHeartRate.classList.add("textSubMenu");
        textHeartRate.textContent = "Heart Rate";
        liHeartRate.appendChild(imgHeartRate);
        liHeartRate.appendChild(textHeartRate);
        ulInCollection.appendChild(liHeartRate);
        liHeartRate.addEventListener("click", function () {
          clickAMenuOption(this.id);
        });

        //create the Physical Activity element
        var liPhysicalActivity = document.createElement("li");
        liPhysicalActivity.classList.add("subMenuElement");
        liPhysicalActivity.setAttribute(
          "id",
          "physicalActivity." + repositoryId
        );
        var imgPhysicalActivity = document.createElement("img");
        imgPhysicalActivity.src = "images/forPhysicalActivity.png";
        imgPhysicalActivity.alt = "";
        imgPhysicalActivity.classList.add("imageForContainers");
        var textPhysicalActivity = document.createElement("div");
        textPhysicalActivity.classList.add("textSubMenu");
        textPhysicalActivity.textContent = "Physical Activity";
        liPhysicalActivity.appendChild(imgPhysicalActivity);
        liPhysicalActivity.appendChild(textPhysicalActivity);
        ulInCollection.appendChild(liPhysicalActivity);
        liPhysicalActivity.addEventListener("click", function () {
          clickAMenuOption(this.id);
        });

        //create the Speed element
        var liSpeed = document.createElement("li");
        liSpeed.classList.add("subMenuElement");
        liSpeed.setAttribute("id", "speed." + repositoryId);
        var imgSpeed = document.createElement("img");
        imgSpeed.src = "images/forSpeed.png";
        imgSpeed.alt = "";
        imgSpeed.classList.add("imageForContainers");
        var textSpeed = document.createElement("div");
        textSpeed.classList.add("textSubMenu");
        textSpeed.textContent = "Speed";
        liSpeed.appendChild(imgSpeed);
        liSpeed.appendChild(textSpeed);
        ulInCollection.appendChild(liSpeed);
        liSpeed.addEventListener("click", function () {
          clickAMenuOption(this.id);
        });

        //create the Step Count element
        var liStepCount = document.createElement("li");
        liStepCount.classList.add("subMenuElement");
        liStepCount.setAttribute("id", "stepCount." + repositoryId);
        var imgStepCount = document.createElement("img");
        imgStepCount.src = "images/forStepCount.png";
        imgStepCount.alt = "";
        imgStepCount.classList.add("imageForContainers");
        var textStepCount = document.createElement("div");
        textStepCount.classList.add("textSubMenu");
        textStepCount.textContent = "Step Count";
        liStepCount.appendChild(imgStepCount);
        liStepCount.appendChild(textStepCount);
        ulInCollection.appendChild(liStepCount);
        liStepCount.addEventListener("click", function () {
          clickAMenuOption(this.id);
        });

        leftContainer.appendChild(headerForLeftList);
        leftContainer.appendChild(ul);

        liBodyHeight.click(); //always show the details of the body height first
      } else {
        startIntervalForRepositories();
        startIntervalForAdditionButton();
      }
    })
    .catch((error) => {
      startIntervalForRepositories();
      startIntervalForAdditionButton();
      console.log(error);
    });
}

/**
 * This function is activated when the user clicks on an element from the menu
 */
function clickAMenuOption(menuId) {
  // console.log(menuId);
  var parts = menuId.split("."); //the part[0] is the menuOption and the part[1] is the repositoryId
  setToAllTheDefaultBackground(parts[1]);

  var clickedElement = document.getElementById(menuId);
  clickedElement.classList.add("clickedLi");

  if (parts[0] === "deleteRepository") {
    //if the user wants to delete the repository
    var result = window.confirm(
      "Are you sure you want to delete the repository: " + parts[1] + "?"
    );
    if (result) {
      fetch(api + "?task=deleteRepository&repoName=" + parts[1])
        .then((response) => response.text())
        .then((data) => {
          window.alert(data);
          homeReturnButton.click();
        })
        .catch((error) => {
          console.log(error);
        });
    } else {
      var bodyHeightContainer = document.getElementById(
        "bodyHeight." + parts[1]
      );
      bodyHeightContainer.click();
    }
  } else if (parts[0] === "addData") {
    stopCheckConnectionWithGraphDbServer();

    var forCenter = document.getElementById("divForRepresentInfo");
    forCenter.innerHTML = "";
    var title = document.createElement("h3");
    title.textContent = "Add Data";

    var divForRepoName = document.createElement("div");

    var labelForInput = document.createElement("label");
    labelForInput.setAttribute("for", "repoNameInput");
    labelForInput.textContent = "Repository Name: ";

    var inputForRepoName = document.createElement("input");
    inputForRepoName.setAttribute("type", "text");
    inputForRepoName.setAttribute("id", "repoNameInput");
    inputForRepoName.setAttribute("name", "repoNameInput");
    inputForRepoName.setAttribute("value", parts[1]);
    inputForRepoName.readOnly = true;

    divForRepoName.appendChild(labelForInput);
    divForRepoName.appendChild(inputForRepoName);
    divForRepoName.appendChild(document.createElement("br"));

    // Create form element
    var uploadForm = document.createElement("form");
    uploadForm.setAttribute("id", "uploadForm");
    uploadForm.classList.add("forForm");
    uploadForm.setAttribute("enctype", "multipart/form-data");

    // Create file input element
    var fileInput = document.createElement("input");
    fileInput.setAttribute("type", "file");
    fileInput.setAttribute("id", "fileInput");
    fileInput.setAttribute("name", "files[]");
    fileInput.setAttribute("multiple", "multiple");
    fileInput.setAttribute("accept", ".json");
    fileInput.addEventListener("change", function () {
      var divForFileNames = document.getElementById("divForFileNames");
      divForFileNames.innerHTML = "";

      var table = document.createElement("table");
      table.classList.add("tableForPopUp");
      var thead = document.createElement("thead");
      var tr = document.createElement("tr");
      var fileNameHeader = document.createElement("th");
      fileNameHeader.textContent = "Filename";
      var featureOfInterestHeader = document.createElement("th");
      featureOfInterestHeader.textContent = "Feature Of Interest Name";

      tr.appendChild(fileNameHeader);
      tr.appendChild(featureOfInterestHeader);
      thead.appendChild(tr);

      var tbody = document.createElement("tbody");

      table.appendChild(thead);
      table.appendChild(tbody);

      var fileInput = document.getElementById("fileInput");

      for (var i = 0; i < fileInput.files.length; i++) {
        var tempLabel = document.createElement("label");
        tempLabel.setAttribute("id", "fileForfeatureOfInterest[" + i + "]");
        tempLabel.setAttribute("for", "featureOfInterest[" + i + "]");
        tempLabel.textContent = fileInput.files[i].name;

        var inputForFeatureOfInterest = document.createElement("input");
        inputForFeatureOfInterest.setAttribute("type", "text");
        inputForFeatureOfInterest.setAttribute(
          "id",
          "featureOfInterest[" + i + "]"
        );
        inputForFeatureOfInterest.setAttribute(
          "name",
          "featureOfInterest[" + i + "]"
        );

        var tr = document.createElement("tr");
        var td1 = document.createElement("td");
        var td2 = document.createElement("td");

        td1.appendChild(tempLabel);
        td2.appendChild(inputForFeatureOfInterest);

        tr.appendChild(td1);
        tr.appendChild(td2);

        tbody.appendChild(tr);
      }

      if (fileInput.files.length > 0) {
        divForFileNames.appendChild(table);
      }
    });

    // Create button element
    var addButton = document.createElement("button");
    addButton.setAttribute("type", "button");
    addButton.classList.add("addDataButton");
    addButton.textContent = "Add";
    addButton.addEventListener("click", function () {
      var fileInput = document.getElementById("fileInput");

      //this var must become 0 in order to insure that the user fill all the inputs.
      //If it is initialized only with 1 (the repository name), this means that the user, didn't choose any file to upload
      var inputsToBeFilled = 1 + fileInput.files.length;

      if (inputsToBeFilled > 1) {
        var repoName = parts[1];
        // console.log("RepoName= "+repoName);

        repoName = repoName.replace(/\s/g, "");
        if (repoName.length > 0) {
          var featureOfInterestNames = [];
          inputsToBeFilled--;

          var booleanContinue = true;

          for (
            var tempInt = 0;
            inputsToBeFilled > 0 && booleanContinue;
            tempInt++
          ) {
            var tempInputFeatureOfInterest = document.getElementById(
              "featureOfInterest[" + tempInt + "]"
            ).value;
            tempInputFeatureOfInterest = tempInputFeatureOfInterest.replace(
              /\s/g,
              ""
            );
            if (tempInputFeatureOfInterest.length > 0) {
              featureOfInterestNames.push(tempInputFeatureOfInterest);
              inputsToBeFilled--;
            } else {
              booleanContinue = false;
              window.alert("Fill all the Feature of Interest Names!");
            }
          }

          if (booleanContinue) {
            console.log(featureOfInterestNames);

            var bodyToSend =
              "folderForFiles/" +
              fileInput.files[0].name +
              "\n" +
              featureOfInterestNames[0];
            for (var i = 1; i < featureOfInterestNames.length; i++) {
              bodyToSend += "\n";
              bodyToSend +=
                "folderForFiles/" +
                fileInput.files[i].name +
                "\n" +
                featureOfInterestNames[i];
            }

            fetch(api + "?task=checkIfRepoExists", {
              method: "POST",
              body: repoName,
            })
              .then((response) => response.text())
              .then((data) => {
                if (data === "no") {
                  window.alert("Error! No such a repository");
                } else {
                  showLoadingMessage();

                  for (var i = 0; i < fileInput.files.length; i++) {
                    var temp = fileInput.files[i].name;
                    var file = fileInput.files[i];
                    var reader = new FileReader();

                    reader.onload = (function (temp) {
                      return function (event) {
                        var fileContents = event.target.result;
                        //send the files to the server and then  create the new Repository
                        fetch(api + "?task=createFile&fileName=" + temp, {
                          method: "POST",
                          body: fileContents,
                        })
                          .then((response) => response.text())
                          .then((data) => {
                            if (data === "success") {
                              fetch(
                                api + "?task=addData&repoName=" + repoName,
                                {
                                  method: "POST",
                                  body: bodyToSend,
                                }
                              )
                                .then((response) => response.text())
                                .then((data) => {
                                  if (data === "success") {
                                    window.alert(
                                      "The data has been successfully added!"
                                    );

                                    startCheckConnectionWithGraphDbServer();

                                    var homeReturnButton =
                                      document.getElementById(
                                        "homeReturnButton"
                                      );
                                    homeReturnButton.click();
                                  } else if(data === "Violation at shacl rules"){
                                    window.alert(
                                      "Violation at shacl rules!\nRead the violation report at: validationReport.ttl"
                                    );

                                    window.open('validationReport.ttl', '_blank');

                                    
                                    startCheckConnectionWithGraphDbServer();

                                    var homeReturnButton =
                                      document.getElementById(
                                        "homeReturnButton"
                                      );
                                    homeReturnButton.click();
                                  }
                                  else {
                                    window.alert(
                                      "Error with the addition of the data!"
                                    );
                                    startCheckConnectionWithGraphDbServer();

                                    var homeReturnButton =
                                      document.getElementById(
                                        "homeReturnButton"
                                      );
                                    homeReturnButton.click();
                                  }
                                })
                                .catch((error) => {
                                  console.log(error);
                                });
                            } else {
                              window.alert(
                                "Error with the reading of the files!"
                              );
                            }
                          })
                          .catch((error) => {
                            console.log(error);
                          });
                      };
                    })(temp);

                    reader.onerror = function (event) {
                      console.error("File reading error:", event.target.error);
                    };

                    reader.readAsText(file);
                  }
                }
              })
              .catch((error) => {
                console.log(error);
              });
          }
        }
      } else {
        window.alert("Choose at list on file to upload!");
      }
    });

    var cancelButton = document.createElement("button");
    cancelButton.setAttribute("type", "button");
    cancelButton.classList.add("cancelButton");
    cancelButton.textContent = "Cancel";
    cancelButton.addEventListener("click", function () {
      startCheckConnectionWithGraphDbServer();
      var overlay = document.getElementById("overlay");
      overlay.remove();
      var bodyHeightContainer = document.getElementById(
        "bodyHeight." + parts[1]
      );
      bodyHeightContainer.click();
    });

    // Append file input and button to form
    var divForButtons = document.createElement("div");
    divForButtons.appendChild(document.createElement("br"));
    divForButtons.appendChild(document.createElement("br"));
    divForButtons.appendChild(addButton);
    divForButtons.appendChild(cancelButton);

    uploadForm.appendChild(fileInput);
    uploadForm.appendChild(divForButtons);

    var overlay = document.createElement("div");
    overlay.classList.add("overlay");
    overlay.setAttribute("id", "overlay");

    var descrForFileNames = document.createElement("h5");
    descrForFileNames.innerHTML =
      "Choose the <u><b>shimmer files</b></u> you want to add:";

    var divForFileNames = document.createElement("div");
    divForFileNames.setAttribute("id", "divForFileNames");
    divForFileNames.classList.add("divForFileNames");

    var popUp = document.createElement("div");
    popUp.classList.add("popUp");
    overlay.setAttribute("id", "overlay");
    popUp.appendChild(title);
    popUp.appendChild(divForRepoName);
    popUp.appendChild(descrForFileNames);
    popUp.appendChild(divForFileNames);
    popUp.appendChild(uploadForm);

    overlay.appendChild(popUp);

    forCenter.appendChild(overlay);
  } else if (parts[0] === "bodyHeight") {
    showBodyHeightDetails(parts[1]);
  } else if (parts[0] === "bodyWeight") {
    showBodyWeightDetails(parts[1]);
  } else if (parts[0] === "caloriesBurned") {
    showCaloriesBurnedDetails(parts[1]);
  } else if (parts[0] === "geoposition") {
    showGeopositionDetails(parts[1]);
  } else if (parts[0] === "heartRate") {
    showHeartRateDetails(parts[1]);
  } else if (parts[0] === "physicalActivity") {
    showPhysicalActivityDetails(parts[1]);
  } else if (parts[0] === "speed") {
    showSpeedDetails(parts[1]);
  } else if (parts[0] === "stepCount") {
    showStepCountDetails(parts[1]);
  }
}

window.onload = () => {
  startCheckConnectionWithGraphDbServer();
  startIntervalForAdditionButton();
  startIntervalForRepositories();
};

function startCheckConnectionWithGraphDbServer() {
  checkConnectionWithGraphDbServer();
  checkConnection = setInterval(checkConnectionWithGraphDbServer, 5000); //in order always check if there is a connection with the graph server
}

function stopCheckConnectionWithGraphDbServer() {
  clearInterval(checkConnection);
}

function startIntervalForAdditionButton() {
  addAdditionButton();
  additionIntervalId = setInterval(addAdditionButton, 5000);
}

function stopIntervalForAdditionButton() {
  clearInterval(additionIntervalId);

  var buttonToRemove = document.getElementById("additionButton");
  buttonToRemove.parentNode.removeChild(buttonToRemove);
}

function startIntervalForRepositories() {
  showAllOpenMHealthRepositories();
  repositoryIntervalId = setInterval(showAllOpenMHealthRepositories, 5000);
}

function stopIntervalForRepositories() {
  clearInterval(repositoryIntervalId);
}

/**
 *
 * Helpful function that set all the backgrounds of the elements of the menu in the default
 */
function setToAllTheDefaultBackground(repositoryId) {
  var allTheElementsId = [
    "addData." + repositoryId,
    "deleteRepository." + repositoryId,
    "bodyHeight." + repositoryId,
    "bodyWeight." + repositoryId,
    "caloriesBurned." + repositoryId,
    "geoposition." + repositoryId,
    "heartRate." + repositoryId,
    "physicalActivity." + repositoryId,
    "speed." + repositoryId,
    "stepCount." + repositoryId,
  ];

  allTheElementsId.forEach(function (id) {
    var tempElement = document.getElementById(id);
    tempElement.classList.remove("clickedLi");
  });
}

/**
 * This function do a post request to the java server. It sends a text that has the fileName and the fileText of the file that the user wants to add inside the graphDB
 */
function sendTheFileToServer(fileName, fileText) {
  fetch(api + "?task=createFile&fileName=" + fileName, {
    method: "POST",
    body: fileText,
  }).catch((error) => {
    console.log(error);
  });
}

function showLoadingMessage() {
  var forCenter = document.getElementById("forLoader");
  forCenter.innerHTML = "";

  var loader = document.createElement("div");
  loader.classList.add("loader");

  var overlay = document.createElement("div");
  overlay.classList.add("overlay");
  overlay.setAttribute("id", "overlay2");

  var popUp = document.createElement("div");
  popUp.classList.add("popUp");

  popUp.appendChild(loader);
  overlay.appendChild(popUp);
  forCenter.appendChild(overlay);
}