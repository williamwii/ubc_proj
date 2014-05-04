<?php 

//put the codes in buffer and execute all at same time when flush
ob_start();

include "../php/config.php";
include "../php/secret_password.php";
$tb_name = 'Post';

$POST_ID=$_POST['POST_ID'];
$EMAIL=$_POST['EMAIL'];
$security_key = $_POST['security_code'];

//error_log($EMAIL, 0);
//error_log($TITLE, 0);
//error_log($ISBN, 0);
//error_log($AUTHOR, 0);
//error_log($IMAGE_URL, 0);
//error_log($COMMENT, 0);
//error_log($ADDRESS, 0);
//error_log($PRICE, 0);

$security_key_check = hash('sha256',$EMAIL.$editPost_password);

//error_log("email: ".$EMAIL."  security Key: ". $security_key_check, 0);

if($security_key_check!=$security_key){
   //echo "security_key does not match";
 header('HTTP/1.0 401 Unauthorized', true, 401);
 exit();
}

//there is no point of having posts without email , title, price, and comment
if(!isset($POST_ID) || !isset($EMAIL)){
header('HTTP/1.0 412 Precondition Failed', true, 412);
die('Bad data');
}

// To protect MySQL injection (delete mysql_real_escape_string and stripslashes)
$POST_ID= mysql_real_escape_string(stripslashes($POST_ID));
$EMAIL= mysql_real_escape_string(stripslashes($EMAIL));

$sql="UPDATE $tb_name SET POST_DATE=NOW() WHERE POST_ID='$POST_ID' AND EMAIL='$EMAIL'";

$result=mysql_query($sql);

if($result==TRUE){
echo "successfully renewed post!";}
else{
//echo "error in renewing post table!";
header('HTTP/1.0 500 Internal Server Error', true, 500);}
ob_end_flush();
exit();
?>