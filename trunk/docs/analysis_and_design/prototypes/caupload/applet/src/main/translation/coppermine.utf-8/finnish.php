<?php
/*
 *******************************************
 plugin JUpload for Coppermine Photo Gallery
 *******************************************

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.
 ********************************************
 $Revision: 185 $
 $Author: etienne_sf $
 $Date: 2008-03-12 20:26:16 +0100 (mer., 12 mars 2008) $
 ********************************************
 *
 * Allows easy upload to the gallery, through a java applet. 
 * 
 * Up to date version of this script can be retrieved with the full JUpload package, here:
 * 
 * http://etienne.sf.free.fr/wiki
 * 
 * Directly here:
 * http://forum.coppermine-gallery.net/index.php/board,100.0.html
 * 
 * Support is available on this forum:
 * http://coppermine-gallery.net/forum/index.php?topic=43432
 * 
 * The applet is published on sourceforge:
 * http://jupload.sourceforge.net
 * 
 */

// ------------------------------------------------------------------------- //
// File jupload.php
// ------------------------------------------------------------------------- //

if (defined('JUPLOAD_PHP')) {
	$lang_jupload_php = array_merge (
		$lang_jupload_php,
		array(
		  'link_title' => 'JUpload',
		  'link_comment' => 'Lähetä tiedosto galleriaan ilman java-applettia.',
		  'perm_denied' => 'Sinulla ei ole oikeutta suorittaa tätä toimintoa.<BR><BR>Jos et ole jo kytkeytynyt, <a href="$1">kirjaudu aluksi sisään</a>',
		  'select_album' => 'Ole hyvä ja valitse albumi, johon haluat lähettää kuvia',
		  'button_update_album' => 'Päivitä albumia',
		  'button_create_album' => 'Luo albumi',
		  'success' => 'Toiminto onnistui!',
		  'error_select_album' => 'Ole hyvä ja valitse ensin albumi',
		  'error_album_name' => 'Anna albumille nimi.',
		  'error_album_already_exists' => 'Olet jo luonut albumin, jolla on annetamasi nimi.<BR><BR>Paina selaimesi paluupainiketta ja valitse toinen nimi uudelle albumillesi.',
		  'album_name' => 'Albumin nimi',
		  'album_presentation' => 'Sinun on valittava albumi tässä. Kuvat, jotka lähetät palvelimelle tallennetaan valittuun albumiin.<BR>Jos sinulla ei ole yhtäkään albumia, albumilista on tyhjä. Käytä \'Luo albumi\'-toimintoa luodaksesi ensimmäisen albumisi.',
		  'album_description' => 'Albumin kuvaus',
		  'add_pictures' => 'Lisää kuvia valittuun albumiin',
		  'max_upload_size' => 'Kuvan maksimikoko on $1 kt',
		  'upload_presentation' => 'Jos allaolevassa neliönmuotoisessa alueessa ei näy java-applettia, ja selaimesi indikoi että sivulla on virheitä, sinulta luultavasti puuttuu javan ajonaikainen ympäristö.<BR>Asennuksen jälkeen tiedostojen lähetys on todella helppoa! Valitse <B>Selaa</B> lähettääksesi tiedostoja tai pudota tiedostoja työpöydältä. Paina sitten <B>Lähetä</B> lähettääksesi kuvat palvelimelle.'
		. "<BR><a href='upload.php'>Siirry käyttämään vanhaa lähetyssivua.</a>",
		  'album' => 'Albumi',
		  //Since 2.1.0
		  'java_not_enabled' => 'Selaimessasi ei ole java-tukea. Tiedostojen lähetys tarvitsee java-tuen. Voit noutaa java-tuen <a href="http:\\java.oracle.com\jre\">javan www-sivustolta</a>.',
		  //Since 3.0.0
		  'picture_data_explanation' => 'Paina tätä linkkiä, ja syötä arvoja allaoleviin kenttiin, jos haluat syöttämäsi tiedot pohjaksi kaikkiin myöhemmin lähettämiisi kuviin.',
		  'quota_used' => 'Olet käyttänyt $1 Mt ($2%) $3 Mt tallennustilastasi.',
		  'quota_about_full' => 'Poista joitakin kuvia, tai pyydä ylläpitoa kasvattamaan tallennustilaasi.',
		  //Since 3.2.0
		  'need_approval' => 'Gallerian ylläpidon on hyväksyttävä nämä lähetetyt kuvat ennen kuin ne ilmestyvät näkyviin.',
		  //Since 4.1.0
		  'see_uploaded_pictures_url' => 'Paina tästä nähdäksesi tällä sivulla olevien kuvien URL:n.',
		)
	);
}
