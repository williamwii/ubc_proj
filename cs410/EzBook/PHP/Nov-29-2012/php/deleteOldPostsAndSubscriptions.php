<?php
include "config.php";
$tb_name = 'Post';

$sql= "DELETE FROM $tb_name WHERE POST_DATE < DATE_SUB(NOW(), INTERVAL 4 MONTH)";
$result=mysql_query($sql);
if($result==TRUE){
error_log( "delete Old Posts successful". date('Y-m-d'));
}

$tb_name = 'SUBSCRIPTION';
$sql= "DELETE FROM $tb_name WHERE SUBMISSION_DATE < DATE_SUB(NOW(), INTERVAL 4 MONTH)";
$result=mysql_query($sql);
if($result==TRUE){
error_log( "delete Old Subscriptions successful". date('Y-m-d'));
}

?>