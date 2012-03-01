/*
 * caUpload fallback
 */
(function($) {
    $(function() {
        // This is the place to hook in an applet for IE, if desired to do so.
        // As per the project requirements, not implementing it currently.
        if (false && $.browser.msie ) {
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
