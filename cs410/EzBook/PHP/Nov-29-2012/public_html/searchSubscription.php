<?php 

//put the codes in buffer and execute all at same time when flush
ob_start();

include "../php/config.php";
include "../php/secret_password.php";

$tb_name = 'SUBSCRIPTION';
					
$ISBN =$_POST['ISBN'];
$PRICE = $_POST['PRICE'];
$security_key= $_POST['security_code'];

$security_key_check = hash('sha256',$ISBN.$searchSubscription_password);

error_log("ISBN: ".$ISBN."  security Key: ". $security_key_check . "    security_key: ".$security_key, 0);

if($security_key_check!=$security_key){
//   echo "security_key does not match";
 header('HTTP/1.0 401 Unauthorized', true, 401);
die();
}

function IsNullOrEmptyString($question){
    return (!isset($question) || trim($question)==='');
}

if (IsNullOrEmptyString($ISBN)){
header('HTTP/1.0 412 Precondition Failed', true, 412);
	die('bad data');
}

// To protect MySQL injection (delete mysql_real_escape_string and stripslashes)
$ISBN = mysql_real_escape_string(stripslashes($ISBN));
$PRICE= mysql_real_escape_string(stripslashes($PRICE));

//dollar to cents
$PRICE = $PRICE * 100;

$sql="SELECT EMAIL FROM $tb_name WHERE ISBN='$ISBN' AND (PRICE_MIN<='$PRICE' AND PRICE_MAX>='$PRICE')";

$result=mysql_query($sql);

if($result==FALSE){
//echo "error searching in the table!";
header('HTTP/1.0 500 Internal Server Error', true, 500);
exit();
}

$list = array();
while($row = mysql_fetch_array($result, MYSQL_ASSOC)){
	$list[] = $row;
}
echo json_encode($list);
ob_end_flush();
exit();
?>