
angular.module('engineHeaterTimer').controller('NewTimerController', function ($scope, $location, locationParser, flash, TimerResource ) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.timer = $scope.timer || {};
    
    $scope.activeList = [
        "true",
        "false"
    ];

    $scope.fridayList = [
        "true",
        "false"
    ];

    $scope.mondayList = [
        "true",
        "false"
    ];

    $scope.saturdayList = [
        "true",
        "false"
    ];

    $scope.sundayList = [
        "true",
        "false"
    ];

    $scope.thursdayList = [
        "true",
        "false"
    ];

    $scope.tuesdayList = [
        "true",
        "false"
    ];

    $scope.wednesdayList = [
        "true",
        "false"
    ];


    $scope.save = function() {
        var successCallback = function(data,responseHeaders){
            var id = locationParser(responseHeaders);
            flash.setMessage({'type':'success','text':'The timer was created successfully.'});
            $location.path('/Timers');
        };
        var errorCallback = function(response) {
            if(response && response.data) {
                flash.setMessage({'type': 'error', 'text': response.data.message || response.data}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        TimerResource.save($scope.timer, successCallback, errorCallback);
    };
    
    $scope.cancel = function() {
        $location.path("/Timers");
    };
});