package client.java;

public class Position {
    // TODO: x,yをprivateにする(Othello.java内で値を直接参照している箇所を修正する必要あり)
    public int x;
    public int y;
    public Position(int y, int x){
        this.x = x;
        this.y = y;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
}
