package smartcity;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by DSV on 05/01/17.
 */
public class Email {

    String id;
    String message;

    public Email(String id) {
        this.id = id;
        this.message = "";
    }

    public static List<Email> getEmailList(List<DataEntryDifference> differences) {

        List<String> addedEmailIDs = new ArrayList<>();
        List<Email> emailList = new ArrayList<>();
        try {

            for (DataEntryDifference difference : differences) {
                String fieldID = difference.fieldID;
                String fieldName = difference.fieldName;
                List<String> previousValues = difference.previousValues;
                List<String> newValues = difference.newValues;
                List<String> headers = difference.headers;


                //MODIFICATION IN WORK
                if (fieldName.equals(LoadProperties.properties.getString("Subscribers.Field1"))) {

                    //getting all the emails subscribed to the work id
                    BasicDBObject whereQuery = new BasicDBObject(LoadProperties.properties.getString("Subscribers.Field1"), Integer.parseInt(fieldID));
                    DBCursor cursor = Database.subscribers.find(whereQuery);
                    List<String> subscribedEmailIDs = new ArrayList<>();
                    while (cursor.hasNext()) {
                        subscribedEmailIDs.add((String) cursor.next().get("email"));
                    }

                    if (subscribedEmailIDs.size() > 0) {

                        //creating the message for a particular work
                        String workDescription = Work.getWorkDescriptionOfWork(Integer.parseInt(fieldID));
                        String currentMessage = "The following has changed in the work: " + workDescription + "\n";
                        int headersSize = headers.size();
                        for (int i = 0; i < headersSize; i++) {
                            String msg = " - " + headers.get(i) + " has changed from '" + previousValues.get(i) + "' to '" + newValues.get(i) + "'";
                            currentMessage += msg + "\n";
                            if (i == headersSize - 1) {
                                currentMessage += "\n";
                            }
                        }
                        //making the email object
                        for (String id : subscribedEmailIDs) {
                            Email email = new Email(id);
                            if (!addedEmailIDs.contains(id)) {
                                email.message += currentMessage + "\n";
                                emailList.add(email);
                                addedEmailIDs.add(id);
                            } else {
                                for (Email everyEmail : emailList) {
                                    if (id.equals(everyEmail.id)) {
                                        email = everyEmail;
                                        break;
                                    }
                                }
                                email.message += currentMessage + "\n";
                            }
                        }
                    }
                }


                //MODIFICATION IN WARD - MODIFIED WORKS IN WARD AND NEW WORKS IN WARD
                if (fieldName.equals(LoadProperties.properties.getString("Subscribers.Field2"))) {

                    //getting all the emails subscribed to the ward
                    BasicDBObject whereQuery = new BasicDBObject(LoadProperties.properties.getString("Subscribers.Field2"), Integer.parseInt(fieldID));
                    DBCursor cursor = Database.subscribers.find(whereQuery);
                    List<String> subscribedEmailIDs = new ArrayList<>();
                    while (cursor.hasNext()) {
                        subscribedEmailIDs.add((String) cursor.next().get("email"));
                    }

                    if (subscribedEmailIDs.size() > 0) {

                        //Modified works in a particular ward
                        if (previousValues != null) {
                            previousValues = previousValues.stream().distinct().collect(Collectors.toList());
                            for (String workID : previousValues) {
                                DataEntryDifference modifiedWork = differences.get(differences.indexOf(new DataEntryDifference(workID, LoadProperties.properties.getString("Subscribers.Field1"))));
                                String workDescription = Work.getWorkDescriptionOfWork(Integer.parseInt(modifiedWork.fieldID));
                                String currentMessage = "In ward " + fieldID + ", the following has changed in the work: " + workDescription + "\n";
                                int modifiedWorkHeadersSize = modifiedWork.headers.size();
                                for (int i = 0; i < modifiedWorkHeadersSize; i++) {
                                    String msg = " - " + modifiedWork.headers.get(i) + " has changed from '" + modifiedWork.previousValues.get(i) + "' to '" + modifiedWork.newValues.get(i) + "'";
                                    currentMessage += msg + "\n";
                                    if (i == modifiedWorkHeadersSize - 1) {
                                        currentMessage += "\n";
                                    }
                                }
                                for (String id : subscribedEmailIDs) {
                                    Email email = new Email(id);
                                    if (!addedEmailIDs.contains(id)) {
                                        email.message += currentMessage + "\n";
                                        emailList.add(email);
                                        addedEmailIDs.add(id);
                                    } else {
                                        for (Email everyEmail : emailList) {
                                            if (id.equals(everyEmail.id)) {
                                                email = everyEmail;
                                                break;
                                            }
                                        }
                                        email.message += currentMessage + "\n";
                                    }
                                }
                            }
                        }

                        //new works in a particular ward
                        if (newValues != null) {
                            for (String workID : newValues) {
                                String workDescription = Work.getWorkDescriptionOfWork(Integer.parseInt(workID));
                                String currentMessage = "In ward " + fieldID + ", the following work has been added: " + workDescription + "\n";

                                for (String id : subscribedEmailIDs) {
                                    Email email = new Email(id);
                                    if (!addedEmailIDs.contains(id)) {
                                        email.message += currentMessage + "\n";
                                        emailList.add(email);
                                        addedEmailIDs.add(id);
                                    } else {
                                        for (Email everyEmail : emailList) {
                                            if (id.equals(everyEmail.id)) {
                                                email = everyEmail;
                                                break;
                                            }
                                        }
                                        email.message += currentMessage + "\n";
                                    }
                                }
                            }
                        }
                    }
                }

                //MODIFICATION IN SOURCE OF INCOME - MODIFIED WORKS AND NEW WORKS
                if (fieldName.equals(LoadProperties.properties.getString("Subscribers.Field3"))) {

                    //getting all the emails subscribed to the source of income
                    BasicDBObject whereQuery = new BasicDBObject(LoadProperties.properties.getString("Subscribers.Field3"), Integer.parseInt(fieldID));
                    DBCursor cursor = Database.subscribers.find(whereQuery);
                    List<String> subscribedEmailIDs = new ArrayList<>();
                    while (cursor.hasNext()) {
                        subscribedEmailIDs.add((String) cursor.next().get("email"));
                    }

                    if (subscribedEmailIDs.size() > 0) {

                        //Modified works with a particular SOI
                        if (previousValues != null) {
                            previousValues = previousValues.stream().distinct().collect(Collectors.toList());
                            for (String workID : previousValues) {
                                DataEntryDifference modifiedWork = differences.get(differences.indexOf(new DataEntryDifference(workID, LoadProperties.properties.getString("Subscribers.Field1"))));
                                String workDescription = Work.getWorkDescriptionOfWork(Integer.parseInt(modifiedWork.fieldID));
                                String sourceOfIncome = Work.getSourceOfIncomeNameOfID(Integer.parseInt(fieldID));
                                String currentMessage = "With Source of Income - " + sourceOfIncome + ", the following has changed in the work: " + workDescription + "\n";
                                int modifiedWorkHeadersSize = modifiedWork.headers.size();
                                for (int i = 0; i < modifiedWorkHeadersSize; i++) {
                                    String msg = " - " + modifiedWork.headers.get(i) + " has changed from '" + modifiedWork.previousValues.get(i) + "' to '" + modifiedWork.newValues.get(i) + "'";
                                    currentMessage += msg + "\n";
                                    if (i == modifiedWorkHeadersSize - 1) {
                                        currentMessage += "\n";
                                    }
                                }
                                for (String id : subscribedEmailIDs) {
                                    Email email = new Email(id);
                                    if (!addedEmailIDs.contains(id)) {
                                        email.message += currentMessage + "\n";
                                        emailList.add(email);
                                        addedEmailIDs.add(id);
                                    } else {
                                        for (Email everyEmail : emailList) {
                                            if (id.equals(everyEmail.id)) {
                                                email = everyEmail;
                                                break;
                                            }
                                        }
                                        email.message += currentMessage + "\n";
                                    }
                                }
                            }
                        }

                        //new works in a particular ward
                        if (newValues != null) {
                            for (String workID : newValues) {
                                String workDescription = Work.getWorkDescriptionOfWork(Integer.parseInt(workID));
                                String sourceOfIncome = Work.getSourceOfIncomeNameOfID(Integer.parseInt(fieldID));
                                String currentMessage = "The following work has been added: " + workDescription + " with Source of Income - " + sourceOfIncome + "\n";
                                for (String id : subscribedEmailIDs) {
                                    Email email = new Email(id);
                                    if (!addedEmailIDs.contains(id)) {
                                        email.message += currentMessage + "\n";
                                        emailList.add(email);
                                        addedEmailIDs.add(id);
                                    } else {
                                        for (Email everyEmail : emailList) {
                                            if (id.equals(everyEmail.id)) {
                                                email = everyEmail;
                                                break;
                                            }
                                        }
                                        email.message += currentMessage + "\n";
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        printEmails(emailList);
        return emailList;
    }

    private static void printEmails(List<Email> emailList) {
        for (Email email : emailList) {
            System.out.println(email.id);
            System.out.println(email.message);
            System.out.println("-----------------------------------------");
        }
    }
}
