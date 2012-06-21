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
		  'link_title' => 'Бързо качване',
		  'link_comment' => 'Качете файлове лесно и бързо',
		  'perm_denied' => 'Нямате права за тази операция.<BR><BR>Проверете дали сте влезли, ако не натиснете:<a href="$1">Вход</a> first',
		  'select_album' => 'Моля изберете албум където изкате да качите файловете',
		  'button_update_album' => 'Обнови албума',
		  'button_create_album' => 'Създай албум',
		  'success' => 'Операцията успешна !',
		  'error_select_album' => 'Моля изберете албум',
		  'error_album_name' => 'Моля дайте име на албума.',
		  'error_album_already_exists' => 'Вече има албум с това име.<BR><BR>кликнета на <I>Back</I> бутон в браузара, да изберете друго име за албума ви.',
		  'album_name' => 'Име на албума',
		  'album_presentation' => 'Вие трябва да изберете албум. Снимките които сте изпратили към сървара ще се съхраняват в този албум. <BR>Ако нямате никакви албуми, листа с албуми ви е празен. Използвайте \'Създай албум\' бутона за да създадете вашият първи албум.',
		  'album_description' => 'Описание на албума',
		  'add_pictures' => 'Добавете снимки в избрания албум',
		  'max_upload_size' => 'Максималния размер на снимките е $1 KB',
		  'upload_presentation' => 'Ако не ви се появява аплета за качване или има някакви грешки в страницата е добра идея да проверите дали имате инсталиран java runtime plugin.<BR>Качванетто е лесно! Кликнета на <B>Browse</B> изберете файл или издърпайте файла от експлорера, след това кликнета на <B>Upload</B> да изпратите снимката към сървара.'
		. "<BR>За да ползвате  <U>другия вариант</U>, <a href='upload.php'>кликнете тук</a>.",
		  'album' => 'Албум',
		  //Since 2.1.0
		  'java_not_enabled' => 'Вашия браузер не подържа Java. За да работи аплета ви трябва Java. Мовете лесно да я свалите от: <a href="http:\\java.sun.com\jre\">java web site</a>',
		  //Since 3.0.0
		  'picture_data_explanation' => 'Кликнете на този линк, и въведете данни в полетата по-долу, ако искате те да се прилагат към всички снимки в следващото качване.',
		  'quota_used' => 'В момента ползвате $1 MB ($2%) от  $3 MB дисково пространство.',
		  'quota_about_full' => 'изтрийте някоя снимка, или пишете на администратора да ви вдигне квотата.',
		  //Since 3.2.0
		  'need_approval' => 'Администратора трябва да одобри качените снимки, преди да ги видите в галерията.'
		)
	);
}
