package forge.ai.ability;


import forge.ai.*;
import forge.game.card.Card;
import forge.game.card.CardPredicates;
import forge.game.phase.PhaseType;
import forge.game.player.Player;
import forge.game.player.PlayerActionConfirmMode;
import forge.game.player.PlayerCollection;
import forge.game.player.PlayerPredicates;
import forge.game.spellability.SpellAbility;
import forge.game.zone.ZoneType;
import forge.util.IterableUtil;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class RepeatAi extends SpellAbilityAi {

    @Override
    protected boolean canPlayAI(Player ai, SpellAbility sa) {
        // Ideally we should be targeting the most dangerous player
        final Player opp = AiAttackController.choosePreferredDefenderPlayer(ai);
        String logic = sa.getParamOrDefault("AILogic", "");

        if ("HelmOfObedience".equals(logic)) {
            final int max = ComputerUtilCost.getMaxXValue(sa, ai, sa.isTrigger());
            if (max == 0) {
                return false;
            }

            // If there are any cards that replace graveyard entering with exile, activate Helm of Obedience immediately
            boolean comboUnlocked = ai.getGame().getCardsIn(ZoneType.Battlefield).anyMatch(new Predicate<Card>() {
                @Override
                public boolean test(Card card) {
                    // Ideally we should be checking for cards that replace graveyard entering with exile
                    if (card.getName().equals("Rest in Peace")) {
                        return true;
                    }

                    if (card.getName().equals("Leyline of the Void") || card.getName().equals("Dauthi Voidwalker")) {
                        return card.getController().equals(ai);
                    }

                    // Theres a few bonus cases if your target casts Yawgmoth's Will or has Yawgmoth's Agenda in play
                    // But probably not really worth it to rely on

                    return false;
                }
            });

            if (comboUnlocked) {
                List<Player> opponents = ai.getOpponents();
                // Ideally sort opponents by Threat level
                if (opponents.isEmpty()) {
                    return false;
                }

                Player target = null;
                for(Player opponent : opponents) {
                    if (!sa.canTarget(opponent)) {
                        continue;
                    }

                    if (opponent.getCardsIn(ZoneType.Library).isEmpty()) {
                        continue;
                    }

                    target = opponent;
                    break;
                }

                if (target == null) {
                    return false;
                }
                assignTarget(target, sa);

                sa.setXManaCostPaid(1);
                return true;
            }

            // If we're waiting fot the combo pieces, don't activate Helm of Obedience
            boolean waitingForCombo = ai.getGame().getCardsIn(ZoneType.Library).anyMatch(new Predicate<Card>() {
                @Override
                public boolean test(Card card) {
                    return card.getName().equals("Rest in Peace") || card.getName().equals("Leyline of the Void") || card.getName().equals("Dauthi Voidwalker");
                }
            });

            if (waitingForCombo) {
                return false;
            }

            // Otherwise Helm of Obedience will be activated in the end step
            if (!(ai.equals(ai.getGame().getPhaseHandler().getNextTurn()) && ai.getGame().getPhaseHandler().is(PhaseType.END_OF_TURN))) {
                return false;
            }

            // If we've fallen through to here, we should target
            if (!assignTarget(opp, sa)) {
                return false;
            }

            sa.setXManaCostPaid(max);
            return max > 0;
        }
        else {
            if (!assignTarget(opp, sa)) {
                return false;
            }
        }
        if ("MaxX".equals(logic) || "MaxXAtOppEOT".equals(logic)) {
            if ("MaxXAtOppEOT".equals(logic) && !(ai.getGame().getPhaseHandler().is(PhaseType.END_OF_TURN) && ai.getGame().getPhaseHandler().getNextTurn() == ai)) {
                return false;
            }
            // Set PayX here to maximum value.
            final int max = ComputerUtilCost.getMaxXValue(sa, ai, sa.isTrigger());
            sa.setXManaCostPaid(max);
            return max > 0;
        }
        return true;
    }

    private boolean assignTarget(Player opp, SpellAbility sa) {
        if (sa.usesTargeting()) {
            if (!sa.canTarget(opp)) {
                return false;
            }
            sa.resetTargets();
            sa.getTargets().add(opp);
        }
        return true;
    }

    
    @Override
    public boolean confirmAction(Player player, SpellAbility sa, PlayerActionConfirmMode mode, String message, Map<String, Object> params) {
      //TODO add logic to have computer make better choice (ArsenalNut)
        return false;
    }

    @Override
    protected boolean doTriggerAINoCost(Player ai, SpellAbility sa, boolean mandatory) {
        String logic = sa.getParamOrDefault("AILogic", "");

        if (sa.usesTargeting()) {
            if (logic.startsWith("CopyBestCreature")) {
                Card best = null;
                Iterable<Card> targetableAi = IterableUtil.filter(ai.getCreaturesInPlay(), CardPredicates.isTargetableBy(sa));
                if (!logic.endsWith("IgnoreLegendary")) {
                    best = ComputerUtilCard.getBestAI(IterableUtil.filter(targetableAi, Card::ignoreLegendRule));
                } else {
                    best = ComputerUtilCard.getBestAI(targetableAi);
                }
                if (best == null && mandatory && sa.canTarget(sa.getHostCard())) {
                    best = sa.getHostCard();
                }
                if (best != null) {
                    sa.resetTargets();
                    sa.getTargets().add(best);
                    return true;
                }
                return false;
            }

            PlayerCollection targetableOpps = ai.getOpponents().filter(PlayerPredicates.isTargetableBy(sa));
            Player opp = targetableOpps.min(PlayerPredicates.compareByLife());
            if (opp != null) {
                sa.resetTargets();
                sa.getTargets().add(opp);
            } else if (!mandatory) {
                return false;
            }

        }

    	// setup subability to repeat
        final SpellAbility repeat = sa.getAdditionalAbility("RepeatSubAbility");

        if (repeat == null) {
        	return mandatory;
        }

        AiController aic = ((PlayerControllerAi)ai.getController()).getAi();
        return aic.doTrigger(repeat, mandatory);
    }
}
