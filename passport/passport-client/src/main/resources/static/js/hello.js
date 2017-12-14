angular.module('hello', [ 'ngRoute' ]).config(function($routeProvider, $httpProvider) {

	$routeProvider.when('/', {
		templateUrl : 'home.html',
		controller : 'home',
		controllerAs : 'controller'
	}).otherwise('/');

	$httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
	$httpProvider.defaults.headers.common['Accept'] = 'application/json';

}).controller('navigation',

function($rootScope, $http, $location, $route) {

	var self = this;

	self.tab = function(route) {
		return $route.current && route === $route.current.controller;
	};

	$http.get('api/account/user').then(function(response) {
		if (response.data.userName) {
			$rootScope.authenticated = true;
			$rootScope.userName = response.data.userName;
			
		} else {
			$rootScope.authenticated = false;
		}
	}, function() {
		$rootScope.authenticated = false;
	});

	self.credentials = {};

	self.logout = function() {
		console.log("Post logout to auth server");
	   $.ajax({
	        url: "http://127.0.0.1:9010/uaa/api/logout",
	        method: "POST",
	        xhrFields: {
	            withCredentials: true
        },
	        success: function(data) {
	          	console.log("Post logout to UI gateway");  
				$http.post('logout', {}).finally(function() {
					$rootScope.authenticated = false;
					$location.path("/");
				});
	        }
    	})		
	}

}).controller('home', function($http) {
	var self = this;
});
