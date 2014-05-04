<?php 

//put the codes in buffer and execute all at same time when flush
ob_start();

include "../php/config.php";
include "../php/secret_password.php";
$tb_name = 'SUBSCRIPTION';

$UNSUBSCRIBE_CODE=$_POST['UNSUBSCRIBE_CODE'];


error_log("UNSUBSCRIBE_CODE: ". $UNSUBSCRIBE_CODE, 0);

function IsNullOrEmptyString($question){
    return (!isset($question) || trim($question)==='');
}

//there is no point of having unsubscribe without unsubscribe_code
if(IsNullOrEmptyString($UNSUBSCRIBE_CODE)){
header('HTTP/1.0 412 Precondition Failed', true, 412);
die('Bad data');
}

// To protect MySQL injection (delete mysql_real_escape_string and stripslashes)
$UNSUBSCRIBE_CODE= mysql_real_escape_string(stripslashes($UNSUBSCRIBE_CODE));

$sql="DELETE FROM $tb_name WHERE UNSUBSCRIBE_CODE='$UNSUBSCRIBE_CODE' LIMIT 1";

$result=mysql_query($sql);

if($result==TRUE){
echo "successfully unsubscribed!";}
else{
echo "error in unsubscribing!";
header('HTTP/1.0 500 Internal Server Error', true, 500);}
ob_end_flush();
exit();
?>