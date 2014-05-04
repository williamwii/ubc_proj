<?php 

//put the codes in buffer and execute all at same time when flush
ob_start();

include "../php/config.php";
include "../php/secret_password.php";

$tb_name = 'SUBSCRIPTION';

$EMAIL=$_POST['EMAIL'];
$ISBN=$_POST['ISBN'];
$TITLE=$_POST['TITLE'];
$AUTHOR=$_POST['AUTHOR'];
$IMAGE_URL=$_POST['IMAGE_URL'];
$PRICE_MIN=$_POST['PRICE_MIN'];
$PRICE_MAX=$_POST['PRICE_MAX'];
$security_key = $_POST['security_code'];
$security_key_check = hash('sha256',$EMAIL.$subscription_password);

//error_log("POST_ID: ".$POST_ID."  security Key: ". $security_key_check, 0);

if($security_key_check!=$security_key){
   echo "security_key does not match";
 header('HTTP/1.0 401 Unauthorized', true, 401);
 exit();
}

//there is no point of having subscribe without email and ISBN
if(!isset($EMAIL) || !isset($ISBN)){
header('HTTP/1.0 412 Precondition Failed', true, 412);
die('Bad data');
}
if($PRICE_MIN<0){
$PRICE_MIN=0;
}
if($PRICE_MAX<=0){
$PRICE_MAX=99999999;
}

$UNSUBSCRIBE_CODE = hash('sha256',$EMAIL.$ISBN);
// To protect MySQL injection (delete mysql_real_escape_string and stripslashes)
$EMAIL= mysql_real_escape_string(stripslashes($EMAIL));
$ISBN= mysql_real_escape_string(stripslashes($ISBN));
$TITLE= mysql_real_escape_string(stripslashes($TITLE));
$AUTHOR= mysql_real_escape_string(stripslashes($AUTHOR));
$IMAGE_URL= mysql_real_escape_string(stripslashes($IMAGE_URL));
$PRICE_MIN= mysql_real_escape_string(stripslashes($PRICE_MIN));
$PRICE_MAX= mysql_real_escape_string(stripslashes($PRICE_MAX));
$UNSUBSCRIBE_CODE= mysql_real_escape_string(stripslashes($UNSUBSCRIBE_CODE));

//dollar to cents
$PRICE_MIN = $PRICE_MIN*100;
$PRICE_MAX = $PRICE_MAX*100;

$sql="INSERT INTO $tb_name(EMAIL, ISBN, TITLE, AUTHOR, IMAGE_URL, PRICE_MIN, PRICE_MAX, UNSUBSCRIBE_CODE) VALUES ('$EMAIL', '$ISBN', '$TITLE', '$AUTHOR', '$IMAGE_URL', '$PRICE_MIN', '$PRICE_MAX', '$UNSUBSCRIBE_CODE')";

$result=mysql_query($sql);

if($result==TRUE){
echo "successfully inserted into table!";}
else{
echo "error in inserting into table!";
header('HTTP/1.0 500 Internal Server Error', true, 500);}
ob_end_flush();
exit();
?>