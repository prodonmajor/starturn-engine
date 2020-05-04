
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Create Members</title>

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>

    <link href='http://fonts.googleapis.com/css?family=Roboto' rel='stylesheet' type='text/css'>

    <!-- use the font -->
    <style>
        body {
            font-family: 'Roboto', sans-serif;
            font-size: 48px;
        }
    </style>
</head>
<body style="margin: 0; padding: 0;">
 <img src="https://myeasycoop.com/imgsrv/easycoopbanner.png" alt="https://myeasycoop.com" style="display: block;padding: 40px 0 30px 0" />
<br />
 <p>Dear ${firstName} ${lastName},</p>
 <p>Please be informed that a transaction occurred on your cooperative account.</p> 
 <p>Kindly see details below:</p>
 <p>Tnx: ${trans_type} </p>
 <p>Acct: ${username} </p>
 <p>Amt: ${amount} </p>
 <p>Desc: ${trans_id} </p>   
 <p>Bal: ${balance} </p>
 <p>Transaction Date: ${trans_date} </p>

 <p>Cooperative Admin's Comment: ${coop_admin_com}</p>

 <strong>Thank you.</strong> 
 <p>${coop_name} <strong>Admin</strong></p>
 <p><small style="color:#808080">Please note that this is an auto-generated email. You are not expected to reply to this mail</small></p>

<footer>

This is an automated Transaction Alert Service. You are getting this email because a transaction just occurred on your account. 
Please DO NOT reply this mail. For further enquiries, please call our Contact Centre on +234813 984 0850-4, or email us at <a href = "mailto:polcoop@myeasycoop.com"> polcoop@myeasycoop.com</a>
<br />
<hr>
<small>This is a genuine email from EasyCoop. However, if you receive an email which you are concerned may not have legitimately originated from us,
 you can report before deleting it. This email message is confidential and for use by the addressee only. 
 If the message is received by anyone other than the addressee, please delete it from your computer.
 EasyCoop does not accept responsibility for changes made to this message after it was sent. 
 Whilst all reasonable care has been taken to avoid the transmission of viruses,
 it is the responsibility of the recipient to ensure that onward transmission, 
 opening or use of this message and any attachments will not adversely affect his systems or data. 
 No responsibility is accepted by EasyCoop in this regard and the recipient should carry out such virus and other checks as considered appropriate.</small>
</footer>

</body>
</html>