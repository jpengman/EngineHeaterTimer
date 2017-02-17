
angular.module('engineHeaterTimer').controller('NewSettingController', function ($scope, $location, locationParser, flash, SettingResource ) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.setting = $scope.setting || {};
    

    $scope.save = function() {
        var successCallback = function(data,responseHeaders){
            var id = locationParser(responseHeaders);
            flash.setMessage({'type':'success','text':'The setting was created successfully.'});
            $location.path('/Settings');
        };
        var errorCallback = function(response) {
            if(response && response.data) {
                flash.setMessage({'type': 'error', 'text': response.data.message || response.data}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        SettingResource.save($scope.setting, successCallback, errorCallback);
    };
    
    $scope.cancel = function() {
        $location.path("/Settings");
    };
});