'use strict';

var
controllers = angular.module('gerbil.controller.alert', []);
controllers.controller('AlertCtrl', function($scope, Alert) {

    $scope.close = function (index) {
        Alert.remove(index);
    };
}); 
