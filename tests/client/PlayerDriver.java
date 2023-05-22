package client;

public class PlayerDriver {
    public static void main(String[] args) {
        Player player = new Player();
        System.out.println("setName: test");
        player.setName("test");
        System.out.println("getName: " + player.getName());
        System.out.println("setColor: black");
        player.setColor("black");
        System.out.println("getColor: " + player.getColor());
    }
    
}
