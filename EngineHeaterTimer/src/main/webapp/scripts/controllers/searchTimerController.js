

angular.module('engineHeaterTimer').controller('SearchTimerController', function($scope, $http, $filter, TimerResource ) {

    $scope.search={};
    $scope.currentPage = 0;
    $scope.pageSize= 10;
    $scope.searchResults = [];
    $scope.filteredResults = [];
    $scope.pageRange = [];
    $scope.numberOfPages = function() {
        var result = Math.ceil($scope.filteredResults.length/$scope.pageSize);
        var max = (result == 0) ? 1 : result;
        $scope.pageRange = [];
        for(var ctr=0;ctr<max;ctr++) {
            $scope.pageRange.push(ctr);
        }
        return max;
    };
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

    $scope.performSearch = function() {
        $scope.searchResults = TimerResource.queryAll(function(){
            $scope.filteredResults = $filter('searchFilter')($scope.searchResults, $scope);
            $scope.currentPage = 0;
        });
    };
    
    $scope.previous = function() {
       if($scope.currentPage > 0) {
           $scope.currentPage--;
       }
    };
    
    $scope.next = function() {
       if($scope.currentPage < ($scope.numberOfPages() - 1) ) {
           $scope.currentPage++;
       }
    };
    
    $scope.setPage = function(n) {
       $scope.currentPage = n;
    };

    $scope.performSearch();
});