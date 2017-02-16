angular.module('engineHeaterTimer').factory('TimerResource', function($resource){
    var resource = $resource('rest/timers/:TimerId',{TimerId:'@timerId'},{'queryAll':{method:'GET',isArray:true},'query':{method:'GET',isArray:false},'update':{method:'PUT'}});
    return resource;
});