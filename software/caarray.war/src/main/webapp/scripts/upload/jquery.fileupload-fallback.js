/*
 * caUpload fallback
 */
(function($) {
    $(function() {
        
        if ( $.browser.msie ) {	

/*
			$('#ie_marker').html('Called from Internet Explorer ' + $.browser.version );
			$('#ie_marker').css({
                'display': 'inline-block',
                'color': 'black',
                'font-weight': 'bold',
                'margin': '2em', 
                'padding': '1em',
                'border': '2px solid red'
			});
*/

            $("BODY").append(" \
<applet                                                                                 \
archive='caUploadApplet.jar'                                                            \
code='wjhk.jupload2.JUploadApplet'                                                      \
name='JUpload'                                                                          \
width='1'                                                                               \
height='1'                                                                              \
mayscript='true'                                                                        \
alt='The java pugin must be installed.'>                                                \
    <!-- param name='CODE'    value='wjhk.jupload2.JUploadApplet' / -->                 \
    <!-- param name='ARCHIVE' value='wjhk.jupload.jar' / -->                            \
    <!-- param name='type'    value='application/x-java-applet;version=1.5' /  -->      \
    <param name='postURL' value='upload_dummy.html' />                                  \
    <!-- Optionnal, see code comments -->                                               \
    <param name='showLogWindow' value='false' />                                        \
    Java 1.5 or higher plugin required.                                                 \
</applet>                                                                               \
<!--                                                                                    \
codebase='../target/classes'                                                            \
-->                                                                                     \
            ");


        } else {
            $('#button_add').append('<input type="file" name="files[]" multiple>');
        }

    });
})(jQuery);
