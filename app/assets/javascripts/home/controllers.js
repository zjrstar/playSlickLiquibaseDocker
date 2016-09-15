/**
 * Home controllers.
 */
define([], function() {
  'use strict';

  /** Controls the index page */
  var HomeCtrl = function($scope, $rootScope, $location, helper) {
    console.log(helper.sayHi());
    $rootScope.pageTitle = 'Welcome';
  };
  HomeCtrl.$inject = ['$scope', '$rootScope', '$location', 'helper'];

  /** Controls the header */
  var HeaderCtrl = function($scope, userService, helper, $location) {
    // Wrap the current user from the service in a watch expression
    $scope.$watch(function() {
      var user = userService.getUser();
      return user;
    }, function(user) {
      $scope.user = user;
    }, true);

    $scope.logout = function() {
      userService.logout();
      $scope.user = undefined;
      $location.path('/');
    };

    $scope.isUserInRole = function(roles) {
      var result = false;
      for (var i = 0, len = roles.length; i < len; i ++) {
        if ($scope.user !== undefined) {
          if (roles[i] === $scope.user.role) {
            result = true;
            break;
          }
        }
      }
      return result;
    };
  };
  HeaderCtrl.$inject = ['$scope', 'userService', 'helper', '$location'];

  /** Controls the footer */
  var FooterCtrl = function(/*$scope*/) {
  };
  //FooterCtrl.$inject = ['$scope'];

  return {
    HeaderCtrl: HeaderCtrl,
    FooterCtrl: FooterCtrl,
    HomeCtrl: HomeCtrl
  };

});
