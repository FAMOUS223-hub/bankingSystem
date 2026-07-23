package com.calbank.ui;

import com.calbank.ui.theme.ThemeManager;
import com.calbank.utils.IconUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.IntConsumer;

/**
 * Visual Edit / Delete buttons in JTable cells with mouse-click handling.
 */
public final class TableActionButtons {

    private static final int GAP = 8;

    private TableActionButtons() {}

    public static TableCellRenderer createRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, GAP, 4));
                panel.setOpaque(true);
                panel.setBackground(isSelected
                    ? ThemeManager.getTableSelectionColor()
                    : (row % 2 == 0 ? ThemeManager.getTableRowEven() : ThemeManager.getTableRowOdd()));

                boolean allowDelete = !(value instanceof Boolean) || (Boolean) value;

                JLabel editLabel = createButtonLabel(IconUtils.get("edit") + " Edit", ThemeManager.getInfoColor());
                panel.add(editLabel);

                JLabel deleteLabel = createButtonLabel(IconUtils.get("delete") + " Delete", allowDelete ? ThemeManager.getErrorColor() : ThemeManager.getTextColorMuted());
                deleteLabel.setEnabled(allowDelete);
                panel.add(deleteLabel);

                return panel;
            }
        };
    }

    public static void attachMouseHandler(JTable table, int column, IntConsumer onEdit, IntConsumer onDelete) {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0 || col != column) {
                    return;
                }

                Rectangle cell = table.getCellRect(row, col, false);
                int relativeX = e.getX() - cell.x;
                int midPoint = 85;

                if (relativeX < midPoint) {
                    if (onEdit != null) {
                        onEdit.accept(row);
                    }
                } else if (onDelete != null) {
                    Object value = table.getValueAt(row, column);
                    boolean allowDelete = !(value instanceof Boolean) || (Boolean) value;
                    if (allowDelete) {
                        onDelete.accept(row);
                    }
                }
            }
        });
    }

    private static JLabel createButtonLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(ThemeManager.getSmallFont());
        label.setForeground(Color.WHITE);
        label.setOpaque(true);
        label.setBackground(color);
        label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return label;
    }
}
