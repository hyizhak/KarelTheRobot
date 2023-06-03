package logic;

import java.io.Serializable;

public class StageSave implements Serializable {

    public String code, logs, errorMessages;
    public int stageNo, gameState;
    public KarelRobot rob;

    public StageSave(String code, String logs, String errorMessages, int stageNo, int gameState,
                     KarelRobot rob) {
        this.code = code;
        this.logs = logs;
        this.errorMessages = errorMessages;
        this.stageNo = stageNo;
        this.gameState = gameState;
        this.rob = rob;
    }

    public static void main(String[] args) {

    }
}
