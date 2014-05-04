<?php 

//put the codes in buffer and execute all at same time when flush
ob_start();

include "../php/config.php";
include "../php/secret_password.php";
$tb_name = 'Post';

$POST_ID=$_POST['POST_ID'];
$EMAIL=$_POST['EMAIL'];					
$TITLE=$_POST['TITLE'];
$AUTHOR =$_POST['AUTHOR'];
$ISBN =$_POST['ISBN'];
$PRICEMIN =$_POST['PRICEMIN'];
$PRICEMAX=$_POST['PRICEMAX'];
$ORDER_BY=$_POST['ORDER_BY'];
$security_key = $_POST['security_code'];

$security_key_check = hash('sha256',$searchMarketPlace_password);
/*
$SEARCH_PAGE=$_GET['SEARCH_PAGE'];
$SEARCH_LIMIT=$_GET['SEARCH_LIMIT'];
*/
$ACSorDESC='ASC';

error_log("email: ".$EMAIL."  security Key: ". $security_key_check . "    security_key: ".$security_key, 0);

if($security_key_check!=$security_key){
   echo "security_key does not match";
 header('HTTP/1.0 401 Unauthorized', true, 401);
 exit();
}

// Checks if Pricemin and Pricemax input are null.

function IsNullOrEmptyString($question){
    return (!isset($question) || trim($question)==='');
}
if (IsNullOrEmptyString($POST_ID)){
	$POST_ID='%';
}
else{
	$TITLE='';
	$AUTHOR='';
	$ISBN='%';
	$PRICEMIN='0';
	$PRICEMAX='99999999';
}
if (IsNullOrEmptyString($EMAIL)){
	$EMAIL='%';
}
else{
	$TITLE='';
	$AUTHOR='';
	$ISBN='%';
	$PRICEMIN='0';
	$PRICEMAX='99999999';
}
if (IsNullOrEmptyString($TITLE)){
	$TITLE='';
}
if (IsNullOrEmptyString($AUTHOR)){
	$AUTHOR='';
}
if (IsNullOrEmptyString($PRICEMIN) || $PRICEMIN<0 ){
	$PRICEMIN='0';
}
if (IsNullOrEmptyString($PRICEMAX) || $PRICEMAX<0){
	$PRICEMAX='99999999';
}
if (IsNullOrEmptyString($ISBN)){
	$ISBN='%';
}
/*
if (IsNullOrEmptyString($SEARCH_PAGE) || $SEARCH_PAGE<0){
	$SEARCH_PAGE ='0';
}
if (IsNullOrEmptyString($SEARCH_LIMIT) || $SEARCH_LIMIT<=0){
	//this is default limit
	$SEARCH_LIMIT='10';
}
if($SEARCH_LIMIT >25){
	$SEARCH_LIMIT='25';
}
*/
if(IsNullOrEmptyString($ORDER_BY) || $ORDER_BY == 'POST_DATE'){
$ORDER_BY= 'POST_DATE';
$ACSorDESC = 'DESC';
}

//dollar to cents
$PRICEMIN = $PRICEMIN*100;
$PRICEMAX = $PRICEMAX*100;

// To protect MySQL injection (delete mysql_real_escape_string and stripslashes)
$POST_ID = mysql_real_escape_string(stripslashes($POST_ID));
$TITLE = mysql_real_escape_string(stripslashes($TITLE));
$ISBN = mysql_real_escape_string(stripslashes($ISBN));
$AUTHOR = mysql_real_escape_string(stripslashes($AUTHOR));
$PRICEMIN = mysql_real_escape_string(stripslashes($PRICEMIN));
$PRICEMAX= mysql_real_escape_string(stripslashes($PRICEMAX));
$ORDER_BY = mysql_real_escape_string(stripslashes($ORDER_BY));
$SEARCH_PAGE= mysql_real_escape_string(stripslashes($SEARCH_PAGE));
$SEARCH_LIMIT= mysql_real_escape_string(stripslashes($SEARCH_LIMIT));

$sql="SELECT POST_ID, EMAIL, FB_NAME, TITLE, ISBN, AUTHOR, IMAGE_URL, COMMENT, ADDRESS, TRUNCATE(PRICE/100,2) as PRICE, DATE_FORMAT(POST_DATE, '%Y-%m-%d') as POST_DATE, LATITUDE, LONGITUDE FROM (SELECT * FROM $tb_name WHERE POST_ID LIKE '$POST_ID' AND EMAIL LIKE '$EMAIL' AND TITLE LIKE '%$TITLE%' AND ISBN LIKE '$ISBN' AND AUTHOR LIKE '%$AUTHOR%' AND (PRICE>='$PRICEMIN' AND PRICE<='$PRICEMAX') ORDER BY $ORDER_BY $ACSorDESC LIMIT 200) $tb_name";

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