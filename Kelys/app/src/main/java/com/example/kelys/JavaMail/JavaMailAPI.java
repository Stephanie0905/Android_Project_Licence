package com.example.kelys.JavaMail;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.kelys.Activities.AdminUserReservActivity;
import com.example.kelys.Activities.RegisterActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class JavaMailAPI extends AsyncTask<Void,Void,Void>  {

    //Add those line in dependencies
    //implementation files('libs/activation.jar')
    //implementation files('libs/additionnal.jar')
    //implementation files('libs/mail.jar')

    //Need INTERNET permission

    //Variables
    private Context mContext;
    private Session mSession;

    private String mEmail;
    private String mSubject;
    private String mMessage;
    private String mFileName;

    private ProgressDialog mProgressDialog;

    ////constructeur pour envoi de mail sans pièce jointe
    public JavaMailAPI(Context mContext, String mEmail, String mSubject, String mMessage) {
        this.mContext = mContext;
        this.mEmail = mEmail;
        this.mSubject = mSubject;
        this.mMessage = mMessage;
        this.mFileName = null;
    }

    //constructeur pour envoi de mail avec pièce jointe
    public JavaMailAPI(Context mContext, String mEmail, String mSubject, String mMessage, String fileName) {
        this.mContext = mContext;
        this.mEmail = mEmail;
        this.mSubject = mSubject;
        this.mMessage = mMessage;
        this.mFileName = fileName;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Show progress dialog while sending email
        mProgressDialog = ProgressDialog.show(mContext,"Sending message", "Please wait...",false,false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        //Dismiss progress dialog when message successfully send
        mProgressDialog.dismiss();

        //Show success toast
        Toast.makeText(mContext,"finished ! Email sent",Toast.LENGTH_SHORT).show();



    }

    @Override
    protected Void doInBackground(Void... params) {
        //Creating properties
        Properties props = new Properties();

        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values
        //props.put("mail.smtp.host", "smtp.gmail.com");
        //props.put("mail.smtp.socketFactory.port", "465");
        //props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        //props.put("mail.smtp.auth", "true");
        //props.put("mail.smtp.port", "465");

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        //props.put("mail.smtp.socketFactory.port", "465");
        //props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        //Creating a new session
        mSession = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Credentials.EMAIL, Credentials.PASSWORD);
                    }
                });

        try {
            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(mSession);

            //Setting sender address
            mm.setFrom(new InternetAddress(Credentials.EMAIL));
            //Adding receiver
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(mEmail));
            //Adding subject
            mm.setSubject(mSubject,"UTF-8");



            Multipart multipart = new MimeMultipart();
            if (this.mFileName != null)
            {


                BodyPart messageBodyPart = new MimeBodyPart();
                DataSource fds = new FileDataSource(this.mFileName);
                messageBodyPart.setDataHandler(new DataHandler(fds));
                messageBodyPart.setFileName(this.mFileName);
                multipart.addBodyPart(messageBodyPart);

                // le message
                messageBodyPart = new MimeBodyPart();
                messageBodyPart.setContent(mMessage,"text/html; charset=utf-8");
                multipart.addBodyPart(messageBodyPart);


                //mm.setText(mMessage);
                mm.setContent(multipart,"text/html; charset=utf-8");
                //Sending email
                Transport.send(mm);
                //messageBodyPart.setHeader("Content-ID","appicon");
            }

            else
                {

                    /* test
                    File logoFile = new File(this.mContext.getExternalFilesDir(null).getAbsolutePath()+"/logo/"+"appicon.png");
                    if (!logoFile.exists())
                    {
                        copyFile(this.mContext);
                    }
                     test */


                    BodyPart messageBodyPart = new MimeBodyPart();
                    messageBodyPart.setContent(mMessage,"text/html; charset=utf-8");
                    multipart.addBodyPart(messageBodyPart);

                    messageBodyPart = new MimeBodyPart();

                    //DataSource fds = new FileDataSource(new File("file:///android_asset/images/appicon.png"));

                    //DataSource fds = new FileDataSource(logoFile);
                    //messageBodyPart.setDataHandler(new DataHandler(fds));
                    //messageBodyPart.setHeader("Content-ID", "<image>");
                    //multipart.addBodyPart(messageBodyPart);

                    mm.setContent(multipart,"text/html; charset=utf-8");
                    //mm.setContent(mMessage,"text/html; charset=utf-8");


                    //Sending email
                    Transport.send(mm, mm.getAllRecipients());
                }





//            BodyPart messageBodyPart = new MimeBodyPart();
//
//            messageBodyPart.setText(message);
//
//            Multipart multipart = new MimeMultipart();
//
//            multipart.addBodyPart(messageBodyPart);
//
//            messageBodyPart = new MimeBodyPart();
//
//            DataSource source = new FileDataSource(filePath);
//
//            messageBodyPart.setDataHandler(new DataHandler(source));
//
//            messageBodyPart.setFileName(filePath);
//
//            multipart.addBodyPart(messageBodyPart);

//            mm.setContent(multipart);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void copyFile(Context context) {
        AssetManager assetManager = context.getAssets();
        try {
            InputStream in = assetManager.open("appicon.png");
            OutputStream out = new FileOutputStream(context.getExternalFilesDir(null).getAbsolutePath()+"/logo/"+"appicon.png");
            byte[] buffer = new byte[1024];
            int read = in.read(buffer);
            while (read != -1) {
                out.write(buffer, 0, read);
                read = in.read(buffer);
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }





}
