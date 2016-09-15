/**
 * User controllers.
 */
define([], function() {
  'use strict';

  var LoginCtrl = function($scope, $location, userService) {
    $scope.credentials = {};

    $scope.login = function(credentials) {
      userService.loginUser(credentials).then(function(/*user*/) {
        $location.path('/dashboard');
      }).catch(function(error){
        alert("登录失败");
      });
    };
  };

  var UserCtrl = function($scope, $location, userService) {
    $scope.result = "";
    $scope.tab = 1;
    $scope.oldPwd = "";
    $scope.pw1 = "";
    $scope.pw2 = "";

    $scope.modifyPassword = function(newPassword) {
      var passObj = {};
      passObj.value = newPassword;
      userService.modifyPassword(passObj).then(function(result) {
        if (result > 0) $scope.result = "密码修改成功";
        else $scope.result = "密码修改失败";
      });
    };

    $scope.selectTab = function(setTab) {
      $scope.tab = setTab;
    };

    $scope.isSelected = function(checkTab) {
      return $scope.tab === checkTab;
    };
  };

  LoginCtrl.$inject = ['$scope', '$location', 'userService'];
  UserCtrl.$inject = ['$scope', '$location', 'userService'];

  return {
    LoginCtrl: LoginCtrl,
    UserCtrl: UserCtrl
  };

});
