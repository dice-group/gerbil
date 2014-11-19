'use strict';

var dependencies = [
    'ngRoute',
    'ngSanitize',
    'ui',
    'ui.bootstrap',
    'ui.select',
    'angularFileUpload',
    'spin.js',
    'gerbil.controller.home',
    'gerbil.controller.config',
    'gerbil.controller.overview',
    'gerbil.controller.about',
    'gerbil.controller.navi',
    'gerbil.controller.alert',
    'gerbil.service.alert'
];

var Gerbil = angular.module('gerbil', dependencies);

Gerbil.constant('Routes', [{
    name: 'home',
    controllerName: 'HomeCtrl',
    label: 'Home'
}, {
    name: 'config',
    controllerName: 'ConfigCtrl',
    label: 'Configure Experiment'
}, {
    name: 'overview',
    controllerName: 'OverviewCtrl',
    label: 'Experiment Overview'
}, {
    name: 'about',
    controllerName: 'AboutCtrl',
    label: 'About Us'
}]);

Gerbil.config(['$routeProvider', '$locationProvider', 'Routes', function ($route, $location, Routes) {

        var path = 'resources/templates/',
            ext = '.html';

        angular.forEach(Routes, function(route) {
            $route.when('/' + route.name + (route.path ? '/' + route.path + '?' : ''), {
                templateUrl: path + (route.template ? route.template : route.name) + ext,
                controller: route.controllerName
            });
        });

        $route.when('/', {
            redirectTo: '/home'
        });

        $route.otherwise({
            redirectTo: '/home'
        });
    }
]);

Gerbil.run(['$rootScope', 'Routes', function ($scope, Routes, Alert) {

    $scope.routes = [];
    angular.forEach(Routes, function (route) {
        if (route.navigation !== false){
            $scope.routes.push(route);
        }
    });

    //Alert.reset();
  
}]);
