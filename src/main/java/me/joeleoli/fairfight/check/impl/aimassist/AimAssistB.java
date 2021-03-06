package me.joeleoli.fairfight.check.impl.aimassist;

import me.joeleoli.fairfight.check.checks.RotationCheck;
import me.joeleoli.fairfight.player.PlayerData;
import me.joeleoli.fairfight.event.player.AlertType;
import me.joeleoli.fairfight.util.MathUtil;
import me.joeleoli.fairfight.util.update.RotationUpdate;
import org.bukkit.entity.Player;

public class AimAssistB extends RotationCheck {

    private float suspiciousYaw;
    
    public AimAssistB(PlayerData playerData) {
        super(playerData, "Aim (Check 2)");
    }
    
    @Override
    public void handleCheck(Player player, RotationUpdate update) {
        if (System.currentTimeMillis() - this.playerData.getLastAttackPacket() > 10000L) {
            return;
        }

        final float diffYaw = MathUtil.getDistanceBetweenAngles(update.getTo().getYaw(), update.getFrom().getYaw());

        if (diffYaw > 1.0f && Math.round(diffYaw * 10.0f) * 0.1f == diffYaw && Math.round(diffYaw) != diffYaw && diffYaw % 1.5f != 0.0f) {
            if (diffYaw == this.suspiciousYaw && this.alert(AlertType.RELEASE, player, String.format("Y %.1f.", diffYaw), true)) {
                final int violations = this.playerData.getViolations(this, 60000L);

                if (!this.playerData.isBanning() && violations > 20) {
                    this.ban(player);
                }
            }

            this.suspiciousYaw = Math.round(diffYaw * 10.0f) * 0.1f;
        } else {
            this.suspiciousYaw = 0.0f;
        }
    }

}
