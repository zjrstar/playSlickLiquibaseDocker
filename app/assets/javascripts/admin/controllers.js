/**
 * Dashboard controllers.
 */
define([], function() {
  'use strict';

  /**
   * user is not a service, but stems from userResolve (Check ../user/services.js) object used by dashboard.routes.
   */
  var AdminDashboardCtrl = function($scope, user, $log, adminService) {
    $scope.user = user;
    $scope.tab = 1;

    $scope.showEdit = true;
    $scope.master = [];
    $scope.queryLogs = [];
    $scope.offset = 0;
    $scope.pageSize = 10;
    $scope.hasMore = false;

    $scope.init = function() {
      $scope.newUser = {role : "SystemAuditor"};
      $scope.result = "";
      $scope.selectedRow = undefined;
    };

    $scope.init();

    $scope.selectUser = function(row) {
      $scope.selectedRow = row;
    };

    $scope.selectTab = function(setTab) {
      $scope.tab = setTab;
      $scope.init();
      if (2 === $scope.tab) $scope.users = $scope.findUsers("%");
      if (3 === $scope.tab) {
        $scope.queryLogs = [];
        $scope.offset = 0;
        $scope.pageSize = 10;
        $scope.hasMore = false;
        $scope.showMore();
      }
    };

    $scope.isSelected = function(checkTab) {
      return $scope.tab === checkTab;
    };

    $scope.createUser = function(newUser) {
      adminService.createUser(newUser).then(function(response) {
        $scope.result = response;
      });
    };

    $scope.findUsers = function(queryEmail) {
      adminService.findUsers(queryEmail).then(function(response) {
        $scope.users = response;
      });
    };

    $scope.deleteUser = function(userId) {
      adminService.deleteUser(userId).then(function(response) {
        if (response) $scope.users.drop(user);
      });
    };

     $scope.listQueryResultOfQueryLogs = function(offset, pageSize) {
          adminService.listQueryResultOfQueryLogs(offset, pageSize).then(function(response) {
            if (response) {
              $scope.queryLogs = $scope.queryLogs.concat(response.elements);
              $scope.hasMore = $scope.queryLogs.length < response.pagination.total;
              $scope.offset = $scope.queryLogs.length;
            }
          });

        };

     $scope.showMore = function() {
       $scope.listQueryResultOfQueryLogs($scope.offset, $scope.pageSize);
     };

   };

  AdminDashboardCtrl.$inject = ['$scope', 'user', '$log', 'adminService'];

  return {
    AdminDashboardCtrl: AdminDashboardCtrl
  };

});
