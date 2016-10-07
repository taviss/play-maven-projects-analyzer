app.controller('reportController', function ($scope, ProjectService, UserService) {
    $scope.page = [];
    $scope.loggedIn = false;
    $scope.searchName = "";
    $scope.searchAuthor = "";
    $scope.selectedFilter = "Author"

    UserService.isAuthenticated(function (success) {
        if(success) {
            $scope.loggedIn = true;
        }
    });

    ProjectService.getPage(1, $scope.searchName, $scope.searchAuthor).then(function (dataResponse) {
            $scope.page = dataResponse.data;
            //Convert dates
            $scope.page.list.forEach(function(entry) {
                entry.inputDate = new Date(entry.inputDate);
            });
            //console.log($scope.page);
    });

    $scope.resetParams = function() {
        $scope.searchAuthor = "";
        $scope.searchName = "";
    };

    $scope.getPage = function () {
        if(($scope.selectedFilter == "Name" && ($scope.searchName == "" || $scope.searchName.length >= 2)) || ($scope.selectedFilter == "Author" && ($scope.searchAuthor == "" || $scope.searchAuthor.length >= 2)))
        ProjectService.getPage(1, $scope.searchName, $scope.searchAuthor).then(function (dataResponse) {
                $scope.page = dataResponse.data;
                //Convert dates
                $scope.page.list.forEach(function(entry) {
                    entry.inputDate = new Date(entry.inputDate);
                });
                //console.log($scope.page);
        });
    };

    $scope.displayNextPage = function () {
        ProjectService.getPage($scope.page.pageIndex + 1, $scope.searchName, $scope.searchAuthor).then(function (dataResponse) {
                    $scope.page = dataResponse.data;
                    //Convert dates
                    $scope.page.list.forEach(function(entry) {
                        entry.inputDate = new Date(entry.inputDate);
                    });
                    //console.log($scope.page.pageIndex);
        });
    };

    $scope.displayPrevPage = function () {
        ProjectService.getPage($scope.page.pageIndex - 1, $scope.searchName, $scope.searchAuthor).then(function (dataResponse) {
                    $scope.page = dataResponse.data;
                    //Convert dates
                    $scope.page.list.forEach(function(entry) {
                        entry.inputDate = new Date(entry.inputDate);
                    });
                    //console.log($scope.page.pageIndex);
        });
    };

    $scope.viewProject = function(id) {
             window.location = "/projects/view/" + id;
    };

    $scope.deleteProject = function(id) {
        if(confirm("Delete project?")) {
            ProjectService.deleteProject(id).then(function () {
                ProjectService.getPage($scope.page.pageIndex, $scope.searchName, $scope.searchAuthor).then(function (dataResponse) {
                            $scope.page = dataResponse.data;
                            //Convert dates
                            $scope.page.list.forEach(function(entry) {
                                entry.inputDate = new Date(entry.inputDate);
                            });
                            //console.log($scope.page.pageIndex);
                });
            });
        } else {

        }
    };
});