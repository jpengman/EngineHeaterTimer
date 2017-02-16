angular.module('engineHeaterTimer').factory('SettingResource', function($resource){
    var resource = $resource('../rest/settings/:SettingId',{SettingId:'@settingId'},{'queryAll':{method:'GET',isArray:true},'query':{method:'GET',isArray:false},'update':{method:'PUT'}});
    return resource;
});