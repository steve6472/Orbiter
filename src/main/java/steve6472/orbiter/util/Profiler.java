package steve6472.orbiter.util;

public class Profiler
{
    private final double[] measurements;
    private final int measurementsToAverage;
    private int currentMeasurement;
    private long lastMeasurementTime;
    private double lastElapsed;
    private double lastAverage;

    public Profiler(int measurementsToAverage)
    {
        this.measurementsToAverage = measurementsToAverage;
        this.measurements = new double[measurementsToAverage];
        this.currentMeasurement = 0;
    }

    public void start()
    {
        lastMeasurementTime = System.nanoTime();
    }

    public void end()
    {
        lastElapsed = System.nanoTime() - lastMeasurementTime;
        measurements[currentMeasurement] = lastElapsed;
        currentMeasurement++;

        if (currentMeasurement == measurementsToAverage)
        {
            currentMeasurement = 0;
            lastAverage = calculateAverage();
        }
    }

    private double calculateAverage()
    {
        double totalElapsedTime = 0;
        for (double measurement : measurements)
        {
            totalElapsedTime += measurement;
        }
        return totalElapsedTime / measurementsToAverage;
    }

    public double averageNano()    { return lastAverage; }
    public double averageMilli()   { return lastAverage / 1e6d; }
    public double averageSeconds() { return lastAverage / 1e9d; }

    public double lastNano()    { return lastElapsed; }
    public double lastMilli()   { return lastElapsed / 1e6d; }
    public double lastSeconds() { return lastElapsed / 1e9d; }
}