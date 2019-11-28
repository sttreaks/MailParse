package com.company;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReceiveMail {

    private static void sendMail(String Receiver){
        String from = "sttreakstest2@gmail.com";
        String host = "smtp.gmail.com";
        int port = 465;

        Properties props = new Properties();

        props.put("mail.smtp.host", host);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        Session sssession = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("sttreakstest2@gmail.com", "1234Test1234");
            }
        });

        try {
            Message msg = new MimeMessage(sssession);

            msg.setFrom(new InternetAddress(from));
            InternetAddress[] address = {new InternetAddress(Receiver)};
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject("Test E-Mail through Java");
            msg.setSentDate(new Date());

            msg.setText("Thank u for this latter, I'll answer you when pass my Java HW \n Best wishes, Simon");

            Transport.send(msg);
        }
        catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    private static void checkMail(String host, String user, String password) {
        try {
            Properties properties = new Properties();

            properties.put("mail.pop3.host", host);
            properties.put("mail.pop3.port", "995");
            properties.put("mail.pop3.starttls.enable", "true");
            Session emailSession = Session.getDefaultInstance(properties);

            Store store = emailSession.getStore("pop3s");

            String[] black_list = new String[]{"sttreaks2@gmail.com"};
            String[] white_list = new String[]{"sttreaks@icloud.com"};

            store.connect(host, user, password);

            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            Message[] messages = emailFolder.getMessages();
            System.out.println("messages.length---" + messages.length);

            for (int i = 0, n = messages.length; i < n; i++) {
                Message message = messages[i];
                if (Arrays.asList(black_list).contains(getNameOfAddress(message)))
                {
                    System.out.println("\n ----------------------" + "\n Blocked User" + "\n ----------------------");
                } else {
                    System.out.println("---------------------------------");
                    System.out.println("Email Number " + (i + 1));
                    System.out.println("Subject: " + message.getSubject());
                    System.out.println("From: " + getNameOfAddress(message));
                    System.out.println("Text: " + getTextFromMessage(message));
                    System.out.println("---------------------------------");
                    System.out.println(getNameOfAddress(message));
                    if (Arrays.asList(white_list).contains(getNameOfAddress(message)))
                    {
                        sendMail(getNameOfAddress(message));
                        System.out.println("Auto check");
                    }
                }
            }
            emailFolder.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append("\n").append(bodyPart.getContent());
                break; // without break same text appears twice in my tests
            } else if (bodyPart.getContent() instanceof MimeMultipart){
                result.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }

    private static String getNameOfAddress(Message text) throws MessagingException {
        String result = "";
        Pattern pattern = Pattern.compile("<(.+)>");
        Matcher matcher = pattern.matcher(text.getFrom()[0].toString());
        while(matcher.find())
            result = matcher.group();
        return result.substring(1, result.length() - 1);
    }

    public static void main(String[] args) throws InterruptedException {

        String host = "pop.gmail.com";
        String username = "sttreakstest2@gmail.com";
        String password = "1234Test1234";
        while (true){
            System.out.println("+++++++++++++++++++++++++++++++\n \n \n     New         try\n \n \n+++++++++++++++++++++++++++++++");
            checkMail(host, username, password);
            TimeUnit.SECONDS.sleep(10);
        }
    }
}
