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
		  'link_comment' => 'Încărcaţi fişiere în galerie, cu ajutorul unui applet',
		  'perm_denied' => 'Nu aveţi permisiunea de a efectua această operaţie.<BR><BR>Dacă nu eşti conectat, vă rugăm să vă <a href="login.php' . ( isset($_SERVER['PHP_SELF']) ? '?referer=' . $_SERVER['PHP_SELF'] : '') . '"> autentificaţi </ a> prima dată',
		  'select_album' => 'Vă rugăm, alegeţi un album, în cazul în care doriţi să încărcaţi imagini',
		  'button_update_album' => 'Actualizare album',
		  'button_create_album' => 'Creare album',
		  'success' => 'Acţiune cu succes !',
		  'error_select_album' => 'Vă rugăm, alegeţi un album prima dată',
		  'error_album_name' => 'Vă rugăm să daţi un nume albumului.',
		  'error_album_already_exists' => 'Aveţi deja un album cu acest nume.<BR><BR>Vă rugăm să faceţi clic pe <I>Înapoi</I> butonul de Navigator-ul tău, pentru a da un alt titlu albumul nou.',
		  'album_name' => 'Nume album',
		  'album_presentation' => 'Trebuie să selectaţi un album aici. Imaginile pe care le trimiteţi la server vor fi stocate în acest album. <BR> Dacă nu ai nici un album, sau lista de albume este goală. Utilizaţi butonul \'Creaţi\' pentru a crea primul album.',
		  'album_description' => 'Descriere album',
		  'add_pictures' => 'Adaugă poze la albumul selectat',
		  'max_upload_size' => 'Dimensiunea maximă pentru o imagine este $1 KB',
		  'upload_presentation' => 'În cazul în care se refuză categoric pentru a afişa appletul pătrat de mai jos, şi navigatorul indică faptul că există erori pe această pagină, o idee bună ar fi să instalaţi plugin-ul Java Runtime.<a href="http://java.sun.com/" target="_blank">Java&#8482;</a><BR>După ce, încărcaţi este foarte simplu! Faceţi clic pe <B>Răsfoire</B> pentru a selecta fişiere sau utilizaţi glisare \'n\' de la explorator, apoi faceţi clic pe <B>Încărcare</B> pentru a trimite imagini pe server.'
		. "<BR>Pentru a utiliza <U>pagina de încărcare veche</U>, <a href='upload.php'>click aici</a>.",
		  'album' => 'Album',
		  //Since 2.1.0
		  'java_not_enabled' => 'Navigator tău nu permite Java. Ai nevoie de Java Applet. Aveţi posibilitatea să descărcaţi cu uşurinţă de la <a href="http:\\java.sun.com\jre\">site-ul web de java</a>',
		  //Since 3.0.0
		  'picture_data_explanation' => 'Faceţi clic pe acest link, şi introduceţi datele în câmpurile de mai jos, dacă doriţi ca acestea să fie aplicate la toate imaginile din încărcare următoare.',
		  'quota_used' => 'În prezent utilizaţi $1 MB ($2%) din $3 MB de volum.',
		  'quota_about_full' => 'Scoateţi nişte poze, sau cereţi de la  admin pentru a mări cota mai mare.',
		  //Since 3.2.0
		  'need_approval' => 'Administradorul Galeriei trebuie să aprobe aceste imagini încărcate, înainte de a le putea vedea pe galerie.'
		)
	);

}