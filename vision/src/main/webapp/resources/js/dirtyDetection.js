
var dirtyData = false;   // used to allow manual setting of the dirty condition
var fieldsToCheck = [];  // holds ids of fields the page has registered to manually check
var buttonsToIgnore = [];  // holds ids of buttons the page has registered as ones to ignore
						   //     even though they will cause a page reload


$(document).ready(function() {
	
	window.onbeforeunload = function() {
		var ae = document.activeElement;
		if( (ae.className == "ui-icon ui-icon-arrow-4-diag") ||    // menu expand - ignore
			(ae.className == "ui-icon ui-icon-triangle-1-w") ||    // menu collapse - ignore
			(ignoreThisButton(ae.id))) {
		}
		else {
			if(dirtyCheck()) {
				ajaxStatusHide();
				$("input.ui-submit-param").remove();
				return "You have unsaved data and will lose it";
			}
		}
	};

});


function saveDataForDirtyDetection() {
	$(".trackDirtyValue").each(function() {
		$(this).data('initial_value', $(this).val() )
	});			

	$(".trackDirtyCheckbox").each(function() {
		$(this).data('initial_value', $(this).prop("checked") );
	});			

	$(".trackDirtyChildValues").each(function() {
		$(this).find("input").each(function(){
			$(this).data('initial_value', $(this).val() );
		});
	});			

	$(".trackDirtyRadio").each(function() {
		$(this).find(":input").each(function(){
			$(this).data('initial_value', $(this).prop("checked") );
		});
	});			

	$(".trackDirtySelect").each(function() {
		$(this).find("select").each(function(){
			$(this).data('initial_value', $(this).val() );
		});
	});			

	saveManualFieldData();
	
}


function dirtyCheck() {
	var dirty = false;
	if(dirtyData == true) {    // has something manually set the dirty condition?
		return true;
	}
	
	$(".trackDirtyValue").each(function() { 
		if ($(this).val() != $(this).data('initial_value')) {
			dirty = true;
          }
	});			

	$(".trackDirtyCheckbox").each(function() {
		if ($(this).prop("checked") != $(this).data('initial_value')) {
			dirty = true;
          }
	});			

	$(".trackDirtyChildValues").each(function() { 
		$(this).find("input").each(function(){
			if ($(this).val() != $(this).data('initial_value')) {
				dirty = true;
	          }
		});
	});			

	$(".trackDirtyRadio").each(function() {
		$(this).find(":input").each(function(){
			if ($(this).prop("checked") != $(this).data('initial_value')) {
				dirty = true;
	          }
		});
	});			

	$(".trackDirtySelect").each(function() {
		$(this).find("select").each(function(){
			if ($(this).val() != $(this).data('initial_value')) {
				dirty = true;
	          }
		});
	});			
	
	if(dirtyManualFields()) {
		dirty = true;
	}
	
	return dirty;
}

// allows page to set dirty status explicitly (usually because dialog)
function setDirtyData(dirty) {   
	dirtyData = dirty;
}

// allows page to use bean to hold a dirty status through a page reload
function setDirtyDataById(elementId) {
	if(document.getElementById(elementId).value == "true") {
		dirtyData = true;
	} else {
		dirtyData = false;
	}
}

// allows page to register a field to check (mostly hidden fields)
function registerManualField(id) {
	fieldsToCheck.push(id);
}

// allows page to register a button to ignore (mostly save)
function registerIgnoreButton(id) {
	buttonsToIgnore.push(id);
}

// saves initial value of manual fields         
function saveManualFieldData() {
	var len = fieldsToCheck.length;
	var element;
	for (var i=0; i<len; ++i) {
		id = fieldsToCheck[i];
		element = '#' + id;
		$(element).data('initial_value', $(element).val());
	}
}

// will check for manual fields that are dirty
function dirtyManualFields() {
	var len = fieldsToCheck.length;
	var element;
	for (var i=0; i<len; ++i) {
		id = fieldsToCheck[i];
		element = '#' + id;
		if($(element).val() != $(element).data('initial_value')) {
			return true;
		}
	}
	return false;
}

// checks if the passed in button is one that should be ignored
function ignoreThisButton(id) {
	var len = buttonsToIgnore.length;
	for (var i=0; i<len; ++i) {
		buttonsToIgnore[i];
		if(id == buttonsToIgnore[i]) {
			return true;
		}
	}
	return false;
}