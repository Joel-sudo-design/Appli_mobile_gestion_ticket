package com.example.appli_mobile;

public class model {

    private final String ticketNumber;
    private final String category;
    private final String priority;
    private final String title;
    private final String description;
    private final String answer;
    private final String Date;
    private final Boolean isopen;
    private static Boolean isExpanded;

    public model(String ticketNumber, String category, String priority, String title, String description, String answer, String date,  Boolean isopen) {
        this.ticketNumber = ticketNumber;
        this.category = category;
        this.priority = priority;
        this.title = title;
        this.description = description;
        this.answer = answer;
        this.Date = date;
        this.isopen = isopen;
        isExpanded = false;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public String getCategory() {
        return category;
    }

    public String getPriority() {
        return priority;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {return Date;}

    public String getAnswer() {return answer;}

    public static Boolean isExpanded() {return isExpanded;}

    public Boolean getIsopen() {return isopen;}

}
