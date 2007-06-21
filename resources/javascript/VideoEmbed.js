function setVideoService(value, instanceId, id) {
	if(value != null) {
		if(instanceId != null) {
			VideoServices.setVideoProperties(value, '', instanceId, {
				callback: function(component) {
					reRenderVideoViewer(component, id);
				}
			});
		}
	}
}
function reRenderVideoViewer(component, id) {
	if(component != null) {
		console.log(component);
		var container = document.getElementById(id);
		if(container != null) {
			removeChildren(container);
			insertNodesToContainer(component, container);
		}
	}
}
function setVideoId(event, value, instanceId, id) {
	if(isEnterEvent(event)) {
		if(value != null) {
			if(instanceId != null) {
				VideoServices.setVideoProperties('', value, instanceId, {
					callback: function(component) {
						reRenderVideoViewer(component, id);
					}
				});
			}
		}
	}
}
function clearVideoViewer(instanceId, id) {
	VideoServices.setVideoProperties('', '', instanceId, {
		callback: function(component) {
			reRenderVideoViewer(component, id);
		}
	});
}