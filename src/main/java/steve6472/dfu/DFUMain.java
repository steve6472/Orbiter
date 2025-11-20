package steve6472.dfu;

import com.google.gson.*;
import steve6472.core.util.GsonUtil;
import steve6472.core.util.JarExport;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Created by steve6472
 * Date: 11/17/2025
 * Project: Orbiter <br>
 */
public class DFUMain
{
    private static final File DATAFIX = new File("modules/datafix_override");
    private static final File DATAFIX_ORBITER = new File(DATAFIX, "orbiter");
    private static final File DATAFIX_MODEL = new File(DATAFIX_ORBITER, "model");
    private static final File DATAFIX_BB = new File(DATAFIX_MODEL, "blockbench");
    private static final File DATAFIX_BB_ANIMATED = new File(DATAFIX_BB, "animated");
    private static final File DATAFIX_BB_STATIC = new File(DATAFIX_BB, "static");

    private static final File SOURCE_ANIMATED = new File("modules/orbiter/orbiter/model/blockbench/animated");
    private static final File SOURCE_STATIC = new File("modules/orbiter/orbiter/model/blockbench/static");

    public static void main(String[] args)
    {
        JarExport.createFolderOrError(DATAFIX_BB_STATIC);
        JarExport.createFolderOrError(DATAFIX_BB_ANIMATED);

        Gson GSON = new GsonBuilder().setPrettyPrinting().create();

        processFolder(SOURCE_ANIMATED, DATAFIX_BB_ANIMATED, GSON);
        processFolder(SOURCE_STATIC, DATAFIX_BB_STATIC, GSON);
    }

    private static void processFolder(File source, File target, Gson gson)
    {
        if (!source.exists())
            return;

        File[] files = source.listFiles();
        if (files == null)
            return;

        for (File file : files)
        {
            if (file.isDirectory())
            {
                // mirror directory and recurse
                File newTarget = new File(target, file.getName());
                JarExport.createFolderOrError(newTarget);
                processFolder(file, newTarget, gson);
            } else if (file.isFile() && file.getName().endsWith(".bbmodel"))
            {
                processFile(file, target, gson);
            }
        }
    }

    private static void processFile(File inputFile, File targetDir, Gson gson)
    {
        JsonElement loaded = GsonUtil.loadJson(inputFile);
        if (!loaded.isJsonObject())
            return;

        JsonObject modelFile = loaded.getAsJsonObject();
        if (!ModelDataFixer.needsUpdate(modelFile))
            return;

        JsonObject fixed = ModelDataFixer.apply(modelFile);
        String output = gson.toJson(fixed);

        File outputFile = new File(targetDir, inputFile.getName());

        try (Writer writer = new FileWriter(outputFile))
        {
            writer.write(output);
            System.out.println("Fixed: " + inputFile.getPath() + " -> " + outputFile.getPath());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
