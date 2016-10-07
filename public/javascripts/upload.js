app.service("FileUpload", function ($http) {

    this.uploadFileToUrl = function(owner, file, uploadUrl) {
        var fd = new FormData();
        fd.append('project', file);
        fd.append('name', owner);

        return $http.post(uploadUrl, fd, {
        transformRequest: angular.identity,
        headers: {'Content-Type': undefined}
        })
    }
});

app.directive('fileModel', ['$parse', function ($parse) {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            var model = $parse(attrs.fileModel);
            var modelSetter = model.assign;

            element.bind('change', function(){
                scope.$apply(function(){
                    modelSetter(scope, element[0].files[0]);
                });
            });
        }
    };
}]);

app.controller('uploadController', function ($scope, ProjectService, FileUpload, UserService) {
    $scope.project = null;
    $scope.name = null;
    $scope.fileNotZip = false;

    $scope.loggedIn = false;

    $scope.errors = {};

    UserService.isAuthenticated(function (success) {
        if(success) {
            $scope.loggedIn = true;
        }
    });

    $scope.uploadFile = function() {
        var file = $scope.project;
        var name = $scope.name;

        if(name == null || name == undefined) {
            name = "";
        }

        var uploadUrl = "/projects";
        FileUpload.uploadFileToUrl(name, file, uploadUrl)
            .success(function(){
                window.location = "/projects/report";
            })

            .error(function(error, status){
                if(status == 413) {
                    alert("The selected file is too large");
                } else {
                    $scope.errors = error;
                }
            });
        /*
        if(ok == -1) {
            $scope.setFileNotZip();
        }
        console.log(ok);*/
    };

    $scope.goToUploadReport = function() {
         window.location = "/projects/report";
    };

    $scope.setFileNotZip = function() {
        $scope.fileNotZip = true;
    };

});