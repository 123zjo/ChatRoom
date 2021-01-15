package cn.edu.hcnu.client;

public class Clicent {
    public static void main(String[] args) {
        Thread thread = new LoginThread();
        thread.start();
    }
}
