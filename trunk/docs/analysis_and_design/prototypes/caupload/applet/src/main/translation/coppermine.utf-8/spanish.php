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
// Thanks to jesusangelwork: jesusangelwork[at]users.sourceforge[dot]net
//  And GecKoTDF (on http://forum.coppermine-gallery.net)
// ------------------------------------------------------------------------- //
 
if (defined('JUPLOAD_PHP')) {
	$lang_jupload_php = array_merge (
		$lang_jupload_php,
		array(
		  'perm_denied' => 'Usted no tiene permisos para realizar esta operación.<BR><BR>Si no está conectado, por favor <a href="$1">conéctese</a> antes',
		  'select_album' => 'Por favor, seleccione el álbum donde desea subir las imágenes',
		  'button_update_album' => 'Actualizar álbum',
		  'button_create_album' => 'Crear álbum',
		  'success' => 'ˇOperación exitosa!',
		  'error_select_album' => 'Por favor, seleccione antes un álbum',
		  'error_album_name' => 'Por favor, escriba un nombre para el álbum.',
		  'error_album_already_exists' => 'Ya tiene un álbum con este nombre.<BR><BR>Pulse, por favor, sobre el botón <I>Atrás</I> de su navegador para escribir otro título para su nuevo álbum.',
		  'album_name' => 'Nombre del álbum',
		  'album_presentation' => 'Debe seleccionar un álbum. Las imágenes que envíe al servidor serán almacenadas en este álbum. <BR>Si no tiene ningún álbum, la lista de álbumes aparecerá vacía. Utilice el botón \'Crear\' para crear su primer álbum.',
		  'album_description' => 'Descripción del álbum',
		  'add_pictures' => 'Ańadir imágenes al álbum seleccionado',
		  'max_upload_size' => 'El tamańo máximo para una imagen es $1 Ko',
		  'upload_presentation' => 'Si el applet no aparece en el cuadrado que hay debajo y el navegador indica que se han producido errores en esta página, sería buena idea que instalase el plugin de JAVA.<BR>Después de eso, subir las imágenes será muy sencillo. Pulse sobre <B>Examinar</B> para seleccionar ficheros o utilice arrastrar y soltar desde el explorador de ficheros, después pulse sobre <B>Subir</B> para enviar las imágenes al servidor.'
		  	. "<BR>Para usar la <U>antigua página de carga</U>, <a href='upload.php'>pulse aquí</a>.",
		  'album' => 'Álbum',
		  //Since 2.1.0
		  'java_not_enabled' => 'Su navegador no soporta Java. Necesita Java. Puede bajarlo desde <a href="http:\\www.java.com/es/download/">el sitio de java.</a>',
		  //Since 3.0.0
		  'picture_data_explanation' => 'Haga click en este link, y ponga la informacion en los campos de abajo, Si quiere que se apliquen en proximas subidas.',
		  'quota_used' => 'Actualmente usa $1 MB ($2%) de sus $3 MB de almacenamiento.',
		  'quota_about_full' => 'Sin espacio, borre algunas imagenes, o pidale al administrador mas espacio.',
		  //Since 3.2.0
		  'need_approval' => 'El administrador debe autorizar estas imagenes, antes de poder ser vistas en la galeria.'
		)
	);
}
