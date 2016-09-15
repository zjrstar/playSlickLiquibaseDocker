/**
 * Dashboard controllers.
 */
define([], function() {
  'use strict';

  /**
   * user is not a service, but stems from userResolve (Check ../user/services.js) object used by dashboard.routes.
   */
  var DashboardCtrl = function($scope, user, creditService, $log) {
    $scope.initDataModel = function() {
        $scope.user = user;
        $scope.creditQuery = {};
        $scope.result = "";
        $scope.mobilePhone = "";
        $scope.mobilePhoneQueryResult = {};
        $scope.creditUsageResult = {};
        $scope.compoundCheck = {};
        $scope.compoundCheckResult = {};
    };

    $scope.tab = 1;
    $scope.initDataModel();

    $scope.selectTab = function(setTab) {
      $scope.tab = setTab;
      $scope.initDataModel();
    };


    $scope.isSelected = function(checkTab) {
      return $scope.tab === checkTab;
    };

    $scope.executeQuery = function(creditQuery) {
          creditService.executeQuery([creditQuery]).then(function(response) {
            $scope.result = response.result;
          }, function(error) {
            $log.error(error);
            $scope.result = error;
          });
        };
    $scope.executeMobilePhoneQuery = function(mobilePhone) {
          creditService.executeMobilePhoneQuery([mobilePhone]).then(function(response) {
            $scope.mobilePhoneQueryResult = response;
          });
    };
    $scope.executeCreditUsageQuery = function() {
        creditService.executeCreditUsageQuery().then(function(response) {
          $scope.creditUsageResult = response;
        });
    };
    $scope.executeCompoundCheckLocal = function(compoundCheckRequest) {
        creditService.executeCompoundCheckLocal([compoundCheckRequest]).then(function(response) {
          $scope.compoundCheckResult = response;
        });
    };
    $scope.executeCompoundCheckRemote = function(compoundCheckRequest) {
            creditService.executeCompoundCheckRemote([compoundCheckRequest]).then(function(response) {
              $scope.compoundCheckResult = response;
            });
        };
  };
  DashboardCtrl.$inject = ['$scope', 'user', 'creditService', '$log'];

  return {
    DashboardCtrl: DashboardCtrl
  };

});
