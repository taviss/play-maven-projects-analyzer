app.service("SonarService", function ($http) {

    this.getSonar = function (id) {
        return $http({
            method: "GET",
            url: "/sonar/" + id
        });
    }

    this.deleteSonar = function (id) {
        return $http({
            method: "DELETE",
            url: "/sonar/" + id
        });
    }

    this.getSonars = function () {
        return $http({
            method: "GET",
            url: "/sonar"
        });
    }

    this.addSonar = function (sonar) {
        return $http({
            method: "POST",
            url: "/sonar",
            headers: { 'contentType': "application/x-www-form-urlencoded" },
            data: {
                "address": sonar.address,
                "user": sonar.user,
                "password": sonar.password
            }
        });
    }
});

app.controller('sonarController', function ($scope, SonarService, UserService) {
    $scope.sonar = {};

    $scope.sonarList = [];

    $scope.sonar.address = null;
    $scope.sonar.user = null;
    $scope.sonar.password = null;

    $scope.loggedIn = false;

    $scope.errors = {};

    UserService.isAuthenticated(function (success) {
        if(success) {
            $scope.loggedIn = true;
        }
    });

    SonarService.getSonars().then(function (dataResponse) {
        $scope.sonarList = dataResponse.data;
    });

    $scope.addSonar = function() {
        SonarService.addSonar($scope.sonar)
            .error(function (response) {
                $scope.errors = response;
            })
            .success(function () {
                SonarService.getSonars().then(function (dataResponse) {
                    $scope.sonarList = dataResponse.data;
                    $scope.goToSonarList();
                });
            });
    };

    $scope.deleteSonar = function(id) {
        SonarService.deleteSonar(id).then(function (dataResponse) {
            SonarService.getSonars().then(function (dataResponse) {
                $scope.sonarList = dataResponse.data;
            });
        });
    };

    $scope.goToSonarList = function() {
         window.location = "/acp/sonar/list";
    };

    $scope.goToSonarAdd = function() {
         window.location = "/acp/sonar/add";
    };

});