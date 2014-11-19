'use strict';

angular.module('gerbil.controller.navi',[])
.controller('NaviCtrl', function ($scope, $window) {
    $scope.back = function(){
        $window.history.back();
    }
});
