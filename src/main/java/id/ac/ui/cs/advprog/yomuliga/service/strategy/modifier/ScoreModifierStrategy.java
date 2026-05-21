package id.ac.ui.cs.advprog.yomuliga.service.strategy.modifier;

public interface ScoreModifierStrategy {
    double applyModifier(double currentScore);
    boolean isApplicable(String clanId);
}
