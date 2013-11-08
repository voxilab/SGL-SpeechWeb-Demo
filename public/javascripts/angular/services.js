'use strict';

/* Services */
angular.module('searchServices', []).
    factory('BinarySearch', function(){
      //Work derived from
      //http://www.nczonline.net/blog/2009/09/01/computer-science-in-javascript-binary-search/
      //Copyright 2009 Nicholas C. Zakas, MIT-Licensed
      return {
        search : function binarySearch(items, value, accessFunction){

          accessFunction = typeof accessFunction !== 'undefined' ? accessFunction : function(b) { return b; };

          var startIndex  = 0,
              stopIndex   = items.length - 1,
              middle      = Math.floor((stopIndex + startIndex)/2);

          var found = function(index, collection, toFind) {
            if (index <= 0 || index >= collection.length -1) {
              return true;
            }

            var value = accessFunction(collection[index]);

            //if 2 following items share the same start: avoids an infinity loop.
            if(toFind==accessFunction(collection[index+1])){
            	return value==toFind;
            }
            else{
            	if(toFind >= value && toFind < accessFunction(collection[index+1])) {
                  return true;
                } else {
                  return false;
                }
            }
            
          }

          //No items or the value is out of range, return not found
          //We return different values to determine if we are searching before or after the items (ie if we click outside of the video, we want to know if it's before or after).
          if(items.length == 0) {
            return -1;
          }
          else if(value < accessFunction(items[0])){
            return -2;
          }
          else if(value > accessFunction(items[items.length - 1])){
            return -3;
          }

          while(!found(middle, items, value) && startIndex < stopIndex){
              //adjusts search area
              if (value < accessFunction(items[middle])){
                  stopIndex = middle - 1;
              } else if (value > accessFunction(items[middle])){
                  startIndex = middle + 1;
              } else if (value != accessFunction(items[middle])) {
                  //value is not < or > and is not equal: we are comparing apples and bananas
                  return -1;
              }

              //recalculates middle
              middle = Math.floor((stopIndex + startIndex)/2);
          }
          
          return middle;
        }
    }
});


angular.module('fileServices', ['ngResource'])
	.factory('File', function($resource){
        return $resource('/assets/files/:fileId',{},{get  : {method:'GET', isArray: true}}  );
    })
    .factory('SentenceBoundaries', function(){
        //Extracts the sentence boundaries from the content of a .seg file and returns it in an array.
        return {
        	get : function(segfileContent){
   				var s="";
   				var bounds=new Array();
    			for(var i=0;i<segfileContent.length;i++){
    	 			s=s+segfileContent[i][0];
    			}
    			var reg=new RegExp("\n+", "g");
    			var reg2=new RegExp(" +", "g");
    			var tab=s.split(reg);
    			
    			for(var i=0;i<tab.length;i++){
    	 			var tab2=tab[i].split(reg2);
    	 			bounds.push({"start":parseInt(tab2[1])/100,"end":parseInt(tab2[2])/100});
    			}
    			
    			bounds.sort(function (a, b) {
              		return a.start-b.start;
            	});
    			
    			return bounds;
        	}
        }
});

angular.module('videoServices', [])
    .factory('Video', function(){
        return {
            //Starts the video at a specific time. We give the corresponding transcriptionsData to init its display.
        	startVideo : function(timeStart,transcriptionsData){
        		$('#mediafile')["0"].player.setCurrentTime(timeStart); //The video have to exist with this id
  				transcriptionsData.initDisplay(timeStart);
        	},
        	//Moves the video at the time corresponding to the word(event) we click on.
        	moveVideo : function(eventObject){
        		var time = eventObject.currentTarget.attributes["data-start"].value;
  				$('#mediafile')["0"].player.setCurrentTime(time);
        	},
        	//Moves the video at a specific time.
        	moveVideoTo : function(time){
  				$('#mediafile')["0"].player.setCurrentTime(time);
        	},
        	//Returns current time.
        	giveCurrentTime : function(){
  				return $('#mediafile')["0"].currentTime;
        	},
        	//Returns the duration.
        	giveDuration : function(){
  				return $('#mediafile')["0"].duration;
        	}
        }
	})
	.factory('Time', function(){
		return {
			//Gives a string representation of a time in second. Rounded to the lower value.
			format : function(time) {
  				var hours = Math.floor(time / 3600);
  				var mins  = Math.floor((time % 3600) / 60);
  				var secs  = Math.floor(time % 60);
	
  				if (secs < 10) {
    				secs = "0" + secs;
  				} 
  				if (hours) {
    				if (mins < 10) {
      					mins = "0" + mins;
    				}
    				return hours + ":" + mins + ":" + secs; // hh:mm:ss
 				 } 
  				else {
    				return mins + ":" + secs; // mm:ss
  				}
			}
		}
	
	});
	
angular.module('positionServices', []).
	factory('Position', function(){
        return {
            //Gives the absolute position of the element in the page.
        	getElementPosition : function(element){
  				var top = 0, left = 0;
    
  				while (element) {
   	 				left   += element.offsetLeft;
    				top    += element.offsetTop;
    				element = element.offsetParent;
  				}
  				return { x: left, y: top };
			},
			//Gives the coordinates of the mouse position.
			getMousePosition : function(event) {
  				if (event.pageX) {
    				return {
             			x: event.pageX,
             			y: event.pageY
           			};
  				} else {
    				return { 
             			x: event.clientX + document.body.scrollLeft + document.documentElement.scrollLeft, 
             			y: event.clientY + document.body.scrollTop  + document.documentElement.scrollTop
           			};
  				}
			}
        }
	});
	
