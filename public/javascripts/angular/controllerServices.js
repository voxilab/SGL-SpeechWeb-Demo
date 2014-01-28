'use strict';

angular.module('controllerServices', []).
	factory('Controller', function(Video, File, Restangular, SentenceBoundaries, TranscriptionsData, SpeakerBar){
        return {
        	initializeTranscriptionComparisonCtrl : function(scope,globalStep,colors){
                scope.startVideo=function(timeStart){
                	Video.startVideo(timeStart,scope.transcriptionsData);
                }
                
                scope.moveVideo=function(eventObject){
                	Video.moveVideo(eventObject);
                }
                
                var refresh=function(){
                	scope.$apply();
                }
                
                $('#calculationOverAlert').hide();
                $('#outTranscriptionAlert').hide();
                $('#progressBar').hide();
                
                //Get the transcription from the server: if the transcription enhanced with the dtw exist, we use it. Otherwise we make the calculation.
                File.get({fileId: 'enhanced-transcription.json'}, 
                    function(transcriptions) {
						scope.transcriptionsData=new TranscriptionsData.instance(transcriptions,globalStep);
					
						scope.speakerBar=new SpeakerBar.instance(scope.transcriptionsData.fullTranscription[0],0,colors);
						scope.speakerBar.initialize();
					
						//We make sure that the nextWordToDisplay value is correct
						scope.startVideo(scope.transcriptionsData.fullTranscription[0].content[0].start);
                    },
                    function(){
                      Restangular.one('files.json', 2).getList('words').then(function(transcriptions) {
                	      scope.transcriptionsData=new TranscriptionsData.instance(transcriptions,globalStep);
                	      scope.transcriptionsData.adjustTranscriptions();
                	      
                	      scope.speakerBar=new SpeakerBar.instance(scope.transcriptionsData.fullTranscription[0],0,colors);
            			  scope.speakerBar.initialize();
                	      
                          File.get({fileId: 'sentence_bounds.seg'}, 
                          	function(data) {
							  //We get the bounds of the sentences we will use for the DTWs
							  scope.sentenceBounds=SentenceBoundaries.get(data);	
							  scope.transcriptionsData.updateTranscriptionsWithDtw(scope.sentenceBounds,refresh);
							  //We make sure that the nextWordToDisplay value is correct
							  scope.startVideo(scope.transcriptionsData.fullTranscription[0].content[0].start);
                          	}
                          );
                      });
                    }
                );

                //Non angular events
                $("#mediafile").on("timeupdate", function (e) {
                  scope.$apply( function() {
                    scope.transcriptionsData.timeUpdateDisplay(e.target.currentTime);
                    scope.speakerBar.timeUpdate(e.target.currentTime);
                  });
                });

                $("#mediafile").on("seeking", function (e) {
                  scope.$apply( function() {
                    scope.transcriptionsData.seekingUpdateDisplay(e.target.currentTime);
                  });
                });
			},
			initializeDiarizationCtrl : function(scope,globalStep,transcriptionNum,colors) {
            	scope.startVideo=function(timeStart){
            		Video.startVideo(timeStart,scope.transcriptionsData);
            	}
            
            	scope.moveVideo=function(eventObject){
            		Video.moveVideo(eventObject);
            	}
            
                $('#outTranscriptionAlert').hide();
                
            	//Get the transcription from the server
            	Restangular.one('files.json', 1).getList('words').then(function(transcriptions) {
            		scope.transcriptionsData=new TranscriptionsData.instance(transcriptions,globalStep);
            		scope.transcriptionsData.adjustTranscriptions();
            		scope.speakerBar=new SpeakerBar.instance(scope.transcriptionsData.fullTranscription[transcriptionNum],transcriptionNum,colors);
            		scope.speakerBar.initialize();
            		//We make sure that the nextWordToDisplay value is correct
              		scope.startVideo(scope.transcriptionsData.fullTranscription[transcriptionNum].content[transcriptionNum].start);
            	});

            	//Non angular events
            	$("#mediafile").on("timeupdate", function (e) {
              		scope.$apply( function() {
                		scope.transcriptionsData.timeUpdateDisplay(e.target.currentTime);
                		scope.speakerBar.timeUpdate(e.target.currentTime);
              		});
            	});

            	$("#mediafile").on("seeking", function (e) {
              		scope.$apply( function() {
                		scope.transcriptionsData.seekingUpdateDisplay(e.target.currentTime);
              		});
            	});
			}
    	}
	});

