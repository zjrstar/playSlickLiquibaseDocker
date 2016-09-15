/**
 * Admin Dashboard routes.
 */
define(['angular', './controllers', 'common'], function(angular, controllers) {
  'use strict';

  var mod = angular.module('admin.routes', ['yourprefix.common']);
  mod.config(['$routeProvider', 'userResolve', function($routeProvider, userResolve) {
    $routeProvider
    .when('/admin/dashboard',  {templateUrl: '/assets/javascripts/admin/dashboard.html',  controller:controllers.AdminDashboardCtrl, resolve:userResolve});
  }]);
  return mod;
});
