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
    public final List<List<Hex>> oldPatterns = new ArrayList<>();

    public final List<Hex> currentPattern = new ArrayList<>();

    public void addHexCoords(Hex hexCoords)
    {
        if (intersectsOldPattern(hexCoords))
            return;

        // Backtracking
        if (currentPattern.size() >= 2)
        {
            if (currentPattern.get(currentPattern.size() - 2).equals(hexCoords))
            {
                currentPattern.removeLast();
            }
        }

        if (isExistingPair(hexCoords))
            return;

        if (currentPattern.isEmpty())
        {
            currentPattern.add(hexCoords);
            return;
        }

        if (!currentPattern.getLast().equals(hexCoords) && currentPattern.getLast().distance(hexCoords) == 1)
        {
            currentPattern.add(hexCoords);
        }
    }

    private boolean intersectsOldPattern(Hex hexCoords)
    {
        for (List<Hex> oldPattern : oldPatterns)
        {
            if (oldPattern.contains(hexCoords))
                return true;
        }
        return false;
    }

    private boolean isExistingPair(Hex hexCoords)
    {
        if (currentPattern.size() < 2)
            return false;
        Hex last = currentPattern.getLast();

        for (int i = 0; i < currentPattern.size() - 1; i++)
        {
            Hex hex1 = currentPattern.get(i);
            Hex hex2 = currentPattern.get(i + 1);

            if ((hex1.equals(last) && hex2.equals(hexCoords)) || (hex1.equals(hexCoords) && hex2.equals(last)))
                return true;
        }
        return false;
    }

    public String finishPattern()
    {
        String r = get();
        if (currentPattern.size() >= 2)
            oldPatterns.add(List.copyOf(currentPattern));
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
