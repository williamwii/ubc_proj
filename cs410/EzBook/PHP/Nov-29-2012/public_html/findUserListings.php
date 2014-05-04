<?php 

//put the codes in buffer and execute all at same time when flush
//ob_start();

include "../php/config.php";
$tb_name = 'Post';
					
$EMAIL=$_GET['EMAIL'];

// Checks if Pricemin and Pricemax input are null.

function IsNullOrEmptyString($question){
    return (!isset($question) || trim($question)==='');
}

if (IsNullOrEmptyString($EMAIL)){
	$EMAIL='';
}

// To protect MySQL injection (delete mysql_real_escape_string and stripslashes)
$EMAIL= mysql_real_escape_string(stripslashes($EMAIL));

$sql="SELECT * FROM $tb_name WHERE EMAIL = '$EMAIL'";

$result=mysql_query($sql);

if($result==FALSE){
echo "error searching in the table!";
header('HTTP/1.0 500 Internal Server Error', true, 500);
exit();
}

$list = array();
while($row = mysql_fetch_array($result, MYSQL_ASSOC)){
	$list[] = $row;
}
echo json_encode($list);
exit();
?>