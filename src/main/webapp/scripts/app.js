'use strict';

angular.module('engineHeaterTimer',['ngRoute','ngResource'])
  .config(['$routeProvider', function($routeProvider) {
    $routeProvider
      .when('/',{templateUrl:'views/Timer/search.html',controller:'SearchTimerController'})
      .when('/Settings',{templateUrl:'views/Setting/search.html',controller:'SearchSettingController'})
      .when('/Settings/new',{templateUrl:'views/Setting/detail.html',controller:'NewSettingController'})
      .when('/Settings/edit/:SettingId',{templateUrl:'views/Setting/detail.html',controller:'EditSettingController'})
      .when('/Timers',{templateUrl:'views/Timer/search.html',controller:'SearchTimerController'})
      .when('/Timers/new',{templateUrl:'views/Timer/detail.html',controller:'NewTimerController'})
      .when('/Timers/edit/:TimerId',{templateUrl:'views/Timer/detail.html',controller:'EditTimerController'})
      .otherwise({
        redirectTo: '/'
      });
  }])
  .controller('LandingPageController', function LandingPageController() {
  })
  .controller('NavController', function NavController($scope, $location) {
    $scope.matchesRoute = function(route) {
        var path = $location.path();
        return (path === ("/" + route) || path.indexOf("/" + route + "/") == 0);
    };
  });
