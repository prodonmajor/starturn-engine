<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>Create Members</title>

        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>

        <link href='http://fonts.googleapis.com/css?family=Roboto' rel='stylesheet' type='text/css'/>

        <!-- use the font -->
        <style>
            body {
                font-family: 'Roboto', sans-serif, century gothic;

            }
            #report {
                font-family: "Trebuchet MS", Arial, Helvetica, sans-serif;
                border-collapse: collapse;
                width: 100%;
            }

            #report td, #report th {
                border: 1px solid #ddd;
                padding: 8px;
            }

            #report tr:nth-child(even){background-color: #f2f2f2;}

            #report tr:hover {background-color: #ddd;}

            #report th {
                padding-top: 12px;
                padding-bottom: 12px;
                text-align: left;
                background-color: red;
                color: white;
            }
        </style>
    </head>
    <body style="margin: 0; padding: 0;">
        <img src="https://myeasycoop.com/imgsrv/easycoopbanner.png" alt="https://myeasycoop.com" style="display: block;padding: 40px 0 30px 0" />
        <br />
        <p>Hello ${firstName} ${lastName},</p>
        <p>${body}</p>
        <div>
            <table id="report" class="report" border="2">
                <tr>
                    <th>reference Id</th>
                    <th>Receiver Account Name</th>
                    <th>Receiver Bank Name</th>
                    <th>Receiver Account Number</th>
                    <th>Transaction Amount</th>
                    <th>Charge Amount</th>
                    <th>Total Amount</th>
                    <th>Error Flag</th>
                    <th>message</th>
                </tr>
                <#list report as r>
                <tr>
                    <td>${r.referenceId}</td>
                    <td>${r.receiverAccountName}</td>
                    <td>${r.receiverBankName}</td>
                    <td>${r.receiverAccountNumber}</td>
                    <td>${r.transactionAmount}</td>
                    <td>${r.chargeAmount}</td>
                    <td>${r.transactionAmount}</td>
                    <td>${r.errorFlag}</td>
                    <td>${r.message}</td>
                </tr>
                </#list>
            </table>
        </div>


        <p>Application URL: Click <a href="http://myeasycoop.com/">here</a>  or copy and paste the link address in your browser address bar - <a href="http://myeasycoop.com">http://myeasycoop.com/</a> </p>

        <p>Log in anytime to check your contribution balance, make withdrawals and more.</p>
        <p>For assistance, please contact customer service or technical support, 
            please call +2348139840850 - 4 or email: <a href = "mailto:ITsupport@myeasycoop.com">ITsupport@myeasycoop.com</a> |
            <a href = "mailto:ITBAS@myeasycoop.com">ITBAS@myeasycoop.com</a></p>
        Thank you.
        <br /> 
        <p>${coop_name} <strong>Admin</strong></p>

        <br />
        <footer>
            <p align="justify" style="font-size:12px;">
                This is an automated Transaction Alert Service. You are getting this email because a transaction just occurred on your account. 
                Please DO NOT reply this mail. For further enquiries, please call our Contact Centre on +234813 984 0850-4, or email us at <a href = "mailto:ITsupport@myeasycoop.com">ITsupport@myeasycoop.com</a> |
                <a href = "mailto:ITBAS@myeasycoop.com">ITBAS@myeasycoop.com</a>
            </p>
            <br />
            <hr/>
            <p align="justify" style="font-size:10px;"><small>This is a genuine email from EasyCoop. However, if you receive an email which you are concerned may not have legitimately originated from us,
                    you can report before deleting it. This email message is confidential and for use by the addressee only. 
                    If the message is received by anyone other than the addressee, please delete it from your computer.
                    EasyCoop does not accept responsibility for changes made to this message after it was sent. 
                    Whilst all reasonable care has been taken to avoid the transmission of viruses,
                    it is the responsibility of the recipient to ensure that onward transmission, 
                    opening or use of this message and any attachments will not adversely affect his systems or data. 
                    No responsibility is accepted by EasyCoop in this regard and the recipient should carry out such virus and other checks as considered appropriate.</small></p>
        </footer>
    </body>
</html>
