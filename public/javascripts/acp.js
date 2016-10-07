app.controller('acpController', function ($scope, UserService) {
    $scope.loggedIn = false;

    UserService.isAuthenticated(function (success) {
        if(success) {
            $scope.loggedIn = true;
        }
    });
});