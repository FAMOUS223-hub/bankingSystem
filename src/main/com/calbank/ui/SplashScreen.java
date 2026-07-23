package com.calbank.ui;

import com.calbank.ui.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public final class SplashScreen extends JPanel {

    private float progress = 0f;
    private float rotation = 0f;
    private final Timer spinTimer;

    public SplashScreen() {
        setOpaque(true);
        setBackground(new Color(20, 60, 160));
        spinTimer = new Timer(30, e -> {
            rotation += 8f;
            if (rotation >= 360f) rotation -= 360f;
            repaint();
        });
        spinTimer.start();
    }

    public void stopAnimation() {
        spinTimer.stop();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(20, 60, 160),
            0, h, new Color(10, 35, 100));
        g2.setPaint(gradient);
        g2.fillRect(0, 0, w, h);

        g2.setColor(new Color(255, 255, 255, 15));
        g2.fillOval(-50, -50, 200, 200);
        g2.fillOval(w - 100, h - 100, 180, 180);
        g2.setColor(new Color(255, 255, 255, 8));
        g2.fillOval(w / 2 - 200, -80, 400, 400);

        int iconX = w / 2 - 30;
        int iconY = h / 2 - 100;
        g2.setColor(new Color(255, 255, 255, 220));
        g2.setStroke(new BasicStroke(3));
        int[] xPoints = {iconX + 30, iconX + 60, iconX + 30, iconX};
        int[] yPoints = {iconY, iconY + 10, iconY + 55, iconY + 10};
        g2.drawPolygon(xPoints, yPoints, 4);
        g2.setColor(new Color(255, 255, 255, 40));
        g2.fillPolygon(xPoints, yPoints, 4);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        String dollar = "$";
        FontMetrics fmDollar = g2.getFontMetrics();
        g2.drawString(dollar, iconX + 30 - fmDollar.stringWidth(dollar) / 2, iconY + 40);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 44));
        String title = "CalBank";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, (w - fm.stringWidth(title)) / 2, h / 2 - 20);

        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        g2.setColor(new Color(200, 215, 255));
        String tagline = "Smart Banking for Everyone";
        fm = g2.getFontMetrics();
        g2.drawString(tagline, (w - fm.stringWidth(tagline)) / 2, h / 2 + 15);

        int cx = w / 2;
        int cy = h / 2 + 70;
        AffineTransform old = g2.getTransform();
        g2.translate(cx, cy);
        g2.rotate(Math.toRadians(rotation));
        g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(255, 255, 255, 200));
        g2.drawArc(-16, -16, 32, 32, 0, 90);
        g2.setColor(new Color(255, 255, 255, 120));
        g2.drawArc(-16, -16, 32, 32, 90, 90);
        g2.setColor(new Color(255, 255, 255, 60));
        g2.drawArc(-16, -16, 32, 32, 180, 90);
        g2.setTransform(old);

        int barWidth = 300;
        int barHeight = 4;
        int barX = (w - barWidth) / 2;
        int barY = h / 2 + 120;
        g2.setColor(new Color(255, 255, 255, 40));
        g2.fillRoundRect(barX, barY, barWidth, barHeight, barHeight, barHeight);

        int fillWidth = (int) (barWidth * progress);
        if (fillWidth > 0) {
            GradientPaint barGrad = new GradientPaint(
                barX, barY, new Color(100, 200, 255),
                barX + fillWidth, barY, new Color(200, 230, 255));
            g2.setPaint(barGrad);
            g2.fillRoundRect(barX, barY, fillWidth, barHeight, barHeight, barHeight);
        }

        g2.setColor(new Color(180, 200, 230));
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        String loading = "Loading your account...";
        fm = g2.getFontMetrics();
        g2.drawString(loading, (w - fm.stringWidth(loading)) / 2, h / 2 + 155);

        g2.setColor(new Color(100, 130, 180));
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        String version = "Version 2.0";
        fm = g2.getFontMetrics();
        g2.drawString(version, (w - fm.stringWidth(version)) / 2, h - 20);
    }

    public void updateProgress(float p) {
        this.progress = Math.min(p, 1f);
        repaint();
    }

    public float getProgress() {
        return progress;
    }
}
