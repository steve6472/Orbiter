package steve6472.orbiter.debug;

import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 10/6/2024
 * Project: Orbiter <br>
 */
class OverlayPanel extends JPanel
{
    private Supplier<CompletableFuture<Suggestions>> suggestionsSupplier;

    OverlayPanel(Supplier<CompletableFuture<Suggestions>> suggestionsSupplier)
    {
        this.suggestionsSupplier = suggestionsSupplier;
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);

        List<Suggestion> list = getSuggestions();

        for (int i = 0; i < list.size(); i++)
        {
            Suggestion suggestion = list.get(i);
            String text = suggestion.getText();

            g.setColor(Color.LIGHT_GRAY);
            FontMetrics metrics = g.getFontMetrics();
            int textWidth = metrics.stringWidth(text);
            int textHeight = metrics.getHeight();
            int x = 2;
            int y = getHeight() - i * textHeight - 25;

            g.fillRect(x, y - textHeight, textWidth, textHeight);

            g.setColor(Color.DARK_GRAY);
            g.drawString(text, x, y - 4);
        }
    }

    private List<Suggestion> getSuggestions()
    {
        CompletableFuture<Suggestions> suggestions = suggestionsSupplier.get();

        try
        {
            return suggestions.get().getList();
        } catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
        }
        return List.of();
    }
}
