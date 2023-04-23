public class test {
    public static void main(String[] args) {
        WorldDirector director = new WorldDirector();
        WorldBuilder builder = new WorldBuilder();
        director.stage3(builder);
        KarelRobot rob = builder.buildRobot();
        CompoundEval testMethod = new CompoundEval("if(noRockPresent()){move()}else{pickRock()}");
        System.out.println(testMethod.ifTrue);
        testMethod.ifInvoke(rob);
    }
}
