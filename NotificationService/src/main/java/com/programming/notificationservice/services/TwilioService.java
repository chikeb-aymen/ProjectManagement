package com.programming.notificationservice.services;


import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

@Service
public class TwilioService {


    public static final String ACCOUNT_SID = "AC7096bbf6fb3e6739194eeda9a6ff4141";
    public static final String AUTH_TOKEN = "c0c65321cb26741905f3f0a3da0ed86d";

    //Phone number successfully purchased!
    //SID                                 Phone Number  Friendly Name
    //PN717bf413f9dd93acd0010342508bdd50  +14635831414  (463) 583-1414


    public void assigneToMessage(Integer phoneNumber,String projectName){

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                        new com.twilio.type.PhoneNumber("+212"+phoneNumber),
                        new com.twilio.type.PhoneNumber("+14635831414"),
                        "You have a new task assigned to you in "+projectName+" project \n" +
                                "Good Luck 🫡")
                .create();

        System.out.println(message.getSid());

    }

}
