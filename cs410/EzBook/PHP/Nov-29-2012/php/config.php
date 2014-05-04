<?
$host="localhost"; //Host name
$username="bosungbo_bosung"; // Mysql username
$password="bosung"; //Mysql password
$db_name = "bosungbo_ezbook"; //Database name

// Connect to server and select database.
mysql_connect("$host", "$username", "$password")or die("cannot connect"); 
mysql_select_db("$db_name")or die("cannot select DB");
?>