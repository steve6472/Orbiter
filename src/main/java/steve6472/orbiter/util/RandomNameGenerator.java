package steve6472.orbiter.util;

import java.util.Random;

public class RandomNameGenerator
{
    private static final String[] FIRST_PREFIXES = {"Al", "Mar", "El", "Jo", "Da", "Ka", "Lu", "Ni", "Sa", "Be", "Ro", "Ti"};
    private static final String[] FIRST_MIDDLES = {"an", "ar", "el", "or", "ia", "en", "al", "is", "us", "on", "et"};
    private static final String[] FIRST_SUFFIXES = {"a", "o", "e", "an", "er", "us", "in", "ie", "en"};

    private static final String[] LAST_PREFIXES = {"Smith", "Ander", "Car", "Mont", "Ash", "Gre", "Whit", "Black", "Brook", "Stone"};
    private static final String[] LAST_SUFFIXES = {"son", "man", "field", "wood", "ton", "well", "ridge", "ford", "berg", "hall"};

    private static final Random rand = new Random();

    public static String generateFirstName()
    {
        String prefix = FIRST_PREFIXES[rand.nextInt(FIRST_PREFIXES.length)];
        String middle = FIRST_MIDDLES[rand.nextInt(FIRST_MIDDLES.length)];
        String suffix = FIRST_SUFFIXES[rand.nextInt(FIRST_SUFFIXES.length)];

        return prefix + middle + suffix;
    }

    public static String generateLastName()
    {
        String prefix = LAST_PREFIXES[rand.nextInt(LAST_PREFIXES.length)];
        String suffix = LAST_SUFFIXES[rand.nextInt(LAST_SUFFIXES.length)];

        return prefix + suffix;
    }

    public static String generateFullName()
    {
        return generateFirstName() + " " + generateLastName();
    }
}