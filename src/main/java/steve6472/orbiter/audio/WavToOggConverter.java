package steve6472.orbiter.audio;

import steve6472.core.log.Log;

import java.io.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class WavToOggConverter
{
    private static final Logger LOGGER = Log.getLogger(WavToOggConverter.class);

    public static void main(String[] args)
    {
        args = new String[] {"C:\\storage\\Download\\Sonniss.com-GDC2024-GameAudioBundle"};

        if (args.length != 1)
        {
            LOGGER.info("Usage: java WavToOggConverter <folder_path>");
            return;
        }

        File rootDir = new File(args[0]);
        if (!rootDir.exists() || !rootDir.isDirectory())
        {
            LOGGER.severe("Invalid directory: " + rootDir.getAbsolutePath());
            return;
        }

        // Create converted root directory
        File convertedRoot = new File(rootDir.getParent(), rootDir.getName() + "_converted");
        if (!convertedRoot.exists())
        {
            convertedRoot.mkdirs();
        }

        processDirectory(rootDir, convertedRoot);
    }

    private static void processDirectory(File sourceDir, File targetDir)
    {
        for (File file : sourceDir.listFiles())
        {
            if (file.isDirectory())
            {
                File newTargetDir = new File(targetDir, file.getName());
                newTargetDir.mkdirs();
                processDirectory(file, newTargetDir);
            } else if (file.isFile() && file.getName().toLowerCase().endsWith(".wav"))
            {
                convertWavToOgg(file, new File(targetDir, file.getName().replaceAll("(?i)\\.wav$", ".ogg")));
            }
        }
    }

    private static void convertWavToOgg(File inputFile, File outputFile)
    {
        try
        {
            String command = String.format("ffmpeg -hide_banner -loglevel error -y -i \"%s\" \"%s\"", inputFile.getAbsolutePath(), outputFile.getAbsolutePath());

            Process process = Runtime.getRuntime().exec(command);
            consumeStream(process.getErrorStream(), LOGGER::fine);
            int exitCode = process.waitFor();

            if (exitCode == 0)
            {
                LOGGER.info("Converted: " + inputFile.getAbsolutePath() + " -> " + outputFile.getAbsolutePath());
            } else
            {
                LOGGER.severe("Failed to convert: " + inputFile.getAbsolutePath());
            }
        } catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private static void consumeStream(InputStream stream, Consumer<String> print)
    {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                print.accept(line);
            }
        } catch (IOException e)
        {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}