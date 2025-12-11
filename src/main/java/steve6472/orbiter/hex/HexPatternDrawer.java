package steve6472.orbiter.hex;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 12/11/2025
 * Project: Orbiter <br>
 */
public class HexPatternDrawer
{
    public final List<Hex> currentPattern = new ArrayList<>();

    public void addHexCoords(Hex hexCoords)
    {
        if (currentPattern.isEmpty())
        {
            currentPattern.add(hexCoords);
            return;
        }

        if (!currentPattern.getLast().equals(hexCoords) && currentPattern.getLast().distance(hexCoords) == 1)
        {
            currentPattern.add(hexCoords);
        }

        // Backtracking
        if (currentPattern.size() >= 3)
        {
            if (currentPattern.get(currentPattern.size() - 3).equals(hexCoords))
            {
                currentPattern.removeLast();
                currentPattern.removeLast();
            }
        }
    }

    public String finishPattern()
    {
        String r = get();
        currentPattern.clear();
        return r;
    }

    public String get()
    {
        return createCodeFromPoints(currentPattern);
    }

    public static String createCodeFromPoints(List<Hex> patternPoints)
    {
        if (patternPoints.size() <= 1)
            return "";

        if (patternPoints.size() > 2)
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < patternPoints.size() - 2; i++)
            {
                HexVector hexVector = toHexVector(patternPoints.get(i), patternPoints.get(i + 1));
                HexVector nextVector = toHexVector(patternPoints.get(i + 1), patternPoints.get(i + 2));
                sb.append(hexVector.getDirection(nextVector).code);
            }
            return sb.toString();
        }
        return "";
    }

    public static HexVector toHexVector(Hex from, Hex to)
    {
        return HexVector.fromDirection(from.sub(to));
    }
}
