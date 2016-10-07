app.service("ProjectService", function ($http) {

    this.getProject = function (id) {
        return $http({
            method: "GET",
            url: "/projects/get/" + id
        });
    }


    this.getPage = function (page, name, author) {
            return $http({
                method: "GET",
                url: "/projects/" + page + "?name=" + name + "&author=" + author
            });
    }

    this.deleteProject = function (id) {
            return $http({
                method: "DELETE",
                url: "/projects/" + id
            });
    }

    this.checkProjectAnalyzed = function (projectName) {
                return $http({
                    method: "GET",
                    url: sonarHost + "/api/projects/index/?key=" + projectName
                });
    }

    this.getReport = function (projectName) {
                    return $http({
                        method: "GET",
                        url: sonarHost + "/api/resources?resource=" + projectName + "&metrics=complexity,duplicated_blocks,violations,bugs,code_smells,coverage"
                    });
    }

    this.getCustomReport = function (projectName, metrics) {
                    return $http({
                        method: "GET",
                        url: sonarHost + "/api/resources?resource=" + projectName + "&metrics=" + metrics
                    });
    }

    this.analyzeProject = function (id, sonarId) {
                    return $http({
                        method: "POST",
                        url: "/projects/analyze/" + id,
                        headers: { 'contentType': "application/x-www-form-urlencoded"},
                        data: {
                            "sonar": sonarId
                        }
                    });
    }

    this.getMetrics = function() {
        return $http({
            method: "GET",
            url: sonarHost + "/api/metrics/search"
        });
    }
});

app.controller('projectViewController', function ($scope, ProjectService, $window, SonarService, UserService, $routeParams) {
    $scope.project = {};
    $scope.sonarResponse = [];
    $scope.showReport = false;
    $scope.showResults = false;
    $scope.showCustomForm = false;
    $scope.showCustomResults = false;

    $scope.selected = {};
    $scope.selected.metrics = [];

    $scope.metrics = [];

    $scope.sonarList = [];
    $scope.selectedSonar = null;

    $scope.loggedIn = false;


    UserService.isAuthenticated(function (success) {
        if(success) {
            $scope.loggedIn = true;
        }
    });

    SonarService.getSonars().then(function (dataResponse) {
        $scope.sonarList = dataResponse.data;
        $scope.selectedSonar = $scope.sonarList.filter(s => s.address == sonarHost)[0].id;
    });

    ProjectService.getProject(location.pathname.split("/")[3]).then(function (dataResponse) {
            if(dataResponse.status == 200) {
                $scope.project = dataResponse.data;
                ProjectService.checkProjectAnalyzed($scope.project.projectKey).then(function (dataResponse) {
                    //console.log(dataResponse.data);
                    if(dataResponse.data.length == 0) {
                        $scope.project.analyzed = false;
                    } else {
                        $scope.project.analyzed = true;
                    }
                });
            } else {
                $scope.project = null;
            }
    });

    ProjectService.getMetrics().then(function (dataResponse) {
            $scope.metrics = dataResponse.data.metrics.filter(m => m.hidden == false);
    });

    $scope.updateSonar = function () {
        //console.log($scope.sonarList.filter(s => s.id == $scope.selectedSonar)[0]);
        sessionStorage.setItem('sonar', $scope.sonarList.filter(s => s.id == $scope.selectedSonar)[0].address);
        location.reload();
    }

    $scope.resetDivShow = function () {
        $scope.showReport = false;
        $scope.showResults = false;
        $scope.showCustomForm = false;
        $scope.showCustomResults = false;
    }

    $scope.goToSonar = function() {
         //$location.path(sonarHost + "/overview?id=" + $scope.project.projectName);
         $window.open(sonarHost + "/overview?id=" + $scope.project.projectKey, '_blank');
         //window.location.href(sonarHost + "/overview?id=" + $scope.project.projectName);
    };

    $scope.displayMetric = function (key) {
        return $scope.metrics.find(metric => metric.key == key).name;
    }

    $scope.getReport = function () {
            $scope.resetDivShow();
            ProjectService.getReport($scope.project.projectKey).then(function (dataResponse) {
                        $scope.sonarResponse = dataResponse.data[0];
                        $scope.showReport = true;
                        $scope.showResults = true;
                        //console.log($scope.sonarResponse);
            });
    };

    $scope.getCustomReport = function () {
            $scope.resetDivShow();
            ProjectService.getCustomReport($scope.project.projectKey, $scope.selected.metrics.join()).then(function (dataResponse) {
                        $scope.sonarResponse = dataResponse.data[0];
                        $scope.showReport = true;
                        $scope.showCustomForm = true;
                        $scope.showCustomResults = true;
                        //console.log($scope.sonarResponse);
            });
    };

    $scope.analyzeProject = function () {
            $scope.resetDivShow();
            //console.log($scope.selectedSonar);
            ProjectService.analyzeProject($scope.project.id, $scope.selectedSonar).then(function (dataResponse) {
                location.reload()
                /*
                //Sometimes gets called before analysis is finished
                ProjectService.getReport($scope.project.projectKey).then(function (dataResponse) {
                    $scope.sonarResponse = dataResponse.data[0];
                    $scope.showReport = true;
                    $scope.showResults = true;
                    //console.log($scope.sonarResponse);
                });*/
                //console.log($scope.sonarResponse);
            });
    };

});