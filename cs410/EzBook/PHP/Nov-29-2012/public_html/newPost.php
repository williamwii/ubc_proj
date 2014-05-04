<?php 

//put the codes in buffer and execute all at same time when flush
ob_start();

include "../php/config.php";
include "../php/secret_password.php";
$tb_name = 'Post';

$EMAIL=$_POST['EMAIL'];
$FB_NAME=$_POST['FB_NAME'];
$TITLE =$_POST['TITLE'];
$ISBN =$_POST['ISBN'];
$AUTHOR =$_POST['AUTHOR'];
$IMAGE_URL =$_POST['IMAGE_URL'];
$COMMENT =$_POST['COMMENT'];
$ADDRESS =$_POST['ADDRESS'];
$PRICE =$_POST['PRICE'];
$LATITUDE=$_POST['LATITUDE'];
$LONGITUDE=$_POST['LONGITUDE'];
$security_key = $_POST['security_code'];

//error_log($EMAIL, 0);
//error_log($TITLE, 0);
//error_log($ISBN, 0);
//error_log($AUTHOR, 0);
//error_log($IMAGE_URL, 0);
//error_log($COMMENT, 0);
//error_log($ADDRESS, 0);
//error_log($PRICE, 0);

$security_key_check = hash('sha256',$EMAIL.$post_password);

//error_log("email: ".$EMAIL."  security Key: ". $security_key_check . "    security_key: ".$security_key, 0);
error_log("latitude: ".$LATITUDE."     longitude: ". $LONGITUDE,0);

if($security_key_check!=$security_key){
   echo "security_key does not match";
 header('HTTP/1.0 401 Unauthorized', true, 401);
 exit();
}

//there is no point of having posts without email , title, price, and comment
if(!isset($EMAIL) || !isset($TITLE ) || !isset($PRICE )|| !isset($COMMENT)){
header('HTTP/1.0 412 Precondition Failed', true, 412);
die('Bad data');
}

// To protect MySQL injection (delete mysql_real_escape_string and stripslashes)
$EMAIL= mysql_real_escape_string(stripslashes($EMAIL));
$FB_NAME= mysql_real_escape_string(stripslashes($FB_NAME));
$TITLE = mysql_real_escape_string(stripslashes($TITLE));
$ISBN = mysql_real_escape_string(stripslashes($ISBN));
$AUTHOR = mysql_real_escape_string(stripslashes($AUTHOR));
$IMAGE_URL = mysql_real_escape_string(stripslashes($IMAGE_URL));
$COMMENT = mysql_real_escape_string(stripslashes($COMMENT));
$ADDRESS = mysql_real_escape_string(stripslashes($ADDRESS));
$PRICE = mysql_real_escape_string(stripslashes($PRICE));
$LATITUDE= mysql_real_escape_string(stripslashes($LATITUDE));
$LONGITUDE= mysql_real_escape_string(stripslashes($LONGITUDE));

$PRICE = $PRICE*100;

$sql="INSERT INTO $tb_name(EMAIL, FB_NAME, TITLE, ISBN, AUTHOR, IMAGE_URL, COMMENT, ADDRESS, PRICE, LATITUDE, LONGITUDE) VALUES ('$EMAIL', '$FB_NAME', '$TITLE', '$ISBN', '$AUTHOR', '$IMAGE_URL', '$COMMENT', '$ADDRESS', '$PRICE', '$LATITUDE', '$LONGITUDE')";

$result=mysql_query($sql);

if($result==TRUE){
//echo "successfully inserted into table!";}
echo mysql_insert_id();
}
else{
echo "error in inserting into table!";
header('HTTP/1.0 500 Internal Server Error', true, 500);}
ob_end_flush();
exit();
?>