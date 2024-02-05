package networking;

import app.ServentInfo;

import java.io.Serializable;

public class ServentNeighbours implements Serializable {
    private ServentInfo firstLeft;
    private ServentInfo firstRight;
    private ServentInfo secondLeft;
    private ServentInfo secondRight;

    @Override
    public String toString() {
        return secondLeft + ", " + firstLeft + ", " + firstRight + ", " + secondRight;
    }

    public void setAll(ServentInfo serventInfo){
        this.firstRight = this.secondRight = this.firstLeft = this.secondLeft = serventInfo;
    }
    public void setFirstLeft(ServentInfo firstLeft) {
        this.firstLeft = firstLeft;
    }

    public void setFirstRight(ServentInfo firstRight) {
        this.firstRight = firstRight;
    }

    public void setSecondLeft(ServentInfo secondLeft) {
        this.secondLeft = secondLeft;
    }

    public void setSecondRight(ServentInfo secondRight) {
        this.secondRight = secondRight;
    }

    public ServentInfo getFirstLeft() {
        return firstLeft;
    }

    public ServentInfo getFirstRight() {
        return firstRight;
    }

    public ServentInfo getSecondLeft() {
        return secondLeft;
    }

    public ServentInfo getSecondRight() {
        return secondRight;
    }
}

