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
id='ca_upload_applet'                                                                   \
name='ca_upload_applet'                                                                 \
archive='caUploadApplet.jar'                                                            \
code='wjhk.jupload2.JUploadApplet'                                                      \
width='1'                                                                               \
height='1'                                                                              \
mayscript='true'                                                                        \
alt='The java pugin must be installed.'>                                                \
    <!-- param name='CODE'    value='wjhk.jupload2.JUploadApplet' / -->                 \
    <!-- param name='ARCHIVE' value='wjhk.jupload.jar' / -->                            \
    <!-- param name='type'    value='application/x-java-applet;version=1.5' /  -->      \
    <param name='postURL' value='upload' />                                             \
    <!-- Optionnal, see code comments -->                                               \
    <param name='showLogWindow' value='false' />                                        \
    Java 1.5 or higher plugin required.                                                 \
</applet>                                                                               \
<!--                                                                                    \
codebase='../target/classes'                                                            \
-->                                                                                     \
            ");

            $('#fileupload').fileupload('option', {
                externallyManaged: true
            });

            $('#button_add').live('click',function (e) {
                var ret = $("#ca_upload_applet")[0].selectFiles();
                var files = $.parseJSON(ret);
                if( files.length > 0 ) {
                    $("#fileupload").fileupload("add", {'files': files});
                }
                e.preventDefault();
            });

            $('#button_start').live('click',function (e) {
                $("#ca_upload_applet")[0].startUpload();
                $("#fileupload").fileupload("progress_init");
                var interval = setInterval( function(){
                    var ret = $("#ca_upload_applet")[0].trackProgress();
                    var progress = $.parseJSON(ret);
                    $("#fileupload").fileupload("progress", {'progress': progress});
                    if( $("#ca_upload_applet")[0].isUploadFinished() ) {
                        clearInterval(interval);
                        $("#fileupload").fileupload("progress_destroy");
                    } 
                }, 500 );

                e.preventDefault();
            });

            
        } else {
            $('#button_add').append('<input type="file" name="files[]" multiple>');
        }

    });
})(jQuery);
