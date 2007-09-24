<script type="text/javascript">
function validate_required(field,alerttxt)
{
with (field)
{
if (value==null||value=="")
  {alert(alerttxt);return false}
else {return true}
}
}

function validateForm(thisform)
{
with (thisform)
{
if (validate_required(upload,"Validation Error.  You must select a file to upload.")==false)
  {upload.focus();return false}
}
}
</script>