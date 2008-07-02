package com.idega.block.media.presentation;

import java.util.Collection;
import java.util.Iterator;

import org.apache.myfaces.renderkit.html.util.AddResource;
import org.apache.myfaces.renderkit.html.util.AddResourceFactory;

import com.idega.block.media.business.MediaConstants;
import com.idega.block.web2.business.Web2Business;
import com.idega.block.web2.presentation.Sound;
import com.idega.business.IBOLookup;
import com.idega.idegaweb.IWBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.slide.business.IWSlideService;
import com.idega.util.expression.ELUtil;

/**
 * A Javascript MP3 player that picks up all links to mp3 files in the page and puts them in a playlist that you can...well...play!
 * Based on jsAMP javascript mp3 player and SoundManager2,   <a href="http://www.schillmania.com/projects/soundmanager2/">See here.</a>
 * @author <a href="mailto:eiki@idega.is">Eirikur Hrafnsson</a> 
 *
 */
public class Mp3LinksPlayer extends Block {

	private boolean useDarkSkin = false;
	private String startingText = "jsAMP Technology Preview v0.99a.20071010";
	private String mp3FolderPath = null;

	public void main(IWContext iwc) throws Exception {
		
		//TODO ADD explorer PNG hack
		
		
		Web2Business web2 = ELUtil.getInstance().getBean(Web2Business.class);
		IWBundle iwb =  this.getBundle(iwc);
		AddResource resourceAdder = AddResourceFactory.getInstance(iwc);
		resourceAdder.addJavaScriptAtPosition(iwc, AddResource.HEADER_BEGIN,web2.getBundleURIToSoundManager2Lib());
		resourceAdder.addJavaScriptAtPosition(iwc, AddResource.HEADER_BEGIN,iwb.getVirtualPathWithFileNameString("javascript/jsAMP.js"));
		if(useDarkSkin){
			resourceAdder.addStyleSheet(iwc, AddResource.HEADER_BEGIN,  this.getBundle(iwc).getVirtualPathWithFileNameString("style/jsAMP-dark.css"));
		}
		else{
			resourceAdder.addStyleSheet(iwc, AddResource.HEADER_BEGIN,  this.getBundle(iwc).getVirtualPathWithFileNameString("style/jsAMP-light.css"));
		}
	

		StringBuffer playerHTML = new StringBuffer();
		playerHTML.append("<div id='player-template' class='sm2player'> \n");

		playerHTML.append("\t<!-- player UI (bar) --> \n");

		playerHTML.append("\t<div class='ui'> \n");

		playerHTML.append("\t<div class='left'> \n");
		playerHTML.append("\t\t<a href='#' title='Pause/Play' onclick='soundPlayer.togglePause();return false' class='trigger pauseplay'><span></span></a> \n");
		playerHTML.append("\t</div> \n");

		playerHTML.append("\t<div class='mid'> \n");

		playerHTML.append("\t<div class='progress'></div> \n");
		playerHTML.append("\t<div class='info'><span class='caption text'>%{artist} - %{title} [%{album}], (%{year}) (%{time})</span></div> \n");
		playerHTML.append("\t<div class='default'>").append(startingText).append("</div> \n");

		playerHTML.append("\t<div class='seek'>Seek to %{time1} of %{time2} (%{percent}%)</div> \n");
		playerHTML.append("\t<div class='divider'>&nbsp;&nbsp;---&nbsp;&nbsp;</div> \n");
		playerHTML.append("\t<a href='#' title='' class='slider'></a> \n");
		playerHTML.append("\t</div> \n");

		playerHTML.append("\t<div class='right'> \n");
		playerHTML.append("\t<div class='divider'></div> \n");
		playerHTML.append("\t<div class='time' title='Time'>0:00</div> \n");

		playerHTML.append("\t<a href='#' title='Previous' class='trigger prev' onclick='soundPlayer.oPlaylist.playPreviousItem();return false'><span></span></a> \n");
		playerHTML.append("\t<a href='#' title='Next' class='trigger next' onclick='soundPlayer.oPlaylist.playNextItem();return false'><span></span></a> \n");
		playerHTML.append("\t<a href='#' title='Shuffle' class='trigger s1 shuffle' onclick='soundPlayer.toggleShuffle();return false'><span></span></a> \n");
		playerHTML.append("\t<a href='#' title='Repeat' class='trigger s2 loop' onclick='soundPlayer.toggleRepeat();return false'><span></span></a> \n");
		playerHTML.append("\t<a href='#' title='Mute' class='trigger s3 mute' onclick='soundPlayer.toggleMute();return false'><span></span></a> \n");
		playerHTML.append("\t<a href='#' title='Volume' onmousedown='soundPlayer.volumeDown(event);return false' onclick='return false' class='trigger s4 volume'><span></span></a> \n");
		playerHTML.append("\t<a href='#' title='Playlist' class='trigger dropdown' onclick='soundPlayer.togglePlaylist();return false'><span></span></a> \n");
		playerHTML.append("\t</div> \n");

		playerHTML.append("\t</div> \n");

		playerHTML.append("\t<div class='sm2playlist-box'> \n");

		playerHTML.append("\t<!-- playlist / controls --> \n");
		playerHTML.append("\t<div id='playlist-template' class='sm2playlist'> \n");

		playerHTML.append("\t<div class='hd'><div class='c'></div></div> \n");
		playerHTML.append("\t<div class='bd'> \n");
		playerHTML.append("\t<ul> \n");
		playerHTML.append("\t<!-- playlist items created, inserted here \n");
		playerHTML.append("\t<li><a href='/path/to/some.mp3'><span>Artist - Song Name, etc.</span></a></li> \n");
		playerHTML.append("\t--> \n");
		playerHTML.append("\t</ul> \n");
		playerHTML.append("\t</div> \n");
		playerHTML.append("\t<div class='ft'><div class='c'></div></div> \n");
		playerHTML.append("\t</div> \n");

		playerHTML.append("<!-- close container --> \n");
		playerHTML.append("</div> \n");

		playerHTML.append("</div> \n");
		
		if(mp3FolderPath!=null){
			IWSlideService slide = (IWSlideService) IBOLookup.getServiceInstance(iwc, IWSlideService.class);
			@SuppressWarnings("unchecked")
			Collection<String> uris = slide.getChildPathsExcludingFoldersAndHiddenFiles(mp3FolderPath);
			if(!uris.isEmpty()){
				playerHTML.append("<div style='display:none'> \n");
				for (Iterator<String> iterator = uris.iterator(); iterator.hasNext();) {
					String uri = iterator.next();
					playerHTML.append("<a href='").append(uri).append("'>").append(uri.substring(uri.lastIndexOf("/")+1)).append("</a> \n");
				}
				playerHTML.append("</div> \n");
			}
			
			
		}
		

		add(playerHTML.toString());

		//needed to initialize
		add(new Sound());



	}
	
	public String getBundleIdentifier() {
		return MediaConstants.IW_BUNDLE_IDENTIFIER;
	}

	public String getStartingText() {
		return startingText;
	}

	public void setStartingText(String startingText) {
		this.startingText = startingText;
	}

	public String getMp3FolderPath() {
		return mp3FolderPath;
	}

	public void setMp3FolderPath(String mp3FolderPath) {
		this.mp3FolderPath = mp3FolderPath;
	}

	public boolean isSetUseDarkSkin() {
		return useDarkSkin;
	}

	public void setToUseDarkSkin(boolean useDarkSkin){
		this.useDarkSkin = useDarkSkin;
	}
	
}
