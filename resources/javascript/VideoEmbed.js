function setVideoService(value, instanceId, id) {
	if(value != null) {
		if(instanceId != null) {
			showElementLoading(id);
			VideoServices.setVideoProperties(value, '', instanceId, window.location.pathname, {
				callback: function(component) {
					reRenderVideoViewer(component, id);
				}
			});
		}
	}
}
function reRenderVideoViewer(component, id) {
	if(component != null) {
		var container = document.getElementById(id);
		if(container != null) {
			var containerParent = container.parentNode;
			removeChildren(containerParent);
			insertNodesToContainer(component.childNodes[0], containerParent);
		}
	}
}
function setVideoId(event, value, instanceId, id) {
	if(isEnterEvent(event)) {
		if(value != null) {
			if(instanceId != null) {
				showElementLoading(id);
				VideoServices.setVideoProperties('', value, instanceId, window.location.pathname, {
					callback: function(component) {
						reRenderVideoViewer(component, id);
					}
				});
			}
		}
	}
}
function setVideoIdOnClick(event, value, instanceId, id) {
		if(value != null) {
			if(instanceId != null) {
				showElementLoading(id);
				VideoServices.setVideoProperties('', value, instanceId, window.location.pathname, {
					callback: function(component) {
						reRenderVideoViewer(component, id);
					}
				});
			}
		}
}
function clearVideoViewer(instanceId, id) {
	showElementLoading(id);
	VideoServices.setVideoProperties('', '', instanceId, window.location.pathname, {
		callback: function(component) {
			reRenderVideoViewer(component, id);
		}
	});
}