/**
 * User package module.
 * Manages all sub-modules so other RequireJS modules only have to import the package.
 */
define(['angular', './routes', './services', './controllers'], function(angular, userRoutes, userService, userController) {
  'use strict';

  var app = angular.module('yourprefix.user', ['ngCookies', 'ngRoute', 'user.routes', 'user.services']);
  app.directive('pwCheck', ['$document', function ($document) {
         return {
           restrict: 'A',
           require: 'ngModel',
           link: function (scope, elem, attrs, ctrl) {
             var firstPassword = '#' + attrs.pwCheck;
             elem.add(firstPassword).on('blur', function () {
               scope.$apply(function () {
                 var v = elem.val()===$(firstPassword).val();
                 ctrl.$setValidity('pwmatch', v);
               });
             });
           }
         };
       }]).directive('oldPwdCheck', ['$document', '$log', 'userService', function($document, $log, userService){
         return {
           restrict: 'A',
           require: 'ngModel',
           link: function(scope, elem, attrs, ctrl) {
             var oldPwd = '#' + attrs.oldPwdCheck;
             elem.add(oldPwd).on('blur', function() {
               scope.$apply(function(){
                 var pass = {};
                 pass.value = elem.val();
                 userService.validatePassword(pass).then(function(result) {
                   $log.info(result);
                   ctrl.$setValidity('oldPwdMatch', result);
                 });
               });
             });
           }
         };
       }]);
  return app;
});
