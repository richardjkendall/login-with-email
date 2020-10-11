package com.richardjameskendall.ses;

import java.io.IOException;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
//import com.amazonaws.services.simpleemail.model.Body;
//import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
//import com.amazonaws.services.simpleemail.model.Message;
//import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendTemplatedEmailRequest;

import com.google.gson.Gson;
import java.util.HashMap;


public class EmailSender {
    private AmazonSimpleEmailService client;

    public EmailSender(String region) {
        client = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.fromName(region)).build();
    }

    public void sendWithTemplate(String to, String from, String template, HashMap<String, String> fields) {
        Gson gson = new Gson();
        SendTemplatedEmailRequest request = new SendTemplatedEmailRequest()
                .withDestination(new Destination().withToAddresses(to))
                .withTemplate(template)
                .withTemplateData(gson.toJson(fields))
                .withSource(from);
        client.sendTemplatedEmail(request);
    }
}
