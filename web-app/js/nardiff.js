/**
 * Created by kkoning on 2/17/15.
 */

(function() {
    var app = angular.module('nardiff', []);
    app.controller('WorkflowController', ['$scope', '$interval', function($scope, $interval) {
        $scope.stage = 1;
        $scope.timeRemaining = 5;

        this.request_id = null;
        this.parent_story_id = null;
        this.distractorAnswer = null;
        this.story = null;
        this.age = null;
        this.gender = null;
        this.education = null;
        this.turker_id = null;

        var removeImage;

        removeImage = function() {
            var img_div = document.getElementById("img-div");
            var to_remove = document.getElementById("toremove");
            img_div.removeChild(to_remove);
        };

        this.startTimer = function() {
            deductTime = $interval(function() {
                $scope.timeRemaining--;
                if ($scope.timeRemaining === 0) {
                    $scope.stage = 3;
                    removeImage();
                }

            }, 1000);
        }




    }]);



})();
