package bulletinBoardService;

interface Messanger {
    void start();
    void stop();
    void send();
}

interface UITasks {
    String getMessage();
    void setText(String txt);
}