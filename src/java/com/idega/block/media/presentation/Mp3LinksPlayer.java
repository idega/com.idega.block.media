package com.idega.block.media.presentation;

import com.idega.block.web2.presentation.Sound;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;

/**
 * A Javascript MP3 player that picks up all links to mp3 files in the page and puts them in a playlist
 * @author eiki
 *
 */
public class Mp3LinksPlayer extends Block {

	public void main(IWContext iwc) throws Exception {

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
		playerHTML.append("\t<div class='default'>jsAMP Technology Preview v0.99a.20071010 (Seriously alpha - use at own risk! :))</div> \n");

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

		add(playerHTML.toString());

		add(new Sound());



	}

}
