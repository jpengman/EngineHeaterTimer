'use strict';

angular.module('engineHeaterTimer').filter('searchFilter', function() {

    function matchObjectProperties(expectedObject, actualObject) {
        var flag = true;
        for(var key in expectedObject) {
            if(expectedObject.hasOwnProperty(key)) {
                var expectedProperty = expectedObject[key];
                if (expectedProperty == null || expectedProperty === "") {
                    continue;
                }
                var actualProperty = actualObject[key];
                if (angular.isUndefined(actualProperty)) {
                    continue;
                }
                if (actualProperty == null) {
                    flag = false;
                } else if (angular.isObject(expectedProperty)) {
                    flag = flag && matchObjectProperties(expectedProperty, actualProperty);
                } else {
                    flag = flag && (actualProperty.toString().indexOf(expectedProperty.toString()) != -1);
                }
            }
        }
        return flag;
    }
    
    function getRepeatDesc(result) {
    	if(result['monday'] && result['tuesday'] && result['wednesday'] && result['thursday'] && result['friday'] && result['saturday'] && result['sunday']){
    		return 'Everyday';
    	}
    	else if(!result['monday'] && !result['tuesday'] && !result['wednesday'] && !result['thursday'] && !result['friday'] && !result['saturday'] && !result['sunday']){
    		return 'No Repeat';
    	}
    	else if(result['monday'] && result['tuesday'] && result['wednesday'] && result['thursday'] && result['friday']){
    		var desc = 'Weekdays';
    		if(result['saturday']){
        		desc = desc+',Saturdays';
        	}   
        	if(result['sunday']){
        		desc = desc+',Sundays';
        	}   
    		return desc;
    	}

    	var desc = '';
    	if(result['monday']){
    		desc = desc+'Mondays,';
    	}  
    	if(result['tuesday']){
    		desc = desc+'Tuesdays,';
    	}   
    	if(result['wednesday']){
    		desc = desc+'Wednesdays,';
    	}   
    	if(result['thursday']){
    		desc = desc+'Thursdays,';
    	}   
    	if(result['friday']){
    		desc = desc+'Fridays,';
    	}   
    	if(result['saturday'] && result['sunday']){
    		return desc + 'Weekends'
    	}
    	if(result['saturday']){
    		desc = desc+'Saturdays,';
    	}   
    	if(result['sunday']){
    		desc = desc+'Sundays,';
    	}   
    	return desc.substring(0, desc.length - 1);
    }

    return function(results, scope) {

        scope.filteredResults = [];
        for (var ctr = 0; ctr < results.length; ctr++) {
            var flag = true;
            var searchCriteria = scope.search;
            var result = results[ctr];
            for (var key in searchCriteria) {
                if (searchCriteria.hasOwnProperty(key)) {
                    var expected = searchCriteria[key];
                    if (expected == null || expected === "") {
                        continue;
                    }
                    var actual = result[key];
                    if (actual == null) {
                        flag = false;
                    } else if (angular.isObject(expected)) {
                    	//Fix for dropdowns with both value and label 
                    	if(!angular.isObject(actual)){
                            var expectedValue = expected["value"]
                            flag = flag && (actual.toString().indexOf(expectedValue.toString()) != -1);
                        }else{
                            flag = flag && matchObjectProperties(expected, actual);
                        }
                    	// used to be: flag = flag && matchObjectProperties(expected, actual);
                    	
                    } else {
                        flag = flag && (actual.toString().indexOf(expected.toString()) != -1);
                    }
                }
            }
            if (flag == true) {
            	if(!angular.isUndefined(result['monday'])){
            		result['repeat'] = getRepeatDesc(result)
            	}
            	scope.filteredResults.push(result);
            }
        }
        scope.numberOfPages();
        return scope.filteredResults;
    };
});
