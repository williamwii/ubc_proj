<?php 

//put the codes in buffer and execute all at same time when flush
ob_start();

include "../php/config.php";
include "../php/secret_password.php";
$tb_name = 'Post';

$POST_ID=$_POST['POST_ID'];
$EMAIL=$_POST['EMAIL'];
$security_key = $_POST['security_code'];

$security_key_check = hash('sha256',$POST_ID.$deletePost_password);

//error_log("POST_ID: ".$POST_ID."  security Key: ". $security_key_check, 0);

if($security_key_check!=$security_key){
   echo "security_key does not match";
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

$sql="DELETE FROM $tb_name WHERE POST_ID='$POST_ID' AND EMAIL='$EMAIL' LIMIT 1";

$result=mysql_query($sql);

if($result==TRUE){
echo "successfully edited post table!";}
else{
echo "error in editing post table!";
header('HTTP/1.0 500 Internal Server Error', true, 500);}
ob_end_flush();
exit();
?>